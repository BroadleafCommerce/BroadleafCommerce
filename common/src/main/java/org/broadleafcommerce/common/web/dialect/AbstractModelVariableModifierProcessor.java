/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.web.dialect;

import org.apache.commons.collections4.MapUtils;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.request.WebRequest;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.Map;

/**
 * @author apazzolini
 * 
 * Wrapper class around Thymeleaf's AbstractElementProcessor that facilitates adding Objects
 * to the current evaluation context (model) for processing in the remainder of the page.
 *
 */
public abstract class AbstractModelVariableModifierProcessor extends AbstractElementTagProcessor implements ApplicationContextAware {

    protected ApplicationContext applicationContext;

    public AbstractModelVariableModifierProcessor(TemplateMode templateMode, String dialectPrefix, String elementName, boolean prefixElementName, String attributeName, boolean prefixAttributeName, int precedence) {
        super(templateMode, dialectPrefix, elementName, prefixElementName, attributeName, prefixAttributeName, precedence);
    }


    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {

        Map<String, Object> newModelVariables = populateModelVariables(context, tag, structureHandler);

        if (MapUtils.isNotEmpty(newModelVariables)) {
            for (Map.Entry<String, Object> entry : newModelVariables.entrySet()) {
                addToGlobalModel(entry.getKey(), entry.getValue());
            }
        }

        // Remove the tag from the DOM
        structureHandler.removeTags();
    }

    protected void addToGlobalModel(String key, Object value) {
        WebRequest request = BroadleafRequestContext.getBroadleafRequestContext()
                .getWebRequest();
        if (request != null) {
            request.setAttribute(key, value, WebRequest.SCOPE_REQUEST);
        }
    }

    protected abstract Map<String, Object> populateModelVariables(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler);


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
