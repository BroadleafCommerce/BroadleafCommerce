/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.dao;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationAdornedTargetCollection;
import org.broadleafcommerce.common.presentation.override.AdminPresentationAdornedTargetCollectionOverride;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.override.AdminPresentationCollectionOverride;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.AdminPresentationMapKey;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMapOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverrides;
import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.UnspecifiedBooleanType;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.ForeignKeyRestrictionType;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.client.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ClassTree;
import org.broadleafcommerce.openadmin.client.dto.CollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MapMetadata;
import org.broadleafcommerce.openadmin.client.dto.MapStructure;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.SimpleValueMapStructure;
import org.broadleafcommerce.openadmin.client.dto.override.AdornedTargetCollectionMetadataOverride;
import org.broadleafcommerce.openadmin.client.dto.override.BasicCollectionMetadataOverride;
import org.broadleafcommerce.openadmin.client.dto.override.BasicFieldMetadataOverride;
import org.broadleafcommerce.openadmin.client.dto.override.MapMetadataOverride;
import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitorAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * @author jfischer
 *
 */
@Component("blDynamicEntityDao")
@Scope("prototype")
public class DynamicEntityDaoImpl extends BaseHibernateCriteriaDao<Serializable> implements DynamicEntityDao {
	
	private static final Log LOG = LogFactory.getLog(DynamicEntityDaoImpl.class);
    protected static final Object LOCK_OBJECT = new Object();
    protected static final Map METADATA_CACHE = new LRUMap(1000);
    protected static final Map POLYMORPHIC_ENTITY_CACHE = new LRUMap(1000);
	
    protected EntityManager standardEntityManager;

    @Resource(name="blEJB3ConfigurationDao")
    protected EJB3ConfigurationDao ejb3ConfigurationDao;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Resource(name="blMetadataOverrides")
    protected Map<String, Map<String, Map<String, BasicFieldMetadataOverride>>> fieldMetadataOverrides;

    @Resource(name="blCollectionMetadataOverrides")
    protected Map<String, Map<String, Map<String, BasicCollectionMetadataOverride>>> collectionMetadataOverrides;

    @Resource(name="blAdornedTargetCollectionMetadataOverrides")
    protected Map<String, Map<String, Map<String, AdornedTargetCollectionMetadataOverride>>> adornedTargetCollectionMetadataOverrides;

    @Resource(name="blMapMetadataOverrides")
    protected Map<String, Map<String, Map<String, MapMetadataOverride>>> mapMetadataOverrides;

	@Override
	public Class<? extends Serializable> getEntityClass() {
		throw new RuntimeException("Must supply the entity class to query and count method calls! Default entity not supported!");
	}
	
	public Serializable persist(Serializable entity) {
		standardEntityManager.persist(entity);
		standardEntityManager.flush();
		return entity;
	}
	
	public Serializable merge(Serializable entity) {
		Serializable response = standardEntityManager.merge(entity);
		standardEntityManager.flush();
		return response;
	}
	
	public void flush() {
		standardEntityManager.flush();
	}
	
	public void detach(Serializable entity) {
		standardEntityManager.detach(entity);
	}
	
	public void refresh(Serializable entity) {
		standardEntityManager.refresh(entity);
	}
 	
	public Serializable retrieve(Class<?> entityClass, Object primaryKey) {
		return (Serializable) standardEntityManager.find(entityClass, primaryKey);
	}
	
	public void remove(Serializable entity) {
        boolean isArchivable = Status.class.isAssignableFrom(entity.getClass());
        if (isArchivable) {
            ((Status) entity).setArchived('Y');
            merge(entity);
        } else {
            standardEntityManager.remove(entity);
            standardEntityManager.flush();
        }
	}
	
	public void clear() {
		standardEntityManager.clear();
	}

    public PersistentClass getPersistentClass(String targetClassName) {
		return ejb3ConfigurationDao.getConfiguration().getClassMapping(targetClassName);
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao#getAllPolymorphicEntitiesFromCeiling(java.lang.Class)
	 */
	public Class<?>[] getAllPolymorphicEntitiesFromCeiling(Class<?> ceilingClass) {
        Class<?>[] cache;
        synchronized(LOCK_OBJECT) {
            cache = (Class<?>[]) POLYMORPHIC_ENTITY_CACHE.get(ceilingClass);
            if (cache == null) {
                List<Class<?>> entities = new ArrayList<Class<?>>();
                for (Object item : getSessionFactory().getAllClassMetadata().values()) {
                    ClassMetadata metadata = (ClassMetadata) item;
                    Class<?> mappedClass = metadata.getMappedClass(EntityMode.POJO);
                    if (mappedClass != null && ceilingClass.isAssignableFrom(mappedClass)) {
                        entities.add(mappedClass);
                    }
                }
                /*
                 * Sort entities with the most derived appearing first
                 */
                Class<?>[] sortedEntities = new Class<?>[entities.size()];
                List<Class<?>> stageItems = new ArrayList<Class<?>>();
                stageItems.add(ceilingClass);
                int j = 0;
                while (j < sortedEntities.length) {
                    List<Class<?>> newStageItems = new ArrayList<Class<?>>();
                    boolean topLevelClassFound = false;
                    for (Class<?> stageItem : stageItems) {
                        Iterator<Class<?>> itr = entities.iterator();
                        while(itr.hasNext()) {
                            Class<?> entity = itr.next();
                            checkitem: {
                                if (ArrayUtils.contains(entity.getInterfaces(), stageItem) || entity.equals(stageItem)) {
                                    topLevelClassFound = true;
                                    break checkitem;
                                }
                                
                                if (topLevelClassFound) {
                                    continue;
                                }
                                
                                if (entity.getSuperclass().equals(stageItem)) {
                                    break checkitem;
                                }
                                
                                continue;
                            }
                            sortedEntities[j] = entity;
                            itr.remove();
                            j++;
                            newStageItems.add(entity);
                        }
                    }
                    if (newStageItems.isEmpty()) {
                        throw new RuntimeException("There was a gap in the inheritance hierarchy for (" + ceilingClass.getName() + ")");
                    }
                    stageItems = newStageItems;
                }
                ArrayUtils.reverse(sortedEntities);
                cache = sortedEntities;
                POLYMORPHIC_ENTITY_CACHE.put(ceilingClass, sortedEntities);
            }
        }

        return cache;
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
            ClassTree myTree = new ClassTree(clazz.getName());
            createClassTreeFromAnnotation(clazz, myTree);
            tree.setChildren((ClassTree[]) ArrayUtils.add(tree.getChildren(), myTree));
        } else {
            for (ClassTree child : tree.getChildren()) {
                addClassToTree(clazz, child);
            }
        }
    }

    protected void createClassTreeFromAnnotation(Class<?> clazz, ClassTree myTree) {
        AdminPresentationClass classPresentation = clazz.getAnnotation(AdminPresentationClass.class);
        if (classPresentation != null) {
            String friendlyName = classPresentation.friendlyName();
            if (!StringUtils.isEmpty(friendlyName)) {
                myTree.setFriendlyName(friendlyName);
            }
        }
    }

    public ClassTree getClassTree(Class<?>[] polymorphicClasses) {
        String ceilingClass = null;
        for (Class<?> clazz : polymorphicClasses) {
            AdminPresentationClass classPresentation = clazz.getAnnotation(AdminPresentationClass.class);
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
            Class<?> topClass = polymorphicClasses[polymorphicClasses.length-1];
            classTree = new ClassTree(topClass.getName());
            createClassTreeFromAnnotation(topClass, classTree);
            for (int j=polymorphicClasses.length-1; j >= 0; j--) {
                addClassToTree(polymorphicClasses[j], classTree);
            }
            classTree.finalizeStructure(1);
        }
        return classTree;
    }

    public ClassTree getClassTreeFromCeiling(Class<?> ceilingClass) {
        Class<?>[] sortedEntities = getAllPolymorphicEntitiesFromCeiling(ceilingClass);
        return getClassTree(sortedEntities);
    }
	
