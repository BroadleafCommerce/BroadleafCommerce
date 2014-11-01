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

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mvel2.CompileException;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public abstract class AbstractRuleProcessor<T> implements RuleProcessor<T> {
    
    protected final Log LOG = LogFactory.getLog(this.getClass());

    @SuppressWarnings("unchecked")
    protected Map<String, Serializable> expressionCache = Collections.synchronizedMap(new LRUMap(1000));
    protected ParserContext parserContext;
    protected Map<String, String> contextClassNames = new HashMap<String, String> ();

    /**
     * Having a parser context that imports the classes speeds MVEL by up to 60%.
     */
    protected ParserContext getParserContext() {
        if (parserContext == null) {
            parserContext = new ParserContext();
            parserContext.addImport("MVEL", MVEL.class);
            parserContext.addImport("MvelHelper", MvelHelper.class);
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
     * @see {@link ParserContext}
     */
    public Map<String, String> getContextClassNames() {
        return contextClassNames;
    }

    /**
     * List of class names to add to the MVEL ParserContext.
     * @see {@link ParserContext}
     */
    public void setContextClassNames(Map<String, String> contextClassNames) {
        this.contextClassNames = contextClassNames;
    }

}
