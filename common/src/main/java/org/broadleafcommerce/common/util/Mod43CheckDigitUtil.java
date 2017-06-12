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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Mod43CheckDigitUtil {

    private static final Log LOG = LogFactory.getLog(Mod43CheckDigitUtil.class);

    private final static String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%";

    public static boolean isValidCheckedValue(String value) {
        boolean valid = false;
        if (value != null && !"".equals(value)) {
            String code = value.substring(0, value.length() - 1);
            char checkDigit = value.substring(value.length() - 1).charAt(0);
            try {
                if (generateCheckDigit(code) == checkDigit) {
                    valid = true;
                }
            } catch (Exception e) {
                LOG.error("Error generating check digit.", e);
            }
        }
        return valid;
    }

    public static char generateCheckDigit(String data) {
        // MOD 43 check digit - take the acsii value of each digit, sum them up, divide by 43. the remainder is the check digit (in ascii)
        int sum = 0;
        for (int i = 0; i < data.length(); ++i) {
            sum += CHARSET.indexOf(data.charAt(i));
        }
        int remainder = sum % 43;
        return CHARSET.charAt(remainder);
    }

}
