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
package org.broadleafcommerce.common.security.util;


public class PasswordUtils {

    public static final Character[] characters = {
        'a','b','c','d','e','f','g','h','j','k','m','n','p','q','r','s','t','u','v','w','x','y',
        '2','3','4','6','7','8','9'
    };
    
    public static String generateTemporaryPassword(int requiredLength) {
        int length = characters.length;
        StringBuffer sb = new StringBuffer(requiredLength);
        for (int j=0;j<requiredLength;j++) {
            sb.append(characters[(int) Math.round(Math.floor(Math.random() * length))]);
        }
        
        return sb.toString();
    }
}
