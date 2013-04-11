package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddFilterPropertiesRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.AddSearchMappingRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.ExtractValueRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;

import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class PersistenceProviderAdapter extends AbstractPersistenceProvider {

    @Override
    public void addSearchMapping(AddSearchMappingRequest addSearchMappingRequest) {
        //do nothing
    }

    @Override
    public void populateValue(PopulateValueRequest populateValueRequest) throws PersistenceException {
        //do nothing
    }

    @Override
    public void extractValue(ExtractValueRequest extractValueRequest) throws PersistenceException {
        //do nothing
    }

    @Override
    public boolean canHandlePersistence(Object instance, Property property, BasicFieldMetadata metadata) {
        return false;
    }

    @Override
    public boolean canHandleSearchMapping(BasicFieldMetadata metadata) {
        return false;
    }

    @Override
    public boolean canHandlePropertyFiltering(Entity entity, Map<String, FieldMetadata> unfilteredProperties) {
        return false;
    }

    @Override
    public void filterProperties(AddFilterPropertiesRequest addFilterPropertiesRequest) {
        //do nothing
    }
}
