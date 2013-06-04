/*
 * Copyright 2008-2013 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.server.service.persistence.validation;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;


/**
 * Ensures that every property that is required from {@link BasicFieldMetadata#getRequired()} has a non-empty value being
 * set.
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blRequiredPropertyValidator")
public class RequiredPropertyValidator implements PropertyValidator {

    @Override
    public PropertyValidationResult validate(Entity entity,
                            Serializable instance,
                            Map<String, FieldMetadata> entityFieldMetadata,
                            Map<String, String> validationConfiguration,
                            BasicFieldMetadata propertyMetadata,
                            String propertyName,
                            String value) {
        boolean required = BooleanUtils.isTrue(propertyMetadata.getRequired());
        if (propertyMetadata.getRequiredOverride() != null) {
            required = propertyMetadata.getRequiredOverride();
        }
        boolean valid = required && StringUtils.isEmpty(value) ? false : true;
        return new PropertyValidationResult(valid, "This field is required");
    }

}
