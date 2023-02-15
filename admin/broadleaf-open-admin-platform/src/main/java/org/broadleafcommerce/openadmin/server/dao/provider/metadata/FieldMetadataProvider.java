/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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

import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddFieldMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromFieldTypeRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromMappingDataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.LateStageAddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;
import org.broadleafcommerce.openadmin.server.service.type.MetadataProviderResponse;
import org.springframework.core.Ordered;

import java.util.Map;

/**
 * Classes implementing this interface are capable of manipulating metadata for a field resulting from the inspection
 * phase for the admin. Providers are typically added in response to new admin presentation annotation support.
 * Implementers should generally extend {@link FieldMetadataProviderAdapter}.
 *
 * @author Jeff Fischer
 */
public interface FieldMetadataProvider extends Ordered {

    //standard ordering constants for BLC providers
    public static final int BASIC = Integer.MAX_VALUE;
    public static final int COLLECTION = 20000;
    public static final int ADORNED_TARGET = 30000;
    public static final int MAP = 40000;
    public static final int MAP_FIELD = 50000;

    /**
     * Contribute to metadata inspection for the {@link java.lang.reflect.Field} instance in the request. Implementations should
     * add values to the metadata parameter.
     *
     * @param addFieldMetadataRequest contains the requested field and support classes.
     * @param metadata implementations should add metadata for the requested field here
     * @return whether or not this implementation adjusted metadata
     */
    MetadataProviderResponse addMetadata(AddFieldMetadataRequest addFieldMetadataRequest, Map<String, FieldMetadata> metadata);

    /**
     * Contribute to metadata inspection for the {@link java.lang.reflect.Field} instance in the request. Implementations should
     * add values to the metadata parameter.
     *
     * This method differs from {@link #addMetadata(AddFieldMetadataRequest, Map)} in that it will be invoked after the cacheable
     * properties are assembled. It is therefore useful in scenarios where you may want to contribute properties to
     * metadata that are dynamic and should not be cached normally.
     *
     * @param lateStageAddMetadataRequest contains the requested field name and support classes.
     * @param metadata implementations should add metadata for the requested field here
     * @return whether or not this implementation adjusted metadata
     */
    MetadataProviderResponse lateStageAddMetadata(LateStageAddMetadataRequest lateStageAddMetadataRequest, Map<String, FieldMetadata> metadata);

    /**
     * Contribute to metadata inspection for the entity in the request. Implementations should override values
     * in the metadata parameter.
     *
     * @param overrideViaAnnotationRequest contains the requested entity and support classes.
     * @param metadata implementations should override metadata here
     * @return whether or not this implementation adjusted metadata
     */
    MetadataProviderResponse overrideViaAnnotation(OverrideViaAnnotationRequest overrideViaAnnotationRequest, Map<String, FieldMetadata> metadata);

    /**
     * Contribute to metadata inspection for the ceiling entity and config key. Implementations should override
     * values in the metadata parameter.
     *
     * @param overrideViaXmlRequest contains the requested config key, ceiling entity and support classes.
     * @param metadata implementations should override metadata here
     * @return whether or not this implementation adjusted metadata
     */
    MetadataProviderResponse overrideViaXml(OverrideViaXmlRequest overrideViaXmlRequest, Map<String, FieldMetadata> metadata);

    /**
     * Contribute to metadata inspection using Hibernate column information. Implementations should impact values
     * in the metadata parameter.
     *
     * @param addMetadataFromMappingDataRequest contains the requested Hibernate type and support classes.
     * @param metadata implementations should impact values for the metadata for the field here
     * @return whether or not this implementation adjusted metadata
     */
    MetadataProviderResponse addMetadataFromMappingData(AddMetadataFromMappingDataRequest addMetadataFromMappingDataRequest, FieldMetadata metadata);

    /**
     * Contribute to metadata inspection for the {@link java.lang.reflect.Field} instance in the request. Implementations should
     * add values to the metadata parameter. This is metadata based on the field type.
     *
     * @param addMetadataFromFieldTypeRequest contains the requested field, property name and support classes.
     * @param metadata implementations should add values for the field here
     * @return whether or not this implementation adjusted metadata
     */
    MetadataProviderResponse addMetadataFromFieldType(AddMetadataFromFieldTypeRequest addMetadataFromFieldTypeRequest, Map<String, FieldMetadata> metadata);

}
