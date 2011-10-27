package org.broadleafcommerce.cms.structure.service;

import java.io.Serializable;
import java.util.*;

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
    private ParserContext parserContext;
    private Map<String, String> contextClassNames = new HashMap<String, String> ();

    /**
     * Having a parser context that imports the classes speeds MVEL by up to 60%.
     * @return
     */
    protected ParserContext getParserContext() {
        if (parserContext == null) {
            parserContext = new ParserContext();
            parserContext.addImport("MVEL", MVEL.class);
           /*  Getting errors when the following is in place.
           for (String key : contextClassNames.keySet()) {
                String className = contextClassNames.get(key);
                try {
                    Class c = Class.forName(className);
                    parserContext.addImport(key, c);
                } catch (ClassNotFoundException e) {
                    LOG.error("Error resolving classname while setting up MVEL context, rule processing based on the key " + key + " will not be optimized", e);
                }

            }  */
        }
        return parserContext;
    }


    protected Boolean executeExpression(String expression, Map<String, Object> vars) {
        Serializable exp = (Serializable) expressionCache.get(expression);
        vars.put("MVEL", MVEL.class);

        if (exp == null) {

            try {
                exp = MVEL.compileExpression(expression, getParserContext());
            } catch (CompileException ce) {
                LOG.warn("Compile exception processing phrase: " + expression,ce);
                return Boolean.FALSE;
            }

            exp = (Serializable) expressionCache.put(expression, exp);
        }
        try {
            return (Boolean) MVEL.executeExpression(exp, vars);
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    public Map<String, String> getContextClassNames() {
        return contextClassNames;
    }

    public void setContextClassNames(Map<String, String> contextClassNames) {
        this.contextClassNames = contextClassNames;
    }
}
