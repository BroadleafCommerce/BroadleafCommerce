/*
 * #%L
 * broadleaf-multitenant-singleschema
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.i18n.service;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.cache.OverridePreCacheInitializer;
import org.broadleafcommerce.common.cache.OverridePreCacheService;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.extension.ItemStatus;
import org.broadleafcommerce.common.extension.ResultType;
import org.broadleafcommerce.common.extension.StandardCacheItem;
import org.broadleafcommerce.common.extension.TemplateOnlyQueryExtensionManager;
import org.broadleafcommerce.common.i18n.dao.TranslationDao;
import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.broadleafcommerce.common.i18n.domain.Translation;
import org.broadleafcommerce.common.i18n.domain.TranslationImpl;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelper;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelperImpl;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.ejb.QueryHints;
import org.hibernate.ejb.criteria.CriteriaBuilderImpl;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * A retrieval and caching strategy for translations. Primarily supports multitenant scenarios with the following characteristics:
 * <ul>
 *     <li>A very large template translation catalog</li>
 *     <li>A small number of standard site overrides</li>
 *     <li>Small or large quantity of individual standard sites</li>
 * </ul>
 * Since the large catalog can be costly for the threshold count bounding inherent to {@link ThresholdCacheTranslationOverrideStrategy},
 * this strategy opts for completely caching overrides and minimizing template queries. The highest template query optimization
 * is achieved in conjunction with the 'precached.sparse.override.template.search.restrict.catalog' property set to true (false by
 * default). See com.broadleafcommerce.tenant.service.extension.MultiTenantTemplateOnlyQueryExtensionHandler for more information,
 * since this setting assumes the translated entity is in the same catalog as the {@link Translation} instance, which may
 * not be true for all installations.
 * </p>
 * Hybrid configurations are also possible for small to medium size template translation catalogs where the threshold
 * count bounding queries are not a concern and complete (or partial) caching of the template catalog can be achieved. However,
 * such a strategy may provide little to no benefit over the out-of-the-box {@link ThresholdCacheTranslationOverrideStrategy}.
 * See the {@link #templateEnabled} property for more information.
 * </p>
 * This strategy also works best when there are few standard sites with isolated values (i.e. the value was created in the standard
 * site and was not inherited from a profile or catalog). This is because the strategy can utilize an optimized template
 * query that is portable across sites if it doesn't have to take into account the standard site. The strategy uses
 * {@link OverridePreCacheService#isActiveIsolatedSiteForType(Long, String)} to figure out this state.
 * </p>
 * This strategy is disabled by default. Please see the javadoc for com.broadleafcommerce.tenant.service.cache.SparseOverridePreCacheServiceImpl (MultiTenant only)
 * for more information on how to enable this strategy via configuration of that service.
 *
 * @see ThresholdCacheTranslationOverrideStrategy
 * @author Jeff Fischer
 */
@Component("blSparseTranslationOverrideStrategy")
public class SparseTranslationOverrideStrategy implements TranslationOverrideStrategy, OverridePreCacheInitializer {

    public static final int PRECACHED_SPARSE_OVERRIDE_ORDER = -1000;

    @Resource(name = "blOverridePreCacheService")
    protected OverridePreCacheService preCachedSparseOverrideService;

    @Resource(name="blTemplateOnlyQueryExtensionManager")
    protected TemplateOnlyQueryExtensionManager extensionManager;

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blTranslationDao")
    protected TranslationDao dao;

    DynamicDaoHelper helper = new DynamicDaoHelperImpl();

    /**
     * Whether or not {@link #getLocaleBasedTemplateValue(String, String, TranslatedEntity, String, String, String, String, String)}
     * will be utilized from this strategy. If false, the fallback
     * {@link ThresholdCacheTranslationOverrideStrategy#getLocaleBasedTemplateValue(String, String, TranslatedEntity, String, String, String, String, String)}
     * will be used instead.
     * </p>
     * If the 'template' repository (MT concept) is large, this value should be left true. However, if the 'template' repository
     * is relatively small, you may want to consider setting this to false in order to leverage the possibility of complete
     * caching in the default strategy. See {@link TranslationSupport#getThresholdForFullCache()} for more info on this
     * option.
     * </p>
     * The default value is true. Set the 'precached.sparse.override.translation.template.enabled' property to change the value.
     * </p>
     * This value is meaningless if the current standard site is found to contain active isolated values for translations.
     * Review the documentation in com.broadleafcommerce.tenant.service.cache.SparseOverridePreCacheServiceImpl for more information.
     */
    @Value("${precached.sparse.override.translation.template.enabled:true}")
    protected boolean templateEnabled = true;

