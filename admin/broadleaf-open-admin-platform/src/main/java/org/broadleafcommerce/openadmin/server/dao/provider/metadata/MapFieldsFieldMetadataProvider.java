/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2024 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.dao.provider.metadata;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.AdminPresentationMapField;
import org.broadleafcommerce.common.presentation.AdminPresentationMapFields;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.override.FieldMetadataOverride;
import org.broadleafcommerce.openadmin.server.dao.FieldInfo;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddFieldMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromFieldTypeRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.Type;
import org.hibernate.type.spi.TypeConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Component("blMapFieldsFieldMetadataProvider")
@Scope("prototype")
public class MapFieldsFieldMetadataProvider extends DefaultFieldMetadataProvider {

    private static final Log LOG = LogFactory.getLog(MapFieldsFieldMetadataProvider.class);

    @Override
    protected boolean canHandleFieldForConfiguredMetadata(
            AddFieldMetadataRequest addMetadataRequest,
            Map<String, FieldMetadata> metadata
    ) {
        AdminPresentationMapFields annot = addMetadataRequest.getRequestedField()
                .getAnnotation(AdminPresentationMapFields.class);
        return annot != null;
    }

    protected boolean canHandleFieldForTypeMetadata(
            AddMetadataFromFieldTypeRequest addMetadataFromFieldTypeRequest,
            Map<String, FieldMetadata> metadata
    ) {
        AdminPresentationMapFields annot = addMetadataFromFieldTypeRequest.getRequestedField()
                .getAnnotation(AdminPresentationMapFields.class);
        return annot != null;
    }

    @Override
    public MetadataProviderResponse addMetadata(
            AddFieldMetadataRequest addMetadataRequest,
            Map<String, FieldMetadata> metadata
    ) {
        if (!canHandleFieldForConfiguredMetadata(addMetadataRequest, metadata)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        AdminPresentationMapFields annot = addMetadataRequest.getRequestedField()
                .getAnnotation(AdminPresentationMapFields.class);
        for (AdminPresentationMapField mapField : annot.mapDisplayFields()) {
            if (mapField.fieldPresentation().fieldType() == SupportedFieldType.UNKNOWN) {
                throw new IllegalArgumentException("fieldType property on AdminPresentation must be set for AdminPresentationMapField");
            }
            FieldMetadataOverride override = constructBasicMetadataOverride(
                    mapField.fieldPresentation(), null, null
            );
            override.setFriendlyName(mapField.fieldPresentation().friendlyName());
            FieldInfo myInfo = new FieldInfo();
            myInfo.setName(addMetadataRequest.getRequestedField().getName() + FieldManager.MAPFIELDSEPARATOR + mapField.fieldName());
            buildBasicMetadata(
                    addMetadataRequest.getParentClass(),
                    addMetadataRequest.getTargetClass(),
                    metadata,
                    myInfo,
                    override,
                    addMetadataRequest.getDynamicEntityDao()
            );
            setClassOwnership(addMetadataRequest.getParentClass(), addMetadataRequest.getTargetClass(), metadata, myInfo);
            BasicFieldMetadata basicFieldMetadata = (BasicFieldMetadata) metadata.get(myInfo.getName());
            if (!mapField.targetClass().equals(Void.class)) {
                if (mapField.targetClass().isInterface()) {
                    throw new IllegalArgumentException("targetClass on @AdminPresentationMapField must be a concrete class");
                }
                basicFieldMetadata.setMapFieldValueClass(mapField.targetClass().getName());
            }
            if (!StringUtils.isEmpty(mapField.manyToField())) {
                basicFieldMetadata.setManyToField(mapField.manyToField());
            }
            AdminPresentationMap annotMap = addMetadataRequest.getRequestedField().getAnnotation(AdminPresentationMap.class);
            if (annotMap != null && !StringUtils.isEmpty(annotMap.toOneTargetProperty())) {
                basicFieldMetadata.setToOneTargetProperty(annotMap.toOneTargetProperty());
            } else if (!StringUtils.isEmpty(annot.toOneTargetProperty())) {
                basicFieldMetadata.setToOneTargetProperty(annot.toOneTargetProperty());
            }
            if (annotMap != null && !StringUtils.isEmpty(annotMap.toOneParentProperty())) {
                basicFieldMetadata.setToOneParentProperty(annotMap.toOneParentProperty());
            } else if (!StringUtils.isEmpty(annot.toOneParentProperty())) {
                basicFieldMetadata.setToOneParentProperty(annot.toOneParentProperty());
            }
            String mapKeyValueProperty = "key";
            if (StringUtils.isNotBlank(myInfo.getMapKey())) {
                mapKeyValueProperty = myInfo.getMapKey();
            }
            if (annotMap != null) {
                if (StringUtils.isNotBlank(annotMap.mapKeyValueProperty())) {
                    mapKeyValueProperty = annotMap.mapKeyValueProperty();
                }
            }
            basicFieldMetadata.setMapKeyValueProperty(mapKeyValueProperty);
        }
        return MetadataProviderResponse.HANDLED;
    }

    @Override
    public MetadataProviderResponse addMetadataFromFieldType(
            AddMetadataFromFieldTypeRequest addMetadataFromFieldTypeRequest,
            Map<String, FieldMetadata> metadata
    ) {
        if (!canHandleFieldForTypeMetadata(addMetadataFromFieldTypeRequest, metadata)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        //look for any map field metadata that was previously added for the requested field
        for (Map.Entry<String, FieldMetadata> entry : addMetadataFromFieldTypeRequest.getPresentationAttributes().entrySet()) {
            if (entry.getKey().startsWith(addMetadataFromFieldTypeRequest.getRequestedPropertyName() + FieldManager.MAPFIELDSEPARATOR)) {
                TypeConfiguration typeConfiguration = new TypeConfiguration();

                Type myType = null;
                //first, check if an explicit type was declared
                String valueClass = ((BasicFieldMetadata) entry.getValue()).getMapFieldValueClass();
                if (valueClass != null) {
                    myType = new ManyToOneType(typeConfiguration, valueClass);
                }
                if (myType == null) {
                    SupportedFieldType fieldType = ((BasicFieldMetadata) entry.getValue()).getExplicitFieldType();
                    Class<?> basicJavaType = getBasicJavaType(fieldType);
                    if (basicJavaType != null) {
                        myType = typeConfiguration.getBasicTypeForJavaType(basicJavaType);
                    }
                }
                if (myType == null) {
                    java.lang.reflect.Type genericType = addMetadataFromFieldTypeRequest.getRequestedField().getGenericType();
                    if (genericType instanceof ParameterizedType) {
                        ParameterizedType pType = (ParameterizedType) genericType;
                        Class<?> clazz = (Class<?>) pType.getActualTypeArguments()[1];
                        Class<?>[] entities = addMetadataFromFieldTypeRequest.getDynamicEntityDao()
                                .getAllPolymorphicEntitiesFromCeiling(clazz);
                        if (!ArrayUtils.isEmpty(entities)) {
                            myType = new ManyToOneType(typeConfiguration, entities[entities.length - 1].getName());
                        }
                    }
                }
                if (myType == null) {
                    throw new IllegalArgumentException("Unable to establish the type for the property (" + entry.getKey() + ")");
                }
                //add property for this map field as if it was a normal field
                super.addMetadataFromFieldType(new AddMetadataFromFieldTypeRequest(
                                addMetadataFromFieldTypeRequest.getRequestedField(),
                                addMetadataFromFieldTypeRequest.getTargetClass(),
                                addMetadataFromFieldTypeRequest.getForeignField(),
                                addMetadataFromFieldTypeRequest.getAdditionalForeignFields(),
                                addMetadataFromFieldTypeRequest.getMergedPropertyType(),
                                addMetadataFromFieldTypeRequest.getComponentProperties(),
                                addMetadataFromFieldTypeRequest.getIdProperty(),
                                addMetadataFromFieldTypeRequest.getPrefix(),
                                entry.getKey(),
                                myType,
                                addMetadataFromFieldTypeRequest.isPropertyForeignKey(),
                                addMetadataFromFieldTypeRequest.getAdditionalForeignKeyIndexPosition(),
                                addMetadataFromFieldTypeRequest.getPresentationAttributes(),
                                entry.getValue(),
                                ((BasicFieldMetadata) entry.getValue()).getExplicitFieldType(),
                                myType.getReturnedClass(),
                                addMetadataFromFieldTypeRequest.getDynamicEntityDao()
                        ),
                        metadata
                );
            }
        }
        return MetadataProviderResponse.HANDLED;
    }

    @Override
    public MetadataProviderResponse overrideViaAnnotation(
            OverrideViaAnnotationRequest overrideViaAnnotationRequest,
            Map<String, FieldMetadata> metadata
    ) {
        //TODO support annotation override
        return MetadataProviderResponse.NOT_HANDLED;
    }

    @Override
    public MetadataProviderResponse overrideViaXml(
            OverrideViaXmlRequest overrideViaXmlRequest,
            Map<String, FieldMetadata> metadata
    ) {
        //TODO support xml override
        return MetadataProviderResponse.NOT_HANDLED;
    }

    @Override
    public int getOrder() {
        return FieldMetadataProvider.MAP_FIELD;
    }

}
