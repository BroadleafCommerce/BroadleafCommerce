/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.rule;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.util.FormatUtil;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for some common rule functions that can be called from mvel as well as utility functions
 * to make calling MVEL rules within Broadleaf easier.  
 * 
 * An instance of this class is available to the mvel runtime under the variable name MvelHelper with the 
 * following functions:
 * 
 *    convertField(type, fieldValue)
 *    toUpperCase(value)
 *
 * @author Jeff Fischer
 */
public class MvelHelper {

    private static final Map DEFAULT_EXPRESSION_CACHE = new LRUMap(5000);
    private static final Log LOG = LogFactory.getLog(MvelHelper.class);

    /**
     * Converts a field to the specified type.    Useful when 
     * @param type
     * @param fieldValue
     * @return
     */
    public static Object convertField(String type, String fieldValue) {
        if (fieldValue == null) {
            return null;
        }
        try {
            if (type.equals(SupportedFieldType.BOOLEAN.toString())) {
                return Boolean.parseBoolean(fieldValue);
            } else if (type.equals(SupportedFieldType.DATE.toString())) {
                return FormatUtil.getTimeZoneFormat().parse(fieldValue);
            } else if (type.equals(SupportedFieldType.INTEGER.toString())) {
                return Integer.parseInt(fieldValue);
            } else if (type.equals(SupportedFieldType.MONEY.toString()) || type.equals(SupportedFieldType.DECIMAL.toString())) {
                return new BigDecimal(fieldValue);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalArgumentException("Unrecognized type(" + type + ") for map field conversion.");
    }

    public static Object toUpperCase(String value) {
        if (value == null) {
            return null;
        }
        return value.toUpperCase();
    }
    
    /**
     * Returns true if the passed in rule passes based on the passed in ruleParameters.   
     * 
     * Also returns true if the rule is blank or null.
     * 
     * Calls the {@link #evaluateRule(String, Map, Map)} method passing in the DEFAULT_EXPRESSION_CACHE.
     * For systems that need to cache a large number of rule expressions, an alternate cache can be passed in.   The
     * default cache is able to cache up to 1,000 rule expressions which should suffice for most systems.
     * 
     * @param rule
     * @param ruleParameters
     * @return
     */
    public static boolean evaluateRule(String rule, Map<String, Object> ruleParameters) {
        return evaluateRule(rule, ruleParameters, DEFAULT_EXPRESSION_CACHE);
    }

    /**
     * Evaluates the passed in rule given the passed in parameters.   
     * 
     * @param rule
     * @param ruleParameters
     * @return
     */
    public static boolean evaluateRule(String rule, Map<String, Object> ruleParameters, Map expressionCache) {
        // Null or empty is a match
        if (rule == null || "".equals(rule)) {
            return true;
        } else {
            // MVEL expression compiling can be expensive so let's cache the expression
            Serializable exp = (Serializable) expressionCache.get(rule);
            if (exp == null) {
                synchronized (expressionCache) {
                    ParserContext context = new ParserContext();
                    context.addImport("MVEL", MVEL.class);
                    context.addImport("MvelHelper", MvelHelper.class);
                    exp = MVEL.compileExpression(rule, context);
                    expressionCache.put(rule, exp);

                }
            }

            Map<String, Object> mvelParameters = new HashMap<String, Object>();

            if (ruleParameters != null) {
                for (String parameter : ruleParameters.keySet()) {
                    mvelParameters.put(parameter, ruleParameters.get(parameter));
                }
            }

            try {
                Object test = MVEL.executeExpression(exp, mvelParameters);
                if (test == null) {
                    // This can occur if there is no actual rule
                    return true;
                }
                return (Boolean) test;
            } catch (Exception e) {
                //Unable to execute the MVEL expression for some reason
                //Return false, but notify about the bad expression through logs
                LOG.info("Unable to parse and/or execute the mvel expression (" + rule + "). Reporting to the logs and returning false for the match expression", e);
                return false;
            }
        }
    }

}
