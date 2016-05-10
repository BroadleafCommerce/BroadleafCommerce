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
package org.broadleafcommerce.common.locale.util;

import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

public class LocaleConverter implements Converter<String, Locale> {

    @Override
    public Locale convert(String localeString) {
        if (localeString == null) {
            return null;
        }
        String[] components = localeString.split("_");
        if (components.length == 1) {
            return new Locale(components[0]);
        } else if (components.length == 2) {
            return new Locale(components[0], components[1]);
        } else if (components.length == 3) {
            return new Locale(components[0], components[1], components[2]);
        }
        return null;
    }

}
