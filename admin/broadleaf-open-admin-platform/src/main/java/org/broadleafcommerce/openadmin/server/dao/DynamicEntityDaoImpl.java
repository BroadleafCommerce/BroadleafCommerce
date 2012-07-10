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
import org.broadleafcommerce.common.presentation.AdminPresentationOverride;
import org.broadleafcommerce.common.presentation.AdminPresentationOverrides;
import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.openadmin.client.dto.ClassTree;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.FieldPresentationAttributes;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspectiveItemType;
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
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
            Map<String, FieldPresentationAttributes> attributesMap = getFieldPresentationAttributes(targetClass);

            for (String property : attributesMap.keySet()) {
                FieldPresentationAttributes presentationAttributes = attributesMap.get(property);
                if (!presentationAttributes.getExcluded()) {
                    Field field = getFieldManager().getSingleField(targetClass, property);
                    if (!Modifier.isStatic(field.getModifiers())) {
                        buildProperty(targetClass, null, new ForeignKey[]{}, MergedPropertyType.PRIMARY, null, mergedProperties, null, "", property, null, false, 0, presentationAttributes, presentationAttributes.getExplicitFieldType(), field.getType());
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
            if (mergedProperties.get(key).getPresentationAttributes().getExcluded() != null && mergedProperties.get(key).getPresentationAttributes().getExcluded()) {
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
                FieldPresentationAttributes attr = metadata.getPresentationAttributes();
                if (attr == null) {
                    metadata.setPresentationAttributes(new FieldPresentationAttributes());
                    attr = metadata.getPresentationAttributes();
                }
                attr.setExcluded(false);
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
                        FieldPresentationAttributes attr = metadata.getPresentationAttributes();
                        if (attr == null) {
                            metadata.setPresentationAttributes(new FieldPresentationAttributes());
                            attr = metadata.getPresentationAttributes();
                        }
                        attr.setExcluded(true);
                    } else {
                        FieldMetadata metadata = mergedProperties.get(key);
                        FieldPresentationAttributes attr = metadata.getPresentationAttributes();
                        if (attr == null) {
                            metadata.setPresentationAttributes(new FieldPresentationAttributes());
                            attr = metadata.getPresentationAttributes();
                        }
                        if (!isParentExcluded) {
                            attr.setExcluded(false);
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
                        FieldPresentationAttributes attr = metadata.getPresentationAttributes();
                        if (attr == null) {
                            metadata.setPresentationAttributes(new FieldPresentationAttributes());
                            attr = metadata.getPresentationAttributes();
                        }
                        attr.setExcluded(true);
                    } else {
                        FieldMetadata metadata = mergedProperties.get(key);
                        FieldPresentationAttributes attr = metadata.getPresentationAttributes();
                        if (attr == null) {
                            metadata.setPresentationAttributes(new FieldPresentationAttributes());
                            attr = metadata.getPresentationAttributes();
                        }
                        if (!isParentExcluded) {
                            attr.setExcluded(false);
                        }
                    }
                }
            }
        }
    }

    protected void applyMetadataOverrides(String ceilingEntityFullyQualifiedClassname, String configurationKey, String prefix, Boolean isParentExcluded, Map<String, FieldMetadata> mergedProperties) throws InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        if (fieldMetadataOverrides != null && configurationKey != null) {
            Map<String, Map<String, FieldMetadata>> configuredOverrides = fieldMetadataOverrides.get(configurationKey);
            if (configuredOverrides != null) {
                Map<String, FieldMetadata> entityOverrides = configuredOverrides.get(ceilingEntityFullyQualifiedClassname);
                if (entityOverrides != null) {
                    for (String propertyName : entityOverrides.keySet()) {
                        FieldMetadata localMetadata = entityOverrides.get(propertyName);
                        Boolean excluded = localMetadata.getPresentationAttributes().getExcluded();
                        if (excluded == null) {
                            excluded = false;
                        }
                        for (String key : mergedProperties.keySet()) {
                            String testKey = prefix + key;
                            if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && excluded) {
                                FieldMetadata metadata = mergedProperties.get(key);
                                FieldPresentationAttributes attr = metadata.getPresentationAttributes();
                                if (attr == null) {
                                    metadata.setPresentationAttributes(new FieldPresentationAttributes());
                                    attr = metadata.getPresentationAttributes();
                                }
                                attr.setExcluded(true);
                                continue;
                            }
                            if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && !excluded) {
                                FieldMetadata metadata = mergedProperties.get(key);
                                FieldPresentationAttributes attr = metadata.getPresentationAttributes();
                                if (attr == null) {
                                    metadata.setPresentationAttributes(new FieldPresentationAttributes());
                                    attr = metadata.getPresentationAttributes();
                                }
                                if (!isParentExcluded) {
                                    attr.setExcluded(false);
                                }
                            }
                            if (key.equals(propertyName)) {
                                FieldMetadata serverMetadata = mergedProperties.get(key);
                                if (localMetadata.getPresentationAttributes().getFriendlyName() != null) {
                                    serverMetadata.getPresentationAttributes().setFriendlyName(localMetadata.getPresentationAttributes().getFriendlyName());
                                }
                                if (localMetadata.getPresentationAttributes().getSecurityLevel() != null) {
                                    serverMetadata.getPresentationAttributes().setSecurityLevel(localMetadata.getPresentationAttributes().getSecurityLevel());
                                }
                                if (localMetadata.getPresentationAttributes().getVisibility() != null) {
                                    serverMetadata.getPresentationAttributes().setVisibility(localMetadata.getPresentationAttributes().getVisibility());
                                }
                                if (localMetadata.getPresentationAttributes().getOrder() != null) {
                                    serverMetadata.getPresentationAttributes().setOrder(localMetadata.getPresentationAttributes().getOrder());
                                }
                                if (localMetadata.getPresentationAttributes().getExplicitFieldType() != null) {
                                    serverMetadata.getPresentationAttributes().setExplicitFieldType(localMetadata.getPresentationAttributes().getExplicitFieldType());
                                    serverMetadata.setFieldType(localMetadata.getPresentationAttributes().getExplicitFieldType());
                                }
                                if (localMetadata.getPresentationAttributes().getGroup() != null) {
                                    serverMetadata.getPresentationAttributes().setGroup(localMetadata.getPresentationAttributes().getGroup());
                                }
                                if (localMetadata.getPresentationAttributes().getGroupCollapsed() != null) {
                                    serverMetadata.getPresentationAttributes().setGroupCollapsed(localMetadata.getPresentationAttributes().getGroupCollapsed());
                                }
                                if (localMetadata.getPresentationAttributes().getGroupOrder() != null) {
                                    serverMetadata.getPresentationAttributes().setGroupOrder(localMetadata.getPresentationAttributes().getGroupOrder());
                                }
                                if (localMetadata.getPresentationAttributes().isLargeEntry() != null) {
                                    serverMetadata.getPresentationAttributes().setLargeEntry(localMetadata.getPresentationAttributes().isLargeEntry());
                                }
                                if (localMetadata.getPresentationAttributes().isProminent() != null) {
                                    serverMetadata.getPresentationAttributes().setProminent(localMetadata.getPresentationAttributes().isProminent());
                                }
                                if (localMetadata.getPresentationAttributes().getColumnWidth() != null) {
                                    serverMetadata.getPresentationAttributes().setColumnWidth(localMetadata.getPresentationAttributes().getColumnWidth());
                                }
                                if (!StringUtils.isEmpty(localMetadata.getPresentationAttributes().getBroadleafEnumeration()) && !localMetadata.getPresentationAttributes().getBroadleafEnumeration().equals(serverMetadata.getPresentationAttributes().getBroadleafEnumeration())) {
                                    serverMetadata.getPresentationAttributes().setBroadleafEnumeration(localMetadata.getPresentationAttributes().getBroadleafEnumeration());
                                    setupBroadleafEnumeration(localMetadata.getPresentationAttributes().getBroadleafEnumeration(), serverMetadata);
                                }
                                if (localMetadata.getPresentationAttributes().getReadOnly() != null) {
                                    serverMetadata.getPresentationAttributes().setReadOnly(localMetadata.getPresentationAttributes().getReadOnly());
                                }
                                if (localMetadata.getPresentationAttributes().getExcluded() != null) {
                                    serverMetadata.getPresentationAttributes().setExcluded(localMetadata.getPresentationAttributes().getExcluded());
                                }
                                if (isParentExcluded) {
                                    serverMetadata.getPresentationAttributes().setExcluded(true);
                                }
                                if (localMetadata.getPresentationAttributes().getTooltip() != null) {
                                    serverMetadata.getPresentationAttributes().setTooltip(localMetadata.getPresentationAttributes().getTooltip());
                                }
                                if (localMetadata.getPresentationAttributes().getHelpText() != null) {
                                    serverMetadata.getPresentationAttributes().setHelpText(localMetadata.getPresentationAttributes().getHelpText());
                                }
                                if (localMetadata.getPresentationAttributes().getHint() != null) {
                                    serverMetadata.getPresentationAttributes().setHint(localMetadata.getPresentationAttributes().getHint());
                                }
                                if (localMetadata.getPresentationAttributes().getRequiredOverride() != null) {
                                    serverMetadata.getPresentationAttributes().setRequiredOverride(localMetadata.getPresentationAttributes().getRequiredOverride());
                                }
                                if (localMetadata.getPresentationAttributes().getValidationConfigurations() != null) {
                                    serverMetadata.getPresentationAttributes().setValidationConfigurations(localMetadata.getPresentationAttributes().getValidationConfigurations());
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

    protected void applyAdminPresentationOverrides(String prefix, Boolean isParentExcluded, Map<String, FieldMetadata> mergedProperties, Map<String, AdminPresentationOverride> presentationOverrides) throws InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException {
        for (String propertyName : presentationOverrides.keySet()) {
            AdminPresentation annot = presentationOverrides.get(propertyName).value();
            for (String key : mergedProperties.keySet()) {
                String testKey = prefix + key;
                if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && annot.excluded()) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    FieldPresentationAttributes attr = metadata.getPresentationAttributes();
                    if (attr == null) {
                        metadata.setPresentationAttributes(new FieldPresentationAttributes());
                        attr = metadata.getPresentationAttributes();
                    }
                    attr.setExcluded(true);
                    continue;
                }
                if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && !annot.excluded()) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    FieldPresentationAttributes attr = metadata.getPresentationAttributes();
                    if (attr == null) {
                        metadata.setPresentationAttributes(new FieldPresentationAttributes());
                        attr = metadata.getPresentationAttributes();
                    }
                    if (!isParentExcluded) {
                        attr.setExcluded(false);
                    }
                }
                if (key.equals(propertyName)) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    FieldPresentationAttributes attr = metadata.getPresentationAttributes();
                    attr.setFriendlyName(annot.friendlyName());
                    attr.setSecurityLevel(annot.securityLevel());
                    attr.setVisibility(annot.visibility());
                    attr.setOrder(annot.order());
                    attr.setExplicitFieldType(annot.fieldType());
                    if (annot.fieldType() != SupportedFieldType.UNKNOWN) {
                        metadata.setFieldType(annot.fieldType());
                    }
                    attr.setGroup(annot.group());
                    attr.setGroupCollapsed(annot.groupCollapsed());
                    attr.setGroupOrder(annot.groupOrder());
                    attr.setLargeEntry(annot.largeEntry());
                    attr.setProminent(annot.prominent());
                    attr.setColumnWidth(annot.columnWidth());
                    if (!StringUtils.isEmpty(annot.broadleafEnumeration()) && !annot.broadleafEnumeration().equals(attr.getBroadleafEnumeration())) {
                        attr.setBroadleafEnumeration(annot.broadleafEnumeration());
                        setupBroadleafEnumeration(annot.broadleafEnumeration(), metadata);
                    }
                    attr.setReadOnly(annot.readOnly());
                    attr.setExcluded(isParentExcluded?true:annot.excluded());
                    attr.setTooltip(annot.tooltip());
                    attr.setHelpText(annot.helpText());
                    attr.setHint(annot.hint());
                    attr.setRequiredOverride(annot.requiredOverride()== RequiredOverride.IGNORED?null:annot.requiredOverride()==RequiredOverride.REQUIRED?true:false);
                    if (annot.validationConfigurations().length != 0) {
                        ValidationConfiguration[] configurations = annot.validationConfigurations();
                        for (ValidationConfiguration configuration : configurations) {
                            ConfigurationItem[] items = configuration.configurationItems();
                            Map<String, String> itemMap = new HashMap<String, String>();
                            for (ConfigurationItem item : items) {
                                itemMap.put(item.itemName(), item.itemValue());
                            }
                            attr.getValidationConfigurations().put(configuration.validationImplementation(), itemMap);
                        }
                    }
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
		FieldPresentationAttributes presentationAttribute, 
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
		FieldPresentationAttributes presentationAttribute, 
		MergedPropertyType mergedPropertyType
	) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		FieldMetadata fieldMetadata = new FieldMetadata();
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
			fieldMetadata.setCollection(false);
		} else {
			fieldMetadata.setCollection(true);
		}
		fieldMetadata.setMutable(true);
		fieldMetadata.setInheritedFromType(targetClass.getName());
		fieldMetadata.setAvailableToTypes(new String[]{targetClass.getName()});
		if (presentationAttribute != null) {
			fieldMetadata.setPresentationAttributes(presentationAttribute);
		}
		fieldMetadata.setMergedPropertyType(mergedPropertyType);
		if (SupportedFieldType.BROADLEAF_ENUMERATION.equals(type)) {
            setupBroadleafEnumeration(presentationAttribute.getBroadleafEnumeration(), fieldMetadata);
		}
		
		return fieldMetadata;
	}

    protected void setupBroadleafEnumeration(String broadleafEnumerationClass, FieldMetadata fieldMetadata) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Map<String, String> enumVals = new TreeMap<String, String>();
        Class<?> broadleafEnumeration = Class.forName(broadleafEnumerationClass);
        Method typeMethod = broadleafEnumeration.getMethod("getType", new Class<?>[]{});
        Method friendlyTypeMethod = broadleafEnumeration.getMethod("getFriendlyType", new Class<?>[]{});
        Field types = getFieldManager().getField(broadleafEnumeration, "TYPES");
        if (types != null) {
            Map typesMap = (Map) types.get(null);
            for (Object value : typesMap.values()) {
                enumVals.put((String) friendlyTypeMethod.invoke(value, new Object[]{}), (String) typeMethod.invoke(value, new Object[]{}));
            }
        } else {
            Field[] fields = getAllFields(broadleafEnumeration);
            for (Field field : fields) {
                boolean isStatic = Modifier.isStatic(field.getModifiers());
                if (isStatic && field.getType().isAssignableFrom(broadleafEnumeration)){
                    enumVals.put((String) friendlyTypeMethod.invoke(field.get(null), new Object[]{}), (String) typeMethod.invoke(field.get(null), new Object[]{}));
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

	protected Map<String, FieldPresentationAttributes> getFieldPresentationAttributes(Class<?> targetClass) {
		Map<String, FieldPresentationAttributes> attributes = new HashMap<String, FieldPresentationAttributes>();
		Field[] fields = getAllFields(targetClass);
		for (Field field : fields) {
			AdminPresentation annot = field.getAnnotation(AdminPresentation.class);
			if (annot != null) {
				FieldPresentationAttributes attr = new FieldPresentationAttributes();
				attr.setName(field.getName());
				attr.setFriendlyName(annot.friendlyName());
				attr.setSecurityLevel(annot.securityLevel());
				attr.setVisibility(annot.visibility());
				attr.setOrder(annot.order());
				attr.setExplicitFieldType(annot.fieldType());
				attr.setGroup(annot.group());
				attr.setGroupOrder(annot.groupOrder());
                attr.setGroupCollapsed(annot.groupCollapsed());
				attr.setLargeEntry(annot.largeEntry());
				attr.setProminent(annot.prominent());
				attr.setColumnWidth(annot.columnWidth());
				attr.setBroadleafEnumeration(annot.broadleafEnumeration());
				attr.setReadOnly(annot.readOnly());
                attr.setExcluded(annot.excluded());
                attr.setTooltip(annot.tooltip());
                attr.setHelpText(annot.helpText());
                attr.setHint(annot.hint());
                attr.setRequiredOverride(annot.requiredOverride()==RequiredOverride.IGNORED?null:annot.requiredOverride()==RequiredOverride.REQUIRED?true:false);
				if (annot.validationConfigurations().length != 0) {
					ValidationConfiguration[] configurations = annot.validationConfigurations();
					for (ValidationConfiguration configuration : configurations) {
						ConfigurationItem[] items = configuration.configurationItems();
						Map<String, String> itemMap = new HashMap<String, String>();
						for (ConfigurationItem item : items) {
							itemMap.put(item.itemName(), item.itemValue());
						}
						attr.getValidationConfigurations().put(configuration.validationImplementation(), itemMap);
					}
				}
				attributes.put(field.getName(), attr);
			} else {
                FieldPresentationAttributes attr = new FieldPresentationAttributes();
		        attr.setName(field.getName());
                attr.setExcluded(false);
                attributes.put(field.getName(), attr);
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
		FieldPresentationAttributes presentationAttribute = new FieldPresentationAttributes();
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
		fields.get(propertyName).setLength(255);
		fields.get(propertyName).setCollection(false);
		fields.get(propertyName).setRequired(true);
		fields.get(propertyName).setUnique(true);
		fields.get(propertyName).setScale(100);
		fields.get(propertyName).setPrecision(100);
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
		Map<String, FieldPresentationAttributes> presentationAttributes = getFieldPresentationAttributes(targetClass);
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
		FieldPresentationAttributes presentationAttribute = new FieldPresentationAttributes();
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
		Map<String, FieldPresentationAttributes> presentationAttributes, 
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
			if (
					!type.isAnyType() && !type.isCollectionType() ||
					isPropertyForeignKey ||
					additionalForeignKeyIndexPosition >= 0 ||
					presentationAttributes.containsKey(propertyName)
			) {
                FieldPresentationAttributes presentationAttribute = presentationAttributes.get(propertyName);
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
		FieldPresentationAttributes presentationAttribute, 
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
			fields.get(propertyName).setForeignKeyProperty(foreignMetadata.getIdentifierPropertyName());
			fields.get(propertyName).setForeignKeyClass(foreignField.getForeignKeyClass());
			fields.get(propertyName).setForeignKeyDisplayValueProperty(foreignField.getDisplayValueProperty());
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
			fields.get(propertyName).setForeignKeyProperty(foreignMetadata.getIdentifierPropertyName());
			fields.get(propertyName).setForeignKeyClass(additionalForeignFields[additionalForeignKeyIndexPosition].getForeignKeyClass());
			fields.get(propertyName).setForeignKeyDisplayValueProperty(additionalForeignFields[additionalForeignKeyIndexPosition].getDisplayValueProperty());
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

    public Boolean testPropertyInclusion(FieldPresentationAttributes presentationAttribute) {
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
		Map<String, FieldPresentationAttributes> componentPresentationAttributes = getFieldPresentationAttributes(returnedClass);
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
