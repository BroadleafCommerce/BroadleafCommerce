package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Jeff Fischer
 */
@Component("blDefaultPersistenceProvider")
@Scope("prototype")
public class DefaultPersistenceProvider extends PersistenceProviderAdapter {

    @Override
    public boolean canHandlePersistence(Object instance, Property property, BasicFieldMetadata metadata) {
        return true;
    }

    public void populateValue(PopulateValueRequest populateValueRequest) throws PersistenceException {
        try {
            populateValueRequest.getFieldManager().setFieldValue(populateValueRequest.getRequestedInstance(),
                    populateValueRequest.getProperty().getName(), populateValueRequest.getRequestedValue());
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void extractValue(ExtractValueRequest extractValueRequest) throws PersistenceException {
        if (extractValueRequest.getRequestedValue() != null) {
            String val = extractValueRequest.getRequestedValue().toString();
            extractValueRequest.getRequestedProperty().setValue(val);
            extractValueRequest.getRequestedProperty().setDisplayValue(extractValueRequest.getDisplayVal());
        }
    }

}
