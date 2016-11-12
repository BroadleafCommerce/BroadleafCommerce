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


import org.apache.commons.lang.RandomStringUtils;

import java.security.SecureRandom;
import java.util.Random;

public class PasswordUtils {

    private static final Random RANDOM = new SecureRandom();
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    
    public static String generateSecurePassword(int requiredLength) {

        int start = 0;
        int end = CHARS.length();
        boolean letters = true;
        boolean numbers = true;

        String password = RandomStringUtils.random(requiredLength, start, end, letters, numbers, CHARS.toCharArray(), RANDOM);

        return password;
    }
}
