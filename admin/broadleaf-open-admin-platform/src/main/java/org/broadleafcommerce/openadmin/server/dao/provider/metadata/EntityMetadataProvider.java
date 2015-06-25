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
 * Classes implementing this interface are capable of manipulating metadata from the inspection
 * phase for the admin. Providers are typically added in response to new admin presentation annotation support.
 * Implementers should generally extend {@link FieldMetadataProviderAdapter}.
 *
 * @author Chris Kittrell
 */
public interface EntityMetadataProvider extends Ordered {

    /**
     * Contribute to metadata inspection for the {@link java.lang.reflect.Field} instance in the request. Implementations should
     * add values to the metadata parameter.
     *
     * @param addMetadataRequest contains the requested field and support classes.
     * @param metadata implementations should add metadata for the requested field here
     * @return whether or not this implementation adjusted metadata
     */
    MetadataProviderResponse addTabAndGroupMetadata(AddMetadataRequest addMetadataRequest, Map<String, TabMetadata> metadata);

    /**
     * Contribute to metadata inspection for the entity in the request. Implementations should override values
     * in the metadata parameter.
     *
     * @param overrideViaAnnotationRequest contains the requested entity and support classes.
     * @param metadata implementations should override metadata here
     * @return whether or not this implementation adjusted metadata
     */
    MetadataProviderResponse overrideMetadataViaAnnotation(OverrideViaAnnotationRequest overrideViaAnnotationRequest, Map<String, TabMetadata> metadata);

    /**
     * Contribute to metadata inspection for the ceiling entity and config key. Implementations should override
     * values in the metadata parameter.
     *
     * @param overrideViaXmlRequest contains the requested config key, ceiling entity and support classes.
     * @param metadata implementations should override metadata here
     * @return whether or not this implementation adjusted metadata
     */
    MetadataProviderResponse overrideMetadataViaXml(OverrideViaXmlRequest overrideViaXmlRequest, Map<String, TabMetadata> metadata);
    
    /**
     * Contribute to metadata inspection for the {@link java.lang.reflect.Field} instance in the request. Implementations should
     * add values to the metadata parameter.
     * 
     * This method differs from {@link #addTabAndGroupMetadata(AddMetadataRequest, Map)} in that it will be invoked after the cacheable
     * properties are assembled. It is therefore useful in scenarios where you may want to contribute properties to 
     * metadata that are dynamic and should not be cached normally.
     *
     * @param lateStageAddMetadataRequest contains the requested field name and support classes.
     * @param metadata implementations should add metadata for the requested field here
     * @return whether or not this implementation adjusted metadata
     */
    MetadataProviderResponse lateStageAddEntityMetadata(LateStageAddMetadataRequest lateStageAddMetadataRequest, Map<String, TabMetadata> metadata);

    /**
     * Contribute to metadata inspection using Hibernate column information. Implementations should impact values
     * in the metadata parameter.
     *
     * @param addMetadataFromMappingDataRequest contains the requested Hibernate type and support classes.
     * @param metadata implementations should impact values for the metadata for the field here
     * @return whether or not this implementation adjusted metadata
     */
    MetadataProviderResponse addEntityMetadataFromMappingData(AddMetadataFromMappingDataRequest addMetadataFromMappingDataRequest, TabMetadata metadata);

}
