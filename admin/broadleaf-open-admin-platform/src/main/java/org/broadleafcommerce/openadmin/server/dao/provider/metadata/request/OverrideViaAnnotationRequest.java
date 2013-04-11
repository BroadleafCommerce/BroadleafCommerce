package org.broadleafcommerce.openadmin.server.dao.provider.metadata.request;

import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;

import java.util.Map;

/**
 * Contains the requested entity, metadata and support classes.
 *
 * @author Jeff Fischer
 */
public class OverrideViaAnnotationRequest {

    private final Class<?> requestedEntity;
    private final Map<String, FieldMetadata> requestedMetadata;
    private final Boolean parentExcluded;
    private final DynamicEntityDao dynamicEntityDao;
    private final String prefix;

    public OverrideViaAnnotationRequest(Class<?> requestedEntity, Map<String, FieldMetadata> requestedMetadata, Boolean parentExcluded, DynamicEntityDao dynamicEntityDao, String prefix) {
        this.requestedEntity = requestedEntity;
        this.requestedMetadata = requestedMetadata;
        this.parentExcluded = parentExcluded;
        this.dynamicEntityDao = dynamicEntityDao;
        this.prefix = prefix;
    }

    public Class<?> getRequestedEntity() {
        return requestedEntity;
    }

    public Map<String, FieldMetadata> getRequestedMetadata() {
        return requestedMetadata;
    }

    public Boolean getParentExcluded() {
        return parentExcluded;
    }

    public DynamicEntityDao getDynamicEntityDao() {
        return dynamicEntityDao;
    }

    public String getPrefix() {
        return prefix;
    }
}
