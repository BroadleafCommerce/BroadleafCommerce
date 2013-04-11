package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddFilterPropertiesRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddSearchMappingRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;

import java.util.Map;

/**
 * Classes implementing this interface are capable of handling persistence related events for fields whose values
 * are being requested or set for the admin. This includes any special translations or transformations required to get
 * from the string representation in the admin back to the field on a Hibernate entity - and the reverse. Providers are
 * typically added in response to new admin presentation annotation support that requires special persistence behavior.
 * Implementers should generally extend <tt>PersistenceProviderAdapter</tt>.
 *
 * @author Jeff Fischer
 */
public interface PersistenceProvider {

    /**
     * Whether or not this provider is qualified to handle persistence events for the given
     * object instance and field metadata.
     *
     * @param instance The Hibernate entity being persisted
     * @param property The Property being requested to apply
     * @param metadata The descriptive metadata for the field
     * @return whether or not this provider is qualified
     */
    boolean canHandlePersistence(Object instance, Property property, BasicFieldMetadata metadata);

    /**
     * Whether or not this provider is qualified to handle search mappings for the given field metadata. Search
     * mappings are in the form of criteria that refine the final select query for a list of entities to
     * display in the admin during a fetch.
     *
     * @param metadata The descriptive metadata for the field
     * @return whether or not this provider is qualified
     */
    boolean canHandleSearchMapping(BasicFieldMetadata metadata);

    /**
     * Whether or not this provider is qualified to filter properties returned from the admin during an
     * update or add before passing into the final persistence phase.
     *
     * @param entity The <tt>Entity</tt> instance passed in from the admin
     * @param unfilteredProperties The property list before filtering
     * @return whether or not this provider is qualified
     */
    boolean canHandlePropertyFiltering(Entity entity, Map<String, FieldMetadata> unfilteredProperties);

    /**
     * Set the property value on the target object. Implementations should translate and set the requestedValue
     * field from the request on the requestedInstance field from the request.
     *
     * @param populateValueRequest contains the requested value, instance and support classes.
     */
    void populateValue(PopulateValueRequest populateValueRequest);

    /**
     * Retrieve the property value from the provided value. Implementations should translate the requestedValue
     * and set on the requestedProperty.
     *
     * @param extractValueRequest contains the requested value, property and support classes.
     */
    void extractValue(ExtractValueRequest extractValueRequest);

    /**
     * Add criteria to the requestCtoConverter. The CtoConverter is used by the system to refine the fetch criteria
     * used to retrieve lists of records for the admin. The requestedCto contains filters requested from the admin
     * and is generally used to drive the criteria added to CtoConverter.
     *
     * @param addSearchMappingRequest contains the requested ctoConverter, cto and support classes.
     */
    void addSearchMapping(AddSearchMappingRequest addSearchMappingRequest);

    /**
     * Filter the list of properties posted by the admin during and add or update. This is the property list
     * immediately before persistence is attempted. Properties may be altered, removed or added.
     *
     * @param addFilterPropertiesRequest contains the <tt>Entity</tt> instance and unfiltered property list.
     */
    void filterProperties(AddFilterPropertiesRequest addFilterPropertiesRequest);

}
