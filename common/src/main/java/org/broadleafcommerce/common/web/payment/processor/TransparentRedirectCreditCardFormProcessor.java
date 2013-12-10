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
/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.web.payment.processor;

import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
import org.springframework.stereotype.Component;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.element.AbstractElementProcessor;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Elbert Bautista (elbertbautista)
 *
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
 * NOTE: please see {@link org.broadleafcommerce.common.web.payment.expression.PaymentGatewayFieldVariableExpression}
 * to modify the input "name" fields for a particular gateway
 *
 * @see {@link org.broadleafcommerce.common.web.payment.expression.PaymentGatewayFieldVariableExpression}
 * @see {@link TRCreditCardExtensionHandler}
 * @see {@link AbstractTRCreditCardExtensionHandler}
 *
 */
@Component("blTransparentRedirectCreditCardFormProcessor")
public class TransparentRedirectCreditCardFormProcessor extends AbstractElementProcessor {

    @Resource(name = "blTRCreditCardExtensionManager")
    protected TRCreditCardExtensionManager extensionManager;

    public TransparentRedirectCreditCardFormProcessor() {
        super("transparent_credit_card_form");
    }

    @Override
    public int getPrecedence() {
        return 1;
    }

    @Override
    protected ProcessorResult processElement(Arguments arguments, Element element) {

        PaymentRequestDTO requestDTO = (PaymentRequestDTO) StandardExpressionProcessor.processExpression(arguments,
                element.getAttributeValue("paymentRequestDTO"));

        Map<String, Map<String,String>> formParameters = new HashMap<String, Map<String,String>>();
        Map<String, String> configurationSettings = new HashMap<String, String>();

        //Create the configuration settings map to pass into the payment module
        Map<String, Attribute> attributeMap  = element.getAttributeMap();
        List<String> keysToRemove = new ArrayList<String>();
        for (String key : attributeMap.keySet()) {
            if (key.startsWith("config-")){
                final int trimLength = "config-".length();
                String configParam = key.substring(trimLength);
                configurationSettings.put(configParam, attributeMap.get(key).getValue());
                keysToRemove.add(key);
            }
        }

        for (String keyToRemove : keysToRemove) {
            element.removeAttribute(keyToRemove);
        }

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
        Map<String,String> actionValue = formParameters.get(formActionKey.toString());
        if (actionValue != null && actionValue.size()>0) {
            String key = (String)actionValue.keySet().toArray()[0];
            actionUrl = actionValue.get(key);
        }
        element.setAttribute("action", actionUrl);

        //Append any hidden fields necessary for the Transparent Redirect
        Map<String, String> hiddenFields = formParameters.get(formHiddenParamsKey.toString());
        if (hiddenFields != null && !hiddenFields.isEmpty()) {
            for (String key : hiddenFields.keySet()) {
                Element hiddenNode = new Element("input");
                hiddenNode.setAttribute("type", "hidden");
                hiddenNode.setAttribute("name", key);
                hiddenNode.setAttribute("value", hiddenFields.get(key));
                element.addChild(hiddenNode);
            }
        }

        // Convert the <blc:transparent_credit_card_form> node to a normal <form> node
        Element newElement = element.cloneElementNodeWithNewName(element.getParent(), "form", false);
        newElement.setRecomputeProcessorsImmediately(true);
        element.getParent().insertAfter(element, newElement);
        element.getParent().removeChild(element);

        return ProcessorResult.OK;
    }

}