    /**
     * Whether or not to restrict the template search to the catalog/site of an associated item. For example, restrict
     * the translation of name for a Sku to the catalog of the Sku. Or, restrict
     * the translation of StructuredContent property to the site discriminator of the StructuredContent.
     * </p>
     * This value is false by default. Change the 'precached.sparse.override.translation.template.search.restrict.association' to
     * true to enable.
     */
    @Value("${precached.sparse.override.translation.template.search.restrict.association:false}")
    protected boolean restrictAssociation = false;

    @Override
    public LocalePair getLocaleBasedOverride(String property, TranslatedEntity entityType, String entityId,
                                             String localeCode, String localeCountryCode, String basicCacheKey) {
        LocalePair override = null;
        if (preCachedSparseOverrideService.isActiveForType(Translation.class.getName())) {
            boolean isSpecificOnly = localeCode.equals(localeCountryCode) && localeCode.contains("_");
            boolean isGeneralOnly = localeCode.equals(localeCountryCode) && !localeCode.contains("_");
            String specificCacheKey = getCacheKey(entityType, entityId, property, localeCountryCode);
            String generalCacheKey = getCacheKey(entityType, entityId, property, localeCode);
            List<StandardCacheItem> overrides;
            if (isSpecificOnly) {
                overrides = preCachedSparseOverrideService.findElements(specificCacheKey);
            } else if (isGeneralOnly) {
                overrides = preCachedSparseOverrideService.findElements(generalCacheKey);
            } else {
                overrides = preCachedSparseOverrideService.findElements(specificCacheKey, generalCacheKey);
            }
            override = new LocalePair();
            if (!overrides.isEmpty()) {
                if (isSpecificOnly && ItemStatus.NONE != overrides.get(0).getItemStatus()) {
                    StandardCacheItem specificTranslation = overrides.get(0);
                    override.setSpecificItem(specificTranslation);
                } else if (isGeneralOnly && ItemStatus.NONE != overrides.get(0).getItemStatus()) {
                    StandardCacheItem generalTranslation = overrides.get(0);
                    override.setGeneralItem(generalTranslation);
                }
                if (!isSpecificOnly && !isGeneralOnly) {
                    if (ItemStatus.NONE != overrides.get(0).getItemStatus()) {
                        StandardCacheItem specificTranslation = overrides.get(0);
                        override.setSpecificItem(specificTranslation);
                    }
                    if (ItemStatus.NONE != overrides.get(1).getItemStatus()) {
                        StandardCacheItem generalTranslation = overrides.get(1);
                        override.setGeneralItem(generalTranslation);
                    }
                }
            }
        }
        return override;
    }

    @Override
    public LocalePair getLocaleBasedTemplateValue(String templateCacheKey, String property, TranslatedEntity entityType,
                                                  String entityId, String localeCode, String localeCountryCode,
                                                  String specificPropertyKey, String generalPropertyKey) {
        LocalePair override = null;
        if (preCachedSparseOverrideService.isActiveForType(Translation.class.getName())) {
            Site currentSite = BroadleafRequestContext.getBroadleafRequestContext().getNonPersistentSite();
            boolean isIsolatedActive = false;
            if (currentSite != null) {
                isIsolatedActive = preCachedSparseOverrideService.isActiveIsolatedSiteForType(currentSite.getId(), TranslationImpl.class.getName());
            }
            if (isIsolatedActive || templateEnabled) {
                override = new LocalePair();
                List<Translation> templateVals;
                if (!isIsolatedActive) {
                    templateVals = getTemplateTranslations(entityType, entityId, property, localeCode);
                } else {
                    //We need to include the standard site catalog in the query, so don't try to use an optimized template query
                    templateVals = new ArrayList<Translation>();
                    Translation translation = dao.readTranslation(entityType, entityId, property, localeCode, localeCountryCode, ResultType.CATALOG_ONLY);
                    if (translation != null) {
                        templateVals.add(translation);
                    }
                }
                List<String> codesToMatch = new ArrayList<String>();
                if (specificPropertyKey.endsWith(localeCountryCode) && generalPropertyKey.endsWith(localeCountryCode)) {
                    codesToMatch.add(localeCountryCode);
                } else if (specificPropertyKey.endsWith(localeCode) && generalPropertyKey.endsWith(localeCode)) {
                    codesToMatch.add(localeCode);
                } else {
                    codesToMatch.add(localeCountryCode);
                    codesToMatch.add(localeCode);
                }
                for (String code : codesToMatch) {
                    for (Translation templateVal : templateVals) {
                        if (templateVal.getLocaleCode().equals(code)) {
                            StandardCacheItem cacheItem = new StandardCacheItem();
                            cacheItem.setItemStatus(ItemStatus.NORMAL);
                            cacheItem.setCacheItem(templateVal);
                            override.setSpecificItem(cacheItem);
                            break;
                        }
                    }
                    if (override.getSpecificItem() != null) {
                        break;
                    }
                }
            }
        }
        return override;
    }

