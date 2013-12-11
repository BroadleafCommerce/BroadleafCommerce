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

package org.broadleafcommerce.common.web.payment.expression;

import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>A Thymeleaf Variable Expression implementation for Payment Gateway Specific fields.
 * The Payment Module specific names are invoked via the ExtensionManager.
 * Therefore, each module will need to register itself properly.</p>
 * <p>The input name mappings are those properties defined in the corresponding
 * DTOs.</p>
 *
 * <pre><code>
 * <input type="text" th:name="${#paymentGatewayField.mapName("creditCard.creditCardNum")}"/>
 * </code></pre>
 * translates to:
 *
 * <pre><code>
 * PayPal PayFlow Pro: <input type="text" name="CARDNUM"/>
 * Braintree:          <input type="text" name="transaction[credit_card][number]"/>
 * etc...
 * </code></pre>
 *
 * @see {@link org.broadleafcommerce.common.payment.dto.PaymentRequestDTO}
 * @see {@link org.broadleafcommerce.common.payment.dto.CreditCardDTO}
 * @see {@link org.broadleafcommerce.common.payment.dto.AddressDTO}
 *
 * @author Elbert Bautista (elbertbautista)
 */
public class PaymentGatewayFieldVariableExpression implements BroadleafVariableExpression {

    @Resource(name = "blPaymentGatewayFieldExtensionManager")
    protected PaymentGatewayFieldExtensionManager extensionManager;

    @Override
    public String getName() {
        return "paymentGatewayField";
    }

    public String mapName(String fieldName) {
        Map<String, String> fieldNameMap = new HashMap<String, String>();
        fieldNameMap.put(fieldName, fieldName);
        extensionManager.getProxy().mapFieldName(fieldName, fieldNameMap);
        return fieldNameMap.get(fieldName);
    }

    public PaymentGatewayFieldExtensionManager getExtensionManager() {
        return extensionManager;
    }

    public void setExtensionManager(PaymentGatewayFieldExtensionManager extensionManager) {
        this.extensionManager = extensionManager;
    }
}
