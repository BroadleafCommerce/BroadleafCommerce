package org.broadleafcommerce.openadmin.server.dao.provider.metadata;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.AdminPresentationAdornedTargetCollection;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.override.AdminPresentationAdornedTargetCollectionOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverrides;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.override.FieldMetadataOverride;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.dao.FieldInfo;
import org.hibernate.mapping.Property;
import org.hibernate.type.Type;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.ManyToOne;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Component("blAdornedTargetCollectionMetadataProvider")
@Scope("prototype")
public class AdornedTargetCollectionMetadataProvider extends AbstractMetadataProvider {

    private static final Log LOG = LogFactory.getLog(AdornedTargetCollectionMetadataProvider.class);

    public boolean canHandleField(Field field) {
        AdminPresentationAdornedTargetCollection annot = field.getAnnotation(AdminPresentationAdornedTargetCollection.class);
        return annot != null;
    }

    @Override
    public boolean canHandleAnnotationOverride(Class<?> clazz) {
        AdminPresentationOverrides myOverrides = clazz.getAnnotation(AdminPresentationOverrides.class);
        return myOverrides != null && !ArrayUtils.isEmpty(myOverrides.adornedTargetCollections());
    }

    @Override
    public boolean canHandleXmlOverride(String ceilingEntityFullyQualifiedClassname, String configurationKey) {
        return true;
    }

    public void addMetadata(Field field, Class<?> parentClass, Class<?> targetClass, Map<String,
                    FieldMetadata> attributes, DynamicEntityDao dynamicEntityDao, String prefix) {
        AdminPresentationAdornedTargetCollection annot = field.getAnnotation(AdminPresentationAdornedTargetCollection.class);
        FieldInfo info = buildFieldInfo(field);
        FieldMetadataOverride override = constructAdornedTargetCollectionMetadataOverride(annot);
        buildAdornedTargetCollectionMetadata(parentClass, targetClass, attributes, info, override, dynamicEntityDao);
        setClassOwnership(parentClass, targetClass, attributes, info);
    }

    @Override
    public void overrideViaAnnotation(Class<?> entity, Map<String, FieldMetadata> mergedProperties, Boolean
            isParentExcluded, DynamicEntityDao dynamicEntityDao, String prefix) {
        Map<String, AdminPresentationAdornedTargetCollectionOverride> presentationAdornedTargetCollectionOverrides = new HashMap<String, AdminPresentationAdornedTargetCollectionOverride>();

        AdminPresentationOverrides myOverrides = entity.getAnnotation(AdminPresentationOverrides.class);
        if (myOverrides != null) {
            for (AdminPresentationAdornedTargetCollectionOverride myOverride : myOverrides.adornedTargetCollections()) {
                presentationAdornedTargetCollectionOverrides.put(myOverride.name(), myOverride);
            }
        }

        for (String propertyName : presentationAdornedTargetCollectionOverrides.keySet()) {
            for (String key : mergedProperties.keySet()) {
                if (key.startsWith(propertyName)) {
                    buildAdminPresentationAdornedTargetCollectionOverride(prefix, isParentExcluded, mergedProperties, presentationAdornedTargetCollectionOverrides, propertyName, key, dynamicEntityDao);
                }
            }
        }
    }

