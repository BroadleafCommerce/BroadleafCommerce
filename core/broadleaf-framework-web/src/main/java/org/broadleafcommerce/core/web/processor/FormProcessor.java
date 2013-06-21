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

package org.broadleafcommerce.core.web.processor;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.security.service.ExploitProtectionService;
import org.broadleafcommerce.core.web.util.ProcessorUtils;
import org.springframework.stereotype.Component;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.element.AbstractElementProcessor;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

/**
 * A Thymeleaf processor that adds a CSRF token to forms that are not going to be submitted
 * via GET
 * 
 * @author apazzolini
 */
@Component("blFormProcessor")
public class FormProcessor extends AbstractElementProcessor {
    
    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */
    public FormProcessor() {
        super("form");
    }
    
    /**
     * We need this replacement to execute as early as possible to allow subsequent processors to act
     * on this element as if it were a normal form instead of a blc:form
     */
    @Override
    public int getPrecedence() {
        return 1;
    }

    @Override
    protected ProcessorResult processElement(Arguments arguments, Element element) {
        // If the form will be not be submitted with a GET, we must add the CSRF token
        // We do this instead of checking for a POST because post is default if nothing is specified
        if (!"GET".equalsIgnoreCase(element.getAttributeValueFromNormalizedName("method"))) {
            try {

                ExploitProtectionService eps = ProcessorUtils.getExploitProtectionService(arguments);
                String csrfToken = eps.getCSRFToken();

                //detect multipart form
                if ("multipart/form-data".equalsIgnoreCase(element.getAttributeValueFromNormalizedName("enctype"))) {
                    String action = (String) StandardExpressionProcessor.processExpression(arguments, element.getAttributeValueFromNormalizedName("th:action"));
                    String csrfQueryParameter = "?" + eps.getCsrfTokenParameter() + "=" + csrfToken;
                    element.removeAttribute("th:action");
                    element.setAttribute("action", action + csrfQueryParameter);
                } else {
                    Element csrfNode = new Element("input");
                    csrfNode.setAttribute("type", "hidden");
                    csrfNode.setAttribute("name", eps.getCsrfTokenParameter());
                    csrfNode.setAttribute("value", csrfToken);
                    element.addChild(csrfNode);
                }

            } catch (ServiceException e) {
                throw new RuntimeException("Could not get a CSRF token for this session", e);
            }
        }
        
        // Convert the <blc:form> node to a normal <form> node
        Element newElement = element.cloneElementNodeWithNewName(element.getParent(), "form", false);
        newElement.setRecomputeProcessorsImmediately(true);
        element.getParent().insertAfter(element, newElement);
        element.getParent().removeChild(element);
        
        return ProcessorResult.OK;
    }
    
}
