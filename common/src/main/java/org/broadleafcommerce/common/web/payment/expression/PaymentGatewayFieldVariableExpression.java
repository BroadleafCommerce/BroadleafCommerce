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

package org.broadleafcommerce.common.web.payment.expression;

import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

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
@Component("blpaymentGatewayFieldVariableExpression")
@ConditionalOnTemplating
public class PaymentGatewayFieldVariableExpression implements BroadleafVariableExpression {

    @Resource(name = "blPaymentGatewayFieldExtensionManager")
    protected PaymentGatewayFieldExtensionManager extensionManager;

    @Override
    public String getName() {
        return "paymentGatewayField";
    }

    public String mapName(String fieldName) {
        Map<String, String> fieldNameMap = new HashMap<>();
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
