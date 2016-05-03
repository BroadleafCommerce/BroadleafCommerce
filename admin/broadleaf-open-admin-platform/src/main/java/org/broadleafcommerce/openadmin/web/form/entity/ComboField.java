/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.form.entity;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author Andre Azzolini (apazzolini)
 */
public class ComboField extends Field {

    protected Map<String, String> options = new LinkedHashMap<String, String>();

    /* *********** */
    /* ADD METHODS */
    /* *********** */
    
    public void putOption(String key, String value) {
        options.put(key, value);
    }
    
    /* ************************** */
    /* CUSTOM GETTERS / SETTERS */
    /* ************************** */
    
    public void setOptions(String[][] options) {
        if (options != null) {
            for (String[] option : options) {
                putOption(option[0], option[1]);
            }
        }
    }
    
    public String getOption(String optionKey) {
        return getOptions().get(optionKey);
    }

    /* ************************** */
    /* STANDARD GETTERS / SETTERS */
    /* ************************** */

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

}