    @Override
    public boolean isOverrideQualified(Class<?> type) {
        return Translation.class.isAssignableFrom(type);
    }

    @Override
    public StandardCacheItem initializeOverride(Object entity) {
        String key = getCacheKey((Translation) entity);
        String dto = ((Translation) entity).getTranslatedValue();
        StandardCacheItem cacheItem = new StandardCacheItem();
        ItemStatus status = ItemStatus.NORMAL;
        if (extensionManager != null) {
            ExtensionResultHolder<ItemStatus> response = new ExtensionResultHolder<ItemStatus>();
            ExtensionResultStatusType result = extensionManager.buildStatus(entity, response);
            if (ExtensionResultStatusType.NOT_HANDLED != result && response.getResult() != null) {
                status = response.getResult();
            }
        }
        cacheItem.setItemStatus(status);
        cacheItem.setKey(key);
        cacheItem.setCacheItem(dto);
        return cacheItem;
    }

    @Override
    public boolean validateTemplateProcessing(String standardCacheKey, String templateCacheKey) {
        return true;
    }

    @Override
    public int getOrder() {
        return PRECACHED_SPARSE_OVERRIDE_ORDER;
    }

    public boolean isTemplateEnabled() {
        return templateEnabled;
    }

    public void setTemplateEnabled(boolean templateEnabled) {
        this.templateEnabled = templateEnabled;
    }

    public boolean isRestrictAssociation() {
        return restrictAssociation;
    }

    public void setRestrictAssociation(boolean restrictAssociation) {
        this.restrictAssociation = restrictAssociation;
    }

    protected List<Translation> getTemplateTranslations(TranslatedEntity entityType, String entityId, String property, String localeCode) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Translation> criteria = builder.createQuery(Translation.class);
        Root<TranslationImpl> root = criteria.from(TranslationImpl.class);
        criteria.select(root);
        List<Predicate> restrictions = new ArrayList<Predicate>();
        restrictions.add(builder.equal(root.get("entityType"), entityType.getFriendlyType()));
        restrictions.add(builder.equal(root.get("entityId"), entityId));
        restrictions.add(builder.equal(root.get("fieldName"), property));
        restrictions.add(builder.like(root.get("localeCode").as(String.class),localeCode + "%"));
        try {
            Object testObject = null;
            if (restrictAssociation) {
                try {
                    Class<?> type = Class.forName(entityType.getType());
                    SessionFactory sessionFactory = ((CriteriaBuilderImpl) em.getCriteriaBuilder()).getEntityManagerFactory().getSessionFactory();
                    Class<?>[] entities = helper.getAllPolymorphicEntitiesFromCeiling(type, sessionFactory, true, true);
                    //This should already be in level 1 cache and this should not cause a hit to the database.
                    Map<String, Object> idMetadata = helper.getIdMetadata(entities[entities.length - 1], (HibernateEntityManager) em);
                    Type idType = (Type) idMetadata.get("type");
                    if (idType instanceof StringType) {
                        testObject = em.find(entities[entities.length - 1], entityId);
                    } else if (idType instanceof LongType) {
                        testObject = em.find(entities[entities.length - 1], Long.parseLong(entityId));
                    }
                } catch (ClassNotFoundException e) {
                    throw ExceptionHelper.refineException(e);
                }
            }
            if (extensionManager != null) {
                extensionManager.setup(TranslationImpl.class);
                extensionManager.refineParameterRetrieve(TranslationImpl.class, testObject, builder, criteria, root, restrictions);
            }
            criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));

            TypedQuery<Translation> query = em.createQuery(criteria);
            if (extensionManager != null) {
                extensionManager.refineQuery(TranslationImpl.class, testObject, query);
            }
            query.setHint(QueryHints.HINT_CACHEABLE, true);
            List response = query.getResultList();
            if (extensionManager != null) {
                extensionManager.filterResults(TranslationImpl.class, testObject, response);
            }
            return response;
        } finally {
            extensionManager.breakdown(TranslationImpl.class);
        }
    }

    protected String getCacheKey(Translation translation) {
        return getCacheKey(translation.getEntityType(), translation.getEntityId(), translation.getFieldName(), translation.getLocaleCode());
    }

    protected String getCacheKey(TranslatedEntity type, String entityId, String fieldName, String localeCode) {
        return StringUtils.join(new String[]{"translation", type.getType(), entityId, fieldName, localeCode},"-");
    }
}
