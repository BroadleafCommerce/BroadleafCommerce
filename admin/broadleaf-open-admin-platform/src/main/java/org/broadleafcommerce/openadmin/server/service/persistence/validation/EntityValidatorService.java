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

import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;

import java.io.Serializable;
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
     * @param mergedProperties all of the merged properties including metadata
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public void validate(Entity entity, Serializable instance, Map<String, FieldMetadata> mergedProperties);

}
