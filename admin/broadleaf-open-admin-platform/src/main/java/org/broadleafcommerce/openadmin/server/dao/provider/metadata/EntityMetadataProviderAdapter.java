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
