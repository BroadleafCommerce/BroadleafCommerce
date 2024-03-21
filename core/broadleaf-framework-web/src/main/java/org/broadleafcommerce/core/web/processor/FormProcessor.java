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
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.AbstractElementModelProcessor;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

/**
 * A Thymeleaf processor that adds a CSRF token to forms that are not going to be submitted
 * via GET
 * 
 * @author apazzolini
 */
@Component("blFormProcessor")
public class FormProcessor extends AbstractElementModelProcessor {
    
    @Resource(name = "blExploitProtectionService")
    protected ExploitProtectionService eps;
    
    /**
     * Sets the name of this processor to be used in Thymeleaf template
     */

    public FormProcessor() {
        super(TemplateMode.HTML, "blc", "form", true, null, false, 1);
    }

    @Override
    public void doProcess(ITemplateContext context, IModel model, IElementModelStructureHandler structureHandler) {
        Map<String, String> formAttributes = new HashMap<>();
        IModel mdl = context.getModelFactory().createModel();
        IProcessableElementTag rootTag = (IProcessableElementTag) model.get(0);
        String rootTagName = rootTag.getElementCompleteName();
        Map<String, String> rootTagAttributes = rootTag.getAttributeMap();
        formAttributes.putAll(rootTagAttributes);

        // If the form will be not be submitted with a GET, we must add the CSRF token
        // We do this instead of checking for a POST because post is default if nothing is specified
        if (!"GET".equalsIgnoreCase(rootTagAttributes.get("method"))) {
            try {
                String csrfToken = eps.getCSRFToken();

                //detect multipart form
                if ("multipart/form-data".equalsIgnoreCase(rootTagAttributes.get("enctype"))) {
                    String action = rootTagAttributes.get("action");
                    String csrfQueryParameter = "?" + eps.getCsrfTokenParameter() + "=" + csrfToken;
                    formAttributes.put("action",  action + csrfQueryParameter);
                } else {


                    Map<String, String> csrfAttributes = new HashMap<>();
                    csrfAttributes.put("type", "hidden");
                    csrfAttributes.put("name", eps.getCsrfTokenParameter());
                    csrfAttributes.put("value", csrfToken);
                    IStandaloneElementTag standaloneTag = context.getModelFactory().createStandaloneElementTag("input", csrfAttributes, AttributeValueQuotes.DOUBLE , false, true);
                    mdl.add(standaloneTag);
                }

            } catch (ServiceException e) {
                throw new RuntimeException("Could not get a CSRF token for this session", e);
            }
        }
        model.insertModel(model.size() - 1, mdl);
        model.replace(0, context.getModelFactory().createOpenElementTag("form", formAttributes, AttributeValueQuotes.DOUBLE, false));
        model.replace(model.size() - 1, context.getModelFactory().createCloseElementTag("form"));
    }
    
}
