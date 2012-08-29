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
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationOverride;
import org.broadleafcommerce.common.presentation.AdminPresentationOverrides;
import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.common.presentation.ForeignKeyRestrictionType;
import org.broadleafcommerce.common.presentation.OperationTypes;
import org.broadleafcommerce.common.presentation.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ClassTree;
import org.broadleafcommerce.openadmin.client.dto.CollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitor;
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
    protected Map<String, Map<String, Map<String, FieldMetadata>>> fieldMetadataOverrides;

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
	
	public Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        Class<?>[] entityClasses = getAllPolymorphicEntitiesFromCeiling(Class.forName(entityName));
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
            Class<?> targetClass = Class.forName(entityName);
            Map<String, FieldMetadata> attributesMap = getFieldPresentationAttributes(targetClass);

            for (String property : attributesMap.keySet()) {
                FieldMetadata presentationAttributes = attributesMap.get(property);
                if (!presentationAttributes.getExcluded()) {
                    Field field = getFieldManager().getSingleField(targetClass, property);
                    if (!Modifier.isStatic(field.getModifiers())) {
                        if (field.getAnnotation(AdminPresentationCollection.class) == null) {
                            buildProperty(targetClass, null, new ForeignKey[]{}, MergedPropertyType.PRIMARY, null, mergedProperties, null, "", property, null, false, 0, presentationAttributes, ((BasicFieldMetadata) presentationAttributes).getExplicitFieldType(), field.getType());
                        } else {
                            CollectionMetadata fieldMetadata = (CollectionMetadata) presentationAttributes;
                            if (StringUtils.isEmpty(fieldMetadata.getCollectionCeilingEntity())) {
                                //TODO this only handles list structure
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
	) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
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
    ) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Map<String, FieldMetadata> mergedProperties = new HashMap<String, FieldMetadata>();
        Boolean classAnnotatedPopulateManyToOneFields = null;

        Map<String, AdminPresentationOverride> presentationOverrides = new HashMap<String, AdminPresentationOverride>();
		//go in reverse order since I want the lowest subclass override to come last to guarantee that it takes effect
		for (int i = entities.length-1;i >= 0; i--) {
			AdminPresentationOverrides myOverrides = entities[i].getAnnotation(AdminPresentationOverrides.class);
            if (myOverrides != null) {
                for (AdminPresentationOverride myOverride : myOverrides.value()) {
                    presentationOverrides.put(myOverride.name(), myOverride);
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

        applyAdminPresentationOverrides(prefix, isParentExcluded, mergedProperties, presentationOverrides);
        applyMetadataOverrides(ceilingEntityFullyQualifiedClassname, configurationKey, prefix, isParentExcluded, mergedProperties);
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

    protected void applyMetadataOverrides(String ceilingEntityFullyQualifiedClassname, String configurationKey, String prefix, final Boolean isParentExcluded, Map<String, FieldMetadata> mergedProperties) throws InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        if (fieldMetadataOverrides != null && configurationKey != null) {
            Map<String, Map<String, FieldMetadata>> configuredOverrides = fieldMetadataOverrides.get(configurationKey);
            if (configuredOverrides != null) {
                Map<String, FieldMetadata> entityOverrides = configuredOverrides.get(ceilingEntityFullyQualifiedClassname);
                if (entityOverrides != null) {
                    for (String propertyName : entityOverrides.keySet()) {
                        final FieldMetadata localMetadata = entityOverrides.get(propertyName);
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
                                FieldMetadata serverMetadata = mergedProperties.get(key);
                                serverMetadata.accept(new MetadataVisitor() {
                                    @Override
                                    public void visit(BasicFieldMetadata serverMetadata) {
                                        BasicFieldMetadata override = (BasicFieldMetadata) localMetadata;
                                        if (override.getFriendlyName() != null) {
                                            serverMetadata.setFriendlyName(override.getFriendlyName());
                                        }
                                        if (override.getSecurityLevel() != null) {
                                            serverMetadata.setSecurityLevel(override.getSecurityLevel());
                                        }
                                        if (override.getVisibility() != null) {
                                            serverMetadata.setVisibility(override.getVisibility());
                                        }
                                        if (override.getOrder() != null) {
                                            serverMetadata.setOrder(override.getOrder());
                                        }
                                        if (override.getExplicitFieldType() != null) {
                                            serverMetadata.setExplicitFieldType(override.getExplicitFieldType());
                                            serverMetadata.setFieldType(override.getExplicitFieldType());
                                        }
                                        if (override.getGroup() != null) {
                                            serverMetadata.setGroup(override.getGroup());
                                        }
                                        if (override.getGroupCollapsed() != null) {
                                            serverMetadata.setGroupCollapsed(override.getGroupCollapsed());
                                        }
                                        if (override.getGroupOrder() != null) {
                                            serverMetadata.setGroupOrder(override.getGroupOrder());
                                        }
                                        if (override.isLargeEntry() != null) {
                                            serverMetadata.setLargeEntry(override.isLargeEntry());
                                        }
                                        if (override.isProminent() != null) {
                                            serverMetadata.setProminent(override.isProminent());
                                        }
                                        if (override.getColumnWidth() != null) {
                                            serverMetadata.setColumnWidth(override.getColumnWidth());
                                        }
                                        if (!StringUtils.isEmpty(override.getBroadleafEnumeration()) && !override.getBroadleafEnumeration().equals(serverMetadata.getBroadleafEnumeration())) {
                                            serverMetadata.setBroadleafEnumeration(override.getBroadleafEnumeration());
                                            try {
                                                setupBroadleafEnumeration(override.getBroadleafEnumeration(), serverMetadata);
                                            } catch (Exception e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                        if (override.getReadOnly() != null) {
                                            serverMetadata.setReadOnly(override.getReadOnly());
                                        }
                                        if (override.getExcluded() != null) {
                                            serverMetadata.setExcluded(override.getExcluded());
                                        }
                                        if (isParentExcluded) {
                                            serverMetadata.setExcluded(true);
                                        }
                                        if (override.getTooltip() != null) {
                                            serverMetadata.setTooltip(override.getTooltip());
                                        }
                                        if (override.getHelpText() != null) {
                                            serverMetadata.setHelpText(override.getHelpText());
                                        }
                                        if (override.getHint() != null) {
                                            serverMetadata.setHint(override.getHint());
                                        }
                                        if (override.getRequiredOverride() != null) {
                                            serverMetadata.setRequiredOverride(override.getRequiredOverride());
                                        }
                                        if (override.getValidationConfigurations() != null) {
                                            serverMetadata.setValidationConfigurations(override.getValidationConfigurations());
                                        }
                                        if (override.getLength() != null) {
                                            serverMetadata.setLength(override.getLength());
                                        }
                                        if (override.getUnique() != null) {
                                            serverMetadata.setUnique(override.getUnique());
                                        }
                                        if (override.getScale() != null) {
                                            serverMetadata.setScale(override.getScale());
                                        }
                                        if (override.getPrecision() != null) {
                                            serverMetadata.setPrecision(override.getPrecision());
                                        }
                                    }

                                    @Override
                                    public void visit(CollectionMetadata metadata) {
                                        //TODO handle collection case
                                    }
                                });
                                
                            }
                        }
                    }
                }
            }
        }
    }

    protected void applyAdminPresentationOverrides(String prefix, final Boolean isParentExcluded, Map<String, FieldMetadata> mergedProperties, Map<String, AdminPresentationOverride> presentationOverrides) throws InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        for (String propertyName : presentationOverrides.keySet()) {
            final AdminPresentation annot = presentationOverrides.get(propertyName).value();
            for (String key : mergedProperties.keySet()) {
                String testKey = prefix + key;
                if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && annot.excluded()) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    metadata.setExcluded(true);
                    continue;
                }
                if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && !annot.excluded()) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    if (!isParentExcluded) {
                        metadata.setExcluded(false);
                    }
                }
                if (key.equals(propertyName)) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    metadata.accept(new MetadataVisitor() {
                        @Override
                        public void visit(BasicFieldMetadata metadata) {
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

                        @Override
                        public void visit(CollectionMetadata metadata) {
                            //TODO handle collection case
                        }
                    });

                    
                }
            }
        }
    }

    protected String pad(String s, int length, char pad) {
        StringBuffer buffer = new StringBuffer(s);
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
	) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
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
                                if (Class.forName(metadata.getInheritedFromType()).isAssignableFrom(clazz2)) {
                                    String[] both = (String[]) ArrayUtils.addAll(metadata.getAvailableToTypes(), new String[]{clazz2.getName()});
                                    metadata.setAvailableToTypes(both);
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
	) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return getFieldMetadata(prefix, propertyName, componentProperties, type, null, entityType, targetClass, presentationAttribute, mergedPropertyType);
	}

	protected FieldMetadata getFieldMetadata(
		String prefix, 
		String propertyName, 
		List<Property> componentProperties,
		SupportedFieldType type, 
		SupportedFieldType secondaryType, 
		Type entityType, 
		Class<?> targetClass, 
		FieldMetadata presentationAttribute,
		MergedPropertyType mergedPropertyType
	) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Field targetField = FieldManager.getSingleField(targetClass, propertyName);
        if (targetField == null) {
            throw new IllegalArgumentException("Unable to find property("+propertyName+") in class("+targetClass.getName()+")");
        }
        presentationAttribute.setInheritedFromType(targetClass.getName());
        presentationAttribute.setAvailableToTypes(new String[]{targetClass.getName()});
        if (targetField.getAnnotation(AdminPresentation.class)!=null) {
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
                setupBroadleafEnumeration(fieldMetadata.getBroadleafEnumeration(), fieldMetadata);
            }
        }
		
		return presentationAttribute;
	}

    protected void setupBroadleafEnumeration(String broadleafEnumerationClass, BasicFieldMetadata fieldMetadata) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
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
			if (annot != null) {
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
                metadata.setRequiredOverride(annot.requiredOverride()==RequiredOverride.IGNORED?null:annot.requiredOverride()==RequiredOverride.REQUIRED);
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
            } else if (annotColl != null) {
                CollectionMetadata metadata = new CollectionMetadata();
                metadata.setAddType(annotColl.addType());

                OperationTypes operationTypes = annotColl.operationTypes();
                org.broadleafcommerce.openadmin.client.dto.OperationTypes dtoOperationTypes = new org.broadleafcommerce.openadmin.client.dto.OperationTypes();
                dtoOperationTypes.setFetchType(operationTypes.fetchType());
                dtoOperationTypes.setInspectType(operationTypes.inspectType());
                dtoOperationTypes.setRemoveType(operationTypes.removeType());
                dtoOperationTypes.setUpdateType(operationTypes.updateType());
                dtoOperationTypes.setAddType(operationTypes.addType());

                //don't allow additional non-persistent properties or additional foreign keys for an advanced collection datasource - they don't make sense in this context
                PersistencePerspective persistencePerspective = new PersistencePerspective(dtoOperationTypes, new String[]{}, new ForeignKey[]{});
                metadata.setPersistencePerspective(persistencePerspective);

                switch (dtoOperationTypes.getFetchType()) {
                    case ENTITY: {
                        String foreignKeyName = null;
                        //try to inspect the JPA annotation
                        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                        ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
                        checkForeignKeyName: {
                            if (!StringUtils.isEmpty(annotColl.persistencePerspectiveItem().entity_manyToField())) {
                                foreignKeyName = annotColl.persistencePerspectiveItem().entity_manyToField();
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
                                throw new IllegalArgumentException("Unable to infer a ManyToOne field name for the @AdminPresentationCollection annotated field("+field.getName()+"). If not using the mappedBy property of @OneToMany or @ManyToMany, please make sure to explicitly define the entity_manyToField field of @PersistencePerspectiveItem");
                            }
                        }
                        ForeignKey foreignKey = new ForeignKey(foreignKeyName, targetClass.getName(), null, ForeignKeyRestrictionType.ID_EQ, annotColl.persistencePerspectiveItem().entity_displayValueProperty());
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
                    }
                    //TODO finish the other cases
                }

                metadata.setExcluded(annotColl.excluded());
                metadata.setFriendlyName(annotColl.friendlyName());
                metadata.setSecurityLevel(annotColl.securityLevel());
                metadata.setOrder(annotColl.order());

                attributes.put(field.getName(), metadata);
			} else {
                BasicFieldMetadata metadata = new BasicFieldMetadata();
                metadata.setName(field.getName());
                metadata.setExcluded(false);
                attributes.put(field.getName(), metadata);
            }
		}
		return attributes;
	}
	
	public Map<String, FieldMetadata> getPropertiesForPrimitiveClass(
		String propertyName, 
		String friendlyPropertyName, 
		Class<?> targetClass, 
		Class<?> parentClass, 
		MergedPropertyType mergedPropertyType
	) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
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
	) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
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
	) throws HibernateException, ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
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
                BasicFieldMetadata presentationAttribute = (BasicFieldMetadata) presentationAttributes.get(propertyName);
                Boolean amIExcluded = isParentExcluded || !testPropertyInclusion(presentationAttribute);
				Boolean includeField = testPropertyRecursion(prefix, parentClasses, propertyName, targetClass, ceilingEntityFullyQualifiedClassname);

				SupportedFieldType explicitType = null;
				if (presentationAttribute != null) {
					explicitType = presentationAttribute.getExplicitFieldType();
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
			} else if (myField != null && myField.getAnnotation(AdminPresentationCollection.class) != null) {
                CollectionMetadata fieldMetadata = (CollectionMetadata) presentationAttributes.get(propertyName);
                if (StringUtils.isEmpty(fieldMetadata.getCollectionCeilingEntity())) {
                    fieldMetadata.setCollectionCeilingEntity(type.getReturnedClass().getName());
                }
                fields.put(propertyName, fieldMetadata);
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
	) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
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
			foreignMetadata = getSessionFactory().getClassMetadata(Class.forName(foreignField.getForeignKeyClass()));
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
			foreignMetadata = getSessionFactory().getClassMetadata(Class.forName(additionalForeignFields[additionalForeignKeyIndexPosition].getForeignKeyClass()));
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

	protected Boolean testPropertyRecursion(String prefix, List<Class<?>> parentClasses, String propertyName, Class<?> targetClass, String ceilingEntityFullyQualifiedClassname) throws ClassNotFoundException {
        Boolean includeField = true;
        if (!StringUtils.isEmpty(prefix)) {
            Field testField = getFieldManager().getField(targetClass, propertyName);
            if (testField == null) {
                Class<?>[] entities = getAllPolymorphicEntitiesFromCeiling(Class.forName(ceilingEntityFullyQualifiedClassname));
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
	) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
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
	) throws MappingException, HibernateException, ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
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

    public Map<String, Map<String, Map<String, FieldMetadata>>> getFieldMetadataOverrides() {
        return fieldMetadataOverrides;
    }

    public void setFieldMetadataOverrides(Map<String, Map<String, Map<String, FieldMetadata>>> fieldMetadataOverrides) {
        this.fieldMetadataOverrides = fieldMetadataOverrides;
    }
}
