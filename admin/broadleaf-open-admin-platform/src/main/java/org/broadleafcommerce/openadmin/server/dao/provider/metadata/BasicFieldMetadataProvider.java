/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.dao.provider.metadata;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumerationValueImpl;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationDataDrivenEnumeration;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.common.presentation.OptionFilterParam;
import org.broadleafcommerce.common.presentation.OptionFilterParamType;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.presentation.client.LookupType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.override.AdminPresentationDataDrivenEnumerationOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverrides;
import org.broadleafcommerce.common.presentation.override.AdminPresentationToOneLookupOverride;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.override.FieldMetadataOverride;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.dao.FieldInfo;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;
import org.broadleafcommerce.openadmin.server.service.type.FieldProviderResponse;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Component("blBasicFieldMetadataProvider")
@Scope("prototype")
public class BasicFieldMetadataProvider extends FieldMetadataProviderAdapter {

    private static final Log LOG = LogFactory.getLog(BasicFieldMetadataProvider.class);

    protected boolean canHandleFieldForConfiguredMetadata(AddMetadataRequest addMetadataRequest, Map<String, FieldMetadata> metadata) {
        AdminPresentation annot = addMetadataRequest.getRequestedField().getAnnotation(AdminPresentation.class);
        return annot != null;
    }

    protected boolean canHandleAnnotationOverride(OverrideViaAnnotationRequest overrideViaAnnotationRequest, Map<String, FieldMetadata> metadata) {
        AdminPresentationOverrides myOverrides = overrideViaAnnotationRequest.getRequestedEntity().getAnnotation(AdminPresentationOverrides.class);
        AdminPresentationMergeOverrides myMergeOverrides = overrideViaAnnotationRequest.getRequestedEntity().getAnnotation(AdminPresentationMergeOverrides.class);
        return (myOverrides != null && (!ArrayUtils.isEmpty(myOverrides.value()) || !ArrayUtils.isEmpty(myOverrides
                .toOneLookups()) || !ArrayUtils.isEmpty(myOverrides.dataDrivenEnums()))) ||
                myMergeOverrides != null;
    }

    @Override
    public FieldProviderResponse addMetadata(AddMetadataRequest addMetadataRequest, Map<String, FieldMetadata> metadata) {
        if (!canHandleFieldForConfiguredMetadata(addMetadataRequest, metadata)) {
            return FieldProviderResponse.NOT_HANDLED;
        }
        AdminPresentation annot = addMetadataRequest.getRequestedField().getAnnotation(AdminPresentation.class);
        FieldInfo info = buildFieldInfo(addMetadataRequest.getRequestedField());
        FieldMetadataOverride override = constructBasicMetadataOverride(annot, addMetadataRequest.getRequestedField().getAnnotation(AdminPresentationToOneLookup.class),
                addMetadataRequest.getRequestedField().getAnnotation(AdminPresentationDataDrivenEnumeration.class));
        buildBasicMetadata(addMetadataRequest.getParentClass(), addMetadataRequest.getTargetClass(), metadata, info, override, addMetadataRequest.getDynamicEntityDao());
        setClassOwnership(addMetadataRequest.getParentClass(), addMetadataRequest.getTargetClass(), metadata, info);
        return FieldProviderResponse.HANDLED;
    }

