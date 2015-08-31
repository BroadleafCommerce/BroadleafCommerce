/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.service.persistence.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.service.AdminEntityService;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceResponse;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.Resource;


/**
 * Checks for uniqueness of this field's value among other entities of this type
 * 
 * @author Brandon Smith
 */
@Component("blUniqueValueValidator")
public class UniqueValueValidator implements PropertyValidator {

    protected static final Log LOG = LogFactory.getLog(UniqueValueValidator.class);

    @Resource(name = "blAdminEntityService")
    protected AdminEntityService adminEntityService;
    
    @Override
    public PropertyValidationResult validate(Entity entity,
            Serializable instance,
            Map<String, FieldMetadata> entityFieldMetadata,
            Map<String, String> validationConfiguration,
            BasicFieldMetadata propertyMetadata,
            String propertyName,
            String value) {

        PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                .withCeilingEntityClassname(entity.getType()[0])
                .withFilterAndSortCriteria(new FilterAndSortCriteria[]{
                        new FilterAndSortCriteria(propertyName, value)
                });

        String message = validationConfiguration.get(ConfigurationItem.ERROR_MESSAGE);
        if (message == null) {
            message = entity.getType()[0] + " with this value for attribute " +
                    propertyName + " already exists. This attribute's value must be unique.";
        }

        try {
            PersistenceResponse response = adminEntityService.getRecords(ppr);
            if(response.getDynamicResultSet().getTotalRecords() == 0) {
                return new PropertyValidationResult(true, message);
            } else {
                return new PropertyValidationResult(false, message);
            }
        } catch (ServiceException e) {
            LOG.error(message, e);
            return new PropertyValidationResult(false, message);
        }
    }
}
