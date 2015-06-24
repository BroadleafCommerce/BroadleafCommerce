/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.dao.provider.metadata;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.AdminPresentationOperationTypes;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.UnspecifiedBooleanType;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMapOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.ForeignKey;
import org.broadleafcommerce.openadmin.dto.MapMetadata;
import org.broadleafcommerce.openadmin.dto.MapStructure;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.dto.SimpleValueMapStructure;
import org.broadleafcommerce.openadmin.dto.override.FieldMetadataOverride;
import org.broadleafcommerce.openadmin.dto.override.MetadataOverride;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.dao.FieldInfo;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromFieldTypeRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Component("blMapFieldMetadataProvider")
@Scope("prototype")
public class MapFieldMetadataProvider extends AdvancedCollectionFieldMetadataProvider {

    private static final Log LOG = LogFactory.getLog(MapFieldMetadataProvider.class);

    protected boolean canHandleFieldForConfiguredMetadata(AddMetadataRequest addMetadataRequest, Map<String, FieldMetadata> metadata) {
        AdminPresentationMap annot = addMetadataRequest.getRequestedField().getAnnotation(AdminPresentationMap.class);
        return annot != null;
    }

    protected boolean canHandleAnnotationOverride(OverrideViaAnnotationRequest overrideViaAnnotationRequest, Map<String, FieldMetadata> metadata) {
        AdminPresentationOverrides myOverrides = overrideViaAnnotationRequest.getRequestedEntity().getAnnotation(AdminPresentationOverrides.class);
        AdminPresentationMergeOverrides myMergeOverrides = overrideViaAnnotationRequest.getRequestedEntity().getAnnotation(AdminPresentationMergeOverrides.class);
        return (myOverrides != null && !ArrayUtils.isEmpty(myOverrides.maps())) || myMergeOverrides != null;
    }

