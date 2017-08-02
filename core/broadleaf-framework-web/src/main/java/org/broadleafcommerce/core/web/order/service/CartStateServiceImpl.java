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
package org.broadleafcommerce.core.web.order.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.broadleafcommerce.common.payment.PaymentAdditionalFieldType;
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.payment.service.OrderPaymentService;
import org.broadleafcommerce.core.web.order.CartState;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Service("blCartStateService")
public class CartStateServiceImpl implements CartStateService {

    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;

    @Resource(name = "blOrderPaymentService")
    protected OrderPaymentService orderPaymentService;

    @Override
    public boolean cartHasPopulatedOrderInfo() {
        return cartHasThirdPartyPayment() || cartHasUnconfirmedCreditCard();
    }

    @Override
    public boolean cartHasPopulatedBillingAddress() {
        Order cart = CartState.getCart();

        List<OrderPayment> orderPayments = orderPaymentService.readPaymentsForOrder(cart);
        for (OrderPayment payment : CollectionUtils.emptyIfNull(orderPayments)) {
            boolean isCreditCardPayment = PaymentType.CREDIT_CARD.equals(payment.getType());
            boolean paymentHasBillingAddress = (payment.getBillingAddress() != null);

            if (payment.isActive() && isCreditCardPayment && paymentHasBillingAddress) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean cartHasPopulatedShippingAddress() {
        Order cart = CartState.getCart();

        for (FulfillmentGroup fulfillmentGroup : CollectionUtils.emptyIfNull(cart.getFulfillmentGroups())) {
            if (fulfillmentGroupService.isShippable(fulfillmentGroup.getType())) {
                if (fulfillmentGroup.getAddress() != null && fulfillmentGroup.getFulfillmentOption() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean cartHasCreditCardPaymentWithSameToken(String paymentToken) {
        Order cart = CartState.getCart();

        List<OrderPayment> orderPayments = orderPaymentService.readPaymentsForOrder(cart);
        for (OrderPayment orderPayment : orderPayments) {
            if (orderPayment.isActive() && PaymentType.CREDIT_CARD.equals(orderPayment.getType())) {
                List<PaymentTransaction> transactions = orderPayment.getTransactions();
                for (PaymentTransaction transaction : transactions) {
                    String orderPaymentToken = transaction.getAdditionalFields().get(PaymentAdditionalFieldType.TOKEN.getType());

                    if (ObjectUtils.equals(orderPaymentToken, paymentToken)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean cartHasTemporaryCreditCard() {
        Order cart = CartState.getCart();

        List<OrderPayment> orderPayments = orderPaymentService.readPaymentsForOrder(cart);
        for (OrderPayment payment : CollectionUtils.emptyIfNull(orderPayments))  {
            boolean isCreditCartPayment = PaymentType.CREDIT_CARD.equals(payment.getType());
            boolean isTemporaryPaymentGateway = PaymentGatewayType.TEMPORARY.equals(payment.getGatewayType());

            if (payment.isActive() && isCreditCartPayment && isTemporaryPaymentGateway) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean cartHasThirdPartyPayment() {
        Order cart = CartState.getCart();

        List<OrderPayment> orderPayments = orderPaymentService.readPaymentsForOrder(cart);
        for (OrderPayment payment : CollectionUtils.emptyIfNull(orderPayments))  {
            if (payment.isActive() && PaymentType.THIRD_PARTY_ACCOUNT.equals(payment.getType())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean cartHasUnconfirmedCreditCard() {
        return getUnconfirmedCCFromCart() != null;
    }

    protected OrderPayment getUnconfirmedCCFromCart() {
        OrderPayment unconfirmedCC = null;

        Order cart = CartState.getCart();
        List<OrderPayment> orderPayments = orderPaymentService.readPaymentsForOrder(cart);
        for (OrderPayment payment : CollectionUtils.emptyIfNull(orderPayments))  {
            boolean isCreditCartPayment = PaymentType.CREDIT_CARD.equals(payment.getType());
            boolean isTemporaryPaymentGateway = PaymentGatewayType.TEMPORARY.equals(payment.getGatewayType());

            if (payment.isActive() && isCreditCartPayment && !isTemporaryPaymentGateway) {
                unconfirmedCC = payment;
            }
        }
        return unconfirmedCC;
    }

}
