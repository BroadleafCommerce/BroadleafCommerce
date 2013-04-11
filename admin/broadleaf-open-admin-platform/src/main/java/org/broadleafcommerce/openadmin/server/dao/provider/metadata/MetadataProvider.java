package org.broadleafcommerce.openadmin.server.dao.provider.metadata;

import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataFromMappingDataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddMetadataRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest;
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaXmlRequest;

import java.lang.reflect.Field;

/**
 * Classes implementing this interface are capable of manipulating metadata resulting from the inspection
 * phase for the admin. Providers are typically added in response to new admin presentation annotation support.
 * Implementers should generally extend <tt>MetadataProviderAdapter</tt>.
 *
 * @author Jeff Fischer
 */
public interface MetadataProvider {

    /**
     * Whether or not this provider is qualified to add metadata for the specified field.
     *
     * @param field the <tt>Field</tt> instance to test
     * @return whether or not this provider is qualified
     */
    boolean canHandleField(Field field);

    /**
     * Whether or not this provider is qualified to override metadata using <tt>AdminPresentationOverrides</tt>
     *
     * @param clazz The class to test for the presence of qualified <tt>AdminPresentationOverrides</tt>
     * @return whether or not this provider is qualified
     */
    boolean canHandleAnnotationOverride(Class<?> clazz);

    /**
     * Whether or not this provider is qualified to override metadata using xml.
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
}
