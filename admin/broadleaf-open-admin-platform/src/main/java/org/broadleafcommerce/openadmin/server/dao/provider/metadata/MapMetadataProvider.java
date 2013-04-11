package org.broadleafcommerce.openadmin.server.dao.provider.metadata;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.UnspecifiedBooleanType;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMapOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverrides;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MapMetadata;
import org.broadleafcommerce.openadmin.client.dto.MapStructure;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.SimpleValueMapStructure;
import org.broadleafcommerce.openadmin.client.dto.override.FieldMetadataOverride;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.dao.FieldInfo;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Component("blMapMetadataProvider")
@Scope("prototype")
public class MapMetadataProvider extends MetadataProviderAdapter {

    private static final Log LOG = LogFactory.getLog(MapMetadataProvider.class);

    @Override
    public boolean canHandleField(Field field) {
        AdminPresentationMap annot = field.getAnnotation(AdminPresentationMap.class);
        return annot != null;
    }

    @Override
    public boolean canHandleAnnotationOverride(Class<?> clazz) {
        AdminPresentationOverrides myOverrides = clazz.getAnnotation(AdminPresentationOverrides.class);
        return myOverrides != null && !ArrayUtils.isEmpty(myOverrides.maps());
    }

    @Override
    public boolean canHandleXmlOverride(String ceilingEntityFullyQualifiedClassname, String configurationKey) {
        return true;
    }

    public void addMetadata(AddMetadataRequest addMetadataRequest) {
        AdminPresentationMap annot = addMetadataRequest.getRequestedField().getAnnotation(AdminPresentationMap.class);
        FieldInfo info = buildFieldInfo(addMetadataRequest.getRequestedField());
        FieldMetadataOverride override = constructMapMetadataOverride(annot);
        buildMapMetadata(addMetadataRequest.getParentClass(), addMetadataRequest.getTargetClass(), addMetadataRequest
                .getRequestedMetadata(), info, override, addMetadataRequest.getDynamicEntityDao(), addMetadataRequest.getPrefix());
        setClassOwnership(addMetadataRequest.getParentClass(), addMetadataRequest.getTargetClass(), addMetadataRequest.getRequestedMetadata(), info);
    }

    @Override
    public void overrideViaAnnotation(OverrideViaAnnotationRequest overrideViaAnnotationRequest) {
        Map<String, AdminPresentationMapOverride> presentationMapOverrides = new HashMap<String, AdminPresentationMapOverride>();

        AdminPresentationOverrides myOverrides = overrideViaAnnotationRequest.getRequestedEntity().getAnnotation(AdminPresentationOverrides.class);
        if (myOverrides != null) {
            for (AdminPresentationMapOverride myOverride : myOverrides.maps()) {
                presentationMapOverrides.put(myOverride.name(), myOverride);
            }
        }

        for (String propertyName : presentationMapOverrides.keySet()) {
            for (String key : overrideViaAnnotationRequest.getRequestedMetadata().keySet()) {
                if (key.startsWith(propertyName)) {
                    buildAdminPresentationMapOverride(overrideViaAnnotationRequest.getPrefix(), overrideViaAnnotationRequest.getParentExcluded(), overrideViaAnnotationRequest.getRequestedMetadata(), presentationMapOverrides,
                            propertyName, key, overrideViaAnnotationRequest.getDynamicEntityDao());
                }
            }
        }
    }

