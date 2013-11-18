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
package org.broadleafcommerce.common.util.dao;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Utilized in conjunction with {@link TypedQueryBuilder} to generate TypedQueries programmatically.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class TQRestriction {
    
    protected String expression;
    protected String operation;
    protected Object parameter;
    
    protected Mode joinMode;
    protected List<TQRestriction> restrictions = new ArrayList<TQRestriction>();
    
    /**
     * Creates a simple restriction. As there is no value associated, it is expected that the operation does not require
     * a parameter value, such as IS NULL.
     * 
     * @param expression
     * @param operation
     */
    public TQRestriction(String expression, String operation) {
        this.expression = expression;
        this.operation = operation;
    }
    
    /**
     * Creates a simple restriction.
     * 
     * @param expression
     * @param operation
     * @param parameter
     */
    public TQRestriction(String expression, String operation, Object parameter) {
        this(expression, operation);
        this.parameter = parameter;
    }
    
    /**
     * Creates an empty restriction node with the specified join mode. It is expected that this restriction would then
     * have at least 2 items in the restrictions list.
     * 
     * @param joinMode
     */
    public TQRestriction(Mode joinMode) {
        this.joinMode = joinMode;
    }
    
    /**
     * Adds a child restriction to the restrictions list
     * 
     * @param r
     * @return this
     */
    public TQRestriction addChildRestriction(TQRestriction r) {
        restrictions.add(r);
        return this;
    }
    
    /**
     * Recursively generates a query string representation of this restriction along with any child restrictions
     * that this object may have.
     * 
     * It will also populate the paramMap for the appropriate values as it's iterating through the restrictions.
     * 
     * @param parameterName
     * @param paramMap
     * @return the query language string
     */
    public String toQl(String parameterName, Map<String, Object> paramMap) {
        StringBuilder sb = new StringBuilder("(");
        if (expression != null && operation != null) {
            sb.append(expression).append(" ").append(operation);

            if (parameter != null) {
                sb.append(' ');
                String pname = ':' + parameterName;
                if (operation.equals("in")) {
                    pname = "(" + pname + ")";
                }
                sb.append(pname);
                paramMap.put(parameterName, parameter);
            }
        }
        
        if (CollectionUtils.isNotEmpty(restrictions)) {
            for (int i = 0; i < restrictions.size(); i++) {
                TQRestriction r = restrictions.get(i);
                String internalParamName = parameterName + "_" + i;
                
                sb.append(r.toQl(internalParamName, paramMap));
                paramMap.put(internalParamName, r.parameter);
                
                if (restrictions.size() - 1 != i) {
                    sb.append(joinMode == Mode.OR ? " OR " : " AND ");
                }
            }
        }
        
        return sb.append(")").toString();
    }
    
    public enum Mode {
        OR, AND
    }
    
}