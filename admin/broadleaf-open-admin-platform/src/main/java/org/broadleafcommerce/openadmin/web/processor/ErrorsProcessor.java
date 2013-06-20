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

import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Tab;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.support.BindStatus;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;
import org.thymeleaf.spring3.util.FieldUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Processor that returns all the errors within an {@link EntityForm} organized by tab, according to the expression passed
 * in as an argument.
 * 
 * For instance, if you would like to get all of the errors for the {@link EntityForm}, invoke this processor with an
 * attribute that looks like:
 * 
 *      blc_admin:errors="*{*}"
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
        String attributeValue = element.getAttributeValue(attributeName);
        
        BindStatus bindStatus = FieldUtils.getBindStatus(arguments, attributeValue, true);
        
        if (bindStatus.isError()) {
            EntityForm form = (EntityForm) ((BindingResult)bindStatus.getErrors()).getTarget();
            
            Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
            for (FieldError err : bindStatus.getErrors().getFieldErrors()) {
                Tab tab = form.findTabForField(err.getField());
                //TODO: didn't find a tab for the field, save these to use in the global errors. This will also
                //make hidden fields work with validation (methinks)
                if (tab != null) {
                    Map<String, String> tabErrors = result.get(tab.getTitle());
                    if (tabErrors == null) {
                        tabErrors = new HashMap<String, String>();
                        result.put(tab.getTitle(), tabErrors);
                    }
                    
                    tabErrors.put(form.findField(err.getField()).getFriendlyName(), err.getCode());
                }
            }
            
            for (ObjectError err : bindStatus.getErrors().getGlobalErrors()) {
                
            }
            
            Map<String,Object> localVariables = new HashMap<String,Object>();
            localVariables.put("tabErrors", result);
            return ProcessorResult.setLocalVariables(localVariables);
        }
        return ProcessorResult.OK;
        
    }

}
