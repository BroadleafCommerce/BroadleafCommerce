/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.presentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Configuration items to be used in conjunction with {@link ValidationConfiguration} and used by an instace of
 * org.broadleafcommerce.openadmin.server.service.persistence.validation.PropertyValidator
 * 
 * @author jfischer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ConfigurationItem {
    
    /**
     * Item name for the error message (could also be a key to a properties file to support localization)
     */
    public static String ERROR_MESSAGE = "errorMessage";
    
    /**
     * <p>The name of the validation configuration item</p>
     * 
     * @return the config item name
     */
    String itemName();
    
    /**
     * <p>The value for the validation configuration item</p>
     * 
     * @return the config item value
     */
    String itemValue();
}
