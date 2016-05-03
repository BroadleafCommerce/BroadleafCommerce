/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.rule;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.RequestDTO;
import org.broadleafcommerce.common.TimeDTO;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.util.EfficientLRUMap;
import org.broadleafcommerce.common.util.FormatUtil;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

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

    private static final Map<String, Serializable> DEFAULT_EXPRESSION_CACHE = new EfficientLRUMap<String, Serializable>(5000);
    private static final Log LOG = LogFactory.getLog(MvelHelper.class);

    private static boolean TEST_MODE = false;
    
    public static final String BLC_RULE_MAP_PARAM = "blRuleMap";

    // The following attribute is set in BroadleafProcessURLFilter
    public static final String REQUEST_DTO = "blRequestDTO";    
    
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
    public static boolean evaluateRule(String rule, Map<String, Object> ruleParameters,
            Map<String, Serializable> expressionCache) {
        return evaluateRule(rule, ruleParameters, expressionCache, null);
    }
    
    /**
     * @param rule
     * @param ruleParameters
     * @param expressionCache
     * @param additionalContextImports additional imports to give to the {@link ParserContext} besides "MVEL" ({@link MVEL} and
     * "MvelHelper" ({@link MvelHelper}) since they are automatically added 
     * @return
     */
    public static boolean evaluateRule(String rule, Map<String, Object> ruleParameters,
        Map<String, Serializable> expressionCache, Map<String, Class<?>> additionalContextImports) {
        
        // Null or empty is a match
        if (rule == null || "".equals(rule)) {
            return true;
        } else {
            // MVEL expression compiling can be expensive so let's cache the expression
            Serializable exp = expressionCache.get(rule);
            if (exp == null) {
                ParserContext context = new ParserContext();
                context.addImport("MVEL", MVEL.class);
                context.addImport("MvelHelper", MvelHelper.class);
                if (MapUtils.isNotEmpty(additionalContextImports)) {
                    for (Entry<String, Class<?>> entry : additionalContextImports.entrySet()) {
                        context.addImport(entry.getKey(), entry.getValue());
                    }
                }
                
                rule = modifyExpression(rule, ruleParameters, context);
                
                exp = MVEL.compileExpression(rule, context);
                expressionCache.put(rule, exp);
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
                if (!TEST_MODE) {
                    LOG.info("Unable to parse and/or execute the mvel expression (" + rule + "). Reporting to the logs and returning false for the match expression", e);
                }
                return false;
            }
        }
    }
    
    /**
     * <p>
     * Provides a hook point to modify the final expression before it's built. By default, this looks for attribute
     * maps and replaces them such that it does string comparison.
     * 
     * <p>
     * For example, given an expression like getProductAttributes()['somekey'] == 'someval', getProductAttributes()['somekey']
     * actually returns a ProductAttribute object, not a String, so the comparison is wrong. Instead, we actually want
     * to do this: getProductAttributes().?get('somekey').?value == 'someval'. This function performs that replacement
     *
     * <p>
     * The modification regex will support both simple and complex expressions like:
     * "(MvelHelper.convertField("INTEGER",orderItem.?product.?getProductAttributes()["myinteger"])>0&&MvelHelper.convertField("INTEGER",orderItem.?product.?getProductAttributes()["myinteger"])<10)"
     *
     * @param rule the rule to replace
     * @return a modified version of <b>rule</b>
     * @see {@link #getRuleAttributeMaps()}
     */
    protected static String modifyExpression(String rule, Map<String, Object> ruleParameters, ParserContext context) {
        String modifiedExpression = rule;
        for (String attributeMap : getRuleAttributeMaps()) {
            modifiedExpression = modifiedExpression.replaceAll(attributeMap + "\\(\\)\\[(.*?)\\](?!\\.\\?value)", attributeMap + "().?get($1).?value");
        }
        return modifiedExpression;
    }

    /**
     * Returns an array of attribute map field names that we need to do replacements for in
     * {@link #modifyExpression(String, Map, ParserContext)}
     */
    protected static String[] getRuleAttributeMaps() {
        // intentionally left out pricing context getPricingContextAttributes because that's a Map<String, String>
        return new String[]{ "getProductAttributes",
            "getCategoryAttributesMap",
            "getSkuAttributes",
            "getOrderItemAttributes",
            "getCustomerAttributes",
            // Map<String, PageAttribute>
            "getAdditionalAttributes",
            // Map<String, AdminUserAttribute>
            "getAdditionalFields"}; 
    }

    /**
     * When true, LOG.info statement will be suppressed.   Should only be set from within MvelHelperTest.
     * Prevents an error from displaying during unit test runs.
     * @param testMode
     */
    public static void setTestMode(boolean testMode) {
        TEST_MODE = testMode;
    }
    
    /**
     * Builds parameters using time, request, customer, and cart.
     * 
     * Should be called from within a valid web request.
     *
     * @param request
     * @return
     */
    public static Map<String, Object> buildMvelParameters() {
        Map<String, Object> mvelParameters = new HashMap<String, Object>();
       BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc != null && brc.getRequest() != null) {
           TimeDTO timeDto = new TimeDTO(SystemTime.asCalendar());
            HttpServletRequest request = brc.getRequest();
            RequestDTO requestDto = brc.getRequestDTO();
            mvelParameters.put("time", timeDto);
            mvelParameters.put("request", requestDto);

            Map<String, Object> blcRuleMap = (Map<String, Object>) request.getAttribute(BLC_RULE_MAP_PARAM);
            if (blcRuleMap != null) {
                for (String mapKey : blcRuleMap.keySet()) {
                    mvelParameters.put(mapKey, blcRuleMap.get(mapKey));
                }
           }
       }

       return mvelParameters;
   }    
}
