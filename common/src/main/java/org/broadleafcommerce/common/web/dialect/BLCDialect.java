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
package org.broadleafcommerce.common.web.dialect;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator;
import org.thymeleaf.standard.expression.StandardExpressions;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

public class BLCDialect extends AbstractDialect {
    
    private Set<IProcessor> processors = new HashSet<IProcessor>();
    
    @Resource(name = "blVariableExpressionEvaluator")
    private IStandardVariableExpressionEvaluator expressionEvaluator;

    @Override
    public String getPrefix() {
        return "blc";
    }

    @Override
    public boolean isLenient() {
        return true;
    }
    
    @Override 
    public Set<IProcessor> getProcessors() {        
        return processors; 
    } 
    
    public void setProcessors(Set<IProcessor> processors) {
        this.processors = processors;
    }
    
    @Override
    public Map<String, Object> getExecutionAttributes() {
        final Map<String,Object> executionAttributes = new LinkedHashMap<String, Object>();
        executionAttributes.put(StandardExpressions.STANDARD_VARIABLE_EXPRESSION_EVALUATOR_ATTRIBUTE_NAME, expressionEvaluator);
        return executionAttributes;
    }

}
