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

import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.openadmin.client.dto.Entity;

import java.io.Serializable;
import java.util.Map;


/**
 * 
 * @author Phillip Verheyden
 */
public interface PropertyValidator {

    /**
     * Validates a property for an entity
     * 
     * @param entity Entity DTO of the entity attempting to save
     * @param validationConfiguration the map represented by the set of {@link ConfigurationItem} for a
     * {@link ValidationConfiguration} on a property
     * @param instance actual object representation of <b>entity</b>. This can be cast to entity interfaces (like Sku or
     * Product)
     * @param property the property name of the value attempting to be saved (could be a sub-entity obtained via dot
     * notation like 'defaultSku.name')
     * @param value the value attempted to be saved
     * @return <b>true</b> if this passes validation, <b>false</b> otherwise.
     */
    public boolean validate(Entity entity, Map<String, String> validationConfiguration, Serializable instance, String value);

}