	public Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective) {
        Class<?>[] entityClasses;
        try {
            entityClasses = getAllPolymorphicEntitiesFromCeiling(Class.forName(entityName));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (!ArrayUtils.isEmpty(entityClasses)) {
            Map<String, FieldMetadata> mergedProperties = getMergedProperties(
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
            return mergedProperties;
        } else {
            Map<String, FieldMetadata> mergedProperties = new HashMap<String, FieldMetadata>();
            Class<?> targetClass;
            try {
                targetClass = Class.forName(entityName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Map<String, FieldMetadata> attributesMap = getFieldPresentationAttributes(targetClass);

            for (String property : attributesMap.keySet()) {
                FieldMetadata presentationAttributes = attributesMap.get(property);
                if (!presentationAttributes.getExcluded()) {
                    Field field = getFieldManager().getSingleField(targetClass, property);
                    if (!Modifier.isStatic(field.getModifiers())) {
                        if (field.getAnnotation(AdminPresentationCollection.class) == null && field.getAnnotation(AdminPresentationAdornedTargetCollection.class) == null && field.getAnnotation(AdminPresentationMap.class) == null) {
                            buildProperty(targetClass, null, new ForeignKey[]{}, MergedPropertyType.PRIMARY, null, mergedProperties, null, "", property, null, false, 0, presentationAttributes, ((BasicFieldMetadata) presentationAttributes).getExplicitFieldType(), field.getType());
                        } else {
                            CollectionMetadata fieldMetadata = (CollectionMetadata) presentationAttributes;
                            if (StringUtils.isEmpty(fieldMetadata.getCollectionCeilingEntity()) && field.getAnnotation(AdminPresentationMap.class) == null) {
                                ParameterizedType listType = (ParameterizedType) field.getGenericType();
                                Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
                                fieldMetadata.setCollectionCeilingEntity(listClass.getName());
                            }
                            mergedProperties.put(property, fieldMetadata);
                        }
                    }
                }
            }

            return mergedProperties;
        }
	}

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
		String prefix
	) {
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
            false
        );

        List<String> removeKeys = new ArrayList<String>();
        for (String key : mergedProperties.keySet()) {
            if (mergedProperties.get(key).getExcluded() != null && mergedProperties.get(key).getExcluded()) {
                removeKeys.add(key);
            }
        }

        for (String removeKey : removeKeys) {
            mergedProperties.remove(removeKey);
        }

        return mergedProperties;
    }

	protected Map<String, FieldMetadata> getMergedPropertiesRecursively(
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
        List<Class<?>> parentClasses,
        String prefix,
        Boolean isParentExcluded
    ) {
		Map<String, FieldMetadata> mergedProperties = new HashMap<String, FieldMetadata>();
        Boolean classAnnotatedPopulateManyToOneFields = null;

        Map<String, AdminPresentationOverride> presentationOverrides = new HashMap<String, AdminPresentationOverride>();
        Map<String, AdminPresentationMapOverride> presentationMapOverrides = new HashMap<String, AdminPresentationMapOverride>();
        Map<String, AdminPresentationCollectionOverride> presentationCollectionOverrides = new HashMap<String, AdminPresentationCollectionOverride>();
        Map<String, AdminPresentationAdornedTargetCollectionOverride> presentationAdornedTargetCollectionOverrides = new HashMap<String, AdminPresentationAdornedTargetCollectionOverride>();

		//go in reverse order since I want the lowest subclass override to come last to guarantee that it takes effect
		for (int i = entities.length-1;i >= 0; i--) {
			AdminPresentationOverrides myOverrides = entities[i].getAnnotation(AdminPresentationOverrides.class);
            if (myOverrides != null) {
                for (AdminPresentationOverride myOverride : myOverrides.value()) {
                    presentationOverrides.put(myOverride.name(), myOverride);
                }
                for (AdminPresentationMapOverride myOverride : myOverrides.maps()) {
                    presentationMapOverrides.put(myOverride.name(), myOverride);
                }
                for (AdminPresentationCollectionOverride myOverride : myOverrides.collections()) {
                    presentationCollectionOverrides.put(myOverride.name(), myOverride);
                }
                for (AdminPresentationAdornedTargetCollectionOverride myOverride : myOverrides.adornedTargetCollections()) {
                    presentationAdornedTargetCollectionOverrides.put(myOverride.name(), myOverride);
                }
            }
            AdminPresentationClass adminPresentationClass = entities[i].getAnnotation(AdminPresentationClass.class);
            if (adminPresentationClass != null && classAnnotatedPopulateManyToOneFields == null && adminPresentationClass.populateToOneFields() != PopulateToOneFieldsEnum.NOT_SPECIFIED) {
                classAnnotatedPopulateManyToOneFields = adminPresentationClass.populateToOneFields()==PopulateToOneFieldsEnum.TRUE;
            }
		}
        if (classAnnotatedPopulateManyToOneFields != null) {
            populateManyToOneFields = classAnnotatedPopulateManyToOneFields;
        }

		buildPropertiesFromPolymorphicEntities(
            entities,
            foreignField,
            additionalNonPersistentProperties,
            additionalForeignFields,
            mergedPropertyType,
            populateManyToOneFields,
            includeFields,
            excludeFields,
            configurationKey,
            ceilingEntityFullyQualifiedClassname,
            mergedProperties,
            parentClasses,
            prefix,
            isParentExcluded
        );

        for (String propertyName : presentationOverrides.keySet()) {
            for (String key : mergedProperties.keySet()) {
                if (key.equals(propertyName)) {
                    buildAdminPresentationOverride(prefix, isParentExcluded, mergedProperties, presentationOverrides, propertyName, key);
                    buildAdminPresentationCollectionOverride(prefix, isParentExcluded, mergedProperties, presentationCollectionOverrides, propertyName, key);
                    buildAdminPresentationAdornedTargetCollectionOverride(prefix, isParentExcluded, mergedProperties, presentationAdornedTargetCollectionOverrides, propertyName, key);
                    buildAdminPresentationMapOverride(prefix, isParentExcluded, mergedProperties, presentationMapOverrides, propertyName, key);
                }
            }
        }

        applyMetadataOverrides(ceilingEntityFullyQualifiedClassname, configurationKey, prefix, isParentExcluded, mergedProperties);
        applyCollectionMetadataOverrides(ceilingEntityFullyQualifiedClassname, configurationKey, prefix, isParentExcluded, mergedProperties);
        applyAdornedTargetCollectionMetadataOverrides(ceilingEntityFullyQualifiedClassname, configurationKey, prefix, isParentExcluded, mergedProperties);
        applyMapMetadataOverrides(ceilingEntityFullyQualifiedClassname, configurationKey, prefix, isParentExcluded, mergedProperties);

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
                        metadata.setExcluded(true);
                    } else {
                        FieldMetadata metadata = mergedProperties.get(key);
                        if (!isParentExcluded) {
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
                        metadata.setExcluded(true);
                    } else {
                        FieldMetadata metadata = mergedProperties.get(key);
                        if (!isParentExcluded) {
                            metadata.setExcluded(false);
                        }
                    }
                }
            }
        }
    }

    protected void applyMapMetadataOverrides(String ceilingEntityFullyQualifiedClassname, String configurationKey, String prefix, final Boolean isParentExcluded, Map<String, FieldMetadata> mergedProperties) {
        if (mapMetadataOverrides != null && configurationKey != null) {
            Map<String, Map<String, MapMetadataOverride>> configuredOverrides = mapMetadataOverrides.get(configurationKey);
            if (configuredOverrides != null) {
                Map<String, MapMetadataOverride> entityOverrides = configuredOverrides.get(ceilingEntityFullyQualifiedClassname);
                if (entityOverrides != null) {
                    for (String propertyName : entityOverrides.keySet()) {
                        final MapMetadataOverride localMetadata = entityOverrides.get(propertyName);
                        Boolean excluded = localMetadata.getExcluded();
                        if (excluded == null) {
                            excluded = false;
                        }
                        for (String key : mergedProperties.keySet()) {
                            String testKey = prefix + key;
                            if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && excluded) {
                                FieldMetadata metadata = mergedProperties.get(key);
                                metadata.setExcluded(true);
                                continue;
                            }
                            if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && !excluded) {
                                FieldMetadata metadata = mergedProperties.get(key);
                                if (!isParentExcluded) {
                                    metadata.setExcluded(false);
                                }
                            }
                            if (key.equals(propertyName)) {
                                try {
                                    MapMetadata serverMetadata = (MapMetadata) mergedProperties.get(key);
                                    Class<?> targetClass = Class.forName(((ForeignKey) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY)).getForeignKeyClass());
                                    String fieldName = ((MapStructure) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE)).getMapProperty();
                                    Field field = targetClass.getField(fieldName);
                                    Map<String, FieldMetadata> temp = new HashMap<String, FieldMetadata>(1);
                                    temp.put(field.getName(), serverMetadata);
                                    buildMapMetadata(targetClass, temp, field, localMetadata);
                                    mergedProperties.put(key, temp.get(field.getName()));
                                    if (localMetadata.getExcluded() != null) {
                                        serverMetadata.setExcluded(localMetadata.getExcluded());
                                    }
                                    if (isParentExcluded) {
                                        serverMetadata.setExcluded(true);
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected void applyAdornedTargetCollectionMetadataOverrides(String ceilingEntityFullyQualifiedClassname, String configurationKey, String prefix, final Boolean isParentExcluded, Map<String, FieldMetadata> mergedProperties) {
        if (adornedTargetCollectionMetadataOverrides != null && configurationKey != null) {
            Map<String, Map<String, AdornedTargetCollectionMetadataOverride>> configuredOverrides = adornedTargetCollectionMetadataOverrides.get(configurationKey);
            if (configuredOverrides != null) {
                Map<String, AdornedTargetCollectionMetadataOverride> entityOverrides = configuredOverrides.get(ceilingEntityFullyQualifiedClassname);
                if (entityOverrides != null) {
                    for (String propertyName : entityOverrides.keySet()) {
                        final AdornedTargetCollectionMetadataOverride localMetadata = entityOverrides.get(propertyName);
                        Boolean excluded = localMetadata.getExcluded();
                        if (excluded == null) {
                            excluded = false;
                        }
                        for (String key : mergedProperties.keySet()) {
                            String testKey = prefix + key;
                            if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && excluded) {
                                FieldMetadata metadata = mergedProperties.get(key);
                                metadata.setExcluded(true);
                                continue;
                            }
                            if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && !excluded) {
                                FieldMetadata metadata = mergedProperties.get(key);
                                if (!isParentExcluded) {
                                    metadata.setExcluded(false);
                                }
                            }
                            if (key.equals(propertyName)) {
                                try {
                                    AdornedTargetCollectionMetadata serverMetadata = (AdornedTargetCollectionMetadata) mergedProperties.get(key);
                                    Class<?> targetClass = Class.forName(serverMetadata.getParentObjectClass());
                                    String fieldName = ((AdornedTargetList) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).getCollectionFieldName();
                                    Field field = targetClass.getField(fieldName);
                                    Map<String, FieldMetadata> temp = new HashMap<String, FieldMetadata>(1);
                                    temp.put(field.getName(), serverMetadata);
                                    buildAdornedTargetCollectionMetadata(targetClass, temp, field, localMetadata);
                                    mergedProperties.put(key, temp.get(field.getName()));
                                    if (localMetadata.getExcluded() != null) {
                                        serverMetadata.setExcluded(localMetadata.getExcluded());
                                    }
                                    if (isParentExcluded) {
                                        serverMetadata.setExcluded(true);
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected void applyCollectionMetadataOverrides(String ceilingEntityFullyQualifiedClassname, String configurationKey, String prefix, final Boolean isParentExcluded, Map<String, FieldMetadata> mergedProperties) {
        if (collectionMetadataOverrides != null && configurationKey != null) {
            Map<String, Map<String, BasicCollectionMetadataOverride>> configuredOverrides = collectionMetadataOverrides.get(configurationKey);
            if (configuredOverrides != null) {
                Map<String, BasicCollectionMetadataOverride> entityOverrides = configuredOverrides.get(ceilingEntityFullyQualifiedClassname);
                if (entityOverrides != null) {
                    for (String propertyName : entityOverrides.keySet()) {
                        final BasicCollectionMetadataOverride localMetadata = entityOverrides.get(propertyName);
                        Boolean excluded = localMetadata.getExcluded();
                        if (excluded == null) {
                            excluded = false;
                        }
                        for (String key : mergedProperties.keySet()) {
                            String testKey = prefix + key;
                            if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && excluded) {
                                FieldMetadata metadata = mergedProperties.get(key);
                                metadata.setExcluded(true);
                                continue;
                            }
                            if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && !excluded) {
                                FieldMetadata metadata = mergedProperties.get(key);
                                if (!isParentExcluded) {
                                    metadata.setExcluded(false);
                                }
                            }
                            if (key.equals(propertyName)) {
                                try {
                                    BasicCollectionMetadata serverMetadata = (BasicCollectionMetadata) mergedProperties.get(key);
                                    Class<?> targetClass = Class.forName(((ForeignKey) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY)).getForeignKeyClass());
                                    String fieldName = serverMetadata.getCollectionFieldName();
                                    Field field = targetClass.getField(fieldName);
                                    Map<String, FieldMetadata> temp = new HashMap<String, FieldMetadata>(1);
                                    temp.put(field.getName(), serverMetadata);
                                    buildCollectionMetadata(targetClass, temp, field, localMetadata);
                                    mergedProperties.put(key, temp.get(field.getName()));
                                    if (localMetadata.getExcluded() != null) {
                                        serverMetadata.setExcluded(localMetadata.getExcluded());
                                    }
                                    if (isParentExcluded) {
                                        serverMetadata.setExcluded(true);
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected void applyMetadataOverrides(String ceilingEntityFullyQualifiedClassname, String configurationKey, String prefix, final Boolean isParentExcluded, Map<String, FieldMetadata> mergedProperties) {
        if (fieldMetadataOverrides != null && configurationKey != null) {
            Map<String, Map<String, BasicFieldMetadataOverride>> configuredOverrides = fieldMetadataOverrides.get(configurationKey);
            if (configuredOverrides != null) {
                Map<String, BasicFieldMetadataOverride> entityOverrides = configuredOverrides.get(ceilingEntityFullyQualifiedClassname);
                if (entityOverrides != null) {
                    for (String propertyName : entityOverrides.keySet()) {
                        final BasicFieldMetadataOverride localMetadata = entityOverrides.get(propertyName);
                        Boolean excluded = localMetadata.getExcluded();
                        if (excluded == null) {
                            excluded = false;
                        }
                        for (String key : mergedProperties.keySet()) {
                            String testKey = prefix + key;
                            if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && excluded) {
                                FieldMetadata metadata = mergedProperties.get(key);
                                metadata.setExcluded(true);
                                continue;
                            }
                            if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && !excluded) {
                                FieldMetadata metadata = mergedProperties.get(key);
                                if (!isParentExcluded) {
                                    metadata.setExcluded(false);
                                }
                            }
                            if (key.equals(propertyName)) {
                                BasicFieldMetadata serverMetadata = (BasicFieldMetadata) mergedProperties.get(key);
                                if (localMetadata.getFriendlyName() != null) {
                                    serverMetadata.setFriendlyName(localMetadata.getFriendlyName());
                                }
                                if (localMetadata.getSecurityLevel() != null) {
                                    serverMetadata.setSecurityLevel(localMetadata.getSecurityLevel());
                                }
                                if (localMetadata.getVisibility() != null) {
                                    serverMetadata.setVisibility(localMetadata.getVisibility());
                                }
                                if (localMetadata.getOrder() != null) {
                                    serverMetadata.setOrder(localMetadata.getOrder());
                                }
                                if (localMetadata.getExplicitFieldType() != null) {
                                    serverMetadata.setExplicitFieldType(localMetadata.getExplicitFieldType());
                                    serverMetadata.setFieldType(localMetadata.getExplicitFieldType());
                                }
                                if (localMetadata.getGroup() != null) {
                                    serverMetadata.setGroup(localMetadata.getGroup());
                                }
                                if (localMetadata.getGroupCollapsed() != null) {
                                    serverMetadata.setGroupCollapsed(localMetadata.getGroupCollapsed());
                                }
                                if (localMetadata.getGroupOrder() != null) {
                                    serverMetadata.setGroupOrder(localMetadata.getGroupOrder());
                                }
                                if (localMetadata.isLargeEntry() != null) {
                                    serverMetadata.setLargeEntry(localMetadata.isLargeEntry());
                                }
                                if (localMetadata.isProminent() != null) {
                                    serverMetadata.setProminent(localMetadata.isProminent());
                                }
                                if (localMetadata.getColumnWidth() != null) {
                                    serverMetadata.setColumnWidth(localMetadata.getColumnWidth());
                                }
                                if (!StringUtils.isEmpty(localMetadata.getBroadleafEnumeration()) && !localMetadata.getBroadleafEnumeration().equals(serverMetadata.getBroadleafEnumeration())) {
                                    serverMetadata.setBroadleafEnumeration(localMetadata.getBroadleafEnumeration());
                                    try {
                                        setupBroadleafEnumeration(localMetadata.getBroadleafEnumeration(), serverMetadata);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                if (localMetadata.getReadOnly() != null) {
                                    serverMetadata.setReadOnly(localMetadata.getReadOnly());
                                }
                                if (localMetadata.getExcluded() != null) {
                                    serverMetadata.setExcluded(localMetadata.getExcluded());
                                }
                                if (isParentExcluded) {
                                    serverMetadata.setExcluded(true);
                                }
                                if (localMetadata.getTooltip() != null) {
                                    serverMetadata.setTooltip(localMetadata.getTooltip());
                                }
                                if (localMetadata.getHelpText() != null) {
                                    serverMetadata.setHelpText(localMetadata.getHelpText());
                                }
                                if (localMetadata.getHint() != null) {
                                    serverMetadata.setHint(localMetadata.getHint());
                                }
                                if (localMetadata.getRequiredOverride() != null) {
                                    serverMetadata.setRequiredOverride(localMetadata.getRequiredOverride());
                                }
                                if (localMetadata.getValidationConfigurations() != null) {
                                    serverMetadata.setValidationConfigurations(localMetadata.getValidationConfigurations());
                                }
                                if (localMetadata.getLength() != null) {
                                    serverMetadata.setLength(localMetadata.getLength());
                                }
                                if (localMetadata.getUnique() != null) {
                                    serverMetadata.setUnique(localMetadata.getUnique());
                                }
                                if (localMetadata.getScale() != null) {
                                    serverMetadata.setScale(localMetadata.getScale());
                                }
                                if (localMetadata.getPrecision() != null) {
                                    serverMetadata.setPrecision(localMetadata.getPrecision());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected void buildAdminPresentationMapOverride(String prefix, Boolean isParentExcluded, Map<String, FieldMetadata> mergedProperties, Map<String, AdminPresentationMapOverride> presentationMapOverrides, String propertyName, String key) {
        AdminPresentationMapOverride override = presentationMapOverrides.get(propertyName);
        if (override != null) {
            AdminPresentationMap annot = override.value();
            if (annot != null) {
                String testKey = prefix + key;
                if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && annot.excluded()) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    metadata.setExcluded(true);
                    return;
                }
                if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && !annot.excluded()) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    if (!isParentExcluded) {
                        metadata.setExcluded(false);
                    }
                }
                MapMetadata metadata = (MapMetadata) mergedProperties.get(key);
                metadata.setFriendlyName(annot.friendlyName());
                metadata.setSecurityLevel(annot.securityLevel());
                metadata.setMutable(annot.mutable());
                metadata.setOrder(annot.order());
                metadata.setTargetElementId(annot.targetUIElementId());
                metadata.setDataSourceName(annot.dataSourceName());
                metadata.setCustomCriteria(annot.customCriteria());
                if (!StringUtils.isEmpty(annot.configurationKey())) {
                    metadata.getPersistencePerspective().setConfigurationKey(annot.configurationKey());
                }
                if (!void.class.equals(annot.keyClass())) {
                    ((MapStructure) metadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE)).setKeyClassName(annot.keyClass().getName());
                }
                ((MapStructure) metadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE)).setKeyPropertyFriendlyName(annot.keyPropertyFriendlyName());
                if (!void.class.equals(annot.valueClass())) {
                    ((MapStructure) metadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE)).setValueClassName(annot.valueClass().getName());
                }
                if (annot.isSimpleValue()!=UnspecifiedBooleanType.UNSPECIFIED) {
                    metadata.setSimpleValue(annot.isSimpleValue()==UnspecifiedBooleanType.TRUE);
                }
                if (metadata.isSimpleValue()) {
                    ((MapStructure) metadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE)).setDeleteValueEntity(annot.deleteEntityUponRemove());
                }
                if (!metadata.isSimpleValue()) {
                    ((SimpleValueMapStructure) metadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE)).setValuePropertyFriendlyName(annot.valuePropertyFriendlyName());
                }
                metadata.setMediaField(annot.mediaField());
                if (!ArrayUtils.isEmpty(annot.keys())) {
                    String[][] keys = new String[annot.keys().length][2];
                    int j = 0;
                    for (AdminPresentationMapKey mapKey : annot.keys()) {
                        keys[j][0] = mapKey.keyName();
                        keys[j][1] = mapKey.friendlyKeyName();
                        j++;
                    }
                    metadata.setKeys(keys);
                }
                if (!void.class.equals(annot.mapKeyOptionEntityClass())) {
                    metadata.setMapKeyOptionEntityClass(annot.mapKeyOptionEntityClass().getName());
                }
                metadata.setMapKeyOptionEntityDisplayField(annot.mapKeyOptionEntityDisplayField());
                metadata.setMapKeyOptionEntityValueField(annot.mapKeyOptionEntityValueField());
            }
        }
    }

    protected void buildAdminPresentationAdornedTargetCollectionOverride(String prefix, Boolean isParentExcluded, Map<String, FieldMetadata> mergedProperties, Map<String, AdminPresentationAdornedTargetCollectionOverride> presentationAdornedTargetCollectionOverrides, String propertyName, String key) {
        AdminPresentationAdornedTargetCollectionOverride override = presentationAdornedTargetCollectionOverrides.get(propertyName);
        if (override != null) {
            AdminPresentationAdornedTargetCollection annot = override.value();
            if (annot != null) {
                String testKey = prefix + key;
                if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && annot.excluded()) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    metadata.setExcluded(true);
                    return;
                }
                if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && !annot.excluded()) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    if (!isParentExcluded) {
                        metadata.setExcluded(false);
                    }
                }
                AdornedTargetCollectionMetadata metadata = (AdornedTargetCollectionMetadata) mergedProperties.get(key);
                metadata.setCustomCriteria(annot.customCriteria());
                metadata.setMutable(annot.mutable());
                metadata.setDataSourceName(annot.dataSourceName());
                metadata.setFriendlyName(annot.friendlyName());
                metadata.setOrder(annot.order());
                metadata.setSecurityLevel(annot.securityLevel());
                if (!StringUtils.isEmpty(annot.configurationKey())) {
                    metadata.getPersistencePerspective().setConfigurationKey(annot.configurationKey());
                }
                if (!StringUtils.isEmpty(annot.parentObjectProperty())) {
                    ((AdornedTargetList) metadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).setLinkedObjectPath(annot.parentObjectProperty());
                }
                ((AdornedTargetList) metadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).setLinkedIdProperty(annot.parentObjectIdProperty());
                ((AdornedTargetList) metadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).setTargetObjectPath(annot.targetObjectProperty());
                ((AdornedTargetList) metadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).setTargetIdProperty(annot.targetObjectIdProperty());
                metadata.setMaintainedAdornedTargetFields(annot.maintainedAdornedTargetFields());
                metadata.setGridVisibleFields(annot.gridVisibleFields());
                String sortProperty;
                if (StringUtils.isEmpty(annot.sortProperty())) {
                    sortProperty = null;
                } else {
                    sortProperty = annot.sortProperty();
                }
                ((AdornedTargetList) metadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).setSortField(sortProperty);
                ((AdornedTargetList) metadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).setSortAscending(annot.sortAscending());
                metadata.setIgnoreAdornedProperties(annot.ignoreAdornedProperties());
                metadata.setTargetElementId(annot.targetUIElementId());
            }
        }
    }

    protected void buildAdminPresentationCollectionOverride(String prefix, Boolean isParentExcluded, Map<String, FieldMetadata> mergedProperties, Map<String, AdminPresentationCollectionOverride> presentationCollectionOverrides, String propertyName, String key) {
        AdminPresentationCollectionOverride override = presentationCollectionOverrides.get(propertyName);
        if (override != null) {
            AdminPresentationCollection annot = override.value();
            if (annot != null) {
                String testKey = prefix + key;
                if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && annot.excluded()) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    metadata.setExcluded(true);
                    return;
                }
                if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && !annot.excluded()) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    if (!isParentExcluded) {
                        metadata.setExcluded(false);
                    }
                }
                BasicCollectionMetadata metadata = (BasicCollectionMetadata) mergedProperties.get(key);
                metadata.setCustomCriteria(annot.customCriteria());
                metadata.setMutable(annot.mutable());
                metadata.setAddMethodType(annot.addType());
                metadata.setDataSourceName(annot.dataSourceName());
                metadata.setFriendlyName(annot.friendlyName());
                metadata.setOrder(annot.order());
                metadata.setSecurityLevel(annot.securityLevel());
                if (!StringUtils.isEmpty(annot.manyToField())) {
                    ((ForeignKey) metadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY)).setManyToField(annot.manyToField());
                }
                metadata.setTargetElementId(annot.targetUIElementId());
                if (!StringUtils.isEmpty(annot.configurationKey())) {
                    metadata.getPersistencePerspective().setConfigurationKey(annot.configurationKey());
                }
            }
        }
    }

    protected void buildAdminPresentationOverride(String prefix, Boolean isParentExcluded, Map<String, FieldMetadata> mergedProperties, Map<String, AdminPresentationOverride> presentationOverrides, String propertyName, String key) {
        AdminPresentationOverride override = presentationOverrides.get(propertyName);
        if (override != null) {
            AdminPresentation annot = override.value();
            if (annot != null) {
                String testKey = prefix + key;
                if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && annot.excluded()) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    metadata.setExcluded(true);
                    return;
                }
                if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && !annot.excluded()) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    if (!isParentExcluded) {
                        metadata.setExcluded(false);
                    }
                }
                BasicFieldMetadata metadata = (BasicFieldMetadata) mergedProperties.get(key);
                metadata.setFriendlyName(annot.friendlyName());
                metadata.setSecurityLevel(annot.securityLevel());
                metadata.setVisibility(annot.visibility());
                metadata.setOrder(annot.order());
                metadata.setExplicitFieldType(annot.fieldType());
                if (annot.fieldType() != SupportedFieldType.UNKNOWN) {
                    metadata.setFieldType(annot.fieldType());
                }
                metadata.setGroup(annot.group());
                metadata.setGroupCollapsed(annot.groupCollapsed());
                metadata.setGroupOrder(annot.groupOrder());
                metadata.setLargeEntry(annot.largeEntry());
                metadata.setProminent(annot.prominent());
                metadata.setColumnWidth(annot.columnWidth());
                if (!StringUtils.isEmpty(annot.broadleafEnumeration()) && !annot.broadleafEnumeration().equals(metadata.getBroadleafEnumeration())) {
                    metadata.setBroadleafEnumeration(annot.broadleafEnumeration());
                    try {
                        setupBroadleafEnumeration(annot.broadleafEnumeration(), metadata);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                metadata.setReadOnly(annot.readOnly());
                metadata.setExcluded(isParentExcluded || annot.excluded());
                metadata.setTooltip(annot.tooltip());
                metadata.setHelpText(annot.helpText());
                metadata.setHint(annot.hint());
                metadata.setRequiredOverride(annot.requiredOverride()== RequiredOverride.IGNORED?null:annot.requiredOverride()==RequiredOverride.REQUIRED);
                if (annot.validationConfigurations().length != 0) {
                    ValidationConfiguration[] configurations = annot.validationConfigurations();
                    for (ValidationConfiguration configuration : configurations) {
                        ConfigurationItem[] items = configuration.configurationItems();
                        Map<String, String> itemMap = new HashMap<String, String>();
                        for (ConfigurationItem item : items) {
                            itemMap.put(item.itemName(), item.itemValue());
                        }
                        metadata.getValidationConfigurations().put(configuration.validationImplementation(), itemMap);
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

    protected String getCacheKey(ForeignKey foreignField, String[] additionalNonPersistentProperties, ForeignKey[] additionalForeignFields, MergedPropertyType mergedPropertyType, Boolean populateManyToOneFields, Class<?> clazz, String configurationKey, Boolean isParentExcluded) {
        StringBuilder sb = new StringBuilder(150);
        sb.append(clazz.hashCode());
        sb.append(foreignField==null?"":foreignField.toString());
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
            BigInteger number = new BigInteger(1,messageDigest);
            digest = number.toString(16);
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return pad(digest, 32, '0');
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
        Boolean isParentExcluded
	) {
		for (Class<?> clazz : entities) {
            String cacheKey = getCacheKey(foreignField, additionalNonPersistentProperties, additionalForeignFields, mergedPropertyType, populateManyToOneFields, clazz, configurationKey, isParentExcluded);

            Map<String, FieldMetadata> cacheData;
            synchronized(LOCK_OBJECT) {
                cacheData = (Map<String, FieldMetadata>) METADATA_CACHE.get(cacheKey);
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
                        isParentExcluded
                    );
                    //first check all the properties currently in there to see if my entity inherits from them
                    for (Class<?> clazz2 : entities) {
                        if (!clazz2.getName().equals(clazz.getName())) {
                            for (Map.Entry<String, FieldMetadata> entry : props.entrySet()) {
                                FieldMetadata metadata = entry.getValue();
                                try {
                                    if (Class.forName(metadata.getInheritedFromType()).isAssignableFrom(clazz2)) {
                                        String[] both = (String[]) ArrayUtils.addAll(metadata.getAvailableToTypes(), new String[]{clazz2.getName()});
                                        metadata.setAvailableToTypes(both);
                                    }
                                } catch (ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                    METADATA_CACHE.put(cacheKey, props);
                    cacheData = props;
                }
            }
            //clone the metadata before passing to the system
            Map<String, FieldMetadata> clonedCache = new HashMap<String, FieldMetadata>(cacheData.size());
            for (Map.Entry<String, FieldMetadata> entry : cacheData.entrySet()) {
                clonedCache.put(entry.getKey(), entry.getValue().cloneFieldMetadata());
            }
            mergedProperties.putAll(clonedCache);
        }
    }

    protected FieldMetadata getFieldMetadata(
		String prefix, 
		String propertyName, 
		List<Property> componentProperties,
		SupportedFieldType type, 
		Type entityType, 
		Class<?> targetClass, 
		FieldMetadata presentationAttribute,
		MergedPropertyType mergedPropertyType
	) {
		return getFieldMetadata(prefix, propertyName, componentProperties, type, null, entityType, targetClass, presentationAttribute, mergedPropertyType);
	}

	protected FieldMetadata getFieldMetadata(
		String prefix, 
		final String propertyName,
		final List<Property> componentProperties,
		final SupportedFieldType type,
		final SupportedFieldType secondaryType,
		final Type entityType,
		Class<?> targetClass, 
		final FieldMetadata presentationAttribute,
		final MergedPropertyType mergedPropertyType
	) {
        presentationAttribute.setInheritedFromType(targetClass.getName());
        presentationAttribute.setAvailableToTypes(new String[]{targetClass.getName()});
        presentationAttribute.accept(new MetadataVisitorAdapter() {
            @Override
            public void visit(BasicFieldMetadata metadata) {
                BasicFieldMetadata fieldMetadata = (BasicFieldMetadata) presentationAttribute;
                fieldMetadata.setFieldType(type);
                fieldMetadata.setSecondaryType(secondaryType);
                if (entityType != null && !entityType.isCollectionType()) {
                    Column column = null;
                    for (Property property : componentProperties) {
                        if (property.getName().equals(propertyName)) {
                            column = (Column) property.getColumnIterator().next();
                            break;
                        }
                    }
                    if (column != null) {
                        fieldMetadata.setLength(column.getLength());
                        fieldMetadata.setScale(column.getScale());
                        fieldMetadata.setPrecision(column.getPrecision());
                        fieldMetadata.setRequired(!column.isNullable());
                        fieldMetadata.setUnique(column.isUnique());
                    }
                    fieldMetadata.setForeignKeyCollection(false);
                } else {
                    fieldMetadata.setForeignKeyCollection(true);
                }
                fieldMetadata.setMutable(true);
                fieldMetadata.setMergedPropertyType(mergedPropertyType);
                if (SupportedFieldType.BROADLEAF_ENUMERATION.equals(type)) {
                    try {
                        setupBroadleafEnumeration(fieldMetadata.getBroadleafEnumeration(), fieldMetadata);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void visit(BasicCollectionMetadata metadata) {
                //do nothing
            }

            @Override
            public void visit(AdornedTargetCollectionMetadata metadata) {
                //do nothing
            }

            @Override
            public void visit(MapMetadata metadata) {
                //do nothing
            }
        });
		
		return presentationAttribute;
	}

    protected void setupBroadleafEnumeration(String broadleafEnumerationClass, BasicFieldMetadata fieldMetadata) {
        try {
            Map<String, String> enumVals = new TreeMap<String, String>();
            Class<?> broadleafEnumeration = Class.forName(broadleafEnumerationClass);
            Method typeMethod = broadleafEnumeration.getMethod("getType");
            Method friendlyTypeMethod = broadleafEnumeration.getMethod("getFriendlyType");
            Field types = getFieldManager().getField(broadleafEnumeration, "TYPES");
            if (types != null) {
                Map typesMap = (Map) types.get(null);
                for (Object value : typesMap.values()) {
                    enumVals.put((String) friendlyTypeMethod.invoke(value), (String) typeMethod.invoke(value));
                }
            } else {
                Field[] fields = getAllFields(broadleafEnumeration);
                for (Field field : fields) {
                    boolean isStatic = Modifier.isStatic(field.getModifiers());
                    if (isStatic && field.getType().isAssignableFrom(broadleafEnumeration)){
                        enumVals.put((String) friendlyTypeMethod.invoke(field.get(null)), (String) typeMethod.invoke(field.get(null)));
                    }
                }
            }
            String[][] enumerationValues = new String[enumVals.size()][2];
            int j = 0;
            for (String key : enumVals.keySet()) {
                enumerationValues[j][0] = enumVals.get(key);
                enumerationValues[j][1] = key;
                j++;
            }
            fieldMetadata.setEnumerationValues(enumerationValues);
            fieldMetadata.setEnumerationClass(broadleafEnumerationClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Field[] getAllFields(Class<?> targetClass) {
		Field[] allFields = new Field[]{};
		boolean eof = false;
		Class<?> currentClass = targetClass;
		while (!eof) {
			Field[] fields = currentClass.getDeclaredFields();
			allFields = (Field[]) ArrayUtils.addAll(allFields, fields);
			if (currentClass.getSuperclass() != null) {
				currentClass = currentClass.getSuperclass();
			} else {
				eof = true;
			}
		}
		
		return allFields;
	}

	protected Map<String, FieldMetadata> getFieldPresentationAttributes(Class<?> targetClass) {
		Map<String, FieldMetadata> attributes = new HashMap<String, FieldMetadata>();
		Field[] fields = getAllFields(targetClass);
		for (Field field : fields) {
			AdminPresentation annot = field.getAnnotation(AdminPresentation.class);
            AdminPresentationCollection annotColl = field.getAnnotation(AdminPresentationCollection.class);
            AdminPresentationAdornedTargetCollection adornedTargetCollection = field.getAnnotation(AdminPresentationAdornedTargetCollection.class);
            AdminPresentationMap map = field.getAnnotation(AdminPresentationMap.class);
			if (annot != null) {
                buildBasicMetadata(attributes, field, annot);
            } else if (annotColl != null) {
                BasicCollectionMetadataOverride override = new BasicCollectionMetadataOverride();
                override.setAddMethodType(annotColl.addType());
                override.setConfigurationKey(annotColl.configurationKey());
                override.setManyToField(annotColl.manyToField());
                override.setCustomCriteria(annotColl.customCriteria());
                override.setDataSourceName(annotColl.dataSourceName());
                override.setExcluded(annotColl.excluded());
                override.setFriendlyName(annotColl.friendlyName());
                override.setMutable(annotColl.mutable());
                override.setOrder(annotColl.order());
                override.setSecurityLevel(annotColl.securityLevel());
                override.setTargetElementId(annotColl.targetUIElementId());
                override.setAddType(annotColl.operationTypes().addType());
                override.setFetchType(annotColl.operationTypes().fetchType());
                override.setRemoveType(annotColl.operationTypes().removeType());
                override.setUpdateType(annotColl.operationTypes().updateType());
                override.setInspectType(annotColl.operationTypes().inspectType());

                buildCollectionMetadata(targetClass, attributes, field, override);
            } else if (adornedTargetCollection != null) {
                AdornedTargetCollectionMetadataOverride override = new AdornedTargetCollectionMetadataOverride();
                override.setConfigurationKey(adornedTargetCollection.configurationKey());
                override.setGridVisibleFields(adornedTargetCollection.gridVisibleFields());
                override.setIgnoreAdornedProperties(adornedTargetCollection.ignoreAdornedProperties());
                override.setMaintainedAdornedTargetFields(adornedTargetCollection.maintainedAdornedTargetFields());
                override.setParentObjectIdProperty(adornedTargetCollection.parentObjectIdProperty());
                override.setParentObjectProperty(adornedTargetCollection.parentObjectProperty());
                override.setSortAscending(adornedTargetCollection.sortAscending());
                override.setSortProperty(adornedTargetCollection.sortProperty());
                override.setTargetObjectIdProperty(adornedTargetCollection.targetObjectIdProperty());
                override.setTargetObjectProperty(adornedTargetCollection.targetObjectProperty());
                override.setCustomCriteria(adornedTargetCollection.customCriteria());
                override.setDataSourceName(adornedTargetCollection.dataSourceName());
                override.setExcluded(adornedTargetCollection.excluded());
                override.setFriendlyName(adornedTargetCollection.friendlyName());
                override.setMutable(adornedTargetCollection.mutable());
                override.setOrder(adornedTargetCollection.order());
                override.setSecurityLevel(adornedTargetCollection.securityLevel());
                override.setTargetElementId(adornedTargetCollection.targetUIElementId());
                override.setAddType(adornedTargetCollection.operationTypes().addType());
                override.setFetchType(adornedTargetCollection.operationTypes().fetchType());
                override.setRemoveType(adornedTargetCollection.operationTypes().removeType());
                override.setUpdateType(adornedTargetCollection.operationTypes().updateType());
                override.setInspectType(adornedTargetCollection.operationTypes().inspectType());

                buildAdornedTargetCollectionMetadata(targetClass, attributes, field, override);
            } else if (map != null) {
                MapMetadataOverride override = new MapMetadataOverride();
                override.setConfigurationKey(map.configurationKey());
                override.setDeleteEntityUponRemove(map.deleteEntityUponRemove());
                override.setKeyClass(map.keyClass().getName());
                override.setKeyPropertyFriendlyName(map.keyPropertyFriendlyName());
                if (!ArrayUtils.isEmpty(map.keys())) {
                    String[][] keys = new String[map.keys().length][2];
                    for (int j=0;j<keys.length;j++){
                        keys[j][0] = map.keys()[j].keyName();
                        keys[j][1] = map.keys()[j].friendlyKeyName();
                    }
                    override.setKeys(keys);
                }
                override.setMapKeyOptionEntityClass(map.mapKeyOptionEntityClass().getName());
                override.setMapKeyOptionEntityDisplayField(map.mapKeyOptionEntityDisplayField());
                override.setMapKeyOptionEntityValueField(map.mapKeyOptionEntityValueField());
                override.setMediaField(map.mediaField());
                override.setSimpleValue(map.isSimpleValue());
                override.setValueClass(map.valueClass().getName());
                override.setValuePropertyFriendlyName(map.valuePropertyFriendlyName());
                override.setCustomCriteria(map.customCriteria());
                override.setDataSourceName(map.dataSourceName());
                override.setExcluded(map.excluded());
                override.setFriendlyName(map.friendlyName());
                override.setMutable(map.mutable());
                override.setOrder(map.order());
                override.setSecurityLevel(map.securityLevel());
                override.setTargetElementId(map.targetUIElementId());
                override.setAddType(map.operationTypes().addType());
                override.setFetchType(map.operationTypes().fetchType());
                override.setRemoveType(map.operationTypes().removeType());
                override.setUpdateType(map.operationTypes().updateType());
                override.setInspectType(map.operationTypes().inspectType());

                buildMapMetadata(targetClass, attributes, field, override);
			} else {
                BasicFieldMetadata metadata = new BasicFieldMetadata();
                metadata.setName(field.getName());
                metadata.setExcluded(false);
                attributes.put(field.getName(), metadata);
            }
		}
		return attributes;
	}

    protected void buildMapMetadata(Class<?> targetClass, Map<String, FieldMetadata> attributes, Field field, MapMetadataOverride map) {
        MapMetadata serverMetadata = (MapMetadata) attributes.get(field.getName());

        MapMetadata metadata = new MapMetadata();
        if (serverMetadata != null && map.isMutable() == null) {
            metadata.setMutable(serverMetadata.isMutable());
        } else {
            metadata.setMutable(map.isMutable());
        }

        org.broadleafcommerce.openadmin.client.dto.OperationTypes dtoOperationTypes = new org.broadleafcommerce.openadmin.client.dto.OperationTypes();
        if (serverMetadata != null && map.getAddType() == null) {
            dtoOperationTypes.setAddType(serverMetadata.getPersistencePerspective().getOperationTypes().getAddType());
        } else {
            dtoOperationTypes.setAddType(map.getAddType());
        }
        if (serverMetadata != null && map.getRemoveType() == null) {
            dtoOperationTypes.setRemoveType(serverMetadata.getPersistencePerspective().getOperationTypes().getRemoveType());
        } else {
            dtoOperationTypes.setRemoveType(map.getRemoveType());
        }
        if (serverMetadata != null && map.getFetchType() == null) {
            dtoOperationTypes.setFetchType(serverMetadata.getPersistencePerspective().getOperationTypes().getFetchType());
        } else {
            dtoOperationTypes.setFetchType(map.getFetchType());
        }
        if (serverMetadata != null && map.getInspectType() == null) {
            dtoOperationTypes.setInspectType(serverMetadata.getPersistencePerspective().getOperationTypes().getInspectType());
        } else {
            dtoOperationTypes.setInspectType(map.getInspectType());
        }
        if (serverMetadata != null && map.getUpdateType() == null) {
            dtoOperationTypes.setUpdateType(serverMetadata.getPersistencePerspective().getOperationTypes().getUpdateType());
        } else {
            dtoOperationTypes.setInspectType(map.getUpdateType());
        }

        //don't allow additional non-persistent properties or additional foreign keys for an advanced collection datasource - they don't make sense in this context
        PersistencePerspective persistencePerspective = new PersistencePerspective(dtoOperationTypes, new String[]{}, new ForeignKey[]{});
        if (serverMetadata != null && map.getConfigurationKey() == null) {
            serverMetadata.getPersistencePerspective().setConfigurationKey(serverMetadata.getPersistencePerspective().getConfigurationKey());
        } else {
            if (!StringUtils.isEmpty(map.getConfigurationKey())) {
                persistencePerspective.setConfigurationKey(map.getConfigurationKey());
            }
        }
        metadata.setPersistencePerspective(persistencePerspective);

        String parentObjectClass = targetClass.getName();
        Map idMetadata = getIdMetadata(targetClass);
        String parentObjectIdField = (String) idMetadata.get("name");

        String keyClassName;
        if (serverMetadata != null && map.getKeyClass() == null) {
            keyClassName = ((MapStructure) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE)).getKeyClassName();
        } else {
            checkProperty: {
                if (!void.class.getName().equals(map.getKeyClass())) {
                    keyClassName = map.getKeyClass();
                    break checkProperty;
                }

                java.lang.reflect.Type type = field.getGenericType();
                if (type instanceof ParameterizedType) {
                    ParameterizedType pType = (ParameterizedType) type;
                    Class<?> clazz = (Class<?>) pType.getActualTypeArguments()[0];
                    if (!ArrayUtils.isEmpty(getAllPolymorphicEntitiesFromCeiling(clazz))) {
                        throw new RuntimeException("Key class for AdminPresentationMap was determined to be a JPA managed type. Only primitive types for the key type are currently supported.");
                    }
                    keyClassName = clazz.getName();
                    break checkProperty;
                }

                keyClassName = String.class.getName();
            }
        }

        String keyPropertyName = "key";
        String keyPropertyFriendlyName;
        if (serverMetadata != null && map.getKeyPropertyFriendlyName() == null) {
            keyPropertyFriendlyName = ((MapStructure) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE)).getKeyPropertyFriendlyName();
        } else {
            keyPropertyFriendlyName = map.getKeyPropertyFriendlyName();
        }
        boolean deleteEntityUponRemove;
        if (serverMetadata != null && map.isDeleteEntityUponRemove()==null) {
            deleteEntityUponRemove = ((MapStructure) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE)).getDeleteValueEntity();
        } else {
            deleteEntityUponRemove = map.isDeleteEntityUponRemove();
        }
        String valuePropertyName = "value";
        String valuePropertyFriendlyName;
        if (serverMetadata != null && map.getValuePropertyFriendlyName()==null) {
            MapStructure structure = (MapStructure) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
            if (structure instanceof SimpleValueMapStructure) {
                valuePropertyFriendlyName = ((SimpleValueMapStructure) structure).getValuePropertyFriendlyName();
            } else {
                valuePropertyFriendlyName = "";
            }
        } else {
            valuePropertyFriendlyName = map.getValuePropertyFriendlyName();
        }
        if (serverMetadata != null && map.getMediaField() == null) {
            metadata.setMediaField(serverMetadata.getMediaField());
        } else {
            metadata.setMediaField(map.getMediaField());
        }

        if (serverMetadata != null && map.getValueClass() == null) {
            metadata.setValueClassName(serverMetadata.getValueClassName());
        } else {
            checkProperty: {
                if (!void.class.getName().equals(map.getValueClass())) {
                    metadata.setValueClassName(map.getValueClass());
                    break checkProperty;
                }

                java.lang.reflect.Type type = field.getGenericType();
                if (type instanceof ParameterizedType) {
                    ParameterizedType pType = (ParameterizedType) type;
                    Class<?> clazz = (Class<?>) pType.getActualTypeArguments()[1];
                    Class<?>[] entities = getAllPolymorphicEntitiesFromCeiling(clazz);
                    if (!ArrayUtils.isEmpty(entities)) {
                        metadata.setValueClassName(entities[entities.length-1].getName());
                        break checkProperty;
                    }
                }

                ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
                if (manyToMany != null && !StringUtils.isEmpty(manyToMany.targetEntity().getName())) {
                    metadata.setValueClassName(manyToMany.mappedBy());
                    break checkProperty;
                }

                metadata.setValueClassName(String.class.getName());
            }
        }

        if (serverMetadata != null &&  map.getSimpleValue()== null) {
            metadata.setSimpleValue(serverMetadata.isSimpleValue());
        } else {
            checkProperty: {
                java.lang.reflect.Type type = field.getGenericType();
                if (type instanceof ParameterizedType) {
                    ParameterizedType pType = (ParameterizedType) type;
                    Class<?> clazz = (Class<?>) pType.getActualTypeArguments()[1];
                    Class<?>[] entities = getAllPolymorphicEntitiesFromCeiling(clazz);
                    if (!ArrayUtils.isEmpty(entities)) {
                        metadata.setSimpleValue(false);
                        break checkProperty;
                    }
                }

                ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
                if (manyToMany != null && !StringUtils.isEmpty(manyToMany.targetEntity().getName())) {
                    metadata.setSimpleValue(false);
                    break checkProperty;
                }

                if (map.getSimpleValue()== UnspecifiedBooleanType.UNSPECIFIED) {
                    throw new RuntimeException("Unable to infer if the value for the map is of a complex or simple type based on any parameterized type or ManyToMany annotation. Please explicitly set the isSimpleValue property.");
                }
                metadata.setSimpleValue(map.getSimpleValue()==UnspecifiedBooleanType.TRUE);
            }
        }

        if (serverMetadata != null &&  map.getKeys() == null) {
            metadata.setKeys(serverMetadata.getKeys());
        } else {
            if (!ArrayUtils.isEmpty(map.getKeys())) {
                metadata.setKeys(map.getKeys());
            }
        }

        if (serverMetadata != null && map.getMapKeyOptionEntityClass()==null) {
            metadata.setMapKeyOptionEntityClass(serverMetadata.getMapKeyOptionEntityClass());
        } else {
            if (!void.class.getName().equals(map.getMapKeyOptionEntityClass())) {
                metadata.setMapKeyOptionEntityClass(map.getMapKeyOptionEntityClass());
            } else {
                metadata.setMapKeyOptionEntityClass("");
            }
        }
        if (serverMetadata != null && map.getMapKeyOptionEntityDisplayField() == null) {
            metadata.setMapKeyOptionEntityDisplayField(serverMetadata.getMapKeyOptionEntityDisplayField());
        } else {
            metadata.setMapKeyOptionEntityDisplayField(map.getMapKeyOptionEntityDisplayField());
        }
        if (serverMetadata != null && map.getMapKeyOptionEntityValueField()==null) {
            metadata.setMapKeyOptionEntityValueField(serverMetadata.getMapKeyOptionEntityValueField());
        } else {
            metadata.setMapKeyOptionEntityValueField(map.getMapKeyOptionEntityValueField());
        }

        if (ArrayUtils.isEmpty(metadata.getKeys()) && (StringUtils.isEmpty(metadata.getMapKeyOptionEntityClass()) || StringUtils.isEmpty(metadata.getMapKeyOptionEntityValueField()) || StringUtils.isEmpty(metadata.getMapKeyOptionEntityDisplayField()))) {
            throw new RuntimeException("Could not ascertain method for generating key options for the annotated map ("+field.getName()+"). Must specify either an array of AdminPresentationMapKey values for the keys property, or utilize the mapOptionKeyClass, mapOptionKeyDisplayField and mapOptionKeyValueField properties");
        }

        ForeignKey foreignKey = new ForeignKey(parentObjectIdField, parentObjectClass);
        MapStructure mapStructure;
        persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, foreignKey);
        if (metadata.isSimpleValue()) {
            mapStructure = new SimpleValueMapStructure(keyClassName, keyPropertyName, keyPropertyFriendlyName, metadata.getValueClassName(), valuePropertyName, valuePropertyFriendlyName, field.getName());
        } else {
            mapStructure = new MapStructure(keyClassName, keyPropertyName, keyPropertyFriendlyName, metadata.getValueClassName(), field.getName(), deleteEntityUponRemove);
        }
        persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.MAPSTRUCTURE, mapStructure);

        if (serverMetadata != null && map.getExcluded() == null) {
            metadata.setExcluded(serverMetadata.getExcluded());
        } else {
            metadata.setExcluded(map.getExcluded());
        }
        if (serverMetadata != null && map.getFriendlyName() == null) {
            metadata.setFriendlyName(serverMetadata.getFriendlyName());
        } else {
            metadata.setFriendlyName(map.getFriendlyName());
        }
        if (serverMetadata != null && map.getSecurityLevel() == null) {
            metadata.setSecurityLevel(serverMetadata.getSecurityLevel());
        } else {
            metadata.setSecurityLevel(map.getSecurityLevel());
        }
        if (serverMetadata != null && map.getOrder() == null) {
            metadata.setOrder(serverMetadata.getOrder());
        } else {
            metadata.setOrder(map.getOrder());
        }

        if (serverMetadata != null && map.getTargetElementId() == null) {
            metadata.setTargetElementId(serverMetadata.getTargetElementId());
        } else {
            if (!StringUtils.isEmpty(map.getTargetElementId())) {
                metadata.setTargetElementId(map.getTargetElementId());
            }
        }

        if (serverMetadata != null && map.getDataSourceName() == null) {
            metadata.setDataSourceName(serverMetadata.getDataSourceName());
        } else {
            if (!StringUtils.isEmpty(map.getDataSourceName())) {
                metadata.setDataSourceName(map.getDataSourceName());
            }
        }

        if (serverMetadata != null &&  map.getCustomCriteria() == null) {
            metadata.setCustomCriteria(serverMetadata.getCustomCriteria());
        } else {
            metadata.setCustomCriteria(map.getCustomCriteria());
        }

        attributes.put(field.getName(), metadata);
    }

    protected void buildAdornedTargetCollectionMetadata(Class<?> targetClass, Map<String, FieldMetadata> attributes, Field field, AdornedTargetCollectionMetadataOverride adornedTargetCollectionMetadata) {
        AdornedTargetCollectionMetadata serverMetadata = (AdornedTargetCollectionMetadata) attributes.get(field.getName());

        AdornedTargetCollectionMetadata metadata = new AdornedTargetCollectionMetadata();
        if (serverMetadata != null && adornedTargetCollectionMetadata.isMutable() == null) {
            metadata.setMutable(serverMetadata.isMutable());
        } else {
            metadata.setMutable(adornedTargetCollectionMetadata.isMutable());
        }

        org.broadleafcommerce.openadmin.client.dto.OperationTypes dtoOperationTypes = new org.broadleafcommerce.openadmin.client.dto.OperationTypes();
        if (serverMetadata != null && adornedTargetCollectionMetadata.getAddType() == null) {
            dtoOperationTypes.setAddType(serverMetadata.getPersistencePerspective().getOperationTypes().getAddType());
        } else {
            dtoOperationTypes.setAddType(adornedTargetCollectionMetadata.getAddType());
        }
        if (serverMetadata != null && adornedTargetCollectionMetadata.getRemoveType() == null) {
            dtoOperationTypes.setRemoveType(serverMetadata.getPersistencePerspective().getOperationTypes().getRemoveType());
        } else {
            dtoOperationTypes.setRemoveType(adornedTargetCollectionMetadata.getRemoveType());
        }
        if (serverMetadata != null && adornedTargetCollectionMetadata.getFetchType() == null) {
            dtoOperationTypes.setFetchType(serverMetadata.getPersistencePerspective().getOperationTypes().getFetchType());
        } else {
            dtoOperationTypes.setFetchType(adornedTargetCollectionMetadata.getFetchType());
        }
        if (serverMetadata != null && adornedTargetCollectionMetadata.getInspectType() == null) {
            dtoOperationTypes.setInspectType(serverMetadata.getPersistencePerspective().getOperationTypes().getInspectType());
        } else {
            dtoOperationTypes.setInspectType(adornedTargetCollectionMetadata.getInspectType());
        }
        if (serverMetadata != null && adornedTargetCollectionMetadata.getUpdateType() == null) {
            dtoOperationTypes.setUpdateType(serverMetadata.getPersistencePerspective().getOperationTypes().getUpdateType());
        } else {
            dtoOperationTypes.setInspectType(adornedTargetCollectionMetadata.getUpdateType());
        }

        //don't allow additional non-persistent properties or additional foreign keys for an advanced collection datasource - they don't make sense in this context
        PersistencePerspective persistencePerspective = new PersistencePerspective(dtoOperationTypes, new String[]{}, new ForeignKey[]{});
        if (serverMetadata != null && adornedTargetCollectionMetadata.getConfigurationKey() == null) {
            serverMetadata.getPersistencePerspective().setConfigurationKey(serverMetadata.getPersistencePerspective().getConfigurationKey());
        } else {
            if (!StringUtils.isEmpty(adornedTargetCollectionMetadata.getConfigurationKey())) {
                persistencePerspective.setConfigurationKey(adornedTargetCollectionMetadata.getConfigurationKey());
            }
        }
        metadata.setPersistencePerspective(persistencePerspective);

        //try to inspect the JPA annotation
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
        String parentObjectProperty = null;
        if (serverMetadata != null && adornedTargetCollectionMetadata.getParentObjectProperty() == null) {
            parentObjectProperty = ((AdornedTargetList) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).getLinkedObjectPath();
        } else {
            checkProperty: {
                if (!StringUtils.isEmpty(adornedTargetCollectionMetadata.getParentObjectProperty())) {
                    parentObjectProperty = adornedTargetCollectionMetadata.getParentObjectProperty();
                    break checkProperty;
                }
                if (oneToMany != null && !StringUtils.isEmpty(oneToMany.mappedBy())) {
                    parentObjectProperty = oneToMany.mappedBy();
                    break checkProperty;
                }
                if (manyToMany != null && !StringUtils.isEmpty(manyToMany.mappedBy())) {
                    parentObjectProperty = manyToMany.mappedBy();
                    break checkProperty;
                }
                if (StringUtils.isEmpty(parentObjectProperty)) {
                    throw new IllegalArgumentException("Unable to infer a parentObjectProperty for the @AdminPresentationAdornedTargetCollection annotated field("+field.getName()+"). If not using the mappedBy property of @OneToMany or @ManyToMany, please make sure to explicitly define the parentObjectProperty property");
                }
            }
        }

        String sortProperty;
        if (serverMetadata != null && adornedTargetCollectionMetadata.getSortProperty() == null) {
            sortProperty = ((AdornedTargetList) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).getSortField();
        } else {
            if (StringUtils.isEmpty(adornedTargetCollectionMetadata.getSortProperty())) {
                sortProperty = null;
            } else {
                sortProperty = adornedTargetCollectionMetadata.getSortProperty();
            }
        }

        String ceiling = null;
        checkCeiling: {
            if (oneToMany != null && oneToMany.targetEntity() != void.class) {
                ceiling = oneToMany.targetEntity().getName();
                break checkCeiling;
            }
            if (manyToMany != null && manyToMany.targetEntity() != void.class) {
                ceiling = manyToMany.targetEntity().getName();
                break checkCeiling;
            }
        }
        if (!StringUtils.isEmpty(ceiling)) {
            metadata.setCollectionCeilingEntity(ceiling);
        }
        metadata.setParentObjectClass(targetClass.getName());
        if (serverMetadata != null && adornedTargetCollectionMetadata.getMaintainedAdornedTargetFields() == null) {
            metadata.setMaintainedAdornedTargetFields(serverMetadata.getMaintainedAdornedTargetFields());
        } else {
            metadata.setMaintainedAdornedTargetFields(adornedTargetCollectionMetadata.getMaintainedAdornedTargetFields());
        }
        if (serverMetadata != null && adornedTargetCollectionMetadata.getGridVisibleFields() == null) {
            metadata.setGridVisibleFields(serverMetadata.getGridVisibleFields());
        } else {
            metadata.setGridVisibleFields(adornedTargetCollectionMetadata.getGridVisibleFields());
        }
        String parentObjectIdProperty;
        if (serverMetadata != null && adornedTargetCollectionMetadata.getParentObjectIdProperty()==null) {
            parentObjectIdProperty = ((AdornedTargetList) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).getLinkedIdProperty();
        } else {
            parentObjectIdProperty = adornedTargetCollectionMetadata.getParentObjectIdProperty();
        }
        String targetObjectProperty;
        if (serverMetadata != null && adornedTargetCollectionMetadata.getTargetObjectProperty()==null) {
            targetObjectProperty = ((AdornedTargetList) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).getTargetObjectPath();
        } else {
            targetObjectProperty = adornedTargetCollectionMetadata.getTargetObjectProperty();
        }
        String targetObjectIdProperty;
        if (serverMetadata != null && adornedTargetCollectionMetadata.getTargetObjectIdProperty()==null) {
            targetObjectIdProperty = ((AdornedTargetList) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).getTargetIdProperty();
        } else {
            targetObjectIdProperty = adornedTargetCollectionMetadata.getTargetObjectIdProperty();
        }
        Boolean isAscending;
        if (serverMetadata != null && adornedTargetCollectionMetadata.isSortAscending()==null) {
            isAscending = ((AdornedTargetList) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).getSortAscending();
        } else {
            isAscending = adornedTargetCollectionMetadata.isSortAscending();
        }

        AdornedTargetList adornedTargetList = new AdornedTargetList(field.getName(), parentObjectProperty, parentObjectIdProperty, targetObjectProperty, targetObjectIdProperty, ceiling, sortProperty, isAscending);
        persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.ADORNEDTARGETLIST, adornedTargetList);

        if (serverMetadata != null && adornedTargetCollectionMetadata.getExcluded() == null) {
            metadata.setExcluded(serverMetadata.getExcluded());
        } else {
            metadata.setExcluded(adornedTargetCollectionMetadata.getExcluded());
        }
        if (serverMetadata != null && adornedTargetCollectionMetadata.getFriendlyName() == null) {
            metadata.setFriendlyName(serverMetadata.getFriendlyName());
        } else {
            metadata.setFriendlyName(adornedTargetCollectionMetadata.getFriendlyName());
        }
        if (serverMetadata != null && adornedTargetCollectionMetadata.getSecurityLevel() == null) {
            metadata.setSecurityLevel(serverMetadata.getSecurityLevel());
        } else {
            metadata.setSecurityLevel(adornedTargetCollectionMetadata.getSecurityLevel());
        }
        if (serverMetadata != null && adornedTargetCollectionMetadata.getOrder() == null) {
            metadata.setOrder(serverMetadata.getOrder());
        } else {
            metadata.setOrder(adornedTargetCollectionMetadata.getOrder());
        }

        if (serverMetadata != null && adornedTargetCollectionMetadata.getTargetElementId() == null) {
            metadata.setTargetElementId(serverMetadata.getTargetElementId());
        } else {
            if (!StringUtils.isEmpty(adornedTargetCollectionMetadata.getTargetElementId())) {
                metadata.setTargetElementId(adornedTargetCollectionMetadata.getTargetElementId());
            }
        }

        if (serverMetadata != null && adornedTargetCollectionMetadata.getDataSourceName() == null) {
            metadata.setDataSourceName(serverMetadata.getDataSourceName());
        } else {
            if (!StringUtils.isEmpty(adornedTargetCollectionMetadata.getDataSourceName())) {
                metadata.setDataSourceName(adornedTargetCollectionMetadata.getDataSourceName());
            }
        }

        if (serverMetadata != null &&  adornedTargetCollectionMetadata.getCustomCriteria() == null) {
            metadata.setCustomCriteria(serverMetadata.getCustomCriteria());
        } else {
            metadata.setCustomCriteria(adornedTargetCollectionMetadata.getCustomCriteria());
        }

        if (serverMetadata != null && adornedTargetCollectionMetadata.isIgnoreAdornedProperties() == null) {
            metadata.setIgnoreAdornedProperties(serverMetadata.isIgnoreAdornedProperties());
        } else {
            metadata.setIgnoreAdornedProperties(adornedTargetCollectionMetadata.isIgnoreAdornedProperties());
        }

        attributes.put(field.getName(), metadata);
    }

    protected void buildCollectionMetadata(Class<?> targetClass, Map<String, FieldMetadata> attributes, Field field, BasicCollectionMetadataOverride collectionMetadata) {
        BasicCollectionMetadata serverMetadata = (BasicCollectionMetadata) attributes.get(field.getName());

        BasicCollectionMetadata metadata = new BasicCollectionMetadata();
        metadata.setCollectionFieldName(field.getName());
        if (serverMetadata != null && collectionMetadata.isMutable() == null) {
            metadata.setMutable(serverMetadata.isMutable());
        } else {
            metadata.setMutable(collectionMetadata.isMutable());
        }
        if (serverMetadata != null && collectionMetadata.getAddMethodType() == null) {
            metadata.setAddMethodType(serverMetadata.getAddMethodType());
        } else {
            metadata.setAddMethodType(collectionMetadata.getAddMethodType());
        }

        org.broadleafcommerce.openadmin.client.dto.OperationTypes dtoOperationTypes = new org.broadleafcommerce.openadmin.client.dto.OperationTypes();
        if (serverMetadata != null && collectionMetadata.getAddType() == null) {
            dtoOperationTypes.setAddType(serverMetadata.getPersistencePerspective().getOperationTypes().getAddType());
        } else {
            dtoOperationTypes.setAddType(collectionMetadata.getAddType());
        }
        if (serverMetadata != null && collectionMetadata.getRemoveType() == null) {
            dtoOperationTypes.setRemoveType(serverMetadata.getPersistencePerspective().getOperationTypes().getRemoveType());
        } else {
            dtoOperationTypes.setRemoveType(collectionMetadata.getRemoveType());
        }
        if (serverMetadata != null && collectionMetadata.getFetchType() == null) {
            dtoOperationTypes.setFetchType(serverMetadata.getPersistencePerspective().getOperationTypes().getFetchType());
        } else {
            dtoOperationTypes.setFetchType(collectionMetadata.getFetchType());
        }
        if (serverMetadata != null && collectionMetadata.getInspectType() == null) {
            dtoOperationTypes.setInspectType(serverMetadata.getPersistencePerspective().getOperationTypes().getInspectType());
        } else {
            dtoOperationTypes.setInspectType(collectionMetadata.getInspectType());
        }
        if (serverMetadata != null && collectionMetadata.getUpdateType() == null) {
            dtoOperationTypes.setUpdateType(serverMetadata.getPersistencePerspective().getOperationTypes().getUpdateType());
        } else {
            dtoOperationTypes.setInspectType(collectionMetadata.getUpdateType());
        }

        if (collectionMetadata.getAddMethodType()== AddMethodType.LOOKUP) {
            dtoOperationTypes.setRemoveType(OperationType.NONDESTRUCTIVEREMOVE);
        }

        //don't allow additional non-persistent properties or additional foreign keys for an advanced collection datasource - they don't make sense in this context
        PersistencePerspective persistencePerspective = new PersistencePerspective(dtoOperationTypes, new String[]{}, new ForeignKey[]{});
        if (serverMetadata != null && collectionMetadata.getConfigurationKey() == null) {
            serverMetadata.getPersistencePerspective().setConfigurationKey(serverMetadata.getPersistencePerspective().getConfigurationKey());
        } else {
            if (!StringUtils.isEmpty(collectionMetadata.getConfigurationKey())) {
                persistencePerspective.setConfigurationKey(collectionMetadata.getConfigurationKey());
            }
        }
        metadata.setPersistencePerspective(persistencePerspective);

        String foreignKeyName = null;
        //try to inspect the JPA annotation
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
        if (serverMetadata != null && collectionMetadata.getManyToField() == null) {
            foreignKeyName = ((ForeignKey) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY)).getManyToField();
        } else {
            checkForeignKeyName: {
                if (!StringUtils.isEmpty(collectionMetadata.getManyToField())) {
                    foreignKeyName = collectionMetadata.getManyToField();
                    break checkForeignKeyName;
                }
                if (oneToMany != null && !StringUtils.isEmpty(oneToMany.mappedBy())) {
                    foreignKeyName = oneToMany.mappedBy();
                    break checkForeignKeyName;
                }
                if (manyToMany != null && !StringUtils.isEmpty(manyToMany.mappedBy())) {
                    foreignKeyName = manyToMany.mappedBy();
                    break checkForeignKeyName;
                }
                if (StringUtils.isEmpty(foreignKeyName)) {
                    throw new IllegalArgumentException("Unable to infer a ManyToOne field name for the @AdminPresentationCollection annotated field("+field.getName()+"). If not using the mappedBy property of @OneToMany or @ManyToMany, please make sure to explicitly define the manyToField property");
                }
            }
        }
        ForeignKey foreignKey = new ForeignKey(foreignKeyName, targetClass.getName(), null, ForeignKeyRestrictionType.ID_EQ);
        persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, foreignKey);

        String ceiling = null;
        checkCeiling: {
            if (oneToMany != null && oneToMany.targetEntity() != void.class) {
                ceiling = oneToMany.targetEntity().getName();
                break checkCeiling;
            }
            if (manyToMany != null && manyToMany.targetEntity() != void.class) {
                ceiling = manyToMany.targetEntity().getName();
                break checkCeiling;
            }
        }
        if (!StringUtils.isEmpty(ceiling)) {
            metadata.setCollectionCeilingEntity(ceiling);
        }

        if (serverMetadata != null && collectionMetadata.getExcluded() == null) {
            metadata.setExcluded(serverMetadata.getExcluded());
        } else {
            metadata.setExcluded(collectionMetadata.getExcluded());
        }
        if (serverMetadata != null && collectionMetadata.getFriendlyName() == null) {
            metadata.setFriendlyName(serverMetadata.getFriendlyName());
        } else {
            metadata.setFriendlyName(collectionMetadata.getFriendlyName());
        }
        if (serverMetadata != null && collectionMetadata.getSecurityLevel() == null) {
            metadata.setSecurityLevel(serverMetadata.getSecurityLevel());
        } else {
            metadata.setSecurityLevel(collectionMetadata.getSecurityLevel());
        }
        if (serverMetadata != null && collectionMetadata.getOrder() == null) {
            metadata.setOrder(serverMetadata.getOrder());
        } else {
            metadata.setOrder(collectionMetadata.getOrder());
        }

        if (serverMetadata != null && collectionMetadata.getTargetElementId() == null) {
            metadata.setTargetElementId(serverMetadata.getTargetElementId());
        } else {
            if (!StringUtils.isEmpty(collectionMetadata.getTargetElementId())) {
                metadata.setTargetElementId(collectionMetadata.getTargetElementId());
            }
        }

        if (serverMetadata != null && collectionMetadata.getDataSourceName() == null) {
            metadata.setDataSourceName(serverMetadata.getDataSourceName());
        } else {
            if (!StringUtils.isEmpty(collectionMetadata.getDataSourceName())) {
                metadata.setDataSourceName(collectionMetadata.getDataSourceName());
            }
        }

        if (serverMetadata != null &&  collectionMetadata.getCustomCriteria() == null) {
            metadata.setCustomCriteria(serverMetadata.getCustomCriteria());
        } else {
            metadata.setCustomCriteria(collectionMetadata.getCustomCriteria());
        }

        attributes.put(field.getName(), metadata);
    }

    protected void buildBasicMetadata(Map<String, FieldMetadata> attributes, Field field, AdminPresentation annot) {
        BasicFieldMetadata metadata = new BasicFieldMetadata();
        metadata.setName(field.getName());
        metadata.setFriendlyName(annot.friendlyName());
        metadata.setSecurityLevel(annot.securityLevel());
        metadata.setVisibility(annot.visibility());
        metadata.setOrder(annot.order());
        metadata.setExplicitFieldType(annot.fieldType());
        metadata.setGroup(annot.group());
        metadata.setGroupOrder(annot.groupOrder());
        metadata.setGroupCollapsed(annot.groupCollapsed());
        metadata.setLargeEntry(annot.largeEntry());
        metadata.setProminent(annot.prominent());
        metadata.setColumnWidth(annot.columnWidth());
        metadata.setBroadleafEnumeration(annot.broadleafEnumeration());
        metadata.setReadOnly(annot.readOnly());
        metadata.setExcluded(annot.excluded());
        metadata.setTooltip(annot.tooltip());
        metadata.setHelpText(annot.helpText());
        metadata.setHint(annot.hint());
        metadata.setRequiredOverride(annot.requiredOverride()== RequiredOverride.IGNORED?null:annot.requiredOverride()==RequiredOverride.REQUIRED);
        if (annot.validationConfigurations().length != 0) {
            ValidationConfiguration[] configurations = annot.validationConfigurations();
            for (ValidationConfiguration configuration : configurations) {
                ConfigurationItem[] items = configuration.configurationItems();
                Map<String, String> itemMap = new HashMap<String, String>();
                for (ConfigurationItem item : items) {
                    itemMap.put(item.itemName(), item.itemValue());
                }
                metadata.getValidationConfigurations().put(configuration.validationImplementation(), itemMap);
            }
        }
        attributes.put(field.getName(), metadata);
    }

    public Map<String, FieldMetadata> getPropertiesForPrimitiveClass(
		String propertyName,
		String friendlyPropertyName,
		Class<?> targetClass,
		Class<?> parentClass,
		MergedPropertyType mergedPropertyType
	) {
		Map<String, FieldMetadata> fields = new HashMap<String, FieldMetadata>();
        BasicFieldMetadata presentationAttribute = new BasicFieldMetadata();
		presentationAttribute.setFriendlyName(friendlyPropertyName);
		if (String.class.isAssignableFrom(targetClass)) {
			presentationAttribute.setExplicitFieldType(SupportedFieldType.STRING);
			presentationAttribute.setVisibility(VisibilityEnum.VISIBLE_ALL);
			fields.put(propertyName, getFieldMetadata("", propertyName, null, SupportedFieldType.STRING, null, parentClass, presentationAttribute, mergedPropertyType));
		} else if (Boolean.class.isAssignableFrom(targetClass)) {
			presentationAttribute.setExplicitFieldType(SupportedFieldType.BOOLEAN);
			presentationAttribute.setVisibility(VisibilityEnum.VISIBLE_ALL);
			fields.put(propertyName, getFieldMetadata("", propertyName, null, SupportedFieldType.BOOLEAN, null, parentClass, presentationAttribute, mergedPropertyType));
		} else if (Date.class.isAssignableFrom(targetClass)) {
			presentationAttribute.setExplicitFieldType(SupportedFieldType.DATE);
			presentationAttribute.setVisibility(VisibilityEnum.VISIBLE_ALL);
			fields.put(propertyName, getFieldMetadata("", propertyName, null, SupportedFieldType.DATE, null, parentClass, presentationAttribute, mergedPropertyType));
		} else if (Money.class.isAssignableFrom(targetClass)) {
			presentationAttribute.setExplicitFieldType(SupportedFieldType.MONEY);
			presentationAttribute.setVisibility(VisibilityEnum.VISIBLE_ALL);
			fields.put(propertyName, getFieldMetadata("", propertyName, null, SupportedFieldType.MONEY, null, parentClass, presentationAttribute, mergedPropertyType));
		} else if (
				Byte.class.isAssignableFrom(targetClass) ||
				Integer.class.isAssignableFrom(targetClass) ||
				Long.class.isAssignableFrom(targetClass) ||
				Short.class.isAssignableFrom(targetClass)
			) {
			presentationAttribute.setExplicitFieldType(SupportedFieldType.INTEGER);
			presentationAttribute.setVisibility(VisibilityEnum.VISIBLE_ALL);
			fields.put(propertyName, getFieldMetadata("", propertyName, null, SupportedFieldType.INTEGER, null, parentClass, presentationAttribute, mergedPropertyType));
		} else if (
				Double.class.isAssignableFrom(targetClass) ||
				BigDecimal.class.isAssignableFrom(targetClass)
			) {
			presentationAttribute.setExplicitFieldType(SupportedFieldType.DECIMAL);
			presentationAttribute.setVisibility(VisibilityEnum.VISIBLE_ALL);
			fields.put(propertyName, getFieldMetadata("", propertyName, null, SupportedFieldType.DECIMAL, null, parentClass, presentationAttribute, mergedPropertyType));
		}
		((BasicFieldMetadata) fields.get(propertyName)).setLength(255);
        ((BasicFieldMetadata) fields.get(propertyName)).setForeignKeyCollection(false);
        ((BasicFieldMetadata) fields.get(propertyName)).setRequired(true);
        ((BasicFieldMetadata) fields.get(propertyName)).setUnique(true);
        ((BasicFieldMetadata) fields.get(propertyName)).setScale(100);
        ((BasicFieldMetadata) fields.get(propertyName)).setPrecision(100);

		return fields;
	}

    protected SessionFactory getSessionFactory() {
        return ((HibernateEntityManager) standardEntityManager).getSession().getSessionFactory();
    }

    public Map<String, Class<?>> getIdMetadata(Class<?> entityClass) {
        Map response = new HashMap();
        SessionFactory sessionFactory = getSessionFactory();
        ClassMetadata metadata = sessionFactory.getClassMetadata(entityClass);
        String idProperty = metadata.getIdentifierPropertyName();
        response.put("name", idProperty);
        Type idType = metadata.getIdentifierType();
        response.put("type", idType);

        return response;
    }

    public List<String> getPropertyNames(Class<?> entityClass) {
        ClassMetadata metadata = getSessionFactory().getClassMetadata(entityClass);
        List<String> propertyNames = new ArrayList<String>();
        for (String propertyName : metadata.getPropertyNames()) {
			propertyNames.add(propertyName);
		}
        return propertyNames;
    }

    public List<Type> getPropertyTypes(Class<?> entityClass) {
        ClassMetadata metadata = getSessionFactory().getClassMetadata(entityClass);
        List<Type> propertyTypes = new ArrayList<Type>();
        for (Type propertyType : metadata.getPropertyTypes()) {
			propertyTypes.add(propertyType);
		}
        return propertyTypes;
    }

	@SuppressWarnings("unchecked")
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
        Boolean isParentExcluded
	) {
		Map<String, FieldMetadata> presentationAttributes = getFieldPresentationAttributes(targetClass);
        if (isParentExcluded) {
            for (String key : presentationAttributes.keySet()) {
                presentationAttributes.get(key).setExcluded(true);
            }
        }

        Map idMetadata = getIdMetadata(targetClass);
		Map<String, FieldMetadata> fields = new HashMap<String, FieldMetadata>();
		String idProperty = (String) idMetadata.get("name");
		List<String> propertyNames = getPropertyNames(targetClass);
		propertyNames.add(idProperty);
		Type idType = (Type) idMetadata.get("type");
        List<Type> propertyTypes = getPropertyTypes(targetClass);
		propertyTypes.add(idType);

		PersistentClass persistentClass = getPersistentClass(targetClass.getName());
		Iterator<Property> testIter = persistentClass.getPropertyIterator();
        List<Property> propertyList = new ArrayList<Property>();

		//check the properties for problems
		while(testIter.hasNext()) {
			Property property = testIter.next();
			if (property.getName().indexOf(".") >= 0) {
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
            isParentExcluded
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
                        fields.put(additionalNonPersistentProperty, getFieldMetadata(prefix, additionalNonPersistentProperty, propertyList, SupportedFieldType.STRING, null, targetClass, presentationAttribute, mergedPropertyType));
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
        Boolean isParentExcluded
	) {
		int j = 0;
		for (String propertyName : propertyNames) {
			Type type = propertyTypes.get(j);
			boolean isPropertyForeignKey = testForeignProperty(foreignField, prefix, propertyName);
			int additionalForeignKeyIndexPosition = findAdditionalForeignKeyIndex(additionalForeignFields, prefix, propertyName);
			j++;
            Field myField = FieldManager.getSingleField(targetClass, propertyName);
			if (
					!type.isAnyType() && !type.isCollectionType() ||
					isPropertyForeignKey ||
					additionalForeignKeyIndexPosition >= 0 ||
					presentationAttributes.containsKey(propertyName)
			) {
                if (myField != null && (
                        myField.getAnnotation(AdminPresentationCollection.class) != null ||
                        myField.getAnnotation(AdminPresentationAdornedTargetCollection.class) != null ||
                        myField.getAnnotation(AdminPresentationMap.class) != null)
                ) {
                    //only a collection that is a direct member of the ceiling entity may be included
                    //if (StringUtils.isEmpty(prefix)) {
                        CollectionMetadata fieldMetadata = (CollectionMetadata) presentationAttributes.get(propertyName);
                        if (StringUtils.isEmpty(fieldMetadata.getCollectionCeilingEntity())) {
                            fieldMetadata.setCollectionCeilingEntity(type.getReturnedClass().getName());
                        }
                        fieldMetadata.setInheritedFromType(targetClass.getName());
                        fieldMetadata.setAvailableToTypes(new String[]{targetClass.getName()});
                        fields.put(propertyName, fieldMetadata);
                        fieldMetadata.accept(new MetadataVisitorAdapter() {
                            @Override
                            public void visit(AdornedTargetCollectionMetadata metadata) {
                                AdornedTargetList targetList = ((AdornedTargetList) metadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST));
                                targetList.setAdornedTargetEntityClassname(metadata.getCollectionCeilingEntity());
                            }

                            @Override
                            public void visit(BasicCollectionMetadata metadata) {
                                //do nothing
                            }

                            @Override
                            public void visit(MapMetadata metadata) {
                                //do nothing
                            }
                        });
                    //}
                } else {
                    FieldMetadata presentationAttribute = presentationAttributes.get(propertyName);
                    Boolean amIExcluded = isParentExcluded || !testPropertyInclusion(presentationAttribute);
                    Boolean includeField = testPropertyRecursion(prefix, parentClasses, propertyName, targetClass, ceilingEntityFullyQualifiedClassname);

                    SupportedFieldType explicitType = null;
                    if (presentationAttribute != null && presentationAttribute instanceof BasicFieldMetadata) {
                        explicitType = ((BasicFieldMetadata) presentationAttribute).getExplicitFieldType();
                    }
                    Class<?> returnedClass = type.getReturnedClass();
                    checkProp: {
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
                                prefix
                            );
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
                                amIExcluded
                            );
                            break checkProp;
                        }
                    }
                    //Don't include this property if it failed manyToOne inclusion and is not a specified foreign key
                    if (includeField || isPropertyForeignKey || additionalForeignKeyIndexPosition >= 0) {
                        buildProperty(
                            targetClass,
                            foreignField,
                            additionalForeignFields,
                            mergedPropertyType,
                            componentProperties,
                            fields,
                            idProperty,
                            prefix,
                            propertyName,
                            type,
                            isPropertyForeignKey,
                            additionalForeignKeyIndexPosition,
                            presentationAttribute,
                            explicitType,
                            returnedClass
                        );
                    }
                }
			}
		}
	}

	protected void buildProperty(
		Class<?> targetClass, 
		ForeignKey foreignField, 
		ForeignKey[] additionalForeignFields, 
		MergedPropertyType mergedPropertyType, 
		List<Property> componentProperties,
		Map<String, FieldMetadata> fields, 
		String idProperty, 
		String prefix,
		String propertyName, 
		Type type, 
		boolean isPropertyForeignKey, 
		int additionalForeignKeyIndexPosition, 
		FieldMetadata presentationAttribute,
		SupportedFieldType explicitType, 
		Class<?> returnedClass
	) {
        if (
            explicitType != null &&
            explicitType != SupportedFieldType.UNKNOWN &&
            explicitType != SupportedFieldType.BOOLEAN &&
            explicitType != SupportedFieldType.INTEGER &&
            explicitType != SupportedFieldType.DATE &&
            explicitType != SupportedFieldType.STRING &&
            explicitType != SupportedFieldType.MONEY &&
            explicitType != SupportedFieldType.DECIMAL &&
            explicitType != SupportedFieldType.FOREIGN_KEY &&
            explicitType != SupportedFieldType.ADDITIONAL_FOREIGN_KEY
        ) {
            fields.put(propertyName, getFieldMetadata(prefix, propertyName, componentProperties, explicitType, type, targetClass, presentationAttribute, mergedPropertyType));
        } else if (
            explicitType != null &&
            explicitType == SupportedFieldType.BOOLEAN
            ||
            returnedClass.equals(Boolean.class) ||
            returnedClass.equals(Character.class)
        ) {
			fields.put(propertyName, getFieldMetadata(prefix, propertyName, componentProperties, SupportedFieldType.BOOLEAN, type, targetClass, presentationAttribute, mergedPropertyType));
		} else if (
            explicitType != null &&
            explicitType == SupportedFieldType.INTEGER
            ||
            returnedClass.equals(Byte.class) ||
            returnedClass.equals(Short.class) ||
            returnedClass.equals(Integer.class) ||
            returnedClass.equals(Long.class)
		) {
			if (propertyName.equals(idProperty)) {
				fields.put(propertyName, getFieldMetadata(prefix, propertyName, componentProperties, SupportedFieldType.ID, SupportedFieldType.INTEGER, type, targetClass, presentationAttribute, mergedPropertyType));
			} else {
				fields.put(propertyName, getFieldMetadata(prefix, propertyName, componentProperties, SupportedFieldType.INTEGER, type, targetClass, presentationAttribute, mergedPropertyType));
			}
		} else if (
            explicitType != null &&
            explicitType == SupportedFieldType.DATE
            ||
            returnedClass.equals(Calendar.class) ||
            returnedClass.equals(Date.class) ||
            returnedClass.equals(Timestamp.class)
		) {
			fields.put(propertyName, getFieldMetadata(prefix, propertyName, componentProperties, SupportedFieldType.DATE, type, targetClass, presentationAttribute, mergedPropertyType));
		} else if (
            explicitType != null &&
            explicitType == SupportedFieldType.STRING
            ||
            returnedClass.equals(String.class)
		) {
			if (propertyName.equals(idProperty)) {
				fields.put(propertyName, getFieldMetadata(prefix, propertyName, componentProperties, SupportedFieldType.ID, SupportedFieldType.STRING, type, targetClass, presentationAttribute, mergedPropertyType));
			} else {
				fields.put(propertyName, getFieldMetadata(prefix, propertyName, componentProperties, SupportedFieldType.STRING, type, targetClass, presentationAttribute, mergedPropertyType));
			}
		} else if (
            explicitType != null &&
            explicitType == SupportedFieldType.MONEY
            ||
            returnedClass.equals(Money.class)
        ) {
			fields.put(propertyName, getFieldMetadata(prefix, propertyName, componentProperties, SupportedFieldType.MONEY, type, targetClass, presentationAttribute, mergedPropertyType));
		} else if (
            explicitType != null &&
            explicitType == SupportedFieldType.DECIMAL
            ||
            returnedClass.equals(Double.class) ||
            returnedClass.equals(BigDecimal.class)
		) {
			fields.put(propertyName, getFieldMetadata(prefix, propertyName, componentProperties, SupportedFieldType.DECIMAL, type, targetClass, presentationAttribute, mergedPropertyType));
		} else if (
            explicitType != null &&
            explicitType == SupportedFieldType.FOREIGN_KEY
            ||
            foreignField != null &&
            isPropertyForeignKey
        ) {
			ClassMetadata foreignMetadata;
            try {
                foreignMetadata = getSessionFactory().getClassMetadata(Class.forName(foreignField.getForeignKeyClass()));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Class<?> foreignResponseType = foreignMetadata.getIdentifierType().getReturnedClass();
			if (foreignResponseType.equals(String.class)) {
				fields.put(propertyName, getFieldMetadata(prefix, propertyName, componentProperties, SupportedFieldType.FOREIGN_KEY, SupportedFieldType.STRING, type, targetClass, presentationAttribute, mergedPropertyType));
			} else {
				fields.put(propertyName, getFieldMetadata(prefix, propertyName, componentProperties, SupportedFieldType.FOREIGN_KEY, SupportedFieldType.INTEGER, type, targetClass, presentationAttribute, mergedPropertyType));
			}
			((BasicFieldMetadata) fields.get(propertyName)).setForeignKeyProperty(foreignMetadata.getIdentifierPropertyName());
            ((BasicFieldMetadata) fields.get(propertyName)).setForeignKeyClass(foreignField.getForeignKeyClass());
            ((BasicFieldMetadata) fields.get(propertyName)).setForeignKeyDisplayValueProperty(foreignField.getDisplayValueProperty());
		} else if (
            explicitType != null &&
            explicitType == SupportedFieldType.ADDITIONAL_FOREIGN_KEY
            ||
            additionalForeignFields != null &&
            additionalForeignKeyIndexPosition >= 0
        ) {
			ClassMetadata foreignMetadata;
            try {
                foreignMetadata = getSessionFactory().getClassMetadata(Class.forName(additionalForeignFields[additionalForeignKeyIndexPosition].getForeignKeyClass()));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Class<?> foreignResponseType = foreignMetadata.getIdentifierType().getReturnedClass();
			if (foreignResponseType.equals(String.class)) {
				fields.put(propertyName, getFieldMetadata(prefix, propertyName, componentProperties, SupportedFieldType.ADDITIONAL_FOREIGN_KEY, SupportedFieldType.STRING, type, targetClass, presentationAttribute, mergedPropertyType));
			} else {
				fields.put(propertyName, getFieldMetadata(prefix, propertyName, componentProperties, SupportedFieldType.ADDITIONAL_FOREIGN_KEY, SupportedFieldType.INTEGER, type, targetClass, presentationAttribute, mergedPropertyType));
			}
            ((BasicFieldMetadata) fields.get(propertyName)).setForeignKeyProperty(foreignMetadata.getIdentifierPropertyName());
            ((BasicFieldMetadata) fields.get(propertyName)).setForeignKeyClass(additionalForeignFields[additionalForeignKeyIndexPosition].getForeignKeyClass());
            ((BasicFieldMetadata) fields.get(propertyName)).setForeignKeyDisplayValueProperty(additionalForeignFields[additionalForeignKeyIndexPosition].getDisplayValueProperty());
		}
		//return type not supported - just skip this property
	}

	protected Boolean testPropertyRecursion(String prefix, List<Class<?>> parentClasses, String propertyName, Class<?> targetClass, String ceilingEntityFullyQualifiedClassname) {
        Boolean includeField = true;
        if (!StringUtils.isEmpty(prefix)) {
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
            if (testField != null) {
                Class<?> testType = testField.getType();
                for (Class<?> parentClass : parentClasses) {
                    if (parentClass.isAssignableFrom(testType) || testType.isAssignableFrom(parentClass)) {
                        includeField = false;
                        break;
                    }
                }
                if (includeField && (targetClass.isAssignableFrom(testType) || testType.isAssignableFrom(targetClass))) {
                    includeField = false;
                }
            }
        }
		return includeField;
	}

    public Boolean testPropertyInclusion(FieldMetadata presentationAttribute) {
        if (presentationAttribute != null && presentationAttribute.getExcluded()) {
            return false;
        }
		return true;
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
        Boolean isParentExcluded
	) {
		Class<?>[] polymorphicEntities = getAllPolymorphicEntitiesFromCeiling(returnedClass);
        List<Class<?>> clonedParentClasses = new ArrayList<Class<?>>();
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
            isParentExcluded
        );
		for (FieldMetadata newMetadata : newFields.values()) {
			newMetadata.setInheritedFromType(targetClass.getName());
			newMetadata.setAvailableToTypes(new String[]{targetClass.getName()});
		}
		Map<String, FieldMetadata> convertedFields = new HashMap<String, FieldMetadata>(newFields.size());
		for (Map.Entry<String, FieldMetadata> key : newFields.entrySet()) {
			convertedFields.put(propertyName + '.' + key.getKey(), key.getValue());
		}
		fields.putAll(convertedFields);
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
        String prefix
	) {
		String[] componentProperties = ((ComponentType) type).getPropertyNames();
		List<String> componentPropertyNames = Arrays.asList(componentProperties);
		Type[] componentTypes = ((ComponentType) type).getSubtypes();
		List<Type> componentPropertyTypes = Arrays.asList(componentTypes);
		Map<String, FieldMetadata> componentPresentationAttributes = getFieldPresentationAttributes(returnedClass);
        if (isParentExcluded) {
            for (String key : componentPresentationAttributes.keySet()) {
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
		Iterator<Property> componentPropertyIterator = ((org.hibernate.mapping.Component) property.getValue()).getPropertyIterator();
        List<Property> componentPropertyList = new ArrayList<Property>();
        while(componentPropertyIterator.hasNext()) {
            componentPropertyList.add(componentPropertyIterator.next());
        }
		Map<String, FieldMetadata> newFields = new HashMap<String, FieldMetadata>();
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
            isParentExcluded
		);
		Map<String, FieldMetadata> convertedFields = new HashMap<String, FieldMetadata>();
		for (String key : newFields.keySet()) {
			convertedFields.put(propertyName + "." + key, newFields.get(key));
		}
		fields.putAll(convertedFields);
	}

	@Override
	public EntityManager getStandardEntityManager() {
		return standardEntityManager;
	}

	public void setStandardEntityManager(EntityManager entityManager) {
		this.standardEntityManager = entityManager;
	}

	public EJB3ConfigurationDao getEjb3ConfigurationDao() {
		return ejb3ConfigurationDao;
	}

	public void setEjb3ConfigurationDao(EJB3ConfigurationDao ejb3ConfigurationDao) {
		this.ejb3ConfigurationDao = ejb3ConfigurationDao;
	}

    public FieldManager getFieldManager() {
        return new FieldManager(entityConfiguration, this);
    }

    public EntityConfiguration getEntityConfiguration() {
        return entityConfiguration;
    }

    public void setEntityConfiguration(EntityConfiguration entityConfiguration) {
        this.entityConfiguration = entityConfiguration;
    }

    public Map<String, Map<String, Map<String, BasicFieldMetadataOverride>>> getFieldMetadataOverrides() {
        return fieldMetadataOverrides;
    }

    public void setFieldMetadataOverrides(Map<String, Map<String, Map<String, BasicFieldMetadataOverride>>> fieldMetadataOverrides) {
        this.fieldMetadataOverrides = fieldMetadataOverrides;
    }

    public Map<String, Map<String, Map<String, MapMetadataOverride>>> getMapMetadataOverrides() {
        return mapMetadataOverrides;
    }

    public void setMapMetadataOverrides(Map<String, Map<String, Map<String, MapMetadataOverride>>> mapMetadataOverrides) {
        this.mapMetadataOverrides = mapMetadataOverrides;
    }

    public Map<String, Map<String, Map<String, AdornedTargetCollectionMetadataOverride>>> getAdornedTargetCollectionMetadataOverrides() {
        return adornedTargetCollectionMetadataOverrides;
    }

    public void setAdornedTargetCollectionMetadataOverrides(Map<String, Map<String, Map<String, AdornedTargetCollectionMetadataOverride>>> adornedTargetCollectionMetadataOverrides) {
        this.adornedTargetCollectionMetadataOverrides = adornedTargetCollectionMetadataOverrides;
    }

    public Map<String, Map<String, Map<String, BasicCollectionMetadataOverride>>> getCollectionMetadataOverrides() {
        return collectionMetadataOverrides;
    }

    public void setCollectionMetadataOverrides(Map<String, Map<String, Map<String, BasicCollectionMetadataOverride>>> collectionMetadataOverrides) {
        this.collectionMetadataOverrides = collectionMetadataOverrides;
    }
}