    @Override
    public FieldProviderResponse overrideViaAnnotation(OverrideViaAnnotationRequest overrideViaAnnotationRequest, Map<String, FieldMetadata> metadata) {
        if (!canHandleAnnotationOverride(overrideViaAnnotationRequest, metadata)) {
            return FieldProviderResponse.NOT_HANDLED;
        }
        Map<String, AdminPresentationOverride> presentationOverrides = new LinkedHashMap<String, AdminPresentationOverride>();
        Map<String, AdminPresentationToOneLookupOverride> presentationToOneLookupOverrides = new LinkedHashMap<String, AdminPresentationToOneLookupOverride>();
        Map<String, AdminPresentationDataDrivenEnumerationOverride> presentationDataDrivenEnumerationOverrides = new LinkedHashMap<String, AdminPresentationDataDrivenEnumerationOverride>();

        AdminPresentationOverrides myOverrides = overrideViaAnnotationRequest.getRequestedEntity().getAnnotation(AdminPresentationOverrides.class);
        if (myOverrides != null) {
            for (AdminPresentationOverride myOverride : myOverrides.value()) {
                presentationOverrides.put(myOverride.name(), myOverride);
            }
            for (AdminPresentationToOneLookupOverride myOverride : myOverrides.toOneLookups()) {
                presentationToOneLookupOverrides.put(myOverride.name(), myOverride);
            }
            for (AdminPresentationDataDrivenEnumerationOverride myOverride : myOverrides.dataDrivenEnums()) {
                presentationDataDrivenEnumerationOverrides.put(myOverride.name(), myOverride);
            }
        }

        for (String propertyName : presentationOverrides.keySet()) {
            for (String key : metadata.keySet()) {
                if (StringUtils.isEmpty(propertyName) || key.startsWith(propertyName)) {
                    buildAdminPresentationOverride(overrideViaAnnotationRequest.getPrefix(), overrideViaAnnotationRequest.getParentExcluded(), metadata, presentationOverrides, propertyName, key, overrideViaAnnotationRequest.getDynamicEntityDao());
                }
            }
        }
        for (String propertyName : presentationToOneLookupOverrides.keySet()) {
            for (String key : metadata.keySet()) {
                if (key.startsWith(propertyName)) {
                    buildAdminPresentationToOneLookupOverride(metadata, presentationToOneLookupOverrides, propertyName, key);
                }
            }
        }
        for (String propertyName : presentationDataDrivenEnumerationOverrides.keySet()) {
            for (String key : metadata.keySet()) {
                if (key.startsWith(propertyName)) {
                    buildAdminPresentationDataDrivenEnumerationOverride(metadata, presentationDataDrivenEnumerationOverrides, propertyName, key,
                            overrideViaAnnotationRequest.getDynamicEntityDao());
                }
            }
        }

        AdminPresentationMergeOverrides myMergeOverrides = overrideViaAnnotationRequest.getRequestedEntity().
                getAnnotation(AdminPresentationMergeOverrides.class);
        if (myMergeOverrides != null) {
            for (AdminPresentationMergeOverride override : myMergeOverrides.value()) {
                String propertyName = override.name();
                Map<String, FieldMetadata> loopMap = new HashMap<String, FieldMetadata>();
                loopMap.putAll(metadata);
                for (Map.Entry<String, FieldMetadata> entry : loopMap.entrySet()) {
                    if (entry.getKey().equals(propertyName) || StringUtils.isEmpty(propertyName)) {
                        FieldMetadata targetMetadata = entry.getValue();
                        if (targetMetadata instanceof BasicFieldMetadata) {
                            BasicFieldMetadata serverMetadata = (BasicFieldMetadata) targetMetadata;
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
                                    temp.put(fieldName, serverMetadata);
                                    FieldInfo info;
                                    if (field != null) {
                                        info = buildFieldInfo(field);
                                    } else {
                                        info = new FieldInfo();
                                        info.setName(fieldName);
                                    }
                                    FieldMetadataOverride fieldMetadataOverride = overrideMergeMetadata(override);
                                    if (serverMetadata.getExcluded() != null && serverMetadata.getExcluded() &&
                                            (fieldMetadataOverride.getExcluded() == null || fieldMetadataOverride.getExcluded())) {
                                        continue;
                                    }
                                    buildBasicMetadata(parentClass, targetClass, temp, info, fieldMetadataOverride,
                                            overrideViaAnnotationRequest.getDynamicEntityDao());
                                    serverMetadata = (BasicFieldMetadata) temp.get(fieldName);
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

        return FieldProviderResponse.HANDLED;
    }

    @Override
    public FieldProviderResponse overrideViaXml(OverrideViaXmlRequest overrideViaXmlRequest, Map<String, FieldMetadata> metadata) {
        Map<String, FieldMetadataOverride> overrides = getTargetedOverride(overrideViaXmlRequest.getRequestedConfigKey(), overrideViaXmlRequest.getRequestedCeilingEntity());
        if (overrides != null) {
            for (String propertyName : overrides.keySet()) {
                final FieldMetadataOverride localMetadata = overrides.get(propertyName);
                for (String key : metadata.keySet()) {
                    if (key.equals(propertyName)) {
                        try {
                            if (metadata.get(key) instanceof BasicFieldMetadata) {
                                BasicFieldMetadata serverMetadata = (BasicFieldMetadata) metadata.get(key);
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
                                    buildBasicMetadata(parentClass, targetClass, temp, info, localMetadata,
                                            overrideViaXmlRequest.getDynamicEntityDao());
                                    serverMetadata = (BasicFieldMetadata) temp.get(field.getName());
                                    metadata.put(key, serverMetadata);
                                    if (overrideViaXmlRequest.getParentExcluded()) {
                                        if (LOG.isDebugEnabled()) {
                                            LOG.debug("applyMetadataOverrides:Excluding " + key + "because the parent was excluded");
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
        return FieldProviderResponse.HANDLED;
    }

    protected void buildAdminPresentationToOneLookupOverride(Map<String, FieldMetadata> mergedProperties, Map<String, AdminPresentationToOneLookupOverride> presentationOverrides, String propertyName, String key) {
        AdminPresentationToOneLookupOverride override = presentationOverrides.get(propertyName);
        if (override != null) {
            AdminPresentationToOneLookup annot = override.value();
            if (annot != null) {
                if (!(mergedProperties.get(key) instanceof BasicFieldMetadata)) {
                    return;
                }
                BasicFieldMetadata metadata = (BasicFieldMetadata) mergedProperties.get(key);
                metadata.setFieldType(SupportedFieldType.ADDITIONAL_FOREIGN_KEY);
                metadata.setExplicitFieldType(SupportedFieldType.ADDITIONAL_FOREIGN_KEY);
                metadata.setLookupDisplayProperty(annot.lookupDisplayProperty());
                if (!StringUtils.isEmpty(annot.lookupDisplayProperty())) {
                    metadata.setForeignKeyDisplayValueProperty(annot.lookupDisplayProperty());
                }
                metadata.setCustomCriteria(annot.customCriteria());
                metadata.setUseServerSideInspectionCache(annot.useServerSideInspectionCache());
            }
        }
    }

    protected void buildAdminPresentationDataDrivenEnumerationOverride(Map<String, FieldMetadata> mergedProperties, Map<String, AdminPresentationDataDrivenEnumerationOverride> presentationOverrides, String propertyName, String key, DynamicEntityDao dynamicEntityDao) {
        AdminPresentationDataDrivenEnumerationOverride override = presentationOverrides.get(propertyName);
        if (override != null) {
            AdminPresentationDataDrivenEnumeration annot = override.value();
            if (annot != null) {
                if (!(mergedProperties.get(key) instanceof BasicFieldMetadata)) {
                    return;
                }
                BasicFieldMetadata metadata = (BasicFieldMetadata) mergedProperties.get(key);
                metadata.setFieldType(SupportedFieldType.DATA_DRIVEN_ENUMERATION);
                metadata.setExplicitFieldType(SupportedFieldType.DATA_DRIVEN_ENUMERATION);
                metadata.setOptionListEntity(annot.optionListEntity().getName());
                if (metadata.getOptionListEntity().equals(DataDrivenEnumerationValueImpl.class.getName())) {
                    metadata.setOptionValueFieldName("key");
                    metadata.setOptionDisplayFieldName("display");
                } else if (metadata.getOptionListEntity() == null && (StringUtils.isEmpty(metadata.getOptionValueFieldName()) || StringUtils.isEmpty(metadata.getOptionDisplayFieldName()))) {
                    throw new IllegalArgumentException("Problem setting up data driven enumeration for ("+propertyName+"). The optionListEntity, optionValueFieldName and optionDisplayFieldName properties must all be included if not using DataDrivenEnumerationValueImpl as the optionListEntity.");
                } else {
                    metadata.setOptionValueFieldName(annot.optionValueFieldName());
                    metadata.setOptionDisplayFieldName(annot.optionDisplayFieldName());
                }
                if (!ArrayUtils.isEmpty(annot.optionFilterParams())) {
                    String[][] params = new String[annot.optionFilterParams().length][3];
                    for (int j=0;j<params.length;j++) {
                        params[j][0] = annot.optionFilterParams()[j].param();
                        params[j][1] = annot.optionFilterParams()[j].value();
                        params[j][2] = String.valueOf(annot.optionFilterParams()[j].paramType());
                    }
                    metadata.setOptionFilterParams(params);
                } else {
                    metadata.setOptionFilterParams(new String[][]{});
                }
                if (!StringUtils.isEmpty(metadata.getOptionListEntity())) {
                    buildDataDrivenList(metadata, dynamicEntityDao);
                }
            }
        }
    }

    protected void buildAdminPresentationOverride(String prefix, Boolean isParentExcluded, Map<String, FieldMetadata> mergedProperties, Map<String, AdminPresentationOverride> presentationOverrides, String propertyName, String key, DynamicEntityDao dynamicEntityDao) {
        AdminPresentationOverride override = presentationOverrides.get(propertyName);
        if (override != null) {
            AdminPresentation annot = override.value();
            if (annot != null) {
                String testKey = prefix + key;
                if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && annot.excluded()) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("buildAdminPresentationOverride:Excluding " + key + "because an override annotation declared "+ testKey + " to be excluded");
                    }
                    metadata.setExcluded(true);
                    return;
                }
                if ((testKey.startsWith(propertyName + ".") || testKey.equals(propertyName)) && !annot.excluded()) {
                    FieldMetadata metadata = mergedProperties.get(key);
                    if (!isParentExcluded) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("buildAdminPresentationOverride:Showing " + key + "because an override annotation declared " + testKey + " to not be excluded");
                        }
                        metadata.setExcluded(false);
                    }
                }
                if (!(mergedProperties.get(key) instanceof BasicFieldMetadata)) {
                    return;
                }
                BasicFieldMetadata serverMetadata = (BasicFieldMetadata) mergedProperties.get(key);
                if (serverMetadata.getTargetClass() != null) {
                    try {
                        Class<?> targetClass = Class.forName(serverMetadata.getTargetClass());
                        Class<?> parentClass = null;
                        if (serverMetadata.getOwningClass() != null) {
                            parentClass = Class.forName(serverMetadata.getOwningClass());
                        }
                        String fieldName = serverMetadata.getFieldName();
                        Field field = dynamicEntityDao.getFieldManager().getField(targetClass, fieldName);
                        FieldMetadataOverride localMetadata = constructBasicMetadataOverride(annot, null, null);
                        //do not include the previous metadata - we want to construct a fresh metadata from the override annotation
                        Map<String, FieldMetadata> temp = new HashMap<String, FieldMetadata>(1);
                        FieldInfo info = buildFieldInfo(field);
                        buildBasicMetadata(parentClass, targetClass, temp, info, localMetadata, dynamicEntityDao);
                        BasicFieldMetadata result = (BasicFieldMetadata) temp.get(field.getName());
                        result.setInheritedFromType(serverMetadata.getInheritedFromType());
                        result.setAvailableToTypes(serverMetadata.getAvailableToTypes());

                        result.setFieldType(serverMetadata.getFieldType());
                        result.setSecondaryType(serverMetadata.getSecondaryType());
                        result.setLength(serverMetadata.getLength());
                        result.setScale(serverMetadata.getScale());
                        result.setPrecision(serverMetadata.getPrecision());
                        result.setRequired(serverMetadata.getRequired());
                        result.setUnique(serverMetadata.getUnique());
                        result.setForeignKeyCollection(serverMetadata.getForeignKeyCollection());
                        result.setMutable(serverMetadata.getMutable());
                        result.setMergedPropertyType(serverMetadata.getMergedPropertyType());
                        mergedProperties.put(key, result);
                        if (isParentExcluded) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("buildAdminPresentationOverride:Excluding " + key + "because the parent was excluded");
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

    protected FieldMetadataOverride overrideMergeMetadata(AdminPresentationMergeOverride merge) {
        FieldMetadataOverride fieldMetadataOverride = new FieldMetadataOverride();
        Map<String, AdminPresentationMergeEntry> overrideValues = getAdminPresentationEntries(merge.mergeEntries());
        for (Map.Entry<String, AdminPresentationMergeEntry> entry : overrideValues.entrySet()) {
            String stringValue = entry.getValue().overrideValue();
            if (entry.getKey().equals(PropertyType.AdminPresentation.FRIENDLYNAME)) {
                fieldMetadataOverride.setFriendlyName(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.SECURITYLEVEL)) {
                fieldMetadataOverride.setSecurityLevel(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.GROUP)) {
                fieldMetadataOverride.setGroup(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.TAB)) {
                fieldMetadataOverride.setTab(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.COLUMNWIDTH)) {
                fieldMetadataOverride.setColumnWidth(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.BROADLEAFENUMERATION)) {
                fieldMetadataOverride.setBroadleafEnumeration(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.TOOLTIP)) {
                fieldMetadataOverride.setTooltip(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.HELPTEXT)) {
                fieldMetadataOverride.setHelpText(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.HINT)) {
                fieldMetadataOverride.setHint(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.SHOWIFPROPERTY)) {
                fieldMetadataOverride.setShowIfProperty(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.CURRENCYCODEFIELD)) {
                fieldMetadataOverride.setCurrencyCodeField(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.RULEIDENTIFIER)) {
                fieldMetadataOverride.setRuleIdentifier(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.ORDER)) {
                fieldMetadataOverride.setOrder(StringUtils.isEmpty(stringValue)?entry.getValue().intOverrideValue():
                                        Integer.parseInt(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.GRIDORDER)) {
                fieldMetadataOverride.setGridOrder(StringUtils.isEmpty(stringValue)?entry.getValue().intOverrideValue():
                                        Integer.parseInt(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.VISIBILITY)) {
                fieldMetadataOverride.setVisibility(VisibilityEnum.valueOf(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.FIELDTYPE)) {
                fieldMetadataOverride.setFieldType(SupportedFieldType.valueOf(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.GROUPORDER)) {
                fieldMetadataOverride.setGroupOrder(StringUtils.isEmpty(stringValue)?entry.getValue().intOverrideValue():
                                        Integer.parseInt(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.GROUPCOLLAPSED)) {
                fieldMetadataOverride.setGroupCollapsed(StringUtils.isEmpty(stringValue)?entry.getValue().booleanOverrideValue():
                                        Boolean.parseBoolean(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.TABORDER)) {
                fieldMetadataOverride.setTabOrder(StringUtils.isEmpty(stringValue)?entry.getValue().intOverrideValue():
                                        Integer.parseInt(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.LARGEENTRY)) {
                fieldMetadataOverride.setLargeEntry(StringUtils.isEmpty(stringValue)?entry.getValue().booleanOverrideValue():
                                        Boolean.parseBoolean(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.PROMINENT)) {
                fieldMetadataOverride.setProminent(StringUtils.isEmpty(stringValue)?entry.getValue().booleanOverrideValue():
                                        Boolean.parseBoolean(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.READONLY)) {
                fieldMetadataOverride.setReadOnly(StringUtils.isEmpty(stringValue)?entry.getValue().booleanOverrideValue():
                                        Boolean.parseBoolean(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.REQUIREDOVERRIDE)) {
                if (RequiredOverride.IGNORED!=RequiredOverride.valueOf(stringValue)) {
                    fieldMetadataOverride.setRequiredOverride(RequiredOverride.REQUIRED==RequiredOverride.valueOf(stringValue));
                }
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.EXCLUDED)) {
                fieldMetadataOverride.setExcluded(StringUtils.isEmpty(stringValue)?entry.getValue().booleanOverrideValue():
                                        Boolean.parseBoolean(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentation.VALIDATIONCONFIGURATIONS)) {
                processValidationAnnotations(entry.getValue().validationConfigurations(), fieldMetadataOverride);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationToOneLookup.LOOKUPDISPLAYPROPERTY)) {
                fieldMetadataOverride.setLookupDisplayProperty(stringValue);
                fieldMetadataOverride.setForeignKeyDisplayValueProperty(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationToOneLookup.USESERVERSIDEINSPECTIONCACHE)) {
                fieldMetadataOverride.setUseServerSideInspectionCache(StringUtils.isEmpty(stringValue)?
                                        entry.getValue().booleanOverrideValue():Boolean.parseBoolean(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentationToOneLookup.LOOKUPTYPE)) {
                fieldMetadataOverride.setLookupType(LookupType.valueOf(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentationToOneLookup.CUSTOMCRITERIA)) {
                fieldMetadataOverride.setCustomCriteria(entry.getValue().stringArrayOverrideValue());
            } else if (entry.getKey().equals(PropertyType.AdminPresentationDataDrivenEnumeration.OPTIONLISTENTITY)) {
                fieldMetadataOverride.setOptionListEntity(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationDataDrivenEnumeration.OPTIONVALUEFIELDNAME)) {
                fieldMetadataOverride.setOptionValueFieldName(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationDataDrivenEnumeration.OPTIONDISPLAYFIELDNAME)) {
                fieldMetadataOverride.setOptionDisplayFieldName(stringValue);
            } else if (entry.getKey().equals(PropertyType.AdminPresentationDataDrivenEnumeration.OPTIONCANEDITVALUES)) {
                fieldMetadataOverride.setOptionCanEditValues(StringUtils.isEmpty(stringValue) ? entry.getValue()
                                        .booleanOverrideValue() : Boolean.parseBoolean(stringValue));
            } else if (entry.getKey().equals(PropertyType.AdminPresentationDataDrivenEnumeration.OPTIONFILTERPARAMS)) {
                OptionFilterParam[] optionFilterParams = entry.getValue().optionFilterParams();
                String[][] params = new String[optionFilterParams.length][3];
                for (int j=0;j<params.length;j++) {
                    params[j][0] = optionFilterParams[j].param();
                    params[j][1] = optionFilterParams[j].value();
                    params[j][2] = String.valueOf(optionFilterParams[j].paramType());
                }
                fieldMetadataOverride.setOptionFilterValues(params);
            } else {
                throw new IllegalArgumentException("Unrecognized type: " + entry.getKey());
            }
        }

        return fieldMetadataOverride;
    }

    protected FieldMetadataOverride constructBasicMetadataOverride(AdminPresentation annot, AdminPresentationToOneLookup toOneLookup,
                                                                   AdminPresentationDataDrivenEnumeration dataDrivenEnumeration) {
        if (annot != null) {
            FieldMetadataOverride override = new FieldMetadataOverride();
            override.setBroadleafEnumeration(annot.broadleafEnumeration());
            override.setColumnWidth(annot.columnWidth());
            override.setExplicitFieldType(annot.fieldType());
            override.setFieldType(annot.fieldType());
            override.setGroup(annot.group());
            override.setGroupCollapsed(annot.groupCollapsed());
            override.setGroupOrder(annot.groupOrder());
            override.setTab(annot.tab());
            override.setRuleIdentifier(annot.ruleIdentifier());
            override.setTabOrder(annot.tabOrder());
            override.setHelpText(annot.helpText());
            override.setHint(annot.hint());
            override.setLargeEntry(annot.largeEntry());
            override.setFriendlyName(annot.friendlyName());
            override.setSecurityLevel(annot.securityLevel());
            override.setOrder(annot.order());
            override.setGridOrder(annot.gridOrder());
            override.setVisibility(annot.visibility());
            override.setProminent(annot.prominent());
            override.setReadOnly(annot.readOnly());
            override.setShowIfProperty(annot.showIfProperty());
            override.setCurrencyCodeField(annot.currencyCodeField());
            override.setRuleIdentifier(annot.ruleIdentifier());
            override.setTranslatable(annot.translatable());

            if (annot.validationConfigurations().length != 0) {
                processValidationAnnotations(annot.validationConfigurations(), override);
            }
            if (annot.requiredOverride()!= RequiredOverride.IGNORED) {
                override.setRequiredOverride(annot.requiredOverride()==RequiredOverride.REQUIRED);
            }
            override.setExcluded(annot.excluded());
            override.setTooltip(annot.tooltip());

            //the following annotations are complimentary to AdminPresentation
            if (toOneLookup != null) {
                override.setExplicitFieldType(SupportedFieldType.ADDITIONAL_FOREIGN_KEY);
                override.setFieldType(SupportedFieldType.ADDITIONAL_FOREIGN_KEY);
                override.setLookupDisplayProperty(toOneLookup.lookupDisplayProperty());
                override.setCustomCriteria(toOneLookup.customCriteria());
                override.setUseServerSideInspectionCache(toOneLookup.useServerSideInspectionCache());
                override.setToOneLookupCreatedViaAnnotation(true);
                override.setLookupType(toOneLookup.lookupType());
            }

            if (dataDrivenEnumeration != null) {
                override.setExplicitFieldType(SupportedFieldType.DATA_DRIVEN_ENUMERATION);
                override.setFieldType(SupportedFieldType.DATA_DRIVEN_ENUMERATION);
                override.setOptionCanEditValues(dataDrivenEnumeration.optionCanEditValues());
                override.setOptionDisplayFieldName(dataDrivenEnumeration.optionDisplayFieldName());
                if (!ArrayUtils.isEmpty(dataDrivenEnumeration.optionFilterParams())) {
                    Serializable[][] params = new Serializable[dataDrivenEnumeration.optionFilterParams().length][3];
                    for (int j=0;j<params.length;j++) {
                        params[j][0] = dataDrivenEnumeration.optionFilterParams()[j].param();
                        params[j][1] = dataDrivenEnumeration.optionFilterParams()[j].value();
                        params[j][2] = dataDrivenEnumeration.optionFilterParams()[j].paramType();
                    }
                    override.setOptionFilterValues(params);
                }
                override.setOptionListEntity(dataDrivenEnumeration.optionListEntity().getName());
                override.setOptionValueFieldName(dataDrivenEnumeration.optionValueFieldName());
            }


            return override;
        }
        throw new IllegalArgumentException("AdminPresentation annotation not found on field");
    }

    protected void processValidationAnnotations(ValidationConfiguration[] configurations, FieldMetadataOverride override) {
        for (ValidationConfiguration configuration : configurations) {
            ConfigurationItem[] items = configuration.configurationItems();
            Map<String, String> itemMap = new HashMap<String, String>();
            for (ConfigurationItem item : items) {
                itemMap.put(item.itemName(), item.itemValue());
            }
            if (override.getValidationConfigurations() == null) {
                override.setValidationConfigurations(new LinkedHashMap<String, Map<String, String>>(5));
            }
            override.getValidationConfigurations().put(configuration.validationImplementation(), itemMap);
        }
    }

    protected void buildBasicMetadata(Class<?> parentClass, Class<?> targetClass, Map<String, FieldMetadata> attributes,
                                      FieldInfo field, FieldMetadataOverride basicFieldMetadata, DynamicEntityDao dynamicEntityDao) {
        BasicFieldMetadata serverMetadata = (BasicFieldMetadata) attributes.get(field.getName());

        BasicFieldMetadata metadata;
        if (serverMetadata != null) {
            metadata = serverMetadata;
        } else {
            metadata = new BasicFieldMetadata();
        }

        metadata.setName(field.getName());
        metadata.setTargetClass(targetClass.getName());
        metadata.setFieldName(field.getName());

        if (basicFieldMetadata.getFieldType() != null) {
            metadata.setFieldType(basicFieldMetadata.getFieldType());
        }
        if (basicFieldMetadata.getFriendlyName() != null) {
            metadata.setFriendlyName(basicFieldMetadata.getFriendlyName());
        }
        if (basicFieldMetadata.getSecurityLevel() != null) {
            metadata.setSecurityLevel(basicFieldMetadata.getSecurityLevel());
        }
        if (basicFieldMetadata.getVisibility() != null) {
            metadata.setVisibility(basicFieldMetadata.getVisibility());
        }
        if (basicFieldMetadata.getOrder() != null) {
            metadata.setOrder(basicFieldMetadata.getOrder());
        }
        if (basicFieldMetadata.getGridOrder() != null) {
            metadata.setGridOrder(basicFieldMetadata.getGridOrder());
        }
        if (basicFieldMetadata.getExplicitFieldType() != null) {
            metadata.setExplicitFieldType(basicFieldMetadata.getExplicitFieldType());
        }
        if (metadata.getExplicitFieldType()==SupportedFieldType.ADDITIONAL_FOREIGN_KEY) {
            //this is a lookup - exclude the fields on this OneToOne or ManyToOne field
            //metadata.setExcluded(true);
            metadata.setChildrenExcluded(true);
            //metadata.setVisibility(VisibilityEnum.GRID_HIDDEN);
        } else {
            if (basicFieldMetadata.getExcluded()!=null) {
                if (LOG.isDebugEnabled()) {
                    if (basicFieldMetadata.getExcluded()) {
                        LOG.debug("buildBasicMetadata:Excluding " + field.getName() + " because it was explicitly declared in config");
                    } else {
                        LOG.debug("buildBasicMetadata:Showing " + field.getName() + " because it was explicitly declared in config");
                    }
                }
                metadata.setExcluded(basicFieldMetadata.getExcluded());
            }
        }
        if (basicFieldMetadata.getGroup()!=null) {
            metadata.setGroup(basicFieldMetadata.getGroup());
        }
        if (basicFieldMetadata.getGroupOrder()!=null) {
            metadata.setGroupOrder(basicFieldMetadata.getGroupOrder());
        }
        if (basicFieldMetadata.getGroupCollapsed()!=null) {
            metadata.setGroupCollapsed(basicFieldMetadata.getGroupCollapsed());
        }
        if (basicFieldMetadata.getTab() != null) {
            metadata.setTab(basicFieldMetadata.getTab());
        }
        if (basicFieldMetadata.getTabOrder() != null) {
            metadata.setTabOrder(basicFieldMetadata.getTabOrder());
        }
        if (basicFieldMetadata.isLargeEntry()!=null) {
            metadata.setLargeEntry(basicFieldMetadata.isLargeEntry());
        }
        if (basicFieldMetadata.isProminent()!=null) {
            metadata.setProminent(basicFieldMetadata.isProminent());
        }
        if (basicFieldMetadata.getColumnWidth()!=null) {
            metadata.setColumnWidth(basicFieldMetadata.getColumnWidth());
        }
        if (basicFieldMetadata.getBroadleafEnumeration()!=null) {
            metadata.setBroadleafEnumeration(basicFieldMetadata.getBroadleafEnumeration());
        }
        if (!StringUtils.isEmpty(metadata.getBroadleafEnumeration()) && metadata.getFieldType()==SupportedFieldType.BROADLEAF_ENUMERATION) {
            try {
                setupBroadleafEnumeration(metadata.getBroadleafEnumeration(), metadata, dynamicEntityDao);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (basicFieldMetadata.getReadOnly()!=null) {
            metadata.setReadOnly(basicFieldMetadata.getReadOnly());
        }
        if (basicFieldMetadata.getTooltip()!=null) {
            metadata.setTooltip(basicFieldMetadata.getTooltip());
        }
        if (basicFieldMetadata.getHelpText()!=null) {
            metadata.setHelpText(basicFieldMetadata.getHelpText());
        }
        if (basicFieldMetadata.getHint()!=null) {
            metadata.setHint(basicFieldMetadata.getHint());
        }
        if (basicFieldMetadata.getShowIfProperty()!=null) {
            metadata.setShowIfProperty(basicFieldMetadata.getShowIfProperty());
        }
        if (basicFieldMetadata.getCurrencyCodeField()!=null) {
            metadata.setCurrencyCodeField(basicFieldMetadata.getCurrencyCodeField());
        }
        if (basicFieldMetadata.getLookupDisplayProperty()!=null) {
            metadata.setLookupDisplayProperty(basicFieldMetadata.getLookupDisplayProperty());
            metadata.setForeignKeyDisplayValueProperty(basicFieldMetadata.getLookupDisplayProperty());
        }
        if (basicFieldMetadata.getCustomCriteria() != null) {
            metadata.setCustomCriteria(basicFieldMetadata.getCustomCriteria());
        }
        if (basicFieldMetadata.getUseServerSideInspectionCache() != null) {
            metadata.setUseServerSideInspectionCache(basicFieldMetadata.getUseServerSideInspectionCache());
        }
        if (basicFieldMetadata.getToOneLookupCreatedViaAnnotation()!=null) {
            metadata.setToOneLookupCreatedViaAnnotation(basicFieldMetadata.getToOneLookupCreatedViaAnnotation());
        }
        if (basicFieldMetadata.getOptionListEntity()!=null) {
            metadata.setOptionListEntity(basicFieldMetadata.getOptionListEntity());
        }
        if (metadata.getOptionListEntity() != null && metadata.getOptionListEntity().equals(DataDrivenEnumerationValueImpl.class.getName())) {
            metadata.setOptionValueFieldName("key");
            metadata.setOptionDisplayFieldName("display");
        } else {
            if (basicFieldMetadata.getOptionValueFieldName()!=null) {
                metadata.setOptionValueFieldName(basicFieldMetadata.getOptionValueFieldName());
            }
            if (basicFieldMetadata.getOptionDisplayFieldName()!=null) {
                metadata.setOptionDisplayFieldName(basicFieldMetadata.getOptionDisplayFieldName());
            }
        }
        if (!StringUtils.isEmpty(metadata.getOptionListEntity()) && (StringUtils.isEmpty(metadata.getOptionValueFieldName()) || StringUtils.isEmpty(metadata.getOptionDisplayFieldName()))) {
            throw new IllegalArgumentException("Problem setting up data driven enumeration for ("+field.getName()+"). The optionListEntity, optionValueFieldName and optionDisplayFieldName properties must all be included if not using DataDrivenEnumerationValueImpl as the optionListEntity.");
        }
        if (basicFieldMetadata.getOptionFilterValues() != null) {
            String[][] options = new String[basicFieldMetadata.getOptionFilterValues().length][3];
            int j = 0;
            for (Serializable[] option : basicFieldMetadata.getOptionFilterValues()) {
                options[j][0] = String.valueOf(option[0]);
                options[j][1] = String.valueOf(option[1]);
                options[j][2] = String.valueOf(option[2]);
            }
            metadata.setOptionFilterParams(options);
        }
        if (!StringUtils.isEmpty(metadata.getOptionListEntity())) {
            buildDataDrivenList(metadata, dynamicEntityDao);
        }
        if (basicFieldMetadata.getRequiredOverride()!=null) {
            metadata.setRequiredOverride(basicFieldMetadata.getRequiredOverride());
        }
        if (basicFieldMetadata.getValidationConfigurations()!=null) {
            metadata.setValidationConfigurations(basicFieldMetadata.getValidationConfigurations());
        }
        if ((basicFieldMetadata.getFieldType() == SupportedFieldType.RULE_SIMPLE ||
                basicFieldMetadata.getFieldType() == SupportedFieldType.RULE_WITH_QUANTITY)
                && basicFieldMetadata.getRuleIdentifier() == null) {
            throw new IllegalArgumentException("ruleIdentifier property must be set on AdminPresentation when the fieldType is RULE_SIMPLE or RULE_WITH_QUANTITY");
        }
        if (basicFieldMetadata.getRuleIdentifier()!=null) {
            metadata.setRuleIdentifier(basicFieldMetadata.getRuleIdentifier());
        }
        if (basicFieldMetadata.getLookupType()!=null) {
            metadata.setLookupType(basicFieldMetadata.getLookupType());
        }
        if (basicFieldMetadata.getTranslatable() != null) {
            metadata.setTranslatable(basicFieldMetadata.getTranslatable());
        }
        if (basicFieldMetadata.getIsDerived() != null) {
            metadata.setDerived(basicFieldMetadata.getIsDerived());
        }

        attributes.put(field.getName(), metadata);
    }

    protected void buildDataDrivenList(BasicFieldMetadata metadata, DynamicEntityDao dynamicEntityDao) {
        try {
            Criteria criteria = dynamicEntityDao.createCriteria(Class.forName(metadata.getOptionListEntity()));
            if (metadata.getOptionListEntity().equals(DataDrivenEnumerationValueImpl.class.getName())) {
                criteria.add(Restrictions.eq("hidden", false));
            }
            if (metadata.getOptionFilterParams() != null) {
                for (String[] param : metadata.getOptionFilterParams()) {
                    Criteria current = criteria;
                    String key = param[0];
                    if (!key.equals(".ignore")) {
                        if (key.contains(".")) {
                            String[] parts = key.split("\\.");
                            for (int j = 0; j < parts.length - 1; j++) {
                                current = current.createCriteria(parts[j], parts[j]);
                            }
                        }
                        current.add(Restrictions.eq(key, convertType(param[1], OptionFilterParamType.valueOf(param[2]))));
                    }
                }
            }
            List results = criteria.list();
            String[][] enumerationValues = new String[results.size()][2];
            int j = 0;
            for (Object param : results) {
                enumerationValues[j][1] = String.valueOf(dynamicEntityDao.getFieldManager().getFieldValue(param, metadata.getOptionDisplayFieldName()));
                enumerationValues[j][0] = String.valueOf(dynamicEntityDao.getFieldManager().getFieldValue(param, metadata.getOptionValueFieldName()));
                j++;
            }
            if (!CollectionUtils.isEmpty(results) && metadata.getOptionListEntity().equals(DataDrivenEnumerationValueImpl.class.getName())) {
                metadata.setOptionCanEditValues((Boolean) dynamicEntityDao.getFieldManager().getFieldValue(results.get(0), "type.modifiable"));
            }
            metadata.setEnumerationValues(enumerationValues);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getOrder() {
        return FieldMetadataProvider.BASIC;
    }
}
