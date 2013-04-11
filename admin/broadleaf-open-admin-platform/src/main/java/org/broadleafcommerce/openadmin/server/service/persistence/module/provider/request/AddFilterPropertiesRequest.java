package org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request;

import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;

import java.util.Map;

/**
 * Contains the <tt>Entity</tt> instance and unfiltered property list.
 *
 * @author Jeff Fischer
 */
public class AddFilterPropertiesRequest {

    private final Entity entity;
    private final Map<String, FieldMetadata> requestedProperties;

    public AddFilterPropertiesRequest(Entity entity, Map<String, FieldMetadata> requestedProperties) {
        this.entity = entity;
        this.requestedProperties = requestedProperties;
    }

    public Entity getEntity() {
        return entity;
    }

    public Map<String, FieldMetadata> getRequestedProperties() {
        return requestedProperties;
    }
}
