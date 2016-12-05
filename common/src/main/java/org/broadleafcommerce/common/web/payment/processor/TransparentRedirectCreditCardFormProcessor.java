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

package org.broadleafcommerce.common.web.payment.processor;

import org.apache.commons.collections.MapUtils;
import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
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
 * <p>The following processor will modify the declared Credit Card Form
 * and call the Transparent Redirect Service of the configured payment gateway. </p>
 *
 * <p>This processor will change the form's action URL and append any hidden input fields
 * that are necessary to make the call. Certain gateway implementations accept configuration
 * settings in order to generate the form. These configuration parameters can be passed into
 * the module, by prefixing any configuration settings name with "config-" + attribute name = attribute value
 * </p>
 * <p>Here is an example:</p>
 *
 * <pre><code>
 *     <blc:transparent_credit_card_form action="#" method="POST"
 *         paymentRequestDTO="${requestDTO}"
 *         config-specificGatewayParam="value1"
 *         config-specificGatewayParam2="value2"
 *         config-specificGatewayParam3="value3">
 *
 *         <input type="text" name="credit_card_num"/>
 *         ...
 *
 *     </blc:transparent_credit_form>
 * </code></pre>
 *
 * <p>NOTE: please see {@link org.broadleafcommerce.common.web.payment.expression.PaymentGatewayFieldVariableExpression}
 * to modify the input "name" fields for a particular gateway</p>
 *
 * @see {@link org.broadleafcommerce.common.web.payment.expression.PaymentGatewayFieldVariableExpression}
 * @see {@link TRCreditCardExtensionHandler}
 * @see {@link AbstractTRCreditCardExtensionHandler}
 *
 * @author Elbert Bautista (elbertbautista)
 */
@Component("blTransparentRedirectCreditCardFormProcessor")
@ConditionalOnTemplating
public class TransparentRedirectCreditCardFormProcessor extends AbstractBroadleafModelModifierProcessor {

    @Resource(name = "blTRCreditCardExtensionManager")
    protected TRCreditCardExtensionManager extensionManager;

    public TRCreditCardExtensionManager getExtensionManager() {
        return extensionManager;
    }

    public void setExtensionManager(TRCreditCardExtensionManager extensionManager) {
        this.extensionManager = extensionManager;
    }
    
    @Override
    public String getName() {
        return "transparent_credit_card_form";
    }
    
    @Override
    public int getPrecedence() {
        return 1;
    }
    
    @Override
    public BroadleafTemplateModelModifierDTO getInjectedModelAndTagAttributes(String rootTagName, Map<String, String> rootTagAttributes, BroadleafTemplateContext context) {
        PaymentRequestDTO requestDTO = (PaymentRequestDTO) context.parseExpression(rootTagAttributes.get("paymentRequestDTO"));

        Map<String, Map<String, String>> formParameters = new HashMap<>();
        Map<String, String> configurationSettings = new HashMap<>();

        //Create the configuration settings map to pass into the payment module
        Map<String, String> keysToKeep = new HashMap<>();
        for (String key : rootTagAttributes.keySet()) {
            if (key.startsWith("config-")) {
                final int trimLength = "config-".length();
                String configParam = key.substring(trimLength);
                configurationSettings.put(configParam, rootTagAttributes.get(key));
            } else {
                keysToKeep.put(key, rootTagAttributes.get(key));
            }
        }
        keysToKeep.remove("paymentRequestDTO");

        try {
            extensionManager.getProxy().createTransparentRedirectForm(formParameters,
                requestDTO, configurationSettings);
        } catch (PaymentException e) {
            throw new RuntimeException("Unable to Create the Transparent Redirect Form", e);
        }

        StringBuilder formActionKey = new StringBuilder("formActionKey");
        StringBuilder formHiddenParamsKey = new StringBuilder("formHiddenParamsKey");
        extensionManager.getProxy().setFormActionKey(formActionKey);
        extensionManager.getProxy().setFormHiddenParamsKey(formHiddenParamsKey);

        //Change the action attribute on the form to the Payment Gateways Endpoint
        String actionUrl = "";
        Map<String, String> actionValue = formParameters.get(formActionKey.toString());
        if (actionValue != null && actionValue.size() > 0) {
            String key = (String) actionValue.keySet().toArray()[0];
            actionUrl = actionValue.get(key);
        }
        keysToKeep.put("action", actionUrl);

        BroadleafTemplateModel model = context.createModel();
        //Append any hidden fields necessary for the Transparent Redirect
        Map<String, String> hiddenFields = formParameters.get(formHiddenParamsKey.toString());
        if (MapUtils.isNotEmpty(hiddenFields)) {
            for (String key : hiddenFields.keySet()) {
                Map<String, String> attributes = new HashMap<>();
                attributes.put("type", "hidden");
                attributes.put("name", key);
                attributes.put("value", hiddenFields.get(key));
                BroadleafTemplateElement input = context.createStandaloneElement("input", attributes, true);
                model.addElement(input);
            }
        }
        return new BroadleafTemplateModelModifierDTO(model, keysToKeep, "form");
    }

    @Override
    public boolean reprocessModel() {
        return true;
    }
}
