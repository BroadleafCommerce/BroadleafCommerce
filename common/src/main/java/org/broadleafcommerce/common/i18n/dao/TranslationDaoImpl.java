/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.i18n.dao;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ResultType;
import org.broadleafcommerce.common.extension.StandardCacheItem;
import org.broadleafcommerce.common.i18n.domain.TranslatedEntity;
import org.broadleafcommerce.common.i18n.domain.Translation;
import org.broadleafcommerce.common.i18n.domain.TranslationImpl;
import org.broadleafcommerce.common.i18n.service.TranslationServiceExtensionManager;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelper;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelperImpl;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.ejb.QueryHints;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;

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

@Repository("blTranslationDao")
public class TranslationDaoImpl implements TranslationDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name="blTranslationServiceExtensionManager")
    protected TranslationServiceExtensionManager extensionManager;

    @Resource(name="blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;

    protected DynamicDaoHelper dynamicDaoHelper = new DynamicDaoHelperImpl();
    
    @Override
    public Translation save(Translation translation) {
        return em.merge(translation);
    }
    
    @Override
    public Translation create() {
        return (Translation) entityConfiguration.createEntityInstance(Translation.class.getName());
    }
    
    @Override
    public void delete(Translation translation) {
        em.remove(translation);
    }
    
    @Override
    public Map<String, Object> getIdPropertyMetadata(TranslatedEntity entity) {
        Class<?> implClass = entityConfiguration.lookupEntityClass(entity.getType());
        return dynamicDaoHelper.getIdMetadata(implClass, (HibernateEntityManager) em);
    }

    @Override
    public Class<?> getEntityImpl(TranslatedEntity entity) {
        return entityConfiguration.lookupEntityClass(entity.getType());
    }
    
    @Override
    public Translation readTranslationById(Long translationId) {
        return em.find(TranslationImpl.class, translationId);
    }
    
    @Override
    public List<Translation> readTranslations(TranslatedEntity entity, String entityId, String fieldName) {
        entityId = getUpdatedEntityId(entity, entityId);

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Translation> criteria = builder.createQuery(Translation.class);
        Root<TranslationImpl> translation = criteria.from(TranslationImpl.class);

        criteria.select(translation);
        criteria.where(builder.equal(translation.get("entityType"), entity.getFriendlyType()),
            builder.equal(translation.get("entityId"), entityId),
            builder.equal(translation.get("fieldName"), fieldName)
        );

        TypedQuery<Translation> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        return query.getResultList();
    }

    @Override
    public Translation readTranslation(TranslatedEntity entity, String entityId, String fieldName, String localeCode) {
        entityId = getUpdatedEntityId(entity, entityId);

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Translation> criteria = builder.createQuery(Translation.class);
        Root<TranslationImpl> translation = criteria.from(TranslationImpl.class);

        criteria.select(translation);
        criteria.where(builder.equal(translation.get("entityType"), entity.getFriendlyType()),
            builder.equal(translation.get("entityId"), entityId),
            builder.equal(translation.get("fieldName"), fieldName),
            builder.equal(translation.get("localeCode"), localeCode)
        );
        TypedQuery<Translation> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        List<Translation> translations = query.getResultList();
        if (translations.size() > 1) {
            throw new IllegalStateException("Found multiple translations for: " + entity.getFriendlyType() + "|" + entityId + "|" + fieldName + "|" + localeCode);
        }
        if (!translations.isEmpty()) {
            return translations.get(0);
        }
        return null;
    }

    @Override
    public String getEntityId(TranslatedEntity entityType, Object entity) {
        Map<String, Object> idMetadata = getIdPropertyMetadata(entityType);
        String idProperty = (String) idMetadata.get("name");
        Type idType = (Type) idMetadata.get("type");

        if (!(idType instanceof LongType || idType instanceof StringType)) {
            throw new UnsupportedOperationException("Only ID types of String and Long are currently supported");
        }

        Object idValue;
        try {
            idValue = PropertyUtils.getProperty(entity, idProperty);
        } catch (Exception e) {
            throw new RuntimeException("Error reading id property", e);
        }

        if (idType instanceof StringType) {
            return (String) idValue;
        } else if (idType instanceof LongType) {
            return getUpdatedEntityId(entityType, (Long) idValue);
        }

        throw new IllegalArgumentException(String.format("Could not retrieve value for id property. Object: [%s], " +
                "ID Property: [%s], ID Type: [%s]", entity, idProperty, idType));
    }

    @Override
    public Long countTranslationEntries(TranslatedEntity entityType, ResultType stage) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<TranslationImpl> root = criteria.from(TranslationImpl.class);
        criteria.select(builder.count(root));
        List<Predicate> restrictions = new ArrayList<Predicate>();
        restrictions.add(builder.equal(root.get("entityType"), entityType.getFriendlyType()));
        try {
            if (extensionManager != null) {
                extensionManager.getProxy().setup(TranslationImpl.class, stage);
                extensionManager.getProxy().refineParameterRetrieve(TranslationImpl.class, stage, builder, criteria, root, restrictions);
            }
            criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));

            TypedQuery<Long> query = em.createQuery(criteria);
            if (extensionManager != null) {
                extensionManager.getProxy().refineQuery(TranslationImpl.class, stage, query);
            }
            query.setHint(QueryHints.HINT_CACHEABLE, true);
            return query.getSingleResult();
        } finally {
            if (extensionManager != null) {
                extensionManager.getProxy().breakdown(TranslationImpl.class, stage);
            }
        }
    }

    @Override
    public List<Translation> readAllTranslationEntries(TranslatedEntity entityType, ResultType stage) {
        return readAllTranslationEntries(entityType, stage, null);
    }
    
    public List<Translation> readAllTranslationEntries(TranslatedEntity entityType, ResultType stage, List<String> entityIds) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Translation> criteria = builder.createQuery(Translation.class);
        Root<TranslationImpl> root = criteria.from(TranslationImpl.class);
        criteria.select(root);
        List<Predicate> restrictions = new ArrayList<Predicate>();
        restrictions.add(builder.equal(root.get("entityType"), entityType.getFriendlyType()));
        if (CollectionUtils.isNotEmpty(entityIds)) {
            restrictions.add(root.get("entityId").in(entityIds));
        }
        try {
            if (extensionManager != null) {
                extensionManager.getProxy().setup(TranslationImpl.class, stage);
                extensionManager.getProxy().refineParameterRetrieve(TranslationImpl.class, stage, builder, criteria, root, restrictions);
            }
            criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));

            TypedQuery<Translation> query = em.createQuery(criteria);
            if (extensionManager != null) {
                extensionManager.getProxy().refineQuery(TranslationImpl.class, stage, query);
            }
            query.setHint(QueryHints.HINT_CACHEABLE, true);
            return query.getResultList();
        } finally {
            if (extensionManager != null) {
                extensionManager.getProxy().breakdown(TranslationImpl.class, stage);
            }
        }
    }

    @Override
    public List<StandardCacheItem> readConvertedTranslationEntries(TranslatedEntity entityType, ResultType stage) {
        List<Translation> results = readAllTranslationEntries(entityType, stage);
        if (extensionManager == null) {
            throw new IllegalStateException("extensionManager cannot be null");
        }
        ExtensionResultHolder<List<StandardCacheItem>> response = new ExtensionResultHolder<List<StandardCacheItem>>();
        extensionManager.getProxy().buildStatus(TranslationImpl.class, results, response);
        return response.getResult();
    }

    @Override
    public Translation readTranslation(TranslatedEntity entityType, String entityId, String fieldName, String localeCode, String localeCountryCode, ResultType stage) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Translation> criteria = builder.createQuery(Translation.class);
        Root<TranslationImpl> root = criteria.from(TranslationImpl.class);
        criteria.select(root);
        List<Predicate> restrictions = new ArrayList<Predicate>();
        restrictions.add(builder.equal(root.get("entityType"), entityType.getFriendlyType()));
        restrictions.add(builder.equal(root.get("entityId"), entityId));
        restrictions.add(builder.equal(root.get("fieldName"), fieldName));
        restrictions.add(builder.like(root.get("localeCode").as(String.class), localeCode + "%"));
        try {
            if (extensionManager != null) {
                extensionManager.getProxy().setup(TranslationImpl.class, stage);
                extensionManager.getProxy().refineParameterRetrieve(TranslationImpl.class, stage, builder, criteria, root, restrictions);
            }
            criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));

            TypedQuery<Translation> query = em.createQuery(criteria);
            if (extensionManager != null) {
                extensionManager.getProxy().refineQuery(TranslationImpl.class, stage, query);
            }
            query.setHint(QueryHints.HINT_CACHEABLE, true);
            List<Translation> translations = query.getResultList();

            if (!translations.isEmpty()) {
                if (!localeCode.equals(localeCountryCode)) {
                    return findBestTranslation(localeCountryCode, translations);
                } else {
                    return findSpecificTranslation(localeCountryCode, translations);
                }
            } else {
                return null;
            }
        } finally {
            if (extensionManager != null) {
                extensionManager.getProxy().breakdown(TranslationImpl.class, stage);
            }
        }
    }

    protected String getUpdatedEntityId(TranslatedEntity entityType, String entityId) {
        return getUpdatedEntityId(entityType, Long.parseLong(entityId));
    }

    protected String getUpdatedEntityId(TranslatedEntity entityType, Long idValue) {
        SandBoxHelper.OriginalIdResponse originalIdResponse = sandBoxHelper.getOriginalId(getEntityImpl(entityType), idValue);
        if (originalIdResponse.isRecordFound() && originalIdResponse.getOriginalId() != null) {
            idValue = originalIdResponse.getOriginalId();
            originalIdResponse = sandBoxHelper.getProductionOriginalId(getEntityImpl(entityType), idValue);
            //We may have a standard site production id - we want the template site original id
            if (originalIdResponse.isRecordFound() && !originalIdResponse.getOriginalId().equals(idValue)) {
                idValue = originalIdResponse.getOriginalId();
            }
        }
        return String.valueOf(idValue);
    }

    protected Translation findBestTranslation(String specificLocale, List<Translation> translations) {
        for (Translation translation : translations) {
            if (translation.getLocaleCode().equals(specificLocale)) {
                return translation;
            }
        }
        return translations.get(0);
    }

    protected Translation findSpecificTranslation(String localeCountryCode, List<Translation> translations) {
        for (Translation translation : translations) {
            if (translation.getLocaleCode().equals(localeCountryCode)) {
                return translation;
            }
        }
        return null;
    }

    public DynamicDaoHelper getDynamicDaoHelper() {
        return dynamicDaoHelper;
    }

    public void setDynamicDaoHelper(DynamicDaoHelper dynamicDaoHelper) {
        this.dynamicDaoHelper = dynamicDaoHelper;
    }

}
