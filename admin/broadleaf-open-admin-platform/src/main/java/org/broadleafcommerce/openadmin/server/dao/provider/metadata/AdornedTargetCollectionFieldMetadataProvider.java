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
import org.broadleafcommerce.common.presentation.AdminPresentationAdornedTargetCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationOperationTypes;
import org.broadleafcommerce.common.presentation.client.AdornedTargetAddMethodType;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.override.AdminPresentationAdornedTargetCollectionOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.openadmin.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.ForeignKey;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.dto.override.FieldMetadataOverride;
import org.broadleafcommerce.openadmin.dto.override.MetadataOverride;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.dao.FieldInfo;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddFieldMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromFieldTypeRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.ManyToOne;

/**
 * @author Jeff Fischer
 */
@Component("blAdornedTargetCollectionFieldMetadataProvider")
@Scope("prototype")
public class AdornedTargetCollectionFieldMetadataProvider extends AdvancedCollectionFieldMetadataProvider {

    private static final Log LOG = LogFactory.getLog(AdornedTargetCollectionFieldMetadataProvider.class);

    protected boolean canHandleFieldForConfiguredMetadata(AddFieldMetadataRequest addMetadataRequest, Map<String, FieldMetadata> metadata) {
        AdminPresentationAdornedTargetCollection annot = addMetadataRequest.getRequestedField().getAnnotation(AdminPresentationAdornedTargetCollection.class);
        return annot != null;
    }

    protected boolean canHandleFieldForTypeMetadata(AddMetadataFromFieldTypeRequest addMetadataFromFieldTypeRequest, Map<String, FieldMetadata> metadata) {
        AdminPresentationAdornedTargetCollection annot = addMetadataFromFieldTypeRequest.getRequestedField().getAnnotation(AdminPresentationAdornedTargetCollection.class);
        return annot != null;
    }

    protected boolean canHandleAnnotationOverride(OverrideViaAnnotationRequest overrideViaAnnotationRequest, Map<String, FieldMetadata> metadata) {
        AdminPresentationOverrides myOverrides = overrideViaAnnotationRequest.getRequestedEntity().getAnnotation(AdminPresentationOverrides.class);
        AdminPresentationMergeOverrides myMergeOverrides = overrideViaAnnotationRequest.getRequestedEntity().getAnnotation(AdminPresentationMergeOverrides.class);
        return (myOverrides != null && !ArrayUtils.isEmpty(myOverrides.adornedTargetCollections())) ||
                myMergeOverrides != null;
    }

