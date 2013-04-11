package org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request;

import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.DataFormatProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;

import java.util.List;

/**
 * Contains the requested value, property and support classes.
 *
 * @author Jeff Fischer
 */
public class ExtractValueRequest {

    private final List<Property> props;
    private final FieldManager fieldManager;
    private final BasicFieldMetadata metadata;
    private final Object requestedValue;
    private final Property requestedProperty;
    private String displayVal;
    private final PersistenceManager persistenceManager;
    private final DataFormatProvider dataFormatProvider;

    public ExtractValueRequest(List<Property> props, FieldManager fieldManager, BasicFieldMetadata metadata, Object requestedValue, Property requestedProperty, String displayVal, PersistenceManager persistenceManager, DataFormatProvider dataFormatProvider) {
        this.props = props;
        this.fieldManager = fieldManager;
        this.metadata = metadata;
        this.requestedValue = requestedValue;
        this.requestedProperty = requestedProperty;
        this.displayVal = displayVal;
        this.persistenceManager = persistenceManager;
        this.dataFormatProvider = dataFormatProvider;
    }

    public List<Property> getProps() {
        return props;
    }

    public FieldManager getFieldManager() {
        return fieldManager;
    }

    public BasicFieldMetadata getMetadata() {
        return metadata;
    }

    public Object getRequestedValue() {
        return requestedValue;
    }

    public Property getRequestedProperty() {
        return requestedProperty;
    }

    public String getDisplayVal() {
        return displayVal;
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public DataFormatProvider getDataFormatProvider() {
        return dataFormatProvider;
    }

    public void setDisplayVal(String displayVal) {
        this.displayVal = displayVal;
    }
}
