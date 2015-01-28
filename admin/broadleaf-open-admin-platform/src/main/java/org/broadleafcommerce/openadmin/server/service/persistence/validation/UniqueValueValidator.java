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

import java.io.Serializable;
import java.util.Map;

import javax.annotation.Resource;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.service.AdminEntityService;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceResponse;
import org.springframework.stereotype.Component;


/**
 * Checks for uniqueness of this field's value among other entities of this type
 * 
 * @author Brandon Smith
 */
@Component("blUniqueValueValidator")
public class UniqueValueValidator implements PropertyValidator {
    
    @Resource(name = "blAdminEntityService")
    protected AdminEntityService adminEntityService;
    
    @Override
    public PropertyValidationResult validate(Entity entity, Serializable instance, Map<String, FieldMetadata> entityFieldMetadata, Map<String, String> validationConfiguration, BasicFieldMetadata propertyMetadata, String propertyName, String value) {
        PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                .withCeilingEntityClassname(entity.getType()[0])
                .withFilterAndSortCriteria(new FilterAndSortCriteria[]{
                        new FilterAndSortCriteria(propertyName, value)
                });
        try {
            PersistenceResponse response = adminEntityService.getRecords(ppr);
            if(response.getDynamicResultSet().getTotalRecords() == 0) {
                return new PropertyValidationResult(true);
            } else {
                return new PropertyValidationResult(false, entity.getType()[0] + " with this value for attribute " + propertyName + " already exists. This attribute's value must be unique.");
            }
        } catch (ServiceException e) {
            e.printStackTrace();
            return new PropertyValidationResult(false, e.getMessage());
        }
    }

}
