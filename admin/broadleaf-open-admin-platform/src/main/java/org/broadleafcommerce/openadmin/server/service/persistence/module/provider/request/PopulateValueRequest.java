package org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request;

import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.DataFormatProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;

import java.io.Serializable;

/**
 * Contains the requested value, instance and support classes.
 *
 * @author Jeff Fischer
 */
public class PopulateValueRequest {

    private final Serializable requestedInstance;
    private final Boolean setId;
    private final FieldManager fieldManager;
    private final Property property;
    private final BasicFieldMetadata metadata;
    private final Class<?> returnType;
    private final String requestedValue;
    private final PersistenceManager persistenceManager;
    private final DataFormatProvider dataFormatProvider;

    public PopulateValueRequest(Serializable requestedInstance, Boolean setId, FieldManager fieldManager, Property property, BasicFieldMetadata metadata, Class<?> returnType, String requestedValue, PersistenceManager persistenceManager, DataFormatProvider dataFormatProvider) {
        this.requestedInstance = requestedInstance;
        this.setId = setId;
        this.fieldManager = fieldManager;
        this.property = property;
        this.metadata = metadata;
        this.returnType = returnType;
        this.requestedValue = requestedValue;
        this.persistenceManager = persistenceManager;
        this.dataFormatProvider = dataFormatProvider;
    }

    public Serializable getRequestedInstance() {
        return requestedInstance;
    }

    public Boolean getSetId() {
        return setId;
    }

    public FieldManager getFieldManager() {
        return fieldManager;
    }

    public Property getProperty() {
        return property;
    }

    public BasicFieldMetadata getMetadata() {
        return metadata;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public String getRequestedValue() {
        return requestedValue;
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public DataFormatProvider getDataFormatProvider() {
        return dataFormatProvider;
    }
}
