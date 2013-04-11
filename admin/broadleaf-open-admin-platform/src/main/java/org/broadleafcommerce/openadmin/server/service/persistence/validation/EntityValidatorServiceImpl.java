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
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;


/**
 * This implementation validates each {@link Property} from the given {@link Entity} according to the
 * {@link ValidationConfiguration}s associated with it.
 * 
 * @author Phillip Verheyden
 * @see {@link EntityValidatorService}
 * @see {@link ValidationConfiguration}
 */
@Service("blEntityValidatorService")
public class EntityValidatorServiceImpl implements EntityValidatorService {

    @Override
    public void validate(Entity entity, Serializable instance, Map<String, FieldMetadata> mergedProperties) {

        //validate each individual property according to their validation configuration
        for (Property property : entity.getProperties()) {
            FieldMetadata metadata = mergedProperties.get(property.getName());
            if (metadata instanceof BasicFieldMetadata) {
                Map<String, Map<String, String>> validations =
                        ((BasicFieldMetadata) metadata).getValidationConfigurations();
                for (Map.Entry<String, Map<String, String>> validation : validations.entrySet()) {
                    String validatorClassname = validation.getKey();
                    Map<String, String> configuration = validation.getValue();

                    PropertyValidator validator = null;
                    try {
                        validator = (PropertyValidator) Class.forName(validatorClassname).newInstance();
                    } catch (Exception e) {
                        throw new PersistenceException(e);
                    }
                    boolean validationResult = validator.validate(entity, configuration, instance, property.getValue());
                    if (!validationResult) {
                        entity.addValidationError(property.getName(), configuration.get(ConfigurationItem.ERROR_MESSAGE));
                        entity.setValidationFailure(true);
                    }
                }
            }
        }
    }

}
