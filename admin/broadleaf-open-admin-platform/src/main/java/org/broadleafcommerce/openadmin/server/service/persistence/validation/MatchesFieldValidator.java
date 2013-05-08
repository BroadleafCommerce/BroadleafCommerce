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

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.openadmin.dto.Entity;

import java.io.Serializable;
import java.util.Map;


/**
 * Checks for equality between this field and a configured 'otherField'
 * 
 * @author Phillip Verheyden
 */
public class MatchesFieldValidator implements PropertyValidator {

    @Override
    public boolean validate(Entity entity, Map<String, String> validationConfiguration, Serializable instance, String value) {
        String otherField = validationConfiguration.get("otherField");
        return StringUtils.equals(entity.getPMap().get(otherField).getValue(), value);
    }

}
