package org.broadleafcommerce.cms.structure.service;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mvel2.CompileException;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

/**
 * This class is useful for rule processors that need to execute MVEL rules.
 *
 * Sets up an LRU cache for rule processing and a convenience method for executing MVEL rules.
 *
 * Created by bpolster.
 *
 */
public abstract class AbstractStructuredContentRuleProcessor implements StructuredContentRuleProcessor {
    private static final Log LOG = LogFactory.getLog(AbstractStructuredContentRuleProcessor.class);

    private Map expressionCache = Collections.synchronizedMap(new LRUMap(1000));

    protected Boolean executeExpression(String expression, Map<String, Object> vars) {
        Serializable exp;
        synchronized (expressionCache) {
            exp = (Serializable) expressionCache.get(expression);
        }

        if (exp == null) {
            ParserContext context = new ParserContext();
            try {
                exp = MVEL.compileExpression(expression, context);
            } catch (CompileException ce) {
                LOG.warn("Compile exception processing phrase: " + expression,ce);
                return Boolean.FALSE;
            }

            synchronized (expressionCache) {
                exp = (Serializable) expressionCache.put(expression, exp);
            }
        }
        try {
            if (expression.contains("product.") && vars.get("product") == null) {
                return false;
            } else {
                return (Boolean) MVEL.executeExpression(exp, vars);
            }
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }
}
