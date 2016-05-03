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
