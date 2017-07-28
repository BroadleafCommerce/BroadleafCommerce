/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.expression.checkout;

import org.broadleafcommerce.common.payment.PaymentAdditionalFieldType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.payment.service.OrderPaymentService;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.stereotype.Component;

import java.util.List;

import javax.annotation.Resource;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Component("blPaymentStageVariableExpression")
@ConditionalOnTemplating
public class PaymentStageVariableExpression implements BroadleafVariableExpression {

    @Resource(name = "blOrderPaymentService")
    protected OrderPaymentService orderPaymentService;

    @Override
    public String getName() {
        return "paymentStage";
    }

    public String getCartOrderPaymentCardType() {
        return getCartOrderPaymentProperty(PaymentAdditionalFieldType.CARD_TYPE.getType());
    }

    public String getCartOrderPaymentLastFour() {
        return getCartOrderPaymentProperty(PaymentAdditionalFieldType.LAST_FOUR.getType());
    }

    public String getCartOrderPaymentExpDate() {
        return getCartOrderPaymentProperty(PaymentAdditionalFieldType.EXP_DATE.getType());
    }

    protected String getCartOrderPaymentProperty(String propertyName) {
        Order cart = CartState.getCart();
        List<OrderPayment> orderPayments = orderPaymentService.readPaymentsForOrder(cart);
        for (OrderPayment orderPayment : orderPayments) {
            if (orderPayment.isActive() && PaymentType.CREDIT_CARD.equals(orderPayment.getType())) {
                List<PaymentTransaction> transactions = orderPayment.getTransactions();
                for (PaymentTransaction transaction : transactions) {
                    return transaction.getAdditionalFields().get(propertyName);
                }
            }
        }
        return null;
    }

}