    @Override
    public void overrideViaXml(OverrideViaXmlRequest overrideViaXmlRequest) {
        Map<String, FieldMetadataOverride> overrides = getTargetedOverride(overrideViaXmlRequest.getRequestedConfigKey(), overrideViaXmlRequest.getRequestedCeilingEntity());
        if (overrides != null) {
            for (String propertyName : overrides.keySet()) {
                final FieldMetadataOverride localMetadata = overrides.get(propertyName);
                for (String key : overrideViaXmlRequest.getRequestedMetadata().keySet()) {
                    if (key.equals(propertyName)) {
                        try {
                            if (overrideViaXmlRequest.getRequestedMetadata().get(key) instanceof MapMetadata) {
                                MapMetadata serverMetadata = (MapMetadata) overrideViaXmlRequest.getRequestedMetadata().get(key);
                                if (serverMetadata.getTargetClass() != null) {
                                    Class<?> targetClass = Class.forName(serverMetadata.getTargetClass());
                                    Class<?> parentClass = null;
                                    if (serverMetadata.getOwningClass() != null) {
                                        parentClass = Class.forName(serverMetadata.getOwningClass());
                                    }
                                    String fieldName = serverMetadata.getFieldName();
                                    Field field = overrideViaXmlRequest.getDynamicEntityDao().getFieldManager().getField(targetClass, fieldName);
                                    Map<String, FieldMetadata> temp = new HashMap<String, FieldMetadata>(1);
                                    temp.put(field.getName(), serverMetadata);
                                    FieldInfo info = buildFieldInfo(field);
                                    buildMapMetadata(parentClass, targetClass, temp, info, localMetadata, overrideViaXmlRequest.getDynamicEntityDao(), serverMetadata.getPrefix());
                                    serverMetadata = (MapMetadata) temp.get(field.getName());
                                    overrideViaXmlRequest.getRequestedMetadata().put(key, serverMetadata);
                                    if (overrideViaXmlRequest.getParentExcluded()) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("applyMapMetadataOverrides:Excluding " + key + "because parent is marked as excluded.");
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

    protected void buildAdminPresentationMapOverride(String prefix, Boolean isParentExcluded, Map<String, FieldMetadata> mergedProperties,
             Map<String, AdminPresentationMapOverride> presentationMapOverrides, String propertyName, String key, DynamicEntityDao dynamicEntityDao) {
        AdminPresentationMapOverride override = presentationMapOverrides.get(propertyName);
        if (override != null) {
            AdminPresentationMap annot = override.value();
            if (annot != null) {
                String testKey = prefix + key;
                if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && annot.excluded()) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("buildAdminPresentationMapOverride:Excluding " + key + "because an override annotation declared " + testKey + "to be excluded");
                    }
                    metadata.setExcluded(true);
                    return;
                }
                if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && !annot.excluded()) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    if (!isParentExcluded) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("buildAdminPresentationMapOverride:Showing " + key + "because an override annotation declared " + testKey + " to not be excluded");
                        }
                        metadata.setExcluded(false);
                    }
                }
                if (!(mergedProperties.get(key) instanceof MapMetadata)) {
                    return;
                }
                MapMetadata serverMetadata = (MapMetadata) mergedProperties.get(key);
                if (serverMetadata.getTargetClass() != null) {
                    try {
                        Class<?> targetClass = Class.forName(serverMetadata.getTargetClass());
                        Class<?> parentClass = null;
                        if (serverMetadata.getOwningClass() != null) {
                            parentClass = Class.forName(serverMetadata.getOwningClass());
                        }
                        String fieldName = serverMetadata.getFieldName();
                        Field field = dynamicEntityDao.getFieldManager().getField(targetClass, fieldName);
                        FieldMetadataOverride localMetadata = constructMapMetadataOverride(annot);
                        //do not include the previous metadata - we want to construct a fresh metadata from the override annotation
                        Map<String, FieldMetadata> temp = new HashMap<String, FieldMetadata>(1);
                        FieldInfo info = buildFieldInfo(field);
                        buildMapMetadata(parentClass, targetClass, temp, info, localMetadata, dynamicEntityDao, serverMetadata.getPrefix());
                        MapMetadata result = (MapMetadata) temp.get(field.getName());
                        result.setInheritedFromType(serverMetadata.getInheritedFromType());
                        result.setAvailableToTypes(serverMetadata.getAvailableToTypes());
                        mergedProperties.put(key, result);
                        if (isParentExcluded) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("buildAdminPresentationMapOverride:Excluding " + key + "because the parent was excluded");
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

    protected FieldMetadataOverride constructMapMetadataOverride(AdminPresentationMap map) {
        if (map != null) {
            FieldMetadataOverride override = new FieldMetadataOverride();
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
            override.setUseServerSideInspectionCache(map.useServerSideInspectionCache());
            override.setDataSourceName(map.dataSourceName());
            override.setExcluded(map.excluded());
            override.setFriendlyName(map.friendlyName());
            override.setReadOnly(map.readOnly());
            override.setOrder(map.order());
            override.setTab(map.tab());
            override.setTabOrder(map.tabOrder());
            override.setSecurityLevel(map.securityLevel());
            override.setTargetElementId(map.targetUIElementId());
            override.setAddType(map.operationTypes().addType());
            override.setFetchType(map.operationTypes().fetchType());
            override.setRemoveType(map.operationTypes().removeType());
            override.setUpdateType(map.operationTypes().updateType());
            override.setInspectType(map.operationTypes().inspectType());
            override.setShowIfProperty(map.showIfProperty());
            override.setCurrencyCodeField(map.currencyCodeField());
            return override;
        }
        throw new IllegalArgumentException("AdminPresentationMap annotation not found on field");
    }

    protected void buildMapMetadata(Class<?> parentClass, Class<?> targetClass, Map<String, FieldMetadata> attributes,
                                    FieldInfo field, FieldMetadataOverride map, DynamicEntityDao dynamicEntityDao, String prefix) {
        MapMetadata serverMetadata = (MapMetadata) attributes.get(field.getName());

        Class<?> resolvedClass = parentClass==null?targetClass:parentClass;
        MapMetadata metadata;
        if (serverMetadata != null) {
            metadata = serverMetadata;
        } else {
            metadata = new MapMetadata();
        }
        if (map.getReadOnly() != null) {
            metadata.setMutable(!map.getReadOnly());
        }
        if (map.getShowIfProperty()!=null) {
            metadata.setShowIfProperty(map.getShowIfProperty());
        }
        metadata.setPrefix(prefix);

        metadata.setTargetClass(targetClass.getName());
        metadata.setFieldName(field.getName());
        org.broadleafcommerce.openadmin.client.dto.OperationTypes dtoOperationTypes = new org.broadleafcommerce.openadmin.client.dto.OperationTypes(OperationType.MAP, OperationType.MAP, OperationType.MAP, OperationType.MAP, OperationType.MAP);
        if (map.getAddType() != null) {
            dtoOperationTypes.setAddType(map.getAddType());
        }
        if (map.getRemoveType() != null) {
            dtoOperationTypes.setRemoveType(map.getRemoveType());
        }
        if (map.getFetchType() != null) {
            dtoOperationTypes.setFetchType(map.getFetchType());
        }
        if (map.getInspectType() != null) {
            dtoOperationTypes.setInspectType(map.getInspectType());
        }
        if (map.getUpdateType() != null) {
            dtoOperationTypes.setUpdateType(map.getUpdateType());
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

        String parentObjectClass = resolvedClass.getName();
        Map idMetadata;
        if(parentClass!=null) {
            idMetadata=dynamicEntityDao.getIdMetadata(parentClass);
        } else {
             idMetadata=dynamicEntityDao.getIdMetadata(targetClass);
        }
        String parentObjectIdField = (String) idMetadata.get("name");

        String keyClassName = null;
        if (serverMetadata != null) {
            keyClassName = ((MapStructure) metadata.getPersistencePerspective().getPersistencePerspectiveItems().get
                    (PersistencePerspectiveItemType.MAPSTRUCTURE)).getKeyClassName();
        }
        if (map.getKeyClass() != null && !void.class.getName().equals(map.getKeyClass())) {
            keyClassName = map.getKeyClass();
        }
        if (keyClassName == null) {
            java.lang.reflect.Type type = field.getGenericType();
            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) type;
                Class<?> clazz = (Class<?>) pType.getActualTypeArguments()[0];
                if (!ArrayUtils.isEmpty(dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(clazz))) {
                    throw new IllegalArgumentException("Key class for AdminPresentationMap was determined to be a JPA managed type. Only primitive types for the key type are currently supported.");
                }
                keyClassName = clazz.getName();
            }
        }
        if (keyClassName == null) {
            keyClassName = String.class.getName();
        }

        String keyPropertyName = "key";
        String keyPropertyFriendlyName = null;
        if (serverMetadata != null) {
            keyPropertyFriendlyName = ((MapStructure) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE)).getKeyPropertyFriendlyName();
        }
        if (map.getKeyPropertyFriendlyName() != null) {
            keyPropertyFriendlyName = map.getKeyPropertyFriendlyName();
        }
        Boolean deleteEntityUponRemove = null;
        if (serverMetadata != null) {
            deleteEntityUponRemove = ((MapStructure) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE)).getDeleteValueEntity();
        }
        if (map.isDeleteEntityUponRemove() != null) {
            deleteEntityUponRemove = map.isDeleteEntityUponRemove();
        }
        String valuePropertyName = "value";
        String valuePropertyFriendlyName = null;
        if (serverMetadata != null) {
            MapStructure structure = (MapStructure) serverMetadata.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
            if (structure instanceof SimpleValueMapStructure) {
                valuePropertyFriendlyName = ((SimpleValueMapStructure) structure).getValuePropertyFriendlyName();
            } else {
                valuePropertyFriendlyName = "";
            }
        }
        if (map.getValuePropertyFriendlyName()!=null) {
            valuePropertyFriendlyName = map.getValuePropertyFriendlyName();
        }
        if (map.getMediaField() != null) {
            metadata.setMediaField(map.getMediaField());
        }

        if (map.getValueClass() != null && !void.class.getName().equals(map.getValueClass())) {
            metadata.setValueClassName(map.getValueClass());
        }
        if (metadata.getValueClassName() == null) {
            java.lang.reflect.Type type = field.getGenericType();
            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) type;
                Class<?> clazz = (Class<?>) pType.getActualTypeArguments()[1];
                Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(clazz);
                if (!ArrayUtils.isEmpty(entities)) {
                    metadata.setValueClassName(entities[entities.length-1].getName());
                }
            }
        }
        if (metadata.getValueClassName() == null) {
            //ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
            String manyToManyMappedBy = field.getManyToManyMappedBy();
            if (!StringUtils.isEmpty(field.getManyToManyTargetEntity())) {
                metadata.setValueClassName(field.getManyToManyMappedBy());
            }
        }
        if (metadata.getValueClassName() == null) {
            metadata.setValueClassName(String.class.getName());
        }

        Boolean simpleValue = null;
        if (map.getSimpleValue()!= null && map.getSimpleValue()!= UnspecifiedBooleanType.UNSPECIFIED) {
            simpleValue = map.getSimpleValue()==UnspecifiedBooleanType.TRUE;
        }
        if (simpleValue==null) {
            java.lang.reflect.Type type = field.getGenericType();
            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) type;
                Class<?> clazz = (Class<?>) pType.getActualTypeArguments()[1];
                Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(clazz);
                simpleValue = ArrayUtils.isEmpty(entities);
            }
        }
        if (simpleValue==null) {
            //ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
            if (!StringUtils.isEmpty(field.getManyToManyTargetEntity())) {
                simpleValue = false;
            }
        }
        if (simpleValue == null) {
            throw new IllegalArgumentException("Unable to infer if the value for the map is of a complex or simple type based on any parameterized type or ManyToMany annotation. Please explicitly set the isSimpleValue property.");
        }
        metadata.setSimpleValue(simpleValue);

