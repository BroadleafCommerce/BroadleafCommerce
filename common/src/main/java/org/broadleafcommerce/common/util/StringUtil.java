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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

/**
 * Convenience methods for interacting with Java String types
 */
public class StringUtil {

    private static final Log LOG = LogFactory.getLog(StringUtil.class);

    public static long getChecksum(String test) {
        try {
            byte buffer[] = test.getBytes();
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            CheckedInputStream cis = new CheckedInputStream(bais, new Adler32());
            byte readBuffer[] = new byte[buffer.length];
            cis.read(readBuffer);
            return cis.getChecksum().getValue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Checks if a string is included in the beginning of another string, but only in dot-separated segment leaps.
     * Examples:
     * <ul>
     *   <li>"sku.date" into "sku.dateExtra" should return false</li>
     *   <li>"sku.date" into "sku.date.extra" should return true</li>
     *   <li>"sku" into "sku" should return true</li>
     * </ul>
     * 
     * This function avoids "collision" between similarly named, multi-leveled property fields.
     * 
     * @param bigger     the bigger (haystack) String          
     * @param included   the string to be sought (needle)
     * @return
     */
    public static boolean segmentInclusion(String bigger, String included) {
        if (StringUtils.isEmpty(bigger) || StringUtils.isEmpty(included)) {
            return false;
        }
        return (bigger.equals(included)) || validateStartsWith(bigger, included);
    }

    private static boolean validateStartsWith(String value, String prefix) {
        boolean isIncluded = value.startsWith(prefix);
        // We check against a false positive where it mismatches sku.date into sku.dateExtra
        if (isIncluded && !prefix.endsWith(".")) {
            isIncluded = value.startsWith(prefix + ".");
        }
        return isIncluded;
    }

    public static double determineSimilarity(String test1, String test2) {
        String first = new String(test1);
        first = first.replaceAll("[ \\t\\n\\r\\f\\v\\/'-]", "");
        Long originalChecksum = StringUtil.getChecksum(first);
        String second = new String(test2);
        second = second.replaceAll("[ \\t\\n\\r\\f\\v\\/'-]", "");
        Long myChecksum = StringUtil.getChecksum(second);
        StatCalc calc = new StatCalc();
        calc.enter(originalChecksum);
        calc.enter(myChecksum);
        return calc.getStandardDeviation();
    }

    /**
     * Protect against HTTP Response Splitting
     * @return
     */
    public static String cleanseUrlString(String input) {
        return removeSpecialCharacters(decodeUrl(input));
    }

    public static String decodeUrl(String encodedUrl) {
        try {
            return encodedUrl == null ? null : URLDecoder.decode(encodedUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // this should not happen
            LOG.warn("Could not decode URL.", e);
            return encodedUrl;
        }
    }

    public static String removeSpecialCharacters(String input) {
        if (input != null) {
            input = input.replaceAll("[ \\r\\n]", "");
        }
        return input;
    }

    /**
     * given a string with the format "fields[someFieldName].value" (very common in error validation), returns
     * only "someFieldName
     * @param expression
     * @return
     */
    public static String extractFieldNameFromExpression(String expression) {
        return expression.substring(expression.indexOf('[') + 1, expression.lastIndexOf(']'));
    }

    public static String getMapAsJson(Map<String, Object> objectMap) {
        String nullString = "null";
        StringBuffer sb = new StringBuffer("{");
        boolean firstIteration = true;

        for (Entry<String, Object> entry : objectMap.entrySet()) {
            if (!firstIteration) {
                sb.append(',');
            }
            sb.append(JSONObject.quote(entry.getKey()));
            sb.append(':');
            Object value = entry.getValue();
            if (value == null) {
                sb.append(nullString);
            } else if (value instanceof Boolean) {
                sb.append(((Boolean) value).booleanValue());
            } else if (value instanceof String) {
                sb.append(JSONObject.quote(value.toString()));
            } else {
                sb.append(value.toString());
            }
            firstIteration = false;
        }
        sb.append("}");

        return sb.toString();
    }

    /**
     * Parses out non-numeric characters
     * @param value
     * @return
     */
    public static String removeNonNumerics(String value) {
        return value.replaceAll("[^\\d.]+", "").trim();

    }

    /**
     * Utility method for sanitizing a String to neutralize any possible malicious content. This is used primarily to protect log
     * messages by encoding for any possible forgery or injection attempts.
     * 
     * Given an Object of type Integer or Long, converts the Object instance to a Long.  This will throw a ClassCastException
     * if the past parameter is not either an Integer or a Long.
     *
     * @param string
     * @return String
     */
    public static String sanitize(String string) {
        if (string == null) {
            return "NULL";
        }
        String sanitized = string.replace('\n', '_').replace('\r', '_');
        return ESAPI.encoder().encodeForHTML(sanitized);
    }
}
