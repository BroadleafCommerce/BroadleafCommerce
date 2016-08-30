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
package org.broadleafcommerce.common.util;

/**
 * Convenience methods for interacting with Java number types
 * 
 * @author Daniel Colgrove (dcolgrove)
 */
public class BLCNumberUtils {
    
    /**
     * Given an Object of type Integer or Long, converts the Object instance to a Long.  This will throw a ClassCastException
     * if the past parameter is not either an Integer or a Long.
     * 
     * @param Object
     * @return Long
     */
    public static Long toLong(Object objectToConvert) {
        Long convertedLongValue;
        if (objectToConvert instanceof Integer) {
            convertedLongValue = new Long((Integer) objectToConvert);
        } else {
            convertedLongValue = (Long) objectToConvert;
        }
        return convertedLongValue;
    }
}