    @Override
    public void overrideViaXml(String configurationKey, String ceilingEntityFullyQualifiedClassname, String prefix, Boolean isParentExcluded, Map<String, FieldMetadata> mergedProperties, DynamicEntityDao dynamicEntityDao) {
        Map<String, FieldMetadataOverride> overrides = getTargetedOverride(configurationKey, ceilingEntityFullyQualifiedClassname);
        if (overrides != null) {
            for (String propertyName : overrides.keySet()) {
                final FieldMetadataOverride localMetadata = overrides.get(propertyName);
                for (String key : mergedProperties.keySet()) {
                    if (key.equals(propertyName)) {
                        try {
                            if (mergedProperties.get(key) instanceof AdornedTargetCollectionMetadata) {
                                AdornedTargetCollectionMetadata serverMetadata = (AdornedTargetCollectionMetadata) mergedProperties.get(key);
                                if (serverMetadata.getTargetClass() != null) {
                                    Class<?> targetClass = Class.forName(serverMetadata.getTargetClass());
                                    Class<?> parentClass = null;
                                    if (serverMetadata.getOwningClass() != null) {
                                        parentClass = Class.forName(serverMetadata.getOwningClass());
                                    }
                                    String fieldName = serverMetadata.getFieldName();
                                    Field field = dynamicEntityDao.getFieldManager().getField(targetClass, fieldName);
                                    Map<String, FieldMetadata> temp = new HashMap<String, FieldMetadata>(1);
                                    temp.put(field.getName(), serverMetadata);
                                    FieldInfo info = buildFieldInfo(field);
                                    buildAdornedTargetCollectionMetadata(parentClass, targetClass, temp, info, localMetadata, dynamicEntityDao);
                                    serverMetadata = (AdornedTargetCollectionMetadata) temp.get(field.getName());
                                    mergedProperties.put(key, serverMetadata);
                                    if (isParentExcluded) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("applyAdornedTargetCollectionMetadataOverrides:Excluding " + key + "because parent is marked as excluded.");
                                        }
                                        serverMetadata.setExcluded(true);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void addMetadataFromMappingData(FieldMetadata presentationAttribute, List<Property> componentProperties, SupportedFieldType type, SupportedFieldType secondaryType, Type entityType, String propertyName, MergedPropertyType mergedPropertyType, DynamicEntityDao dynamicEntityDao) {
        //do nothing
    }

    protected void buildAdminPresentationAdornedTargetCollectionOverride(String prefix, Boolean isParentExcluded, Map<String, FieldMetadata> mergedProperties, Map<String, AdminPresentationAdornedTargetCollectionOverride> presentationAdornedTargetCollectionOverrides, String propertyName, String key, DynamicEntityDao dynamicEntityDao) {
        AdminPresentationAdornedTargetCollectionOverride override = presentationAdornedTargetCollectionOverrides.get(propertyName);
        if (override != null) {
            AdminPresentationAdornedTargetCollection annot = override.value();
            if (annot != null) {
                String testKey = prefix + key;
                if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && annot.excluded()) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("buildAdminPresentationAdornedTargetCollectionOverride:Excluding " + key + "because an override annotation declared " + testKey + "to be excluded");
                    }
                    metadata.setExcluded(true);
                    return;
                }
                if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && !annot.excluded()) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    if (!isParentExcluded) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("buildAdminPresentationAdornedTargetCollectionOverride:Showing " + key + "because an override annotation declared " + testKey + " to not be excluded");
                        }
                        metadata.setExcluded(false);
                    }
                }
                if (!(mergedProperties.get(key) instanceof AdornedTargetCollectionMetadata)) {
                    return;
                }
                AdornedTargetCollectionMetadata serverMetadata = (AdornedTargetCollectionMetadata) mergedProperties.get(key);
                if (serverMetadata.getTargetClass() != null) {
                    try {
                        Class<?> targetClass = Class.forName(serverMetadata.getTargetClass());
                        Class<?> parentClass = null;
                        if (serverMetadata.getOwningClass() != null) {
                            parentClass = Class.forName(serverMetadata.getOwningClass());
                        }
                        String fieldName = serverMetadata.getFieldName();
                        Field field = dynamicEntityDao.getFieldManager().getField(targetClass, fieldName);
                        FieldMetadataOverride localMetadata = constructAdornedTargetCollectionMetadataOverride(annot);
                        //do not include the previous metadata - we want to construct a fresh metadata from the override annotation
                        Map<String, FieldMetadata> temp = new HashMap<String, FieldMetadata>(1);
                        FieldInfo info = buildFieldInfo(field);
                        buildAdornedTargetCollectionMetadata(parentClass, targetClass, temp, info, localMetadata, dynamicEntityDao);
                        AdornedTargetCollectionMetadata result = (AdornedTargetCollectionMetadata) temp.get(field.getName());
                        result.setInheritedFromType(serverMetadata.getInheritedFromType());
                        result.setAvailableToTypes(serverMetadata.getAvailableToTypes());
                        mergedProperties.put(key, result);
                        if (isParentExcluded) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("buildAdminPresentationAdornedTargetCollectionOverride:Excluding " + key + "because the parent was excluded");
                            }
                            serverMetadata.setExcluded(true);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    protected FieldMetadataOverride constructAdornedTargetCollectionMetadataOverride(AdminPresentationAdornedTargetCollection adornedTargetCollection) {
        if (adornedTargetCollection != null) {
            FieldMetadataOverride override = new FieldMetadataOverride();
            override.setGridVisibleFields(adornedTargetCollection.gridVisibleFields());
            override.setIgnoreAdornedProperties(adornedTargetCollection.ignoreAdornedProperties());
            override.setMaintainedAdornedTargetFields(adornedTargetCollection.maintainedAdornedTargetFields());
            override.setParentObjectIdProperty(adornedTargetCollection.parentObjectIdProperty());
            override.setParentObjectProperty(adornedTargetCollection.parentObjectProperty());
            override.setSortAscending(adornedTargetCollection.sortAscending());
            override.setSortProperty(adornedTargetCollection.sortProperty());
            override.setTargetObjectIdProperty(adornedTargetCollection.targetObjectIdProperty());
            override.setTargetObjectProperty(adornedTargetCollection.targetObjectProperty());
            override.setJoinEntityClass(adornedTargetCollection.joinEntityClass());
            override.setCustomCriteria(adornedTargetCollection.customCriteria());
            override.setUseServerSideInspectionCache(adornedTargetCollection.useServerSideInspectionCache());
            override.setDataSourceName(adornedTargetCollection.dataSourceName());
            override.setExcluded(adornedTargetCollection.excluded());
            override.setFriendlyName(adornedTargetCollection.friendlyName());
            override.setReadOnly(adornedTargetCollection.readOnly());
            override.setOrder(adornedTargetCollection.order());
            override.setTab(adornedTargetCollection.tab());
            override.setTabOrder(adornedTargetCollection.tabOrder());
            override.setSecurityLevel(adornedTargetCollection.securityLevel());
            override.setTargetElementId(adornedTargetCollection.targetUIElementId());
            override.setAddType(adornedTargetCollection.operationTypes().addType());
            override.setFetchType(adornedTargetCollection.operationTypes().fetchType());
            override.setRemoveType(adornedTargetCollection.operationTypes().removeType());
            override.setUpdateType(adornedTargetCollection.operationTypes().updateType());
            override.setInspectType(adornedTargetCollection.operationTypes().inspectType());
            override.setShowIfProperty(adornedTargetCollection.showIfProperty());
            override.setCurrencyCodeField(adornedTargetCollection.currencyCodeField());
            return override;
        }
        throw new IllegalArgumentException("AdminPresentationAdornedTargetCollection annotation not found on field.");
    }

    protected void buildAdornedTargetCollectionMetadata(Class<?> parentClass, Class<?> targetClass, Map<String, FieldMetadata> attributes, FieldInfo field, FieldMetadataOverride adornedTargetCollectionMetadata, DynamicEntityDao dynamicEntityDao) {
        AdornedTargetCollectionMetadata serverMetadata = (AdornedTargetCollectionMetadata) attributes.get(field.getName());

        Class<?> resolvedClass = parentClass==null?targetClass:parentClass;
        AdornedTargetCollectionMetadata metadata;
        if (serverMetadata != null) {
            metadata = serverMetadata;
        } else {
            metadata = new AdornedTargetCollectionMetadata();
        }
        metadata.setTargetClass(targetClass.getName());
        metadata.setFieldName(field.getName());

        if (adornedTargetCollectionMetadata.getReadOnly() != null) {
            metadata.setMutable(!adornedTargetCollectionMetadata.getReadOnly());
        }
        if (adornedTargetCollectionMetadata.getShowIfProperty()!=null) {
            metadata.setShowIfProperty(adornedTargetCollectionMetadata.getShowIfProperty());
        }

        org.broadleafcommerce.openadmin.client.dto.OperationTypes dtoOperationTypes = new org.broadleafcommerce.openadmin.client.dto.OperationTypes(OperationType.ADORNEDTARGETLIST, OperationType.ADORNEDTARGETLIST, OperationType.ADORNEDTARGETLIST, OperationType.ADORNEDTARGETLIST, OperationType.BASIC);
        if (adornedTargetCollectionMetadata.getAddType() != null) {
            dtoOperationTypes.setAddType(adornedTargetCollectionMetadata.getAddType());
        }
        if (adornedTargetCollectionMetadata.getRemoveType() != null) {
            dtoOperationTypes.setRemoveType(adornedTargetCollectionMetadata.getRemoveType());
        }
        if (adornedTargetCollectionMetadata.getFetchType() != null) {
            dtoOperationTypes.setFetchType(adornedTargetCollectionMetadata.getFetchType());
        }
        if (adornedTargetCollectionMetadata.getInspectType() != null) {
            dtoOperationTypes.setInspectType(adornedTargetCollectionMetadata.getInspectType());
        }
        if (adornedTargetCollectionMetadata.getUpdateType() != null) {
            dtoOperationTypes.setUpdateType(adornedTargetCollectionMetadata.getUpdateType());
        }

        //don't allow additional non-persistent properties or additional foreign keys for an advanced collection datasource - they don't make sense in this context
        PersistencePerspective persistencePerspective;
        if (serverMetadata != null) {
            persistencePerspective = metadata.getPersistencePerspective();
            persistencePerspective.setOperationTypes(dtoOperationTypes);
        } else {
            persistencePerspective = new PersistencePerspective(dtoOperationTypes, new String[]{}, new ForeignKey[]{});
            metadata.setPersistencePerspective(persistencePerspective);
        }

        String parentObjectProperty = null;
        if (serverMetadata != null) {
            parentObjectProperty = ((AdornedTargetList) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).getLinkedObjectPath();
        }
        if (!StringUtils.isEmpty(adornedTargetCollectionMetadata.getParentObjectProperty())) {
            parentObjectProperty = adornedTargetCollectionMetadata.getParentObjectProperty();
        }
        if (parentObjectProperty == null && !StringUtils.isEmpty(field.getOneToManyMappedBy())) {
            parentObjectProperty = field.getOneToManyMappedBy();
        }
        if (parentObjectProperty == null && !StringUtils.isEmpty(field.getManyToManyMappedBy())) {
            parentObjectProperty = field.getManyToManyMappedBy();
        }
        if (StringUtils.isEmpty(parentObjectProperty)) {
            throw new IllegalArgumentException("Unable to infer a parentObjectProperty for the @AdminPresentationAdornedTargetCollection annotated field("+field.getName()+"). If not using the mappedBy property of @OneToMany or @ManyToMany, please make sure to explicitly define the parentObjectProperty property");
        }

        String sortProperty = null;
        if (serverMetadata != null) {
            sortProperty = ((AdornedTargetList) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).getSortField();
        }
        if (!StringUtils.isEmpty(adornedTargetCollectionMetadata.getSortProperty())) {
            sortProperty = adornedTargetCollectionMetadata.getSortProperty();
        }

        metadata.setParentObjectClass(resolvedClass.getName());
        if (adornedTargetCollectionMetadata.getMaintainedAdornedTargetFields() != null) {
            metadata.setMaintainedAdornedTargetFields(adornedTargetCollectionMetadata.getMaintainedAdornedTargetFields());
        }
        if (adornedTargetCollectionMetadata.getGridVisibleFields() != null) {
            metadata.setGridVisibleFields(adornedTargetCollectionMetadata.getGridVisibleFields());
        }
        String parentObjectIdProperty = null;
        if (serverMetadata != null) {
            parentObjectIdProperty = ((AdornedTargetList) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).getLinkedIdProperty();
        }
        if (adornedTargetCollectionMetadata.getParentObjectIdProperty()!=null) {
            parentObjectIdProperty = adornedTargetCollectionMetadata.getParentObjectIdProperty();
        }
        String targetObjectProperty = null;
        if (serverMetadata != null) {
            targetObjectProperty = ((AdornedTargetList) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).getTargetObjectPath();
        }
        if (adornedTargetCollectionMetadata.getTargetObjectProperty()!=null) {
            targetObjectProperty = adornedTargetCollectionMetadata.getTargetObjectProperty();
        }

        String joinEntityClass = null;
        if (serverMetadata != null) {
            joinEntityClass = ((AdornedTargetList) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).getJoinEntityClass();
        }
        if (adornedTargetCollectionMetadata.getJoinEntityClass() != null) {
            joinEntityClass = adornedTargetCollectionMetadata.getJoinEntityClass();
        }

        Class<?> collectionTarget = null;
        try {
            checkCeiling: {
                try {
                    ParameterizedType pt = (ParameterizedType) field.getGenericType();
                    java.lang.reflect.Type collectionType = pt.getActualTypeArguments()[0];
                    String ceilingEntityName = ((Class<?>) collectionType).getName();
                    collectionTarget = entityConfiguration.lookupEntityClass(ceilingEntityName);
                    break checkCeiling;
                } catch (NoSuchBeanDefinitionException e) {
                    // We weren't successful at looking at entity configuration to find the type of this collection.
                    // We will continue and attempt to find it via the Hibernate annotations
                }
                if (!StringUtils.isEmpty(field.getOneToManyTargetEntity()) && !void.class.getName().equals(field.getOneToManyTargetEntity())) {
                    collectionTarget = Class.forName(field.getOneToManyTargetEntity());
                    break checkCeiling;
                }
                if (!StringUtils.isEmpty(field.getManyToManyTargetEntity()) && !void.class.getName().equals(field.getManyToManyTargetEntity())) {
                    collectionTarget = Class.forName(field.getManyToManyTargetEntity());
                    break checkCeiling;
                }
            }
            if (StringUtils.isNotBlank(joinEntityClass)) {
                collectionTarget = Class.forName(joinEntityClass);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (collectionTarget == null) {
            throw new IllegalArgumentException("Unable to infer the type of the collection from the targetEntity property of a OneToMany or ManyToMany collection.");
        }
        Field collectionTargetField = dynamicEntityDao.getFieldManager().getField(collectionTarget, targetObjectProperty);
        ManyToOne manyToOne = collectionTargetField.getAnnotation(ManyToOne.class);
        String ceiling = null;
        checkCeiling: {
            if (manyToOne != null && manyToOne.targetEntity() != void.class) {
                ceiling = manyToOne.targetEntity().getName();
                break checkCeiling;
            }
            ceiling = collectionTargetField.getType().getName();
        }
        if (!StringUtils.isEmpty(ceiling)) {
            metadata.setCollectionCeilingEntity(ceiling);
        }

        String targetObjectIdProperty = null;
        if (serverMetadata != null) {
            targetObjectIdProperty = ((AdornedTargetList) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).getTargetIdProperty();
        }
        if (adornedTargetCollectionMetadata.getTargetObjectIdProperty()!=null) {
            targetObjectIdProperty = adornedTargetCollectionMetadata.getTargetObjectIdProperty();
        }
        Boolean isAscending = true;
        if (serverMetadata != null) {
            isAscending = ((AdornedTargetList) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST)).getSortAscending();
        }
        if (adornedTargetCollectionMetadata.isSortAscending()!=null) {
            isAscending = adornedTargetCollectionMetadata.isSortAscending();
        }

        if (serverMetadata != null) {
            AdornedTargetList adornedTargetList = (AdornedTargetList) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);
            adornedTargetList.setCollectionFieldName(field.getName());
            adornedTargetList.setLinkedObjectPath(parentObjectProperty);
            adornedTargetList.setLinkedIdProperty(parentObjectIdProperty);
            adornedTargetList.setTargetObjectPath(targetObjectProperty);
            adornedTargetList.setTargetIdProperty(targetObjectIdProperty);
            adornedTargetList.setJoinEntityClass(joinEntityClass);
            adornedTargetList.setAdornedTargetEntityClassname(collectionTarget.getName());
            adornedTargetList.setSortField(sortProperty);
            adornedTargetList.setSortAscending(isAscending);
        } else {
            AdornedTargetList adornedTargetList = new AdornedTargetList(field.getName(), parentObjectProperty, parentObjectIdProperty, targetObjectProperty, targetObjectIdProperty, collectionTarget.getName(), sortProperty, isAscending);
            adornedTargetList.setJoinEntityClass(joinEntityClass);
            persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.ADORNEDTARGETLIST, adornedTargetList);
        }

        if (adornedTargetCollectionMetadata.getExcluded() != null) {
            if (LOG.isDebugEnabled()) {
                if (adornedTargetCollectionMetadata.getExcluded()) {
                    LOG.debug("buildAdornedTargetCollectionMetadata:Excluding " + field.getName() + " because it was explicitly declared in config");
                } else {
                    LOG.debug("buildAdornedTargetCollectionMetadata:Showing " + field.getName() + " because it was explicitly declared in config");
                }
            }
            metadata.setExcluded(adornedTargetCollectionMetadata.getExcluded());
        }
        if (adornedTargetCollectionMetadata.getFriendlyName() != null) {
            metadata.setFriendlyName(adornedTargetCollectionMetadata.getFriendlyName());
        }
        if (adornedTargetCollectionMetadata.getSecurityLevel() != null) {
            metadata.setSecurityLevel(adornedTargetCollectionMetadata.getSecurityLevel());
        }
        if (adornedTargetCollectionMetadata.getOrder() != null) {
            metadata.setOrder(adornedTargetCollectionMetadata.getOrder());
        }

        if (adornedTargetCollectionMetadata.getTab() != null) {
            metadata.setTab(adornedTargetCollectionMetadata.getTab());
        }
        if (adornedTargetCollectionMetadata.getTabOrder() != null) {
            metadata.setTabOrder(adornedTargetCollectionMetadata.getTabOrder());
        }

        if (!StringUtils.isEmpty(adornedTargetCollectionMetadata.getTargetElementId())) {
            metadata.setTargetElementId(adornedTargetCollectionMetadata.getTargetElementId());
        }

        if (!StringUtils.isEmpty(adornedTargetCollectionMetadata.getDataSourceName())) {
            metadata.setDataSourceName(adornedTargetCollectionMetadata.getDataSourceName());
        }

        if (adornedTargetCollectionMetadata.getCustomCriteria() != null) {
            metadata.setCustomCriteria(adornedTargetCollectionMetadata.getCustomCriteria());
        }

        if (adornedTargetCollectionMetadata.getUseServerSideInspectionCache() != null) {
            persistencePerspective.setUseServerSideInspectionCache(adornedTargetCollectionMetadata.getUseServerSideInspectionCache());
        }

        if (adornedTargetCollectionMetadata.isIgnoreAdornedProperties() != null) {
            metadata.setIgnoreAdornedProperties(adornedTargetCollectionMetadata.isIgnoreAdornedProperties());
        }
        if (adornedTargetCollectionMetadata.getCurrencyCodeField()!=null) {
            metadata.setCurrencyCodeField(adornedTargetCollectionMetadata.getCurrencyCodeField());
        }

        attributes.put(field.getName(), metadata);
    }
}
