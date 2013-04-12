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

import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromFieldTypeRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromMappingDataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;
import org.hibernate.mapping.Property;
import org.hibernate.type.Type;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Classes implementing this interface are capable of manipulating metadata resulting from the inspection
 * phase for the admin. Providers are typically added in response to new admin presentation annotation support.
 * Implementers should generally extend <tt>MetadataProviderAdapter</tt>.
 *
 * @author Jeff Fischer
 */
public interface MetadataProvider {

    /**
     * Whether or not this provider is qualified to add metadata for the specified field. The metadata
     * is derived from annotations and any other forms of explicit configuration.
     * Determines whether or not addMetadata is called.
     *
     * @param field the <tt>Field</tt> instance to test
     * @return whether or not this provider is qualified
     */
    boolean canHandleFieldForConfiguredMetadata(Field field);

    /**
     * Whether or not this provider is qualified to add metadata for the specified field based
     * on the field type. Determines whether or not addMetadataFromFieldType is called.
     *
     * @param field the <tt>Field</tt> instance to test
     * @return whether or not this provider is qualified
     */
    boolean canHandleFieldForTypeMetadata(Field field);

    /**
     * Whether or not this provider is qualified to add metadata for the specified Hibernate mapping information.
     * Determines whether or not addMetadataFromMappingData is called.
     *
     * @param propertyName the name of the property
     * @param componentProperties the list of hibernate properties keyed by property name
     * @param entityType the hibernate type for the entity
     * @return whether or not this provider is qualified
     */
    boolean canHandleMappingForTypeMetadata(String propertyName, List<Property> componentProperties, Type entityType);

    /**
     * Whether or not this provider is qualified to override metadata using <tt>AdminPresentationOverrides</tt>.
     * Determines whether or not overrideViaAnnotation is called.
     *
     * @param clazz The class to test for the presence of qualified <tt>AdminPresentationOverrides</tt>
     * @return whether or not this provider is qualified
     */
    boolean canHandleAnnotationOverride(Class<?> clazz);

    /**
     * Whether or not this provider is qualified to override metadata using xml. Determines whether or not
     * overrideViaXml is called.
     *
     * @param ceilingEntityFullyQualifiedClassname the fully qualified name of the ceiling entity for this inspect
     * @param configurationKey the configuration key (if any) for this inspect
     * @return whether or not this provider is qualified
     */
    boolean canHandleXmlOverride(String ceilingEntityFullyQualifiedClassname, String configurationKey);

    /**
     * Contribute to metadata inspection for the <tt>Field</tt> instance in the request. Implementations should
     * add values to the requestedMetadata field of the request object.
     *
     * @param addMetadataRequest contains the requested field, metadata and support classes.
     */
    void addMetadata(AddMetadataRequest addMetadataRequest);

    /**
     * Contribute to metadata inspection for the entity in the request. Implementations should override values
     * in the requestedMetadata field of the request object.
     *
     * @param overrideViaAnnotationRequest contains the requested entity, metadata and support classes.
     */
    void overrideViaAnnotation(OverrideViaAnnotationRequest overrideViaAnnotationRequest);

    /**
     * Contribute to metadata inspection for the ceiling entity and config key. Implementations should override
     * values in the requestedMetadata field of the request object.
     *
     * @param overrideViaXmlRequest contains the requested config key, ceiling entity, metadata and support classes.
     */
    void overrideViaXml(OverrideViaXmlRequest overrideViaXmlRequest);

    /**
     * Contribute to metadata inspection using Hibernate column information. Implementations should impact values
     * in the requestedMetadata field of the request object.
     *
     * @param addMetadataFromMappingDataRequest contains the requested Hibernate type, metadata and support classes.
     */
    void addMetadataFromMappingData(AddMetadataFromMappingDataRequest addMetadataFromMappingDataRequest);

    /**
     * Contribute to metadata inspection for the <tt>Field</tt> instance in the request. Implementations should
     * add values to the requestedProperties field of the request object.
     *
     * @param addMetadataFromFieldTypeRequest contains the requested field, properties, property name and support classes.
     */
    void addMetadataFromFieldType(AddMetadataFromFieldTypeRequest addMetadataFromFieldTypeRequest);
}
