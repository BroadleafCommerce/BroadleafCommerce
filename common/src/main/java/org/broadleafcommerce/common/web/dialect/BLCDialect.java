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
package org.broadleafcommerce.common.web.dialect;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator;
import org.thymeleaf.standard.expression.StandardExpressionExecutor;
import org.thymeleaf.standard.expression.StandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

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
        final StandardExpressionExecutor executor = StandardExpressionProcessor.createStandardExpressionExecutor(expressionEvaluator);
        final StandardExpressionParser parser = StandardExpressionProcessor.createStandardExpressionParser(executor);
        
        final Map<String,Object> executionAttributes = new LinkedHashMap<String, Object>();
        executionAttributes.put(StandardDialect.EXPRESSION_EVALUATOR_EXECUTION_ATTRIBUTE, expressionEvaluator);
        executionAttributes.put(StandardExpressionProcessor.STANDARD_EXPRESSION_EXECUTOR_ATTRIBUTE_NAME, executor);
        executionAttributes.put(StandardExpressionProcessor.STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME, parser);
        return executionAttributes;
    }

}
