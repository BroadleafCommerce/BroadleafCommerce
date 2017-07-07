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
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.web.order.CartState;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Service("blCartStateService")
public class CartStateServiceImpl implements CartStateService {

    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;

    @Override
    public boolean hasPopulatedOrderInfo() {
        Order cart = CartState.getCart();

        return orderContainsThirdPartyPayment() || orderContainsUnconfirmedCreditCard();
    }

    @Override
    public boolean hasPopulatedBillingAddress() {
        Order cart = CartState.getCart();

        for (OrderPayment payment : CollectionUtils.emptyIfNull(cart.getPayments())) {
            boolean isCreditCardPayment = PaymentType.CREDIT_CARD.equals(payment.getType());
            boolean paymentHasBillingAddress = (payment.getBillingAddress() != null);

            if (payment.isActive() && isCreditCardPayment && paymentHasBillingAddress) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasPopulatedShippingAddress() {
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
    public boolean orderContainsThirdPartyPayment() {
        Order cart = CartState.getCart();

        for (OrderPayment payment : CollectionUtils.emptyIfNull(cart.getPayments())) {
            if (payment.isActive() && PaymentType.THIRD_PARTY_ACCOUNT.equals(payment.getType())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean orderContainsUnconfirmedCreditCard() {
        return getUnconfirmedCCFromCart() != null;
    }

    protected OrderPayment getUnconfirmedCCFromCart() {
        OrderPayment unconfirmedCC = null;

        Order cart = CartState.getCart();
        for (OrderPayment payment : CollectionUtils.emptyIfNull(cart.getPayments())) {
            boolean isCreditCartPayment = PaymentType.CREDIT_CARD.equals(payment.getType());
            boolean isTemporaryPaymentGateway = PaymentGatewayType.TEMPORARY.equals(payment.getGatewayType());

            if (payment.isActive() && (isCreditCartPayment && !isTemporaryPaymentGateway)) {
                unconfirmedCC = payment;
            }
        }
        return unconfirmedCC;
    }

}
