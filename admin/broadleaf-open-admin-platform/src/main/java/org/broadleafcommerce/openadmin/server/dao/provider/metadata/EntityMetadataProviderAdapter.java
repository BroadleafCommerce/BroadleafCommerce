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

import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.TabMetadata;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromMappingDataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.LateStageAddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.springframework.core.Ordered;

import java.util.Map;

/**
 * @author Chris Kittrell
 */
public class EntityMetadataProviderAdapter extends AbstractEntityMetadataProvider {

    @Override
    public MetadataProviderResponse addTabAndGroupMetadata(AddMetadataRequest addMetadataRequest, Map<String, TabMetadata> metadata) {
        return MetadataProviderResponse.NOT_HANDLED;
    }

    @Override
    public MetadataProviderResponse overrideMetadataViaAnnotation(OverrideViaAnnotationRequest overrideViaAnnotationRequest, Map<String, TabMetadata> metadata) {
        return MetadataProviderResponse.NOT_HANDLED;
    }

    @Override
    public MetadataProviderResponse overrideMetadataViaXml(OverrideViaXmlRequest overrideViaXmlRequest, Map<String, TabMetadata> metadata) {
        return MetadataProviderResponse.NOT_HANDLED;
    }

    @Override
    public MetadataProviderResponse addTabAndGroupMetadataFromCmdProperties(ClassMetadata cmd, Map<String, TabMetadata> metadata) {
        return MetadataProviderResponse.NOT_HANDLED;
    }

    @Override
    public MetadataProviderResponse lateStageAddEntityMetadata(LateStageAddMetadataRequest addMetadataRequest, Map<String, TabMetadata> metadata) {
        return MetadataProviderResponse.NOT_HANDLED;
    }

    @Override
    public MetadataProviderResponse addEntityMetadataFromMappingData(AddMetadataFromMappingDataRequest addMetadataFromMappingDataRequest, TabMetadata metadata) {
        return MetadataProviderResponse.NOT_HANDLED;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
