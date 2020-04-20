/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.dao;


import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelper;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelperImpl;
import org.broadleafcommerce.common.util.dao.HibernateMappingProvider;
import org.broadleafcommerce.openadmin.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.ClassTree;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.ForeignKey;
import org.broadleafcommerce.openadmin.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.dto.TabMetadata;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.FieldMetadataProvider;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromFieldTypeRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.LateStageAddMetadataRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.FieldNamePropertyValidator;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.hibernate.Criteria;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author jfischer
 */
@Component("blDynamicEntityDao")
@Scope("prototype")
public class DynamicEntityDaoImpl implements DynamicEntityDao, ApplicationContextAware {

    private static final Log LOG = LogFactory.getLog(DynamicEntityDaoImpl.class);

    protected static final Map<String, Map<String, FieldMetadata>> METADATA_CACHE = new LRUMap<>(1000);

    /**
     * Lifetime cache for the existence of DynamicEntityDaoImpl that just stores how many properties we have cached in METADATA_CACHE over the lifetime
     * of the application. This should survive evictions from METADATA_CACHE because it is for the purpose of diagnosing when we store different property
     * counts in METADATA_CACHE as a result of cache eviction
     */
    protected static final Map<String, Integer> METADATA_CACHE_SIZES = new HashMap<>();

    /*
     * This is the same as POLYMORPHIC_ENTITY_CACHE, except that it does not contain classes that are abstract or have been marked for exclusion
     * from polymorphism
     */

    protected EntityManager standardEntityManager;

