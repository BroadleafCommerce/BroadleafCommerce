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

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddFieldMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromFieldTypeRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromMappingDataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.LateStageAddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Adds a new 'passwordConfirm' field to the metadata as well as ensures that the field type for the password field is
 * actually a password
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blPasswordFieldMetadataProvider")
@Scope("prototype")
public class PasswordFieldMetadataProvider extends AbstractFieldMetadataProvider implements FieldMetadataProvider {

    @Override
    public int getOrder() {
        return FieldMetadataProvider.BASIC;
    }

    @Override
    public MetadataProviderResponse addMetadataFromFieldType(AddMetadataFromFieldTypeRequest addMetadataFromFieldTypeRequest, Map<String, FieldMetadata> metadata) {
        if (addMetadataFromFieldTypeRequest.getPresentationAttribute() instanceof BasicFieldMetadata && 
                SupportedFieldType.PASSWORD.equals(((BasicFieldMetadata) addMetadataFromFieldTypeRequest.getPresentationAttribute()).getExplicitFieldType())) {
            //build the metadata for the password field
            addMetadataFromFieldTypeRequest.getDynamicEntityDao().getDefaultFieldMetadataProvider().addMetadataFromFieldType(addMetadataFromFieldTypeRequest, metadata);
            ((BasicFieldMetadata)addMetadataFromFieldTypeRequest.getPresentationAttribute()).setFieldType(SupportedFieldType.PASSWORD);
            
            //clone the password field and add in a custom one
            BasicFieldMetadata confirmMd = (BasicFieldMetadata) addMetadataFromFieldTypeRequest.getPresentationAttribute().cloneFieldMetadata();
            confirmMd.setFieldName("passwordConfirm");
            confirmMd.setFriendlyName("AdminUserImpl_Admin_Password_Confirm");
            confirmMd.setExplicitFieldType(SupportedFieldType.PASSWORD_CONFIRM);
            confirmMd.setValidationConfigurations(new HashMap<String, List<Map<String,String>>>());
            metadata.put("passwordConfirm", confirmMd);
            return MetadataProviderResponse.HANDLED;
        } else {
            return MetadataProviderResponse.NOT_HANDLED;
        }
    }
    
    @Override
    public MetadataProviderResponse addMetadata(AddFieldMetadataRequest addMetadataRequest, Map<String, FieldMetadata> metadata) {
        return MetadataProviderResponse.NOT_HANDLED;
    }

    @Override
    public MetadataProviderResponse lateStageAddMetadata(LateStageAddMetadataRequest addMetadataRequest, Map<String, FieldMetadata> metadata) {
        return MetadataProviderResponse.NOT_HANDLED;
    }

    @Override
    public MetadataProviderResponse overrideViaAnnotation(OverrideViaAnnotationRequest overrideViaAnnotationRequest, Map<String, FieldMetadata> metadata) {
        return MetadataProviderResponse.NOT_HANDLED;
    }

    @Override
    public MetadataProviderResponse overrideViaXml(OverrideViaXmlRequest overrideViaXmlRequest, Map<String, FieldMetadata> metadata) {
        return MetadataProviderResponse.NOT_HANDLED;
    }

    @Override
    public MetadataProviderResponse addMetadataFromMappingData(AddMetadataFromMappingDataRequest addMetadataFromMappingDataRequest, FieldMetadata metadata) {
        return MetadataProviderResponse.NOT_HANDLED;
    }

}
