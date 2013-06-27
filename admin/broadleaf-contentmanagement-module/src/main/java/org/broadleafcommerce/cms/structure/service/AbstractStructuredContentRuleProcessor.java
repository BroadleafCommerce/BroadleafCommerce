/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.structure.service;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.rule.MvelHelper;
import org.mvel2.CompileException;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is useful as a starting point for rule processors that need to execute MVEL rules.
 *
 * Sets up an LRU cache for rule processing and a convenience method for executing MVEL rules.
 *
 * @author bpolster
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
            parserContext.addImport("MvelHelper", MvelHelper.class);
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

    /**
     * Helpful method for processing a boolean MVEL expression and associated arguments.
     *
     * Caches the expression in an LRUCache.
     * @param expression
     * @param vars
     * @return the result of the expression
     */
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

            expressionCache.put(expression, exp);
        }
        try {
            return (Boolean) MVEL.executeExpression(exp, vars);
        } catch (Exception e) {
            LOG.error(e);
        }
        return false;
    }

    /**
     * List of class names to add to the MVEL ParserContext.
     *
     * @return
     * @see {@link ParserContext}
     */
    public Map<String, String> getContextClassNames() {
        return contextClassNames;
    }


    /**
     * List of class names to add to the MVEL ParserContext.
     *
     * @return
     * @see {@link ParserContext}
     */
    public void setContextClassNames(Map<String, String> contextClassNames) {
        this.contextClassNames = contextClassNames;
    }
}
