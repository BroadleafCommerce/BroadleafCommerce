/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.openadmin.server.service.handler;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.config.domain.SystemProperty;
import org.broadleafcommerce.common.config.service.type.SystemPropertyFieldType;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;

/**
 * Custom persistence handler for SystemProperty to ensure that the value is validated against the type appropriately.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Component("blSystemPropertyCustomPersistenceHandler")
public class SystemPropertyCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {
    private final Log LOG = LogFactory.getLog(SystemPropertyCustomPersistenceHandler.class);

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return SystemProperty.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) 
            throws ServiceException {
        Entity entity = persistencePackage.getEntity();
        try {
            // Get an instance of SystemProperty with the updated values from the form
            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(SystemProperty.class.getName(), persistencePerspective);
            Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            SystemProperty adminInstance = (SystemProperty) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
            adminInstance = (SystemProperty) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            // Verify that the value entered matches up with the type of this property
            Entity errorEntity = validateTypeAndValueCombo(adminInstance);
            if (errorEntity != null) {
                entity.setValidationErrors(errorEntity.getValidationErrors());
                return entity;
            }

            adminInstance = (SystemProperty) dynamicEntityDao.merge(adminInstance);

            // Fill out the DTO and add in the product option value properties to it
            return helper.getRecord(adminProperties, adminInstance, null, null);
        } catch (Exception e) {
            throw new ServiceException("Unable to perform fetch for entity: " + SystemProperty.class.getName(), e);
        }
    }
    
    protected Entity validateTypeAndValueCombo(SystemProperty prop) {
        boolean validated = false;

        if (prop.getPropertyType().equals(SystemPropertyFieldType.BOOLEAN_TYPE)) {
            String value = prop.getValue().toUpperCase();
            if (value != null && (value.equals("TRUE") || value.equals("FALSE"))) {
                validated = true;
            }
        } else if (prop.getPropertyType().equals(SystemPropertyFieldType.INT_TYPE)) {
            try {
                Integer.parseInt(prop.getValue());
                validated = true;
            } catch (Exception e) {
                // Do nothing - we will fail on validation
            }
        } else if (prop.getPropertyType().equals(SystemPropertyFieldType.DOUBLE_TYPE)) {
            try {
                Double.parseDouble(prop.getValue());
                validated = true;
            } catch (Exception e) {
                // Do nothing - we will fail on validation
            }
        } else if (prop.getPropertyType().equals(SystemPropertyFieldType.STRING_TYPE)) {
            validated = true;
        }

        if (!validated) {
            Entity errorEntity = new Entity();
            errorEntity.addValidationError("value", "valueIllegalForPropertyType");
            return errorEntity;
        }

        return null;
    }
}
