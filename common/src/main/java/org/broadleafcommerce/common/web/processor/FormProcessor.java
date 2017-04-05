/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.common.web.processor;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.security.handler.CsrfFilter;
import org.broadleafcommerce.common.security.service.ExploitProtectionService;
import org.broadleafcommerce.common.security.service.StaleStateProtectionService;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafModelModifierProcessor;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.broadleafcommerce.presentation.model.BroadleafTemplateElement;
import org.broadleafcommerce.presentation.model.BroadleafTemplateModel;
import org.broadleafcommerce.presentation.model.BroadleafTemplateModelModifierDTO;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

/**
 * Used as a replacement to the HTML {@code <form>} element which adds a CSRF token input field to forms that are submitted
 * via anything but GET. This is required to properly bypass the {@link CsrfFilter}.
 * 
 * @author apazzolini
 * @see {@link CsrfFilter}
 */
@Component("blFormProcessor")
@ConditionalOnTemplating
public class FormProcessor extends AbstractBroadleafModelModifierProcessor {
    
    @Resource(name = "blExploitProtectionService")
    protected ExploitProtectionService eps;

    @Resource(name = "blStaleStateProtectionService")
    protected StaleStateProtectionService spps;
    
    @Override
    public String getName() {
        return "form";
    }
    
    @Override
    public int getPrecedence() {
        return 1001;
    }
    
    @Override
    public BroadleafTemplateModelModifierDTO getInjectedModelAndTagAttributes(String rootTagName, Map<String, String> rootTagAttributes, BroadleafTemplateContext context) {
        Map<String, String> formAttributes = new HashMap<>();
        formAttributes.putAll(rootTagAttributes);
        BroadleafTemplateModel model = context.createModel();
        BroadleafTemplateModelModifierDTO dto = new BroadleafTemplateModelModifierDTO();

        // If the form will be not be submitted with a GET, we must add the CSRF token
        // We do this instead of checking for a POST because post is default if nothing is specified
        if (!"GET".equalsIgnoreCase(formAttributes.get("method"))) {
            try {
                String csrfToken = eps.getCSRFToken();
                String stateVersionToken = null;
                if (spps.isEnabled()) {
                    stateVersionToken = spps.getStateVersionToken();
                }

                //detect multipart form
                if ("multipart/form-data".equalsIgnoreCase(formAttributes.get("enctype"))) {
                    String csrfQueryParameter = "?" + eps.getCsrfTokenParameter() + "=" + csrfToken;
                    if (stateVersionToken != null) {
                        csrfQueryParameter += "&" + spps.getStateVersionTokenParameter() + "=" + stateVersionToken;
                    }
                    
                    // Add this into the attribute map to be used for the new <form> tag. The expression has already
                    // been executed, don't need to treat the value as an expression
                    String actionValue = formAttributes.get("action");
                    actionValue += csrfQueryParameter;
                    formAttributes.put("action", actionValue);
                } else {
                    
                    Map<String, String> csrfAttributes = new HashMap<>();
                    csrfAttributes.put("type", "hidden");
                    csrfAttributes.put("name", eps.getCsrfTokenParameter());
                    csrfAttributes.put("value", csrfToken);
                    BroadleafTemplateElement csrfTag = context.createStandaloneElement("input", csrfAttributes, true);
                    model.addElement(csrfTag);

                    if (stateVersionToken != null) {
                        
                        Map<String, String> stateVersionAttributes = new HashMap<>();
                        stateVersionAttributes.put("type", "hidden");
                        stateVersionAttributes.put("name", spps.getStateVersionTokenParameter());
                        stateVersionAttributes.put("value", stateVersionToken);
                        BroadleafTemplateElement stateVersionTag = context.createStandaloneElement("input", stateVersionAttributes, true);
                        model.addElement(stateVersionTag);
                    }
                    dto.setModel(model);
                }
                
            } catch (ServiceException e) {
                throw new RuntimeException("Could not get a CSRF token for this session", e);
            }
        }
        dto.setFormParameters(formAttributes);
        dto.setReplacementTagName("form");
        return dto;
    }
    
    @Override
    public boolean reprocessModel() {
        return true;
    }
    
}