        if (map.getKeys() != null) {
            metadata.setKeys(map.getKeys());
        }

        if (map.getMapKeyOptionEntityClass()!=null) {
            if (!void.class.getName().equals(map.getMapKeyOptionEntityClass())) {
                metadata.setMapKeyOptionEntityClass(map.getMapKeyOptionEntityClass());
            } else {
                metadata.setMapKeyOptionEntityClass("");
            }
        }

        if (map.getMapKeyOptionEntityDisplayField() != null) {
            metadata.setMapKeyOptionEntityDisplayField(map.getMapKeyOptionEntityDisplayField());
        }
        if (map.getMapKeyOptionEntityValueField()!=null) {
            metadata.setMapKeyOptionEntityValueField(map.getMapKeyOptionEntityValueField());
        }

        if (ArrayUtils.isEmpty(metadata.getKeys()) && (StringUtils.isEmpty(metadata.getMapKeyOptionEntityClass()) || StringUtils.isEmpty(metadata.getMapKeyOptionEntityValueField()) || StringUtils.isEmpty(metadata.getMapKeyOptionEntityDisplayField()))) {
            throw new IllegalArgumentException("Could not ascertain method for generating key options for the annotated map ("+field.getName()+"). Must specify either an array of AdminPresentationMapKey values for the keys property, or utilize the mapOptionKeyClass, mapOptionKeyDisplayField and mapOptionKeyValueField properties");
        }

