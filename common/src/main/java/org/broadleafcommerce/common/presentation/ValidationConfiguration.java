/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
