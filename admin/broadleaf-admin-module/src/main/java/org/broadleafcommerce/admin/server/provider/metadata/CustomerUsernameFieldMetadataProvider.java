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
package org.broadleafcommerce.admin.server.provider.metadata;

import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.AbstractFieldMetadataProvider;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.FieldMetadataProvider;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddFieldMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromFieldTypeRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromMappingDataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.LateStageAddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * Modifies Username/EmailAddress fields based on the 'use.email.for.site.login' property
 *
 * @author ckittrell
 */
@Component("blCustomerUsernameFieldMetadataProvider")
@Scope("prototype")
public class CustomerUsernameFieldMetadataProvider extends AbstractFieldMetadataProvider implements FieldMetadataProvider {

    @Value("${use.email.for.site.login:true}")
    protected boolean useEmailForLogin;

    @Override
    public int getOrder() {
        return FieldMetadataProvider.BASIC;
    }

    private boolean canHandleRequest(LateStageAddMetadataRequest addMetadataRequest) {
        return Customer.class.isAssignableFrom(addMetadataRequest.getTargetClass());
    }

    @Override
    public MetadataProviderResponse lateStageAddMetadata(LateStageAddMetadataRequest addMetadataRequest, Map<String, FieldMetadata> metadata) {
        if (!canHandleRequest(addMetadataRequest)) {
            return MetadataProviderResponse.NOT_HANDLED;
        }

        if (useEmailForLogin) {
            BasicFieldMetadata username = (BasicFieldMetadata) metadata.get("username");
            username.setVisibility(VisibilityEnum.HIDDEN_ALL);
            username.setRequiredOverride(false);

            BasicFieldMetadata emailAddress = (BasicFieldMetadata) metadata.get("emailAddress");
            emailAddress.setRequiredOverride(true);
        }

        return MetadataProviderResponse.HANDLED;
    }

    @Override
    public MetadataProviderResponse addMetadataFromFieldType(AddMetadataFromFieldTypeRequest addMetadataFromFieldTypeRequest, Map<String, FieldMetadata> metadata) {
        return MetadataProviderResponse.NOT_HANDLED;
    }

    @Override
    public MetadataProviderResponse addMetadata(AddFieldMetadataRequest addMetadataRequest, Map<String, FieldMetadata> metadata) {
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
