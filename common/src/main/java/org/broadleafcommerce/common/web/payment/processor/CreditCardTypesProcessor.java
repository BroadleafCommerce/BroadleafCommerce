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

package org.broadleafcommerce.common.web.payment.processor;

import org.springframework.stereotype.Component;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.element.AbstractLocalVariableDefinitionElementProcessor;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>The following processor will add any Payment Gateway specific Card Type 'codes' to the model if
 * the gateway requires that a 'Card Type' (e.g. Visa, MasterCard, etc...) be sent along with
 * the credit card number and expiry date.
 * </p>
 *
 * <p>This processor will put the key 'paymentGatewayCardTypes' on the model if there are any types available</p>
 *
 * <p>Here is an example:</p>
 *
 * <pre><code>
 *     <blc:credit_card_types />
 * </code></pre>
 *
 * @author Elbert Bautista (elbertbautista)
 */
@Component("blCreditCardTypesProcessor")
public class CreditCardTypesProcessor extends AbstractLocalVariableDefinitionElementProcessor {

    @Resource(name = "blCreditCardTypesExtensionManager")
    protected CreditCardTypesExtensionManager extensionManager;

    public CreditCardTypesProcessor() {
        super("credit_card_types");
    }

    @Override
    public int getPrecedence() {
        return 100;
    }

    @Override
    protected boolean removeHostElement(Arguments arguments, Element element) {
        return false;
    }

    @Override
    protected Map<String, Object> getNewLocalVariables(Arguments arguments, Element element) {
        Map<String, Object> localVars = new HashMap<String, Object>();

        Map<String, String> creditCardTypes = new HashMap<String, String>();
        if (extensionManager != null && extensionManager.getProxy()!=null) {
            extensionManager.getProxy().populateCreditCardMap(creditCardTypes);
        }

        if (!creditCardTypes.isEmpty()) {
            localVars.put("paymentGatewayCardTypes", creditCardTypes);
        }

        return localVars;
    }



}
