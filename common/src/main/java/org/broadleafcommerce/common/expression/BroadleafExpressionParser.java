/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.common.expression;

import java.util.Map;

/**
 * This component provides an expression parser, most typically for SPEL expressions.
 *
 * @author Nick Crum ncrum
 * @see BroadleafExpressionParserImpl for default implementation
 */
public interface BroadleafExpressionParser {

    /**
     * Parses the given expression string with the given context and returns the parsed result string.
     *
     * @param expressionString the expression string
     * @param context          the context
     * @return the parsed expression result
     */
    String parseExpression(String expressionString, Map<String, Object> context);

    /**
     * Parses the given expression string with the given context and returns the parsed result with the target type.
     *
     * @param expressionString the expression string
     * @param context          the context
     * @param targetType       the target type for the result
     * @param <T>              the generic type of the result
     * @return the parsed expression result
     */
    <T> T parseExpression(String expressionString, Map<String, Object> context, Class<T> targetType);

}
