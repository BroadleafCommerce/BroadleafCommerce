/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.service.persistence.validation;

import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author Phillip Verheyden
 */
public interface EntityValidatorService {

    /**
     * Validate the given entity. Implementers should set {@link Entity#setValidationFailure(boolean)} appropriately.
     * 
     * @param entity DTO representation of <b>instance</b>
     * @param instance actual domain representation of <b>entity</b>
     * @param propertiesMetadata all of the merged properties metadata for the given {@link Entity}
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public void validate(Entity entity, Serializable instance, Map<String, FieldMetadata> propertiesMetadata);

    /**
     * @return the global validators that will be executed for every {@link Entity}
     */
    public List<PropertyValidator> getGlobalEntityValidators();

    
    /**
     * <p>Set the global validators that will be run on every entity that is attempted to be saved in the admin. Global
     * validators are useful to operate on things like field types and other scenarios that could occur with a number of
     * entities. Rather than being required to define a {@link ValidationConfiguration} on all of those properties, this
     * can more conveniently validate that set of properties.</p>
     * 
     * <p>An example of a global validator in Broadleaf is the {@link RequiredPropertyValidator} which will ensure that every
     * property that is marked as required will fail validation if a value is unset.</p>
     * @param globalEntityValidators the globalEntityValidators to set
     */
    public void setGlobalEntityValidators(List<PropertyValidator> globalEntityValidators);

}
