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
import org.broadleafcommerce.common.extension.StandardCacheItem;
import org.broadleafcommerce.common.extension.TemplateOnlyQueryExtensionManager;
import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.broadleafcommerce.common.i18n.domain.Translation;
import org.broadleafcommerce.common.i18n.domain.TranslationImpl;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelper;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelperImpl;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.QueryHints;
import org.hibernate.ejb.criteria.CriteriaBuilderImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
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
     */
    @Value("${precached.sparse.override.translation.template.enabled:true}")
    protected boolean templateEnabled = true;

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
        if (preCachedSparseOverrideService.isActiveForType(Translation.class.getName()) && templateEnabled) {
            override = new LocalePair();
            List<Translation> templateVals = getTemplateTranslations(entityType, entityId, property, localeCode);
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
    public int getOrder() {
        return PRECACHED_SPARSE_OVERRIDE_ORDER;
    }

    public boolean isTemplateEnabled() {
        return templateEnabled;
    }

    public void setTemplateEnabled(boolean templateEnabled) {
        this.templateEnabled = templateEnabled;
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
            Object testObject;
            try {
                SessionFactory sessionFactory = ((CriteriaBuilderImpl) em.getCriteriaBuilder()).getEntityManagerFactory().getSessionFactory();
                Class<?>[] entities = helper.getAllPolymorphicEntitiesFromCeiling(Class.forName(entityType.getType()), sessionFactory, true, true);
                //This should already be in level 1 cache and this should not cause a hit to the database.
                testObject = em.find(entities[entities.length-1], Long.parseLong(entityId));
            } catch (ClassNotFoundException e) {
                throw ExceptionHelper.refineException(e);
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
            return query.getResultList();
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