        if (serverMetadata != null) {
            ForeignKey foreignKey = (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
            foreignKey.setManyToField(parentObjectIdField);
            foreignKey.setForeignKeyClass(parentObjectClass);
            if (metadata.isSimpleValue()) {
                SimpleValueMapStructure mapStructure = (SimpleValueMapStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
                mapStructure.setKeyClassName(keyClassName);
                mapStructure.setKeyPropertyName(keyPropertyName);
                mapStructure.setKeyPropertyFriendlyName(keyPropertyFriendlyName);
                mapStructure.setValueClassName(metadata.getValueClassName());
                mapStructure.setValuePropertyName(valuePropertyName);
                mapStructure.setValuePropertyFriendlyName(valuePropertyFriendlyName);
                mapStructure.setMapProperty(prefix + field.getName());
            } else {
                MapStructure mapStructure = (MapStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
                mapStructure.setKeyClassName(keyClassName);
                mapStructure.setKeyPropertyName(keyPropertyName);
                mapStructure.setKeyPropertyFriendlyName(keyPropertyFriendlyName);
                mapStructure.setValueClassName(metadata.getValueClassName());
                mapStructure.setMapProperty(prefix + field.getName());
                mapStructure.setDeleteValueEntity(deleteEntityUponRemove);
            }
        } else {
            ForeignKey foreignKey = new ForeignKey(parentObjectIdField, parentObjectClass);
            persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, foreignKey);
            MapStructure mapStructure;
            if (metadata.isSimpleValue()) {
                mapStructure = new SimpleValueMapStructure(keyClassName, keyPropertyName, keyPropertyFriendlyName, metadata.getValueClassName(), valuePropertyName, valuePropertyFriendlyName, prefix + field.getName());
            } else {
                mapStructure = new MapStructure(keyClassName, keyPropertyName, keyPropertyFriendlyName, metadata.getValueClassName(), prefix + field.getName(), deleteEntityUponRemove);
            }
            persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.MAPSTRUCTURE, mapStructure);
        }

        if (map.getExcluded() != null) {
            if (LOG.isDebugEnabled()) {
                if (map.getExcluded()) {
                    LOG.debug("buildMapMetadata:Excluding " + field.getName() + " because it was explicitly declared in config");
                } else {
                    LOG.debug("buildMapMetadata:Showing " + field.getName() + " because it was explicitly declared in config");
                }
            }
            metadata.setExcluded(map.getExcluded());
        }
        if (map.getFriendlyName() != null) {
            metadata.setFriendlyName(map.getFriendlyName());
        }
        if (map.getSecurityLevel() != null) {
            metadata.setSecurityLevel(map.getSecurityLevel());
        }
        if (map.getOrder() != null) {
            metadata.setOrder(map.getOrder());
        }

        if (map.getTab() != null) {
            metadata.setTab(map.getTab());
        }
        if (map.getTabOrder() != null) {
            metadata.setTabOrder(map.getTabOrder());
        }

        if (map.getTargetElementId() != null) {
            metadata.setTargetElementId(map.getTargetElementId());
        }

        if (map.getDataSourceName() != null) {
            metadata.setDataSourceName(map.getDataSourceName());
        }

        if (map.getCustomCriteria() != null) {
            metadata.setCustomCriteria(map.getCustomCriteria());
        }

        if (map.getUseServerSideInspectionCache() != null) {
            persistencePerspective.setUseServerSideInspectionCache(map.getUseServerSideInspectionCache());
        }

        if (map.getCurrencyCodeField()!=null) {
            metadata.setCurrencyCodeField(map.getCurrencyCodeField());
        }

        attributes.put(field.getName(), metadata);
    }
}
