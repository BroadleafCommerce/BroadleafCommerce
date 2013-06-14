/*
 * Copyright 2008-2013 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.web.processor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.BindStatus;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;
import org.thymeleaf.spring3.naming.SpringContextVariableNames;
import org.thymeleaf.spring3.util.FieldUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blErrorsProcessor")
public class ErrorsProcessor extends AbstractAttrProcessor {

    public ErrorsProcessor() {
        super("errors");
    }
    
    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    protected ProcessorResult processAttribute(Arguments arguments, Element element, String attributeName) {
        final String attributeValue = element.getAttributeValue(attributeName);
        
        final BindStatus bindStatus = 
            FieldUtils.getBindStatus(arguments, attributeValue, true);
        
        if (bindStatus.isError()) {
            final Map<String,Object> localVariables = new HashMap<String,Object>();
            ((Map<String, Object>) arguments.getExpressionEvaluationRoot()).put(SpringContextVariableNames.SPRING_FIELD_BIND_STATUS, bindStatus);
            return ProcessorResult.setLocalVariables(localVariables);
        }
        return ProcessorResult.OK;
        
    }

}
