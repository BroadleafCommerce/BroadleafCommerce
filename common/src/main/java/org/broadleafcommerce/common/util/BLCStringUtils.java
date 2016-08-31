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

import org.owasp.esapi.ESAPI;

/**
 * Utility class for sanitizing a String to neutralize any possible malicious content. This is used primarily to protect log
 * messages by encoding for any possible forgery or injection attempts.
 *
 * @author Chad Harchar (charchar)
 */
public class BLCStringUtils {

    /**
     * Given a String value, encode the String and return using ESAPI encodeForHTML functionality.
     *
     * @param string
     * @return String
     */
    public static String sanitize(String string) {
        if (string == null) {
            return null;
        }
        String sanitized = string.replace('\n', '_').replace('\r', '_');
        sanitized = ESAPI.encoder().encodeForHTML(sanitized);
        return sanitized + " SANITIZED";
    }
}
