package org.broadleafcommerce.common.expression;

import java.util.Map;

/**
 * This component provides an expression parser, most typically for SPEL expressions.
 *
 * @see BroadleafExpressionParserImpl for default implementation
 * @author Nick Crum ncrum
 */
public interface BroadleafExpressionParser {

    /**
     * Parses the given expression string with the given context and returns the parsed result string.
     *
     * @param expressionString the expression string
     * @param context the context
     * @return the parsed expression result
     */
    String parseExpression(String expressionString, Map<String, Object> context);

    /**
     * Parses the given expression string with the given context and returns the parsed result with the target type.
     *
     * @param expressionString the expression string
     * @param context the context
     * @param targetType the target type for the result
     * @param <T> the generic type of the result
     * @return the parsed expression result
     */
    <T> T parseExpression(String expressionString, Map<String, Object> context, Class<T> targetType);
}
