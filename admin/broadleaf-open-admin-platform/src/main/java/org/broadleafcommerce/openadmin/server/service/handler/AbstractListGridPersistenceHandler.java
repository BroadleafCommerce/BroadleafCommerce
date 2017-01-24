package org.broadleafcommerce.openadmin.server.service.handler;

import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import java.util.Map;

/**
 * @author Chad Harchar (charchar)
 */
public class AbstractListGridPersistenceHandler implements ListGridPersistenceHandler {

    @Override
    public Boolean canHandleEntity(PersistencePackage persistencePackage) {
        return false;
    }

    @Override
    public Map<String, FieldMetadata> stripHandledProperties(Map<String, FieldMetadata> properties) {
        return properties;
    }

    @Override
    public Entity[] handleEntities(PersistencePackage persistencePackage, Entity[] entities, DynamicEntityDao dynamicEntityDao, RecordHelper recordHelper) {
        return entities;
    }
}