    @Override
    public MetadataProviderResponse addMetadata(AddFieldMetadataRequest addMetadataRequest, Map<String, FieldMetadata> metadata) {
        if (!canHandleFieldForConfiguredMetadata(addMetadataRequest, metadata)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        AdminPresentationMap annot = addMetadataRequest.getRequestedField().getAnnotation(AdminPresentationMap.class);
        FieldInfo info = buildFieldInfo(addMetadataRequest.getRequestedField());
        FieldMetadataOverride override = constructMapMetadataOverride(annot);
        buildMapMetadata(addMetadataRequest.getParentClass(), addMetadataRequest.getTargetClass(),
        metadata, info, override, addMetadataRequest.getDynamicEntityDao(), addMetadataRequest.getPrefix());
        setClassOwnership(addMetadataRequest.getParentClass(), addMetadataRequest.getTargetClass(), metadata, info);
        return MetadataProviderResponse.HANDLED;
    }

    @Override
    public MetadataProviderResponse overrideViaAnnotation(OverrideViaAnnotationRequest overrideViaAnnotationRequest, Map<String, FieldMetadata> metadata) {
        if (!canHandleAnnotationOverride(overrideViaAnnotationRequest, metadata)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        Map<String, AdminPresentationMapOverride> presentationMapOverrides = new HashMap<String, AdminPresentationMapOverride>();

        AdminPresentationOverrides myOverrides = overrideViaAnnotationRequest.getRequestedEntity().getAnnotation(AdminPresentationOverrides.class);
        if (myOverrides != null) {
            for (AdminPresentationMapOverride myOverride : myOverrides.maps()) {
                presentationMapOverrides.put(myOverride.name(), myOverride);
            }
        }

        for (String propertyName : presentationMapOverrides.keySet()) {
            for (String key : metadata.keySet()) {
                if (key.startsWith(propertyName)) {
                    buildAdminPresentationMapOverride(overrideViaAnnotationRequest.getPrefix(), overrideViaAnnotationRequest.getParentExcluded(), metadata, presentationMapOverrides,
                            propertyName, key, overrideViaAnnotationRequest.getDynamicEntityDao());
                }
            }
        }

        AdminPresentationMergeOverrides myMergeOverrides = overrideViaAnnotationRequest.getRequestedEntity().getAnnotation(AdminPresentationMergeOverrides.class);
        if (myMergeOverrides != null) {
            for (AdminPresentationMergeOverride override : myMergeOverrides.value()) {
                String propertyName = override.name();
                Map<String, FieldMetadata> loopMap = new HashMap<String, FieldMetadata>();
                loopMap.putAll(metadata);
                for (Map.Entry<String, FieldMetadata> entry : loopMap.entrySet()) {
                    if (entry.getKey().startsWith(propertyName) || StringUtils.isEmpty(propertyName)) {
                        FieldMetadata targetMetadata = entry.getValue();
                        if (targetMetadata instanceof MapMetadata) {
                            MapMetadata serverMetadata = (MapMetadata) targetMetadata;
                            if (serverMetadata.getTargetClass() != null) {
                                try {
                                    Class<?> targetClass = Class.forName(serverMetadata.getTargetClass());
                                    Class<?> parentClass = null;
                                    if (serverMetadata.getOwningClass() != null) {
                                        parentClass = Class.forName(serverMetadata.getOwningClass());
                                    }
                                    String fieldName = serverMetadata.getFieldName();
                                    Field field = overrideViaAnnotationRequest.getDynamicEntityDao().getFieldManager()
                                                .getField(targetClass, fieldName);
                                    Map<String, FieldMetadata> temp = new HashMap<String, FieldMetadata>(1);
                                    temp.put(field.getName(), serverMetadata);
                                    FieldInfo info = buildFieldInfo(field);
                                    FieldMetadataOverride fieldMetadataOverride = overrideMapMergeMetadata(override);
                                    if (serverMetadata.getExcluded() != null && serverMetadata.getExcluded() &&
                                            (fieldMetadataOverride.getExcluded() == null || fieldMetadataOverride.getExcluded())) {
                                        continue;
                                    }
                                    buildMapMetadata(parentClass, targetClass, temp, info, fieldMetadataOverride,
                                            overrideViaAnnotationRequest.getDynamicEntityDao(), serverMetadata.getPrefix());
                                    serverMetadata = (MapMetadata) temp.get(field.getName());
                                    metadata.put(entry.getKey(), serverMetadata);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }
            }
        }

        return MetadataProviderResponse.HANDLED;
    }

    @Override
    public MetadataProviderResponse overrideViaXml(OverrideViaXmlRequest overrideViaXmlRequest, Map<String, FieldMetadata> metadata) {
        Map<String, MetadataOverride> overrides = getTargetedOverride(overrideViaXmlRequest.getDynamicEntityDao(), overrideViaXmlRequest.getRequestedConfigKey(), overrideViaXmlRequest.getRequestedCeilingEntity());
        if (overrides != null) {
            for (String propertyName : overrides.keySet()) {
                MetadataOverride localMetadata = overrides.get(propertyName);
                if (localMetadata instanceof FieldMetadataOverride) {
                    FieldMetadataOverride localFieldMetadata = (FieldMetadataOverride) localMetadata;
                    for (String key : metadata.keySet()) {
                        if (key.equals(propertyName)) {
                            try {
                                if (metadata.get(key) instanceof MapMetadata) {
                                    MapMetadata serverMetadata = (MapMetadata) metadata.get(key);
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
                                        buildMapMetadata(parentClass, targetClass, temp, info, localFieldMetadata, overrideViaXmlRequest.getDynamicEntityDao(), serverMetadata.getPrefix());
                                        serverMetadata = (MapMetadata) temp.get(field.getName());
                                        metadata.put(key, serverMetadata);
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
        return MetadataProviderResponse.HANDLED;
    }

    @Override
    public MetadataProviderResponse addMetadataFromFieldType(AddMetadataFromFieldTypeRequest addMetadataFromFieldTypeRequest, Map<String, FieldMetadata> metadata) {
        if (!canHandleFieldForTypeMetadata(addMetadataFromFieldTypeRequest, metadata)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        //do nothing but add the property without manipulation
        metadata.put(addMetadataFromFieldTypeRequest.getRequestedPropertyName(), addMetadataFromFieldTypeRequest.getPresentationAttribute());
        return MetadataProviderResponse.HANDLED;
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

    protected FieldMetadataOverride overrideMapMergeMetadata(AdminPresentationMergeOverride merge) {
        FieldMetadataOverride fieldMetadataOverride = new FieldMetadataOverride();
        Map<String, AdminPresentationMergeEntry> overrideValues = getAdminPresentationEntries(merge.mergeEntries());
        for (Map.Entry<String, AdminPresentationMergeEntry> entry : overrideValues.entrySet()) {
            String stringValue = entry.getValue().overrideValue();
            if (entry.getKey().equals(PropertyType.AdminPresentationMap.CURRENCYCODEFIELD)) {
                fieldMetadataOverride.setCurrencyCodeField(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.CUSTOMCRITERIA)) {
                fieldMetadataOverride.setCustomCriteria(entry.getValue().stringArrayOverrideValue());
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.DELETEENTITYUPONREMOVE)) {
                fieldMetadataOverride.setDeleteEntityUponRemove(StringUtils.isEmpty(stringValue) ? entry.getValue()
                        .booleanOverrideValue() : Boolean.parseBoolean(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.EXCLUDED)) {
                fieldMetadataOverride.setExcluded(StringUtils.isEmpty(stringValue) ? entry.getValue()
                        .booleanOverrideValue() : Boolean.parseBoolean(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.FORCEFREEFORMKEYS)) {
                fieldMetadataOverride.setForceFreeFormKeys(StringUtils.isEmpty(stringValue) ? entry.getValue()
                        .booleanOverrideValue() : Boolean.parseBoolean(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.FRIENDLYNAME)) {
                fieldMetadataOverride.setFriendlyName(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.ISSIMPLEVALUE)) {
                fieldMetadataOverride.setSimpleValue(UnspecifiedBooleanType.valueOf(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.KEYCLASS)) {
                fieldMetadataOverride.setKeyClass(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.KEYPROPERTYFRIENDLYNAME)) {
                fieldMetadataOverride.setKeyPropertyFriendlyName(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.MAPKEYVALUEPROPERTY)) {
                fieldMetadataOverride.setMapKeyValueProperty(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.KEYS)) {
                if (!ArrayUtils.isEmpty(entry.getValue().keys())) {
                    String[][] keys = new String[entry.getValue().keys().length][2];
                    for (int j=0;j<keys.length;j++){
                        keys[j][0] = entry.getValue().keys()[j].keyName();
                        keys[j][1] = entry.getValue().keys()[j].friendlyKeyName();
                    }
                    fieldMetadataOverride.setKeys(keys);
                }
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.MANYTOFIELD)) {
                fieldMetadataOverride.setManyToField(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.MAPKEYOPTIONENTITYCLASS)) {
                fieldMetadataOverride.setMapKeyOptionEntityClass(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.MAPKEYOPTIONENTITYDISPLAYFIELD)) {
                fieldMetadataOverride.setMapKeyOptionEntityDisplayField(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.MAPKEYOPTIONENTITYVALUEFIELD)) {
                fieldMetadataOverride.setMapKeyOptionEntityValueField(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.MEDIAFIELD)) {
                fieldMetadataOverride.setMediaField(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.OPERATIONTYPES)) {
                AdminPresentationOperationTypes operationType = entry.getValue().operationTypes();
                fieldMetadataOverride.setAddType(operationType.addType());
                fieldMetadataOverride.setRemoveType(operationType.removeType());
                fieldMetadataOverride.setUpdateType(operationType.updateType());
                fieldMetadataOverride.setFetchType(operationType.fetchType());
                fieldMetadataOverride.setInspectType(operationType.inspectType());
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.ORDER)) {
                fieldMetadataOverride.setOrder(StringUtils.isEmpty(stringValue) ? entry.getValue().intOverrideValue() :
                        Integer.parseInt(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.READONLY)) {
                fieldMetadataOverride.setReadOnly(StringUtils.isEmpty(stringValue) ? entry.getValue()
                        .booleanOverrideValue() : Boolean.parseBoolean(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.SECURITYLEVEL)) {
                fieldMetadataOverride.setSecurityLevel(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.SHOWIFPROPERTY)) {
                fieldMetadataOverride.setShowIfProperty(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.TAB)) {
                fieldMetadataOverride.setTab(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.TABORDER)) {
                fieldMetadataOverride.setTabOrder(StringUtils.isEmpty(stringValue) ? entry.getValue()
                        .intOverrideValue() : Integer.parseInt(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.USESERVERSIDEINSPECTIONCACHE)) {
                fieldMetadataOverride.setUseServerSideInspectionCache(StringUtils.isEmpty(stringValue) ? entry
                        .getValue().booleanOverrideValue() : Boolean.parseBoolean(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.VALUECLASS)) {
                fieldMetadataOverride.setValueClass(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationMap.VALUEPROPERTYFRIENDLYNAME)) {
                fieldMetadataOverride.setValuePropertyFriendlyName(stringValue);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Unrecognized type: " + entry.getKey() + ". Not setting on map field.");
                }
            }
        }

        return fieldMetadataOverride;
    }

    protected FieldMetadataOverride constructMapMetadataOverride(AdminPresentationMap map) {
        if (map != null) {
            FieldMetadataOverride override = new FieldMetadataOverride();
            override.setDeleteEntityUponRemove(map.deleteEntityUponRemove());
            override.setKeyClass(map.keyClass().getName());
            override.setMapKeyValueProperty(map.mapKeyValueProperty());
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
            override.setToOneTargetProperty(map.toOneTargetProperty());
            override.setSimpleValue(map.isSimpleValue());
            override.setValueClass(map.valueClass().getName());
            override.setValuePropertyFriendlyName(map.valuePropertyFriendlyName());
            override.setCustomCriteria(map.customCriteria());
            override.setUseServerSideInspectionCache(map.useServerSideInspectionCache());
            override.setExcluded(map.excluded());
            override.setFriendlyName(map.friendlyName());
            override.setReadOnly(map.readOnly());
            override.setOrder(map.order());
            override.setTab(map.tab());
            override.setTabOrder(map.tabOrder());
            override.setSecurityLevel(map.securityLevel());
            override.setAddType(map.operationTypes().addType());
            override.setFetchType(map.operationTypes().fetchType());
            override.setRemoveType(map.operationTypes().removeType());
            override.setUpdateType(map.operationTypes().updateType());
            override.setInspectType(map.operationTypes().inspectType());
            override.setShowIfProperty(map.showIfProperty());
            override.setCurrencyCodeField(map.currencyCodeField());
            override.setForceFreeFormKeys(map.forceFreeFormKeys());
            override.setManyToField(map.manyToField());
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
        org.broadleafcommerce.openadmin.dto.OperationTypes dtoOperationTypes = new org.broadleafcommerce.openadmin.dto.OperationTypes(OperationType.MAP, OperationType.MAP, OperationType.MAP, OperationType.MAP, OperationType.MAP);
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
        String mapKeyValueProperty = "";
        if (StringUtils.isNotBlank(field.getMapKey())) {
            mapKeyValueProperty = field.getMapKey();
        }
        if (StringUtils.isNotBlank(map.getMapKeyValueProperty())) {
            mapKeyValueProperty = map.getMapKeyValueProperty();
        }
        
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
        if (map.getToOneTargetProperty() != null) {
            metadata.setToOneTargetProperty(map.getToOneTargetProperty());
        }
        if (map.getToOneParentProperty() != null) {
            metadata.setToOneParentProperty((map.getToOneParentProperty()));
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
            if (!StringUtils.isEmpty(field.getManyToManyTargetEntity())) {
                metadata.setValueClassName(field.getManyToManyTargetEntity());
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
        
        metadata.setMapKeyValueProperty(mapKeyValueProperty);

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

        if (map.getForceFreeFormKeys() != null) {
            if (!map.getForceFreeFormKeys() && ArrayUtils.isEmpty(metadata.getKeys()) && (StringUtils.isEmpty(metadata.getMapKeyOptionEntityClass()) || StringUtils.isEmpty(metadata.getMapKeyOptionEntityValueField()) || StringUtils.isEmpty(metadata.getMapKeyOptionEntityDisplayField()))) {
                throw new IllegalArgumentException("Could not ascertain method for generating key options for the annotated map ("+field.getName()+"). Must specify either an array of AdminPresentationMapKey values for the keys property, or utilize the mapOptionKeyClass, mapOptionKeyDisplayField and mapOptionKeyValueField properties. If you wish to allow free form entry for key values, then set forceFreeFormKeys on AdminPresentationMap.");
            }
        }

        MapStructure mapStructure;
        if (serverMetadata != null) {
            ForeignKey foreignKey = (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
            foreignKey.setManyToField(parentObjectIdField);
            foreignKey.setForeignKeyClass(parentObjectClass);
            if (metadata.isSimpleValue()) {
                mapStructure = (SimpleValueMapStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
                mapStructure.setKeyClassName(keyClassName);
                mapStructure.setKeyPropertyName(keyPropertyName);
                mapStructure.setKeyPropertyFriendlyName(keyPropertyFriendlyName);
                mapStructure.setValueClassName(metadata.getValueClassName());
                ((SimpleValueMapStructure) mapStructure).setValuePropertyName(valuePropertyName);
                ((SimpleValueMapStructure) mapStructure).setValuePropertyFriendlyName(valuePropertyFriendlyName);
                mapStructure.setMapProperty(prefix + field.getName());
                mapStructure.setMutable(metadata.isMutable());
            } else {
                mapStructure = (MapStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
                mapStructure.setKeyClassName(keyClassName);
                mapStructure.setKeyPropertyName(keyPropertyName);
                mapStructure.setKeyPropertyFriendlyName(keyPropertyFriendlyName);
                mapStructure.setValueClassName(metadata.getValueClassName());
                mapStructure.setMapProperty(prefix + field.getName());
                mapStructure.setDeleteValueEntity(deleteEntityUponRemove);
                mapStructure.setMutable(metadata.isMutable());
            }
        } else {
            ForeignKey foreignKey = new ForeignKey(parentObjectIdField, parentObjectClass);
            persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, foreignKey);
            if (metadata.isSimpleValue()) {
                mapStructure = new SimpleValueMapStructure(keyClassName, keyPropertyName, keyPropertyFriendlyName, metadata.getValueClassName(), valuePropertyName, valuePropertyFriendlyName, prefix + field.getName(), mapKeyValueProperty);
                mapStructure.setMutable(metadata.isMutable());
            } else {
                mapStructure = new MapStructure(keyClassName, keyPropertyName, keyPropertyFriendlyName, metadata.getValueClassName(), prefix + field.getName(), deleteEntityUponRemove, mapKeyValueProperty);
                mapStructure.setMutable(metadata.isMutable());
            }
            persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.MAPSTRUCTURE, mapStructure);
        }

        if (!StringUtils.isEmpty(map.getManyToField())) {
            mapStructure.setManyToField(map.getManyToField());
        }
        if (mapStructure.getManyToField() == null) {
            //try to infer the value
            if (field.getManyToManyMappedBy() != null) {
                mapStructure.setManyToField(field.getManyToManyMappedBy());
            }
        }
        if (mapStructure.getManyToField() == null) {
            //try to infer the value
            if (field.getOneToManyMappedBy() != null) {
                mapStructure.setManyToField(field.getOneToManyMappedBy());
            }
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

        if (map.getCustomCriteria() != null) {
            metadata.setCustomCriteria(map.getCustomCriteria());
        }

        if (map.getUseServerSideInspectionCache() != null) {
            persistencePerspective.setUseServerSideInspectionCache(map.getUseServerSideInspectionCache());
        }

        if (map.getCurrencyCodeField()!=null) {
            metadata.setCurrencyCodeField(map.getCurrencyCodeField());
        }

        if (map.getForceFreeFormKeys()!=null) {
            metadata.setForceFreeFormKeys(map.getForceFreeFormKeys());
        }

        attributes.put(field.getName(), metadata);
    }

    @Override
    public int getOrder() {
        return FieldMetadataProvider.MAP;
    }
}
