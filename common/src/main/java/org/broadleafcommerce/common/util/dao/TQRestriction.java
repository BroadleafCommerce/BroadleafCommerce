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
        this.operation = operation.toLowerCase();
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
                if (operation.equals("in") || operation.equals("not in")) {
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
