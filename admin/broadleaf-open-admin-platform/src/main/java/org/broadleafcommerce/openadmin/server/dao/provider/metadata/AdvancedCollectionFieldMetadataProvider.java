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
package org.broadleafcommerce.openadmin.server.dao.provider.metadata;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.CollectionMetadata;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.server.dao.FieldInfo;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromFieldTypeRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Jeff Fischer
 */
public class AdvancedCollectionFieldMetadataProvider extends FieldMetadataProviderAdapter {

    public static String FOREIGN_KEY_ADDITIONAL_METADATA_KEY = "foreign_key";
    
    @Resource(name = "blDefaultFieldMetadataProvider")
    protected DefaultFieldMetadataProvider defaultMetadataProvider;
    
    protected boolean canHandleFieldForTypeMetadata(AddMetadataFromFieldTypeRequest addMetadataFromFieldTypeRequest, Map<String, FieldMetadata> metadata) {
        AdminPresentationMap map = addMetadataFromFieldTypeRequest.getRequestedField().getAnnotation(AdminPresentationMap.class);
        AdminPresentationCollection collection = addMetadataFromFieldTypeRequest.getRequestedField().getAnnotation(AdminPresentationCollection.class);
        return map != null || collection != null;
    }

    @Override
    public MetadataProviderResponse addMetadataFromFieldType(AddMetadataFromFieldTypeRequest addMetadataFromFieldTypeRequest, Map<String, FieldMetadata> metadata) {
        if (!canHandleFieldForTypeMetadata(addMetadataFromFieldTypeRequest, metadata)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }
        CollectionMetadata fieldMetadata = (CollectionMetadata) addMetadataFromFieldTypeRequest.getPresentationAttribute();
        if (StringUtils.isEmpty(fieldMetadata.getCollectionCeilingEntity())) {
            ParameterizedType listType = (ParameterizedType) addMetadataFromFieldTypeRequest.getRequestedField().getGenericType();
            Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
            fieldMetadata.setCollectionCeilingEntity(listClass.getName());
        }
        if (addMetadataFromFieldTypeRequest.getTargetClass() != null) {
            if (StringUtils.isEmpty(fieldMetadata.getInheritedFromType())) {
                fieldMetadata.setInheritedFromType(addMetadataFromFieldTypeRequest.getTargetClass().getName());
            }
            if (ArrayUtils.isEmpty(fieldMetadata.getAvailableToTypes())) {
                fieldMetadata.setAvailableToTypes(new String[]{addMetadataFromFieldTypeRequest.getTargetClass().getName()});
            }
        }
        
        // Handle scenarios where the collection metadata is also a foreign key. The {@link BasicFieldMetadata} that has all
        // of the information about the foreign key will travel along with the built {@link BasicCollectionMetadata} under
        // the {@link FieldMetadata#getAdditionalMetadata()} field. This is then pulled out within
        // {@link BasicPersistenceModule#filterOutCollectionMetadata}
        if (addMetadataFromFieldTypeRequest.getForeignField() != null && addMetadataFromFieldTypeRequest.isPropertyForeignKey()) {
            FieldInfo info = buildFieldInfo(addMetadataFromFieldTypeRequest.getRequestedField());
            BasicFieldMetadata basicMetadata = new BasicFieldMetadata();
            basicMetadata.setName(info.getName());
            basicMetadata.setExcluded(false);
            // Don't show this anywhere on the form and ensure it's explicitly not required
            basicMetadata.setVisibility(VisibilityEnum.HIDDEN_ALL);
            basicMetadata.setRequired(false);
            
            setClassOwnership(addMetadataFromFieldTypeRequest.getReturnedClass(), addMetadataFromFieldTypeRequest.getTargetClass(), metadata, info);
            Map<String, FieldMetadata> fakedMd = new HashMap<String, FieldMetadata>();
            fakedMd.put(addMetadataFromFieldTypeRequest.getRequestedField().getName(), basicMetadata);
            // Fake out a request and some metadata to pass along as additional metadata within this metadata
            AddMetadataFromFieldTypeRequest fakedRequest = new AddMetadataFromFieldTypeRequest(addMetadataFromFieldTypeRequest.getRequestedField(),
                    addMetadataFromFieldTypeRequest.getTargetClass(),
                    addMetadataFromFieldTypeRequest.getForeignField(),
                    addMetadataFromFieldTypeRequest.getAdditionalForeignFields(),
                    addMetadataFromFieldTypeRequest.getMergedPropertyType(),
                    addMetadataFromFieldTypeRequest.getComponentProperties(),
                    addMetadataFromFieldTypeRequest.getIdProperty(),
                    addMetadataFromFieldTypeRequest.getPrefix(),
                    addMetadataFromFieldTypeRequest.getRequestedPropertyName(),
                    addMetadataFromFieldTypeRequest.getType(),
                    addMetadataFromFieldTypeRequest.isPropertyForeignKey(),
                    addMetadataFromFieldTypeRequest.getAdditionalForeignKeyIndexPosition(),
                    fakedMd,
                    basicMetadata,
                    addMetadataFromFieldTypeRequest.getExplicitType(),
                    addMetadataFromFieldTypeRequest.getReturnedClass(),
                    addMetadataFromFieldTypeRequest.getDynamicEntityDao());
            defaultMetadataProvider.addMetadataFromFieldType(fakedRequest, fakedMd);
            fieldMetadata.getAdditionalMetadata().put(FOREIGN_KEY_ADDITIONAL_METADATA_KEY, basicMetadata);
        }
        
        metadata.put(addMetadataFromFieldTypeRequest.getRequestedPropertyName(), fieldMetadata);
        return MetadataProviderResponse.HANDLED;
    }
    
}
