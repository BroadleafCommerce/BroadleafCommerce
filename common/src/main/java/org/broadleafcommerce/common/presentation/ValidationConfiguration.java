/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.presentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 
 * @author jfischer
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ValidationConfiguration {
    
    /**
     * <p>The fully qualified classname of the org.broadleafcommerce.openadmin.server.service.persistence.validation.PropertyValidator
     * instance to use for validation</p>
     * 
     * <p>If you need to do dependency injection, this can also correspond to a bean ID that implements the 
     * org.broadleafcommerce.openadmin.server.service.persistence.validation.PropertyValidator interface.</p>
     * 
     * @return the validator implementation. This implementation should be an instance of
     * org.broadleafcommerce.openadmin.server.service.persistence.validation.PropertyValidator and could be either a bean
     * name or fully-qualified class name
     */
    String validationImplementation();
    
    /**
     * <p>Optional configuration items that can be used to setup the validator</p>. Most validators should have at least
     * a single configuration item with {@link ConfigurationItem#ERROR_MESSAGE}.
     * 
     * @return validator configuration attributes
     */
    ConfigurationItem[] configurationItems() default {};
}
