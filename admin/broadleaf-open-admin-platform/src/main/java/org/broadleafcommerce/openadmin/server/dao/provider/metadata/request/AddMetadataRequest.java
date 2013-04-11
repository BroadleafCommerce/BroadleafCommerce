package org.broadleafcommerce.openadmin.server.dao.provider.metadata.request;

import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Contains the requested field, metadata and support classes.
 *
 * @author Jeff Fischer
 */
public class AddMetadataRequest {

    private final Field requestedField;
    private final Class<?> parentClass;
    private final Class<?> targetClass;
    private final Map<String, FieldMetadata> requestedMetadata;
    private final DynamicEntityDao dynamicEntityDao;
    private final String prefix;

    public AddMetadataRequest(Field requestedField, Class<?> parentClass, Class<?> targetClass, Map<String, FieldMetadata> requestedMetadata, DynamicEntityDao dynamicEntityDao, String prefix) {
        this.requestedField = requestedField;
        this.parentClass = parentClass;
        this.targetClass = targetClass;
        this.requestedMetadata = requestedMetadata;
        this.dynamicEntityDao = dynamicEntityDao;
        this.prefix = prefix;
    }

    public Field getRequestedField() {
        return requestedField;
    }

    public Class<?> getParentClass() {
        return parentClass;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Map<String, FieldMetadata> getRequestedMetadata() {
        return requestedMetadata;
    }

    public DynamicEntityDao getDynamicEntityDao() {
        return dynamicEntityDao;
    }

    public String getPrefix() {
        return prefix;
    }
}