    @Resource(name = "blMetadata")
    protected Metadata metadata;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name = "blFieldMetadataProviders")
    protected List<FieldMetadataProvider> fieldMetadataProviders = new ArrayList<>();

    @Resource(name = "blDefaultFieldMetadataProvider")
    protected FieldMetadataProvider defaultFieldMetadataProvider;

    @Resource(name = "blAppConfigurationMap")
    protected Map<String, String> propertyConfigurations = new HashMap<>();

    protected DynamicDaoHelper dynamicDaoHelper = new DynamicDaoHelperImpl();

    @Value("${cache.entity.dao.metadata.ttl}")
    protected int cacheEntityMetaDataTtl;

    /**
     * Whether or not we should use {@link #METADATA_CACHE_SIZES} in the normal runtime of the application
     */
    @Value("${validate.metadata.cache.sizes:false}")
    protected boolean validateMetadataCacheSizes;

    protected long lastCacheFlushTime = System.currentTimeMillis();

    protected ApplicationContext applicationContext;

    protected FieldManager fieldManager;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Criteria createCriteria(Class<?> entityClass) {
        return getStandardEntityManager().unwrap(Session.class).createCriteria(entityClass);
    }

    @Override
    public <T> T persist(T entity) {
        standardEntityManager.persist(entity);
        standardEntityManager.flush();
        return entity;
    }

    @Override
    public Object find(Class<?> entityClass, Object key) {
        return standardEntityManager.find(entityClass, key);
    }

    @Override
    public <T> T merge(T entity) {
        T response = standardEntityManager.merge(entity);
        standardEntityManager.flush();
        return response;
    }

    @Override
    public void flush() {
        standardEntityManager.flush();
    }

    @Override
    public void detach(Serializable entity) {
        standardEntityManager.detach(entity);
    }

    @Override
    public void refresh(Serializable entity) {
        standardEntityManager.refresh(entity);
    }

    @Override
    public Serializable retrieve(Class<?> entityClass, Object primaryKey) {
        return (Serializable) standardEntityManager.find(entityClass, primaryKey);
    }

    @Override
    public void remove(Serializable entity) {
        standardEntityManager.remove(entity);
        standardEntityManager.flush();
    }

    @Override
    public void clear() {
        standardEntityManager.clear();
    }

    @Override
    public PersistentClass getPersistentClass(String targetClassName) {
        return HibernateMappingProvider.getMapping(targetClassName);
    }

    @Override
    public boolean useCache() {
        if (cacheEntityMetaDataTtl < 0) {
            return true;
        }
        if (cacheEntityMetaDataTtl == 0) {
            return false;
        } else {
            if ((System.currentTimeMillis() - lastCacheFlushTime) > cacheEntityMetaDataTtl) {
                lastCacheFlushTime = System.currentTimeMillis();
                METADATA_CACHE.clear();
                DynamicDaoHelperImpl.POLYMORPHIC_ENTITY_CACHE.clear();
                DynamicDaoHelperImpl.POLYMORPHIC_ENTITY_CACHE_WO_EXCLUSIONS.clear();
                LOG.trace("Metadata cache evicted");
                return true; // cache is empty
            } else {
                return true;
            }
        }
    }

    @Override
    public Class<?>[] getAllPolymorphicEntitiesFromCeiling(Class<?> ceilingClass) {
        return getAllPolymorphicEntitiesFromCeiling(ceilingClass, true);
    }

    @Override
    public Class<?>[] getAllPolymorphicEntitiesFromCeiling(Class<?> ceilingClass, boolean includeUnqualifiedPolymorphicEntities) {
        return dynamicDaoHelper.getAllPolymorphicEntitiesFromCeiling(ceilingClass, includeUnqualifiedPolymorphicEntities, useCache());
    }

    @Override
    public Class<?>[] getUpDownInheritance(Class<?> testClass) {
        return dynamicDaoHelper.getUpDownInheritance(testClass, true, useCache());
    }

    @Override
    public Class<?> getImplClass(String className) {
        Class<?> clazz = null;
        try {
            clazz = entityConfiguration.lookupEntityClass(className);
        } catch (NoSuchBeanDefinitionException e) {
            //do nothing
        }
        if (clazz == null) {
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            clazz = DynamicDaoHelperImpl.getNonProxyImplementationClassIfNecessary(clazz);
        }
        return clazz;
    }

    @Override
    public Class<?> getCeilingImplClass(String className) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Class<?>[] entitiesFromCeiling = getAllPolymorphicEntitiesFromCeiling(clazz, true);
        if (entitiesFromCeiling == null || entitiesFromCeiling.length < 1) {
            clazz = DynamicDaoHelperImpl.getNonProxyImplementationClassIfNecessary(clazz);
            entitiesFromCeiling = getAllPolymorphicEntitiesFromCeiling(clazz, true);
        }
        if (entitiesFromCeiling == null || entitiesFromCeiling.length < 1) {
            throw new IllegalArgumentException(String.format("Unable to find ceiling implementation for the requested class name (%s)", className));
        }
        clazz = entitiesFromCeiling[entitiesFromCeiling.length - 1];
        return clazz;
    }

    @Override
    public List<Long> readOtherEntitiesWithPropertyValue(Serializable instance, String propertyName, String value) {
        Class clazz = DynamicDaoHelperImpl.getNonProxyImplementationClassIfNecessary(instance.getClass());
        CriteriaBuilder builder = this.standardEntityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root root = criteria.from(clazz);
        Path idField = root.get(this.getIdField(clazz).getName());
        criteria.select(idField.as(Long.class));
        List<Predicate> restrictions = new ArrayList();

        Path path = null;

        // Support property name such as "defaultSku.name"
        if (propertyName.contains(".")) {
            String[] split = propertyName.split("\\.");
            for (String splitResult : split) {
                if (path == null) {
                    path = root.get(splitResult);
                } else {
                    path = path.get(splitResult);
                }
            }
        } else {
            path = root.get(propertyName);
        }

        restrictions.add(builder.equal(path, value));
        Serializable identifier = this.getIdentifier(instance);
        //when we creating the new item identifier is not exists
        if (identifier != null) {
            restrictions.add(builder.notEqual(idField, identifier));
        }

        if (instance instanceof Status) {
            restrictions.add(builder.or(builder.isNull(root.get("archiveStatus").get("archived")), builder.equal(root.get("archiveStatus").get("archived"), 'N')));
        }

        criteria.where(restrictions.toArray(new Predicate[restrictions.size()]));
        return this.standardEntityManager.createQuery(criteria).getResultList();
    }

    @Override
    public Serializable getIdentifier(Object entity) {
        return dynamicDaoHelper.getIdentifier(entity);
    }

    protected Field getIdField(Class<?> clazz) {
        return dynamicDaoHelper.getIdField(clazz);
    }

    public Class<?>[] sortEntities(Class<?> ceilingClass, List<Class<?>> entities) {
        return dynamicDaoHelper.sortEntities(ceilingClass, entities);
    }

    protected void addClassToTree(Class<?> clazz, ClassTree tree) {
        Class<?> testClass;
        try {
            testClass = Class.forName(tree.getFullyQualifiedClassname());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (clazz.equals(testClass)) {
            return;
        }
        if (clazz.getSuperclass().equals(testClass)) {
            ClassTree myTree = new ClassTree(clazz.getName(), isExcludeClassFromPolymorphism(clazz));
            createClassTreeFromAnnotation(clazz, myTree);
            tree.setChildren(ArrayUtils.add(tree.getChildren(), myTree));
        } else {
            for (ClassTree child : tree.getChildren()) {
                addClassToTree(clazz, child);
            }
        }
    }

    protected void createClassTreeFromAnnotation(Class<?> clazz, ClassTree myTree) {
        AdminPresentationClass classPresentation = AnnotationUtils.findAnnotation(clazz, AdminPresentationClass.class);
        if (classPresentation != null) {
            String friendlyName = classPresentation.friendlyName();
            if (!StringUtils.isEmpty(friendlyName)) {
                myTree.setFriendlyName(friendlyName);
            }
        }
    }

    @Override
    public ClassTree getClassTree(Class<?>[] polymorphicClasses) {
        String ceilingClass = null;
        for (Class<?> clazz : polymorphicClasses) {
            AdminPresentationClass classPresentation = AnnotationUtils.findAnnotation(clazz, AdminPresentationClass.class);
            if (classPresentation != null) {
                String ceilingEntity = classPresentation.ceilingDisplayEntity();
                if (!StringUtils.isEmpty(ceilingEntity)) {
                    ceilingClass = ceilingEntity;
                    break;
                }
            }
        }
        if (ceilingClass != null) {
            int pos = -1;
            int j = 0;
            for (Class<?> clazz : polymorphicClasses) {
                if (clazz.getName().equals(ceilingClass)) {
                    pos = j;
                    break;
                }
                j++;
            }
            if (pos >= 0) {
                Class<?>[] temp = new Class<?>[pos + 1];
                System.arraycopy(polymorphicClasses, 0, temp, 0, j + 1);
                polymorphicClasses = temp;
            }
        }

        ClassTree classTree = null;
        if (!ArrayUtils.isEmpty(polymorphicClasses)) {
            Class<?> topClass = polymorphicClasses[polymorphicClasses.length - 1];
            classTree = new ClassTree(topClass.getName(), isExcludeClassFromPolymorphism(topClass));
            createClassTreeFromAnnotation(topClass, classTree);
            for (int j = polymorphicClasses.length - 1; j >= 0; j--) {
                addClassToTree(polymorphicClasses[j], classTree);
            }
            classTree.finalizeStructure(1);
        }
        return classTree;
    }

    @Override
    public ClassTree getClassTreeFromCeiling(Class<?> ceilingClass) {
        Class<?>[] sortedEntities = getAllPolymorphicEntitiesFromCeiling(ceilingClass);
        return getClassTree(sortedEntities);
    }

    @Override
    public Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective) {
        Class<?>[] entityClasses;
        try {
            entityClasses = getAllPolymorphicEntitiesFromCeiling(Class.forName(entityName));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (!ArrayUtils.isEmpty(entityClasses)) {
            return getMergedProperties(
                    entityName,
                    entityClasses,
                    (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY),
                    persistencePerspective.getAdditionalNonPersistentProperties(),
                    persistencePerspective.getAdditionalForeignKeys(),
                    MergedPropertyType.PRIMARY,
                    persistencePerspective.getPopulateToOneFields(),
                    persistencePerspective.getIncludeFields(),
                    persistencePerspective.getExcludeFields(),
                    persistencePerspective.getConfigurationKey(),
                    ""
            );
        } else {
            Map<String, FieldMetadata> mergedProperties = new HashMap<>();
            Class<?> targetClass;
            try {
                targetClass = Class.forName(entityName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Map<String, FieldMetadata> attributesMap = metadata.getFieldMetadataForTargetClass(null, targetClass, this, "");
            for (String property : attributesMap.keySet()) {
                FieldMetadata presentationAttribute = attributesMap.get(property);
                if (!presentationAttribute.getExcluded()) {
                    Field field = FieldManager.getSingleField(targetClass, property);
                    if (!Modifier.isStatic(field.getModifiers())) {
                        boolean handled = false;
                        for (FieldMetadataProvider provider : fieldMetadataProviders) {
                            MetadataProviderResponse response = provider.addMetadataFromFieldType(
                                    new AddMetadataFromFieldTypeRequest(field, targetClass, null, new ForeignKey[]{},
                                            MergedPropertyType.PRIMARY, null, null, "",
                                            property, null, false, 0, attributesMap, presentationAttribute,
                                            ((BasicFieldMetadata) presentationAttribute).getExplicitFieldType(), field.getType(), this),
                                    mergedProperties);
                            if (MetadataProviderResponse.NOT_HANDLED != response) {
                                handled = true;
                            }
                            if (MetadataProviderResponse.HANDLED_BREAK == response) {
                                break;
                            }
                        }
                        if (!handled) {
                            //this provider is not included in the provider list on purpose - it is designed to handle basic
                            //AdminPresentation fields, and those fields not admin presentation annotated at all
                            defaultFieldMetadataProvider.addMetadataFromFieldType(
                                    new AddMetadataFromFieldTypeRequest(field, targetClass, null, new ForeignKey[]{},
                                            MergedPropertyType.PRIMARY, null, null, "", property,
                                            null, false, 0, attributesMap, presentationAttribute, ((BasicFieldMetadata) presentationAttribute).getExplicitFieldType(),
                                            field.getType(), this), mergedProperties);
                        }
                    }
                }
            }

            return mergedProperties;
        }
    }

    @Override
    public Map<String, FieldMetadata> getMergedProperties(@Nonnull Class<?> cls) {
        Class<?>[] polymorphicTypes = getAllPolymorphicEntitiesFromCeiling(cls);
        return getMergedProperties(
                cls.getName(),
                polymorphicTypes,
                null,
                new String[]{},
                new ForeignKey[]{},
                MergedPropertyType.PRIMARY,
                true,
                new String[]{},
                new String[]{},
                null,
                ""
        );
    }

    @Override
    public Map<String, FieldMetadata> getMergedProperties(
            String ceilingEntityFullyQualifiedClassname,
            Class<?>[] entities,
            ForeignKey foreignField,
            String[] additionalNonPersistentProperties,
            ForeignKey[] additionalForeignFields,
            MergedPropertyType mergedPropertyType,
            Boolean populateManyToOneFields,
            String[] includeFields,
            String[] excludeFields,
            String configurationKey,
            String prefix) {
        Map<String, FieldMetadata> mergedProperties = getMergedPropertiesRecursively(
                ceilingEntityFullyQualifiedClassname,
                entities,
                foreignField,
                additionalNonPersistentProperties,
                additionalForeignFields,
                mergedPropertyType,
                populateManyToOneFields,
                includeFields,
                excludeFields,
                configurationKey,
                new ArrayList<Class<?>>(),
                prefix,
                false,
                "");

        final List<String> removeKeys = new ArrayList<>();

        for (final String key : mergedProperties.keySet()) {
            if (mergedProperties.get(key).getExcluded() != null && mergedProperties.get(key).getExcluded()) {
                removeKeys.add(key);
            }
        }

        for (String removeKey : removeKeys) {
            mergedProperties.remove(removeKey);
        }

        // Allow field metadata providers to contribute additional fields here. These latestage handlers take place
        // after any cached lookups occur, and are ideal for adding in dynamic properties that are not globally cacheable
        // like properties gleaned from reflection typically are.
        Set<String> keys = new HashSet<>(mergedProperties.keySet());
        for (Class<?> targetClass : entities) {
            for (String key : keys) {
                LateStageAddMetadataRequest amr = new LateStageAddMetadataRequest(key, null, targetClass, this, "");

                boolean foundOneOrMoreHandlers = false;
                for (FieldMetadataProvider fieldMetadataProvider : fieldMetadataProviders) {
                    MetadataProviderResponse response = fieldMetadataProvider.lateStageAddMetadata(amr, mergedProperties);
                    if (MetadataProviderResponse.NOT_HANDLED != response) {
                        foundOneOrMoreHandlers = true;
                    }
                    if (MetadataProviderResponse.HANDLED_BREAK == response) {
                        break;
                    }
                }
                if (!foundOneOrMoreHandlers) {
                    defaultFieldMetadataProvider.lateStageAddMetadata(amr, mergedProperties);
                }
            }
        }

        return mergedProperties;
    }

    protected Map<String, FieldMetadata> getMergedPropertiesRecursively(
            final String ceilingEntityFullyQualifiedClassname,
            final Class<?>[] entities,
            final ForeignKey foreignField,
            final String[] additionalNonPersistentProperties,
            final ForeignKey[] additionalForeignFields,
            final MergedPropertyType mergedPropertyType,
            final Boolean populateManyToOneFields,
            final String[] includeFields,
            final String[] excludeFields,
            final String configurationKey,
            final List<Class<?>> parentClasses,
            final String prefix,
            final Boolean isParentExcluded,
            final String parentPrefix) {
        PropertyBuilder propertyBuilder = new PropertyBuilder() {
            @Override
            public Map<String, FieldMetadata> execute(Boolean overridePopulateManyToOne) {
                Map<String, FieldMetadata> mergedProperties = new HashMap<>();
                Boolean classAnnotatedPopulateManyToOneFields;
                if (overridePopulateManyToOne != null) {
                    classAnnotatedPopulateManyToOneFields = overridePopulateManyToOne;
                } else {
                    classAnnotatedPopulateManyToOneFields = populateManyToOneFields;
                }

                buildPropertiesFromPolymorphicEntities(
                        entities,
                        foreignField,
                        additionalNonPersistentProperties,
                        additionalForeignFields,
                        mergedPropertyType,
                        classAnnotatedPopulateManyToOneFields,
                        includeFields,
                        excludeFields,
                        configurationKey,
                        ceilingEntityFullyQualifiedClassname,
                        mergedProperties,
                        parentClasses,
                        prefix,
                        isParentExcluded,
                        parentPrefix);

                return mergedProperties;
            }
        };

        Map<String, FieldMetadata> mergedProperties = metadata.overrideMetadata(entities, propertyBuilder, prefix, isParentExcluded, ceilingEntityFullyQualifiedClassname, configurationKey, this);
        applyIncludesAndExcludes(includeFields, excludeFields, prefix, isParentExcluded, mergedProperties);
        applyForeignKeyPrecedence(foreignField, additionalForeignFields, mergedProperties);

        return mergedProperties;
    }

    protected void applyForeignKeyPrecedence(ForeignKey foreignField, ForeignKey[] additionalForeignFields, Map<String, FieldMetadata> mergedProperties) {
        for (String key : mergedProperties.keySet()) {
            boolean isForeign = false;
            if (foreignField != null) {
                isForeign = foreignField.getManyToField().equals(key);
            }
            if (!isForeign && !ArrayUtils.isEmpty(additionalForeignFields)) {
                for (ForeignKey foreignKey : additionalForeignFields) {
                    isForeign = foreignKey.getManyToField().equals(key);
                    if (isForeign) {
                        break;
                    }
                }
            }
            if (isForeign) {
                FieldMetadata metadata = mergedProperties.get(key);
                metadata.setExcluded(false);
            }
        }
    }

    protected void applyIncludesAndExcludes(String[] includeFields, String[] excludeFields, String prefix, Boolean isParentExcluded, Map<String, FieldMetadata> mergedProperties) {
        //check includes
        if (!ArrayUtils.isEmpty(includeFields)) {
            for (String include : includeFields) {
                for (String key : mergedProperties.keySet()) {
                    String testKey = prefix + key;
                    if (!(testKey.startsWith(include + ".") || testKey.equals(include))) {
                        FieldMetadata metadata = mergedProperties.get(key);
                        LOG.debug("applyIncludesAndExcludes:Excluding " + key + " because this field did not appear in the explicit includeFields list");
                        metadata.setExcluded(true);
                    } else {
                        FieldMetadata metadata = mergedProperties.get(key);
                        if (!isParentExcluded) {
                            LOG.debug("applyIncludesAndExcludes:Showing " + key + " because this field appears in the explicit includeFields list");
                            metadata.setExcluded(false);
                        }
                    }
                }
            }
        } else if (!ArrayUtils.isEmpty(excludeFields)) {
            //check excludes
            for (String exclude : excludeFields) {
                for (String key : mergedProperties.keySet()) {
                    String testKey = prefix + key;
                    if (testKey.startsWith(exclude + ".") || testKey.equals(exclude)) {
                        FieldMetadata metadata = mergedProperties.get(key);
                        LOG.debug("applyIncludesAndExcludes:Excluding " + key + " because this field appears in the explicit excludeFields list");
                        metadata.setExcluded(true);
                    } else {
                        FieldMetadata metadata = mergedProperties.get(key);
                        if (!isParentExcluded) {
                            LOG.debug("applyIncludesAndExcludes:Showing " + key + " because this field did not appear in the explicit excludeFields list");
                            metadata.setExcluded(false);
                        }
                    }
                }
            }
        }
    }

    protected String pad(String s, int length, char pad) {
        StringBuilder buffer = new StringBuilder(s);
        while (buffer.length() < length) {
            buffer.insert(0, pad);
        }
        return buffer.toString();
    }

    protected String getCacheKey(String ceilingEntityFullyQualifiedClassname, ForeignKey foreignField, String[] additionalNonPersistentProperties, ForeignKey[] additionalForeignFields, MergedPropertyType mergedPropertyType, Boolean populateManyToOneFields, Class<?> clazz, String configurationKey, Boolean isParentExcluded) {
        StringBuilder sb = new StringBuilder(150);
        sb.append(ceilingEntityFullyQualifiedClassname);
        sb.append(clazz.hashCode());
        sb.append(foreignField == null ? "" : foreignField.toString());
        sb.append(configurationKey);
        sb.append(isParentExcluded);
        if (additionalNonPersistentProperties != null) {
            for (String prop : additionalNonPersistentProperties) {
                sb.append(prop);
            }
        }
        if (additionalForeignFields != null) {
            for (ForeignKey key : additionalForeignFields) {
                sb.append(key.toString());
            }
        }
        sb.append(mergedPropertyType);
        sb.append(populateManyToOneFields);

        String digest;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(sb.toString().getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            digest = number.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        String key = pad(digest, 32, '0');

        if (LOG.isDebugEnabled()) {
            LOG.debug("Created cache key: " + key + " from the following string: " + sb.toString());
        }
        return key;
    }

    protected void buildPropertiesFromPolymorphicEntities(
            Class<?>[] entities,
            ForeignKey foreignField,
            String[] additionalNonPersistentProperties,
            ForeignKey[] additionalForeignFields,
            MergedPropertyType mergedPropertyType,
            Boolean populateManyToOneFields,
            String[] includeFields,
            String[] excludeFields,
            String configurationKey,
            String ceilingEntityFullyQualifiedClassname,
            Map<String, FieldMetadata> mergedProperties,
            List<Class<?>> parentClasses,
            String prefix,
            Boolean isParentExcluded,
            String parentPrefix) {
        for (Class<?> clazz : entities) {
            String cacheKey = getCacheKey(ceilingEntityFullyQualifiedClassname, foreignField, additionalNonPersistentProperties, additionalForeignFields, mergedPropertyType, populateManyToOneFields, clazz, configurationKey, isParentExcluded);

            Map<String, FieldMetadata> cacheData = null;
            synchronized (DynamicDaoHelperImpl.LOCK_OBJECT) {
                if (useCache()) {
                    cacheData = METADATA_CACHE.get(cacheKey);
                }

                if (cacheData == null) {
                    Map<String, FieldMetadata> props = getPropertiesForEntityClass(
                            clazz,
                            foreignField,
                            additionalNonPersistentProperties,
                            additionalForeignFields,
                            mergedPropertyType,
                            populateManyToOneFields,
                            includeFields,
                            excludeFields,
                            configurationKey,
                            ceilingEntityFullyQualifiedClassname,
                            parentClasses,
                            prefix,
                            isParentExcluded,
                            parentPrefix);
                    //first check all the properties currently in there to see if my entity inherits from them
                    for (Class<?> clazz2 : entities) {
                        if (!clazz2.getName().equals(clazz.getName())) {
                            for (Map.Entry<String, FieldMetadata> entry : props.entrySet()) {
                                FieldMetadata metadata = entry.getValue();
                                try {
                                    if (Class.forName(metadata.getInheritedFromType()).isAssignableFrom(clazz2)) {
                                        String[] both = ArrayUtils.addAll(metadata.getAvailableToTypes(), new String[]{clazz2.getName()});
                                        metadata.setAvailableToTypes(both);
                                    }
                                } catch (ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                    METADATA_CACHE.put(cacheKey, props);

                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Added " + props.size() + " to the metadata cache with key " + cacheKey + " for the class " + ceilingEntityFullyQualifiedClassname);
                    }

                    if (validateMetadataCacheSizes) {
                        Integer previousSize = METADATA_CACHE_SIZES.get(cacheKey);
                        Integer currentSize = props.size();
                        if (previousSize == null) {
                            METADATA_CACHE_SIZES.put(cacheKey, currentSize);
                        } else if (!currentSize.equals(previousSize)) {
                            String msg = "Attempted to store " + currentSize + " properties in the cache for the key " + cacheKey + " but we had previously stored " + previousSize + " properties";
                            LOG.error(msg);
                            throw new RuntimeException(msg);
                        }
                    }

                    cacheData = props;
                } else {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Read " + cacheData.size() + " from the metada cache with key " + cacheKey + " for the class " + ceilingEntityFullyQualifiedClassname);
                    }
                }
            }
            //clone the metadata before passing to the system
            Map<String, FieldMetadata> clonedCache = new HashMap<>(cacheData.size());
            for (Map.Entry<String, FieldMetadata> entry : cacheData.entrySet()) {
                clonedCache.put(entry.getKey(), entry.getValue().cloneFieldMetadata());
            }
            mergedProperties.putAll(clonedCache);
        }
    }

    @Override
    public Field[] getAllFields(Class<?> targetClass) {
        Field[] allFields = new Field[]{};
        boolean eof = false;
        Class<?> currentClass = targetClass;
        while (!eof) {
            Field[] fields = currentClass.getDeclaredFields();
            allFields = ArrayUtils.addAll(allFields, fields);
            if (currentClass.getSuperclass() != null) {
                currentClass = currentClass.getSuperclass();
            } else {
                eof = true;
            }
        }

        return allFields;
    }

    @Override
    public Map<String, FieldMetadata> getPropertiesForPrimitiveClass(
            String propertyName,
            String friendlyPropertyName,
            Class<?> targetClass,
            Class<?> parentClass,
            MergedPropertyType mergedPropertyType
    ) {
        Map<String, FieldMetadata> fields = new HashMap<>();
        BasicFieldMetadata presentationAttribute = new BasicFieldMetadata();
        presentationAttribute.setFriendlyName(friendlyPropertyName);
        if (String.class.isAssignableFrom(targetClass)) {
            presentationAttribute.setExplicitFieldType(SupportedFieldType.STRING);
            presentationAttribute.setVisibility(VisibilityEnum.VISIBLE_ALL);
            fields.put(propertyName, metadata.getFieldMetadata("", propertyName, null, SupportedFieldType.STRING, null, parentClass, presentationAttribute, mergedPropertyType, this));
        } else if (Boolean.class.isAssignableFrom(targetClass)) {
            presentationAttribute.setExplicitFieldType(SupportedFieldType.BOOLEAN);
            presentationAttribute.setVisibility(VisibilityEnum.VISIBLE_ALL);
            fields.put(propertyName, metadata.getFieldMetadata("", propertyName, null, SupportedFieldType.BOOLEAN, null, parentClass, presentationAttribute, mergedPropertyType, this));
        } else if (Date.class.isAssignableFrom(targetClass)) {
            presentationAttribute.setExplicitFieldType(SupportedFieldType.DATE);
            presentationAttribute.setVisibility(VisibilityEnum.VISIBLE_ALL);
            fields.put(propertyName, metadata.getFieldMetadata("", propertyName, null, SupportedFieldType.DATE, null, parentClass, presentationAttribute, mergedPropertyType, this));
        } else if (Money.class.isAssignableFrom(targetClass)) {
            presentationAttribute.setExplicitFieldType(SupportedFieldType.MONEY);
            presentationAttribute.setVisibility(VisibilityEnum.VISIBLE_ALL);
            fields.put(propertyName, metadata.getFieldMetadata("", propertyName, null, SupportedFieldType.MONEY, null, parentClass, presentationAttribute, mergedPropertyType, this));
        } else if (
                Byte.class.isAssignableFrom(targetClass) ||
                        Integer.class.isAssignableFrom(targetClass) ||
                        Long.class.isAssignableFrom(targetClass) ||
                        Short.class.isAssignableFrom(targetClass)
                ) {
            presentationAttribute.setExplicitFieldType(SupportedFieldType.INTEGER);
            presentationAttribute.setVisibility(VisibilityEnum.VISIBLE_ALL);
            fields.put(propertyName, metadata.getFieldMetadata("", propertyName, null, SupportedFieldType.INTEGER, null, parentClass, presentationAttribute, mergedPropertyType, this));
        } else if (
                Double.class.isAssignableFrom(targetClass) ||
                        BigDecimal.class.isAssignableFrom(targetClass)
                ) {
            presentationAttribute.setExplicitFieldType(SupportedFieldType.DECIMAL);
            presentationAttribute.setVisibility(VisibilityEnum.VISIBLE_ALL);
            fields.put(propertyName, metadata.getFieldMetadata("", propertyName, null, SupportedFieldType.DECIMAL, null, parentClass, presentationAttribute, mergedPropertyType, this));
        }
        ((BasicFieldMetadata) fields.get(propertyName)).setLength(255);
        ((BasicFieldMetadata) fields.get(propertyName)).setForeignKeyCollection(false);
        ((BasicFieldMetadata) fields.get(propertyName)).setRequired(true);
        ((BasicFieldMetadata) fields.get(propertyName)).setUnique(true);
        ((BasicFieldMetadata) fields.get(propertyName)).setScale(100);
        ((BasicFieldMetadata) fields.get(propertyName)).setPrecision(100);

        return fields;
    }

    @Override
    public Map<String, Object> getIdMetadata(Class<?> entityClass) {
        return dynamicDaoHelper.getIdMetadata(entityClass, standardEntityManager);
    }

    @Override
    public List<String> getPropertyNames(Class<?> entityClass) {
        return dynamicDaoHelper.getPropertyNames(entityClass);
    }

    @Override
    public List<Type> getPropertyTypes(Class<?> entityClass) {
        return dynamicDaoHelper.getPropertyTypes(entityClass);
    }

    @Override
    public Map<String, TabMetadata> getTabAndGroupMetadata(Class<?>[] entities, ClassMetadata cmd) {
        Class<?>[] superClassEntities = getSuperClassHierarchy(entities[entities.length - 1]);

        Map<String, TabMetadata> mergedTabAndGroupMetadata = metadata.getBaseTabAndGroupMetadata(entities);
        metadata.applyTabAndGroupMetadataOverrides(superClassEntities, mergedTabAndGroupMetadata);
        metadata.buildAdditionalTabAndGroupMetadataFromCmdProperties(cmd, mergedTabAndGroupMetadata);

        return mergedTabAndGroupMetadata;
    }

    public Class<?>[] getSuperClassHierarchy(Class<?> ceilingEntity) {
        Class<?>[] entities = new Class<?>[]{};

        if (ceilingEntity != null) {
            entities = ArrayUtils.add(entities, ceilingEntity);
            while (!ceilingEntity.getSuperclass().equals(Object.class)) {
                entities = ArrayUtils.add(entities, ceilingEntity.getSuperclass());
                ceilingEntity = ceilingEntity.getSuperclass();
            }
        }
        return entities;
    }

    protected Map<String, FieldMetadata> getPropertiesForEntityClass(
            Class<?> targetClass,
            ForeignKey foreignField,
            String[] additionalNonPersistentProperties,
            ForeignKey[] additionalForeignFields,
            MergedPropertyType mergedPropertyType,
            Boolean populateManyToOneFields,
            String[] includeFields,
            String[] excludeFields,
            String configurationKey,
            String ceilingEntityFullyQualifiedClassname,
            List<Class<?>> parentClasses,
            String prefix,
            Boolean isParentExcluded,
            String parentPrefix) {
        Map<String, FieldMetadata> presentationAttributes = metadata.getFieldMetadataForTargetClass(null, targetClass, this, "");
        if (isParentExcluded) {
            for (String key : presentationAttributes.keySet()) {
                LOG.debug("getPropertiesForEntityClass:Excluding " + key + " because parent is excluded.");
                presentationAttributes.get(key).setExcluded(true);
            }
        }

        Map idMetadata = getIdMetadata(targetClass);
        Map<String, FieldMetadata> fields = new HashMap<>();
        String idProperty = (String) idMetadata.get("name");
        List<String> propertyNames = getPropertyNames(targetClass);
        propertyNames.add(idProperty);
        Type idType = (Type) idMetadata.get("type");
        List<Type> propertyTypes = getPropertyTypes(targetClass);
        propertyTypes.add(idType);

        PersistentClass persistentClass = getPersistentClass(targetClass.getName());
        Iterator testIter = persistentClass.getPropertyIterator();
        List<Property> propertyList = new ArrayList<>();

        //check the properties for problems
        while (testIter.hasNext()) {
            Property property = (Property) testIter.next();
            if (property.getName().contains(".")) {
                throw new IllegalArgumentException("Properties from entities that utilize a period character ('.') in their name are incompatible with this system. The property name in question is: (" + property.getName() + ") from the class: (" + targetClass.getName() + ")");
            }
            propertyList.add(property);
        }

        buildProperties(
                targetClass,
                foreignField,
                additionalForeignFields,
                additionalNonPersistentProperties,
                mergedPropertyType,
                presentationAttributes,
                propertyList,
                fields,
                propertyNames,
                propertyTypes,
                idProperty,
                populateManyToOneFields,
                includeFields,
                excludeFields,
                configurationKey,
                ceilingEntityFullyQualifiedClassname,
                parentClasses,
                prefix,
                isParentExcluded,
                false,
                parentPrefix
        );
        BasicFieldMetadata presentationAttribute = new BasicFieldMetadata();
        presentationAttribute.setExplicitFieldType(SupportedFieldType.STRING);
        presentationAttribute.setVisibility(VisibilityEnum.HIDDEN_ALL);
        if (!ArrayUtils.isEmpty(additionalNonPersistentProperties)) {
            Class<?>[] entities = getAllPolymorphicEntitiesFromCeiling(targetClass);
            for (String additionalNonPersistentProperty : additionalNonPersistentProperties) {
                if (StringUtils.isEmpty(prefix) || (!StringUtils.isEmpty(prefix) && additionalNonPersistentProperty.startsWith(prefix))) {
                    String myAdditionalNonPersistentProperty = additionalNonPersistentProperty;
                    //get final property if this is a dot delimited property
                    int finalDotPos = additionalNonPersistentProperty.lastIndexOf('.');
                    if (finalDotPos >= 0) {
                        myAdditionalNonPersistentProperty = myAdditionalNonPersistentProperty.substring(finalDotPos + 1, myAdditionalNonPersistentProperty.length());
                    }
                    //check all the polymorphic types on this target class to see if the end property exists
                    Field testField = null;
                    Method testMethod = null;
                    for (Class<?> clazz : entities) {
                        try {
                            testMethod = clazz.getMethod(myAdditionalNonPersistentProperty);
                            if (testMethod != null) {
                                break;
                            }
                        } catch (NoSuchMethodException e) {
                            //do nothing - method does not exist
                        }
                        testField = getFieldManager().getField(clazz, myAdditionalNonPersistentProperty);
                        if (testField != null) {
                            break;
                        }
                    }
                    //if the property exists, add it to the metadata for this class
                    if (testField != null || testMethod != null) {
                        fields.put(additionalNonPersistentProperty, metadata.getFieldMetadata(prefix, additionalNonPersistentProperty, propertyList, SupportedFieldType.STRING, null, targetClass, presentationAttribute, mergedPropertyType, this));
                    }
                }
            }
        }

        return fields;
    }

    protected void buildProperties(
            Class<?> targetClass,
            ForeignKey foreignField,
            ForeignKey[] additionalForeignFields,
            String[] additionalNonPersistentProperties,
            MergedPropertyType mergedPropertyType,
            Map<String, FieldMetadata> presentationAttributes,
            List<Property> componentProperties,
            Map<String, FieldMetadata> fields,
            List<String> propertyNames,
            List<Type> propertyTypes,
            String idProperty,
            Boolean populateManyToOneFields,
            String[] includeFields,
            String[] excludeFields,
            String configurationKey,
            String ceilingEntityFullyQualifiedClassname,
            List<Class<?>> parentClasses,
            String prefix,
            Boolean isParentExcluded,
            Boolean isComponentPrefix,
            String parentPrefix) {
        int j = 0;
        Comparator<String> propertyComparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                //check for property name equality and for map field properties
                if (o1.equals(o2) || o1.startsWith(o2 + FieldManager.MAPFIELDSEPARATOR) || o2.startsWith(o1 + FieldManager.MAPFIELDSEPARATOR)) {
                    return 0;
                }
                return o1.compareTo(o2);
            }
        };
        List<String> presentationKeyList = new ArrayList<>(presentationAttributes.keySet());
        Collections.sort(presentationKeyList);

        for (String propertyName : propertyNames) {
            final Type type = propertyTypes.get(j);
            boolean isPropertyForeignKey = testForeignProperty(foreignField, prefix, propertyName);
            int additionalForeignKeyIndexPosition = findAdditionalForeignKeyIndex(additionalForeignFields, prefix, propertyName);
            j++;
            Field myField = getFieldManager().getField(targetClass, propertyName);
            if (myField == null) {
                //try to get the field with the prefix - needed for advanced collections that appear in @Embedded classes
                myField = getFieldManager().getField(targetClass, prefix + propertyName);
            }
            if (
                    !type.isAnyType() && !type.isCollectionType() ||
                            isPropertyForeignKey ||
                            additionalForeignKeyIndexPosition >= 0 ||
                            Collections.binarySearch(presentationKeyList, propertyName, propertyComparator) >= 0
                    ) {
                if (myField != null) {
                    boolean handled = false;
                    for (FieldMetadataProvider provider : fieldMetadataProviders) {
                        FieldMetadata presentationAttribute = presentationAttributes.get(propertyName);
                        if (presentationAttribute != null) {
                            setExcludedBasedOnShowIfProperty(presentationAttribute);
                        }
                        MetadataProviderResponse response = provider.addMetadataFromFieldType(
                                new AddMetadataFromFieldTypeRequest(myField, targetClass, foreignField, additionalForeignFields,
                                        mergedPropertyType, componentProperties, idProperty, prefix,
                                        propertyName, type, isPropertyForeignKey, additionalForeignKeyIndexPosition,
                                        presentationAttributes, presentationAttribute, null, type.getReturnedClass(), this), fields);
                        if (MetadataProviderResponse.NOT_HANDLED != response) {
                            handled = true;
                        }
                        if (MetadataProviderResponse.HANDLED_BREAK == response) {
                            break;
                        }
                    }
                    if (!handled) {
                        buildBasicProperty(myField, targetClass, foreignField, additionalForeignFields,
                                additionalNonPersistentProperties, mergedPropertyType, presentationAttributes,
                                componentProperties, fields, idProperty, populateManyToOneFields, includeFields,
                                excludeFields, configurationKey, ceilingEntityFullyQualifiedClassname, parentClasses,
                                prefix, isParentExcluded, propertyName, type, isPropertyForeignKey, additionalForeignKeyIndexPosition, isComponentPrefix, parentPrefix);
                    }
                }
            }
        }
    }

    public Boolean testPropertyInclusion(FieldMetadata presentationAttribute) {
        setExcludedBasedOnShowIfProperty(presentationAttribute);

        return !(presentationAttribute != null && ((presentationAttribute.getExcluded() != null && presentationAttribute.getExcluded()) || (presentationAttribute.getChildrenExcluded() != null && presentationAttribute.getChildrenExcluded())));

    }

    protected void setExcludedBasedOnShowIfProperty(FieldMetadata fieldMetadata) {
        if (fieldMetadata != null
                && StringUtils.isNotEmpty(fieldMetadata.getShowIfProperty())
                && propertyConfigurations.get(fieldMetadata.getShowIfProperty()) != null
                && !Boolean.valueOf(propertyConfigurations.get(fieldMetadata.getShowIfProperty()))) {

            //do not include this in the display if it returns false.
            fieldMetadata.setExcluded(true);
        }
    }

    protected Boolean testPropertyRecursion(String prefix, List<Class<?>> parentClasses, String propertyName, Class<?> targetClass,
                                            String ceilingEntityFullyQualifiedClassname, Boolean isComponentPrefix, String parentPrefix) {

        Boolean standardRecursionDetected = testStandardPropertyRecursion(prefix, parentClasses, propertyName, targetClass,
                ceilingEntityFullyQualifiedClassname, isComponentPrefix);

        Boolean multiLevelEmbeddableRecursionDetected = testMultiLevelEmbeddableRecursion(prefix, isComponentPrefix,
                parentPrefix, propertyName);

        return standardRecursionDetected || multiLevelEmbeddableRecursionDetected;
    }

    protected Boolean testMultiLevelEmbeddableRecursion(String prefix, Boolean isComponentPrefix, String parentPrefix, String propertyName) {
        return isComponentPrefix && parentPrefix.contains("." + prefix + propertyName);
    }

    protected Boolean testStandardPropertyRecursion(String prefix, List<Class<?>> parentClasses, String
            propertyName, Class<?> targetClass, String ceilingEntityFullyQualifiedClassname, Boolean
                                                            isComponentPrefix) {
        Boolean response = false;
        //don't want to shun a self-referencing property in an @Embeddable
        boolean shouldTest = !StringUtils.isEmpty(prefix) && (!isComponentPrefix || prefix.split("\\.").length > 1);
        if (shouldTest) {
            Field testField = getFieldManager().getField(targetClass, propertyName);
            if (testField == null) {
                Class<?>[] entities;
                try {
                    entities = getAllPolymorphicEntitiesFromCeiling(Class.forName(ceilingEntityFullyQualifiedClassname));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                for (Class<?> clazz : entities) {
                    testField = getFieldManager().getField(clazz, propertyName);
                    if (testField != null) {
                        break;
                    }
                }
                String testProperty = prefix + propertyName;
                if (testField == null) {
                    testField = getFieldManager().getField(targetClass, testProperty);
                }
                if (testField == null) {
                    for (Class<?> clazz : entities) {
                        testField = getFieldManager().getField(clazz, testProperty);
                        if (testField != null) {
                            break;
                        }
                    }
                }
            }
            response = determineExclusionForField(parentClasses, targetClass, testField);
        }
        return response;
    }

    protected Boolean determineExclusionForField(List<Class<?>> parentClasses, Class<?> targetClass, Field testField) {
        Boolean response = false;
        if (testField != null) {
            Class<?> testType = testField.getType();
            for (Class<?> parentClass : parentClasses) {
                if (parentClass.isAssignableFrom(testType) || testType.isAssignableFrom(parentClass)) {
                    response = true;
                    break;
                }
            }
            if (!response && (targetClass.isAssignableFrom(testType) || testType.isAssignableFrom(targetClass))) {
                response = true;
            }
        }
        return response;
    }

    protected void buildBasicProperty(
            Field field,
            Class<?> targetClass,
            ForeignKey foreignField,
            ForeignKey[] additionalForeignFields,
            String[] additionalNonPersistentProperties,
            MergedPropertyType mergedPropertyType,
            Map<String, FieldMetadata> presentationAttributes,
            List<Property> componentProperties,
            Map<String, FieldMetadata> fields,
            String idProperty,
            Boolean populateManyToOneFields,
            String[] includeFields,
            String[] excludeFields,
            String configurationKey,
            String ceilingEntityFullyQualifiedClassname,
            List<Class<?>> parentClasses,
            String prefix,
            Boolean isParentExcluded,
            String propertyName,
            Type type,
            boolean propertyForeignKey,
            int additionalForeignKeyIndexPosition,
            Boolean isComponentPrefix,
            String parentPrefix) {
        FieldMetadata presentationAttribute = presentationAttributes.get(propertyName);
        Boolean amIExcluded = isParentExcluded || !testPropertyInclusion(presentationAttribute);
        Boolean includeField = !testPropertyRecursion(prefix, parentClasses, propertyName, targetClass,
                ceilingEntityFullyQualifiedClassname, isComponentPrefix, parentPrefix);

        SupportedFieldType explicitType = null;
        if (presentationAttribute != null && presentationAttribute instanceof BasicFieldMetadata) {
            explicitType = ((BasicFieldMetadata) presentationAttribute).getExplicitFieldType();
        }
        Class<?> returnedClass = type.getReturnedClass();
        checkProp:
        {
            if (type.isComponentType() && includeField) {
                buildComponentProperties(
                        targetClass,
                        foreignField,
                        additionalForeignFields,
                        additionalNonPersistentProperties,
                        mergedPropertyType,
                        fields,
                        idProperty,
                        populateManyToOneFields,
                        includeFields,
                        excludeFields,
                        configurationKey,
                        ceilingEntityFullyQualifiedClassname,
                        propertyName,
                        type,
                        returnedClass,
                        parentClasses,
                        amIExcluded,
                        prefix,
                        parentPrefix);
                break checkProp;
            }
            /*
             * Currently we do not support ManyToOne fields whose class type is the same
             * as the target type, since this forms an infinite loop and will cause a stack overflow.
             */
            if (
                    type.isEntityType() &&
                            !returnedClass.isAssignableFrom(targetClass) &&
                            populateManyToOneFields &&
                            includeField
                    ) {
                buildEntityProperties(
                        fields,
                        foreignField,
                        additionalForeignFields,
                        additionalNonPersistentProperties,
                        populateManyToOneFields,
                        includeFields,
                        excludeFields,
                        configurationKey,
                        ceilingEntityFullyQualifiedClassname,
                        propertyName,
                        returnedClass,
                        targetClass,
                        parentClasses,
                        prefix,
                        amIExcluded,
                        parentPrefix
                );
                break checkProp;
            }
        }
        //Don't include this property if it failed manyToOne inclusion and is not a specified foreign key
        if (includeField || propertyForeignKey || additionalForeignKeyIndexPosition >= 0) {
            defaultFieldMetadataProvider.addMetadataFromFieldType(
                    new AddMetadataFromFieldTypeRequest(field, targetClass, foreignField, additionalForeignFields,
                            mergedPropertyType, componentProperties, idProperty, prefix, propertyName, type,
                            propertyForeignKey, additionalForeignKeyIndexPosition, presentationAttributes,
                            presentationAttribute, explicitType, returnedClass, this), fields);
        }
    }

    protected boolean testForeignProperty(ForeignKey foreignField, String prefix, String propertyName) {
        boolean isPropertyForeignKey = false;
        if (foreignField != null) {
            isPropertyForeignKey = foreignField.getManyToField().equals(prefix + propertyName);
        }
        return isPropertyForeignKey;
    }

    protected int findAdditionalForeignKeyIndex(ForeignKey[] additionalForeignFields, String prefix, String propertyName) {
        int additionalForeignKeyIndexPosition = -1;
        if (additionalForeignFields != null) {
            additionalForeignKeyIndexPosition = Arrays.binarySearch(additionalForeignFields, new ForeignKey(prefix + propertyName, null, null), new Comparator<ForeignKey>() {
                @Override
                public int compare(ForeignKey o1, ForeignKey o2) {
                    return o1.getManyToField().compareTo(o2.getManyToField());
                }
            });
        }
        return additionalForeignKeyIndexPosition;
    }

    protected void buildEntityProperties(
            Map<String, FieldMetadata> fields,
            ForeignKey foreignField,
            ForeignKey[] additionalForeignFields,
            String[] additionalNonPersistentProperties,
            Boolean populateManyToOneFields,
            String[] includeFields,
            String[] excludeFields,
            String configurationKey,
            String ceilingEntityFullyQualifiedClassname,
            String propertyName,
            Class<?> returnedClass,
            Class<?> targetClass,
            List<Class<?>> parentClasses,
            String prefix,
            Boolean isParentExcluded,
            String parentPrefix) {
        Class<?>[] polymorphicEntities = getAllPolymorphicEntitiesFromCeiling(returnedClass);
        List<Class<?>> clonedParentClasses = new ArrayList<>();

        for (Class<?> parentClass : parentClasses) {
            clonedParentClasses.add(parentClass);
        }

        clonedParentClasses.add(targetClass);

        Map<String, FieldMetadata> newFields = getMergedPropertiesRecursively(
                ceilingEntityFullyQualifiedClassname,
                polymorphicEntities,
                foreignField,
                additionalNonPersistentProperties,
                additionalForeignFields,
                MergedPropertyType.PRIMARY,
                populateManyToOneFields,
                includeFields,
                excludeFields,
                configurationKey,
                clonedParentClasses,
                prefix + propertyName + '.',
                isParentExcluded,
                parentPrefix);

        final String targetClassName = targetClass.getName();

        for (FieldMetadata newMetadata : newFields.values()) {
            newMetadata.setInheritedFromType(targetClassName);
            newMetadata.setAvailableToTypes(new String[]{targetClassName});
        }

        Map<String, FieldMetadata> convertedFields = new HashMap<>(newFields.size());

        for (Map.Entry<String, FieldMetadata> newField : newFields.entrySet()) {
            final FieldMetadata fieldMetadata = newField.getValue();
            final String key = newField.getKey();

            convertedFields.put(propertyName + '.' + key, fieldMetadata);

            if (fieldMetadata instanceof BasicFieldMetadata) {
                for (Map.Entry<String, List<Map<String, String>>> validationConfigurations : ((BasicFieldMetadata) fieldMetadata).getValidationConfigurations().entrySet()) {
                    Class<?> validatorImpl = null;

                    try {
                        validatorImpl = Class.forName(validationConfigurations.getKey());
                    } catch (ClassNotFoundException e) {
                        Object bean = applicationContext.getBean(validationConfigurations.getKey());

                        if (bean != null) {
                            validatorImpl = bean.getClass();
                        }
                    }

                    if (validatorImpl != null && FieldNamePropertyValidator.class.isAssignableFrom(validatorImpl)) {
                        for (Map<String, String> configs : validationConfigurations.getValue()) {
                            for (Map.Entry<String, String> config : configs.entrySet()) {
                                final String value = config.getValue();

                                if (newFields.containsKey(value)) {
                                    config.setValue(propertyName + "." + value);
                                }
                            }
                        }
                    }
                }
            }

            if (isForeignKey(fieldMetadata)) {
                setOriginatingFieldForForeignKey(propertyName, key, fieldMetadata);
            }
        }

        fields.putAll(convertedFields);
    }

    protected boolean isForeignKey(FieldMetadata fieldMetadata) {
        return fieldMetadata instanceof BasicCollectionMetadata
                && !((BasicCollectionMetadata) fieldMetadata).getPersistencePerspective().getPersistencePerspectiveItems().isEmpty()
                && ((BasicCollectionMetadata) fieldMetadata).getPersistencePerspective().getPersistencePerspectiveItems().containsKey(PersistencePerspectiveItemType.FOREIGNKEY);
    }

    /*
     * There may be multiple pathways to this foreign key which may have come from a cached source.
     * Since ForeignKey contains an originating field concept that is occurrence specific, we need
     * to make sure it is set appropriately here.
     *
     * A known use case is vendorPortal.embeddableMultitenantSite.adminUsers and
     * owningSite.embeddableMultitenantSite.adminUsers.
     */
    protected void setOriginatingFieldForForeignKey(String propertyName, String key, FieldMetadata fieldMetadata) {
        ForeignKey foreignKey = (ForeignKey) ((BasicCollectionMetadata) fieldMetadata).getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
        foreignKey.setOriginatingField(propertyName + '.' + key);
    }

    protected void buildComponentProperties(
            Class<?> targetClass,
            ForeignKey foreignField,
            ForeignKey[] additionalForeignFields,
            String[] additionalNonPersistentProperties,
            MergedPropertyType mergedPropertyType,
            Map<String, FieldMetadata> fields,
            String idProperty,
            Boolean populateManyToOneFields,
            String[] includeFields,
            String[] excludeFields,
            String configurationKey,
            String ceilingEntityFullyQualifiedClassname,
            String propertyName,
            Type type,
            Class<?> returnedClass,
            List<Class<?>> parentClasses,
            Boolean isParentExcluded,
            String prefix,
            String parentPrefix) {
        String[] componentProperties = ((ComponentType) type).getPropertyNames();
        List<String> componentPropertyNames = Arrays.asList(componentProperties);
        Type[] componentTypes = ((ComponentType) type).getSubtypes();
        List<Type> componentPropertyTypes = Arrays.asList(componentTypes);
        String tempPrefix = "";

        int pos = prefix.indexOf(".");
        final int prefixLength = prefix.length();

        if (pos > 0 && pos < prefixLength - 1) {
            //only use part of the prefix if it's more than one layer deep
            tempPrefix = prefix.substring(pos + 1, prefixLength);
        }

        Map<String, FieldMetadata> componentPresentationAttributes = metadata.getFieldMetadataForTargetClass(targetClass, returnedClass, this, tempPrefix + propertyName + ".");

        if (isParentExcluded) {
            for (String key : componentPresentationAttributes.keySet()) {
                LOG.debug("buildComponentProperties:Excluding " + key + " because the parent was excluded");
                componentPresentationAttributes.get(key).setExcluded(true);
            }
        }

        PersistentClass persistentClass = getPersistentClass(targetClass.getName());
        Property property;

        try {
            property = persistentClass.getProperty(propertyName);
        } catch (MappingException e) {
            property = persistentClass.getProperty(prefix + propertyName);
        }

        Iterator componentPropertyIterator = ((org.hibernate.mapping.Component) property.getValue()).getPropertyIterator();
        List<Property> componentPropertyList = new ArrayList<>();

        while (componentPropertyIterator.hasNext()) {
            componentPropertyList.add((Property) componentPropertyIterator.next());
        }

        Map<String, FieldMetadata> newFields = new HashMap<>();
        buildProperties(
                targetClass,
                foreignField,
                additionalForeignFields,
                additionalNonPersistentProperties,
                mergedPropertyType,
                componentPresentationAttributes,
                componentPropertyList,
                newFields,
                componentPropertyNames,
                componentPropertyTypes,
                idProperty,
                populateManyToOneFields,
                includeFields,
                excludeFields,
                configurationKey,
                ceilingEntityFullyQualifiedClassname,
                parentClasses,
                propertyName + ".",
                isParentExcluded,
                true,
                parentPrefix + prefix
        );

        Map<String, FieldMetadata> convertedFields = new HashMap<>();

        for (String key : newFields.keySet()) {
            final FieldMetadata fieldMetadata = newFields.get(key);
            convertedFields.put(propertyName + "." + key, fieldMetadata);

            if (isForeignKey(fieldMetadata)) {
                setOriginatingFieldForForeignKey(propertyName, key, fieldMetadata);
            }
        }
        fields.putAll(convertedFields);
    }

    @Override
    public EntityManager getStandardEntityManager() {
        return standardEntityManager;
    }

    @Override
    public void setStandardEntityManager(EntityManager entityManager) {
        this.standardEntityManager = entityManager;
        fieldManager = new FieldManager(entityConfiguration, entityManager);
    }

    @Override
    public FieldManager getFieldManager() {
        return this.getFieldManager(true);
    }

    @Override
    public FieldManager getFieldManager(boolean cleanFieldManager) {
        if (fieldManager == null) {
            //keep in mind that getStandardEntityManager() can return null, this is in general OK,
            // we re-init fieldManager in setStandardEntityManager method
            fieldManager = new FieldManager(entityConfiguration, getStandardEntityManager());
        } else if (cleanFieldManager){
            fieldManager.clearMiddleFields();
        }
        return fieldManager;
    }

    @Override
    public EntityConfiguration getEntityConfiguration() {
        return entityConfiguration;
    }

    @Override
    public void setEntityConfiguration(EntityConfiguration entityConfiguration) {
        this.entityConfiguration = entityConfiguration;
    }

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<FieldMetadataProvider> getFieldMetadataProviders() {
        return fieldMetadataProviders;
    }

    public void setFieldMetadataProviders(List<FieldMetadataProvider> fieldMetadataProviders) {
        this.fieldMetadataProviders = fieldMetadataProviders;
    }

    @Override
    public FieldMetadataProvider getDefaultFieldMetadataProvider() {
        return defaultFieldMetadataProvider;
    }

    public void setDefaultFieldMetadataProvider(FieldMetadataProvider defaultFieldMetadataProvider) {
        this.defaultFieldMetadataProvider = defaultFieldMetadataProvider;
    }

    protected boolean isExcludeClassFromPolymorphism(Class<?> clazz) {
        return dynamicDaoHelper.isExcludeClassFromPolymorphism(clazz);
    }

    @Override
    public DynamicDaoHelper getDynamicDaoHelper() {
        return dynamicDaoHelper;
    }

    public void setDynamicDaoHelper(DynamicDaoHelper dynamicDaoHelper) {
        this.dynamicDaoHelper = dynamicDaoHelper;
    }

}
