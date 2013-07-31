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

import org.broadleafcommerce.openadmin.web.form.entity.DynamicEntityFormInfo;
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
                //attempt to look up which tab the field error is on. If it can't be found, just use
                //the default tab for the group
                String tabName = EntityForm.DEFAULT_TAB_NAME;
                Tab tab = form.findTabForField(err.getField());
                if (tab != null) {
                    tabName = tab.getTitle();
                }
                
                Map<String, String> tabErrors = result.get(tabName);
                if (tabErrors == null) {
                    tabErrors = new HashMap<String, String>();
                    result.put(tabName, tabErrors);
                }
                if (err.getField().contains(DynamicEntityFormInfo.FIELD_SEPARATOR)) {
                    //at this point the field name actually occurs within some array syntax
                    String fieldName = err.getField().substring(err.getField().indexOf('[') + 1, err.getField().lastIndexOf(']'));
                    String[] fieldInfo = fieldName.split("\\" + DynamicEntityFormInfo.FIELD_SEPARATOR);
                    tabErrors.put(form.getDynamicForm(fieldInfo[0]).getFields().get(fieldName).getFriendlyName(), err.getCode());
                } else {
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