    @Override
    public MetadataProviderResponse addMetadata(AddFieldMetadataRequest addMetadataRequest, Map<String, FieldMetadata> metadata) {
        if (!canHandleFieldForConfiguredMetadata(addMetadataRequest, metadata)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        AdminPresentationAdornedTargetCollection annot = addMetadataRequest.getRequestedField().getAnnotation(AdminPresentationAdornedTargetCollection.class);
        FieldInfo info = buildFieldInfo(addMetadataRequest.getRequestedField());
        FieldMetadataOverride override = constructAdornedTargetCollectionMetadataOverride(annot);
        buildAdornedTargetCollectionMetadata(addMetadataRequest.getParentClass(), addMetadataRequest.getTargetClass(), metadata, info, override, addMetadataRequest.getDynamicEntityDao());
        setClassOwnership(addMetadataRequest.getParentClass(), addMetadataRequest.getTargetClass(), metadata, info);
        return MetadataProviderResponse.HANDLED;
    }

    @Override
    public MetadataProviderResponse overrideViaAnnotation(OverrideViaAnnotationRequest overrideViaAnnotationRequest, Map<String, FieldMetadata> metadata) {
        if (!canHandleAnnotationOverride(overrideViaAnnotationRequest, metadata)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        Map<String, AdminPresentationAdornedTargetCollectionOverride> presentationAdornedTargetCollectionOverrides = new HashMap<String, AdminPresentationAdornedTargetCollectionOverride>();

        AdminPresentationOverrides myOverrides = overrideViaAnnotationRequest.getRequestedEntity().getAnnotation(AdminPresentationOverrides.class);
        if (myOverrides != null) {
            for (AdminPresentationAdornedTargetCollectionOverride myOverride : myOverrides.adornedTargetCollections()) {
                presentationAdornedTargetCollectionOverrides.put(myOverride.name(), myOverride);
            }
        }

        for (String propertyName : presentationAdornedTargetCollectionOverrides.keySet()) {
            for (String key : metadata.keySet()) {
                if (key.startsWith(propertyName)) {
                    buildAdminPresentationAdornedTargetCollectionOverride(overrideViaAnnotationRequest.getPrefix(), overrideViaAnnotationRequest.getParentExcluded(), metadata, presentationAdornedTargetCollectionOverrides, propertyName, key, overrideViaAnnotationRequest.getDynamicEntityDao());
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
                        if (targetMetadata instanceof AdornedTargetCollectionMetadata) {
                            AdornedTargetCollectionMetadata serverMetadata = (AdornedTargetCollectionMetadata) targetMetadata;
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
                                    FieldMetadataOverride fieldMetadataOverride = overrideAdornedTargetMergeMetadata(override);
                                    if (serverMetadata.getExcluded() != null && serverMetadata.getExcluded() &&
                                            (fieldMetadataOverride.getExcluded() == null || fieldMetadataOverride.getExcluded())) {
                                        continue;
                                    }
                                    buildAdornedTargetCollectionMetadata(parentClass, targetClass, temp, info,
                                            fieldMetadataOverride,
                                            overrideViaAnnotationRequest.getDynamicEntityDao());
                                    serverMetadata = (AdornedTargetCollectionMetadata) temp.get(field.getName());
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
                                if (metadata.get(key) instanceof AdornedTargetCollectionMetadata) {
                                    AdornedTargetCollectionMetadata serverMetadata = (AdornedTargetCollectionMetadata) metadata.get(key);
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
                                        buildAdornedTargetCollectionMetadata(parentClass, targetClass, temp, info, localFieldMetadata, overrideViaXmlRequest.getDynamicEntityDao());
                                        serverMetadata = (AdornedTargetCollectionMetadata) temp.get(field.getName());
                                        metadata.put(key, serverMetadata);
                                        if (overrideViaXmlRequest.getParentExcluded()) {
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
        return MetadataProviderResponse.HANDLED;
    }

    @Override
    public MetadataProviderResponse addMetadataFromFieldType(AddMetadataFromFieldTypeRequest addMetadataFromFieldTypeRequest, Map<String, FieldMetadata> metadata) {
        if (!canHandleFieldForTypeMetadata(addMetadataFromFieldTypeRequest, metadata)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        super.addMetadataFromFieldType(addMetadataFromFieldTypeRequest, metadata);
        //add additional adorned target support
        AdornedTargetCollectionMetadata fieldMetadata = (AdornedTargetCollectionMetadata) addMetadataFromFieldTypeRequest.getPresentationAttribute();
        if (StringUtils.isEmpty(fieldMetadata.getCollectionCeilingEntity())) {
            fieldMetadata.setCollectionCeilingEntity(addMetadataFromFieldTypeRequest.getType().getReturnedClass().getName());
            AdornedTargetList targetList = ((AdornedTargetList) fieldMetadata.getPersistencePerspective().
                    getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST));
            targetList.setAdornedTargetEntityClassname(fieldMetadata.getCollectionCeilingEntity());
        }
        return MetadataProviderResponse.HANDLED;
    }

    protected FieldMetadataOverride overrideAdornedTargetMergeMetadata(AdminPresentationMergeOverride merge) {
        FieldMetadataOverride fieldMetadataOverride = new FieldMetadataOverride();
        Map<String, AdminPresentationMergeEntry> overrideValues = getAdminPresentationEntries(merge.mergeEntries());
        for (Map.Entry<String, AdminPresentationMergeEntry> entry : overrideValues.entrySet()) {
            String stringValue = entry.getValue().overrideValue();
            if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.CURRENCYCODEFIELD)) {
                fieldMetadataOverride.setCurrencyCodeField(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.CUSTOMCRITERIA)) {
                fieldMetadataOverride.setCustomCriteria(entry.getValue().stringArrayOverrideValue());
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.EXCLUDED)) {
                fieldMetadataOverride.setExcluded(StringUtils.isEmpty(stringValue)?entry.getValue().booleanOverrideValue():
                                    Boolean.parseBoolean(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.FRIENDLYNAME)) {
                fieldMetadataOverride.setFriendlyName(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.GRIDVISIBLEFIELDS)) {
                fieldMetadataOverride.setGridVisibleFields(entry.getValue().stringArrayOverrideValue());
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.IGNOREADORNEDPROPERTIES)) {
                fieldMetadataOverride.setIgnoreAdornedProperties(StringUtils.isEmpty(stringValue)?entry.getValue().booleanOverrideValue():
                                    Boolean.parseBoolean(stringValue));
            }else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.ADORNEDTARGETADDTYPE)) {
                fieldMetadataOverride.setAdornedTargetAddMethodType(AdornedTargetAddMethodType.valueOf(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.JOINENTITYCLASS)) {
                fieldMetadataOverride.setJoinEntityClass(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.MAINTAINEDADORNEDTARGETFIELDS)) {
                fieldMetadataOverride.setMaintainedAdornedTargetFields(entry.getValue().stringArrayOverrideValue());
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.OPERATIONTYPES)) {
                AdminPresentationOperationTypes operationType = entry.getValue().operationTypes();
                fieldMetadataOverride.setAddType(operationType.addType());
                fieldMetadataOverride.setRemoveType(operationType.removeType());
                fieldMetadataOverride.setUpdateType(operationType.updateType());
                fieldMetadataOverride.setFetchType(operationType.fetchType());
                fieldMetadataOverride.setInspectType(operationType.inspectType());
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.ORDER)) {
                fieldMetadataOverride.setOrder(StringUtils.isEmpty(stringValue) ? entry.getValue().intOverrideValue() :
                        Integer.parseInt(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.PARENTOBJECTIDPROPERTY)) {
                fieldMetadataOverride.setParentObjectIdProperty(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.PARENTOBJECTPROPERTY)) {
                fieldMetadataOverride.setParentObjectProperty(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.READONLY)) {
                fieldMetadataOverride.setReadOnly(StringUtils.isEmpty(stringValue) ? entry.getValue()
                        .booleanOverrideValue() :
                        Boolean.parseBoolean(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.SECURITYLEVEL)) {
                fieldMetadataOverride.setSecurityLevel(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.SHOWIFPROPERTY)) {
                fieldMetadataOverride.setShowIfProperty(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.SORTASCENDING)) {
                fieldMetadataOverride.setSortAscending(StringUtils.isEmpty(stringValue) ? entry.getValue()
                        .booleanOverrideValue() :
                        Boolean.parseBoolean(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.SORTPROPERTY)) {
                fieldMetadataOverride.setSortProperty(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.TAB)) {
                fieldMetadataOverride.setTab(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.TABORDER)) {
                fieldMetadataOverride.setTabOrder(StringUtils.isEmpty(stringValue) ? entry.getValue()
                        .intOverrideValue() : Integer.parseInt(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.TARGETOBJECTIDPROPERTY)) {
                fieldMetadataOverride.setTargetObjectIdProperty(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.TARGETOBJECTPROPERTY)) {
                fieldMetadataOverride.setTargetObjectProperty(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationAdornedTargetCollection.USESERVERSIDEINSPECTIONCACHE)) {
                fieldMetadataOverride.setUseServerSideInspectionCache(StringUtils.isEmpty(stringValue) ? entry
                        .getValue().booleanOverrideValue() :
                        Boolean.parseBoolean(stringValue));
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Unrecognized type: " + entry.getKey() + ". Not setting on adorned target field.");
                }
            }
        }

        return fieldMetadataOverride;
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
            override.setExcluded(adornedTargetCollection.excluded());
            override.setFriendlyName(adornedTargetCollection.friendlyName());
            override.setReadOnly(adornedTargetCollection.readOnly());
            override.setOrder(adornedTargetCollection.order());
            override.setTab(adornedTargetCollection.tab());
            override.setTabOrder(adornedTargetCollection.tabOrder());
            override.setGroup(adornedTargetCollection.group());
            override.setSecurityLevel(adornedTargetCollection.securityLevel());
            override.setAddType(adornedTargetCollection.operationTypes().addType());
            override.setFetchType(adornedTargetCollection.operationTypes().fetchType());
            override.setRemoveType(adornedTargetCollection.operationTypes().removeType());
            override.setUpdateType(adornedTargetCollection.operationTypes().updateType());
            override.setInspectType(adornedTargetCollection.operationTypes().inspectType());
            override.setShowIfProperty(adornedTargetCollection.showIfProperty());
            override.setCurrencyCodeField(adornedTargetCollection.currencyCodeField());
            override.setAdornedTargetAddMethodType(adornedTargetCollection.addType());
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

        org.broadleafcommerce.openadmin.dto.OperationTypes dtoOperationTypes = new org.broadleafcommerce.openadmin.dto.OperationTypes(OperationType.ADORNEDTARGETLIST, OperationType.ADORNEDTARGETLIST, OperationType.ADORNEDTARGETLIST, OperationType.ADORNEDTARGETLIST, OperationType.BASIC);
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
        if (StringUtils.isEmpty(parentObjectIdProperty)) {
            throw new IllegalArgumentException("targetObjectProperty not defined");
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
            adornedTargetList.setIdProperty((String) dynamicEntityDao.getIdMetadata(collectionTarget).get("name"));
            adornedTargetList.setAdornedTargetEntityClassname(collectionTarget.getName());
            adornedTargetList.setSortField(sortProperty);
            adornedTargetList.setSortAscending(isAscending);
            adornedTargetList.setMutable(metadata.isMutable());
        } else {
            AdornedTargetList adornedTargetList = new AdornedTargetList(field.getName(), parentObjectProperty, parentObjectIdProperty, targetObjectProperty, targetObjectIdProperty, collectionTarget.getName(), sortProperty, isAscending);
            adornedTargetList.setJoinEntityClass(joinEntityClass);
            adornedTargetList.setIdProperty((String) dynamicEntityDao.getIdMetadata(collectionTarget).get("name"));
            adornedTargetList.setMutable(metadata.isMutable());
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

        if (adornedTargetCollectionMetadata.getGroup() != null) {
            metadata.setGroup(adornedTargetCollectionMetadata.getGroup());
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
        if (adornedTargetCollectionMetadata.getAdornedTargetAddMethodType()!= null) {
            metadata.setAdornedTargetAddMethodType(adornedTargetCollectionMetadata.getAdornedTargetAddMethodType());
        }
        if (adornedTargetCollectionMetadata.getCurrencyCodeField()!=null) {
            metadata.setCurrencyCodeField(adornedTargetCollectionMetadata.getCurrencyCodeField());
        }

        attributes.put(field.getName(), metadata);
    }

    @Override
    public int getOrder() {
        return FieldMetadataProvider.ADORNED_TARGET;
    }
}
