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
import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.util.BLCPaymentMethodUtils;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.broadleafcommerce.common.web.payment.controller.PaymentGatewayAbstractController;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.payment.service.OrderPaymentService;
import org.broadleafcommerce.core.payment.service.OrderToPaymentRequestDTOService;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.core.web.order.service.CartStateService;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.stereotype.Component;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Component("blPaymentMethodVariableExpression")
@ConditionalOnTemplating
public class PaymentMethodVariableExpression implements BroadleafVariableExpression {

    @Resource(name = "blCartStateService")
    protected CartStateService cartStateService;

    @Resource(name = "blOrderPaymentService")
    protected OrderPaymentService orderPaymentService;

    @Resource(name = "blOrderToPaymentRequestDTOService")
    protected OrderToPaymentRequestDTOService orderToPaymentRequestDTOService;

    @Override
    public String getName() {
        return "paymentMethod";
    }

    public PaymentRequestDTO getPaymentRequestDTO() {
        Order cart = CartState.getCart();

        return isNullOrder(cart) ? null : orderToPaymentRequestDTOService.translateOrder(cart);
    }

    protected boolean isNullOrder(Order cart) {
        return cart == null || (cart instanceof NullOrderImpl);
    }

    public boolean cartContainsThirdPartyPayment() {
        return cartStateService.cartHasThirdPartyPayment();
    }

    public boolean cartContainsTemporaryCreditCard() {
        return cartStateService.cartHasTemporaryCreditCard();
    }

    public boolean orderContainsCODPayment(Order order) {
        return orderContainsPaymentOfType(order, PaymentType.COD);
    }

    public boolean orderContainsCreditCardPayment(Order order) {
        return orderContainsPaymentOfType(order, PaymentType.CREDIT_CARD);
    }

    protected boolean orderContainsPaymentOfType(Order order, PaymentType paymentType) {
        List<OrderPayment> orderPayments = orderPaymentService.readPaymentsForOrder(order);

        for (OrderPayment payment : orderPayments) {
            boolean isActive = payment.isActive();
            boolean isOfCorrectType = paymentType.equals(payment.getType());

            if (isActive && isOfCorrectType) {
                return true;
            }
        }
        return false;
    }

    /**
     * A helper method used to construct a list of Credit Card Expiration Months
     * Useful for expiration dropdown menus.
     * Will use locale to determine language if a locale is available.
     *
     * @return List containing expiration months of the form "01 - January"
     */
    public List<String> getExpirationMonthOptions() {
        return BLCPaymentMethodUtils.getExpirationMonthOptions();
    }

    /**
     * A helper method used to construct a list of Credit Card Expiration Years
     * Useful for expiration dropdown menus.
     *
     * @return List of the next ten years starting with the current year.
     */
    public List<String> getExpirationYearOptions() {
        return BLCPaymentMethodUtils.getExpirationYearOptions();
    }

    public String getCreditCardTypeFromCart() {
        return getCartOrderPaymentProperty(PaymentAdditionalFieldType.CARD_TYPE.getType());
    }

    public String getCreditCardLastFourFromCart() {
        return getCartOrderPaymentProperty(PaymentAdditionalFieldType.LAST_FOUR.getType());
    }

    public String getCreditCardExpDateFromCart() {
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

    /**
     * This method is responsible for gathering any Payment Processing Errors that may have been stored
     * as a Redirect Attribute when attempting to checkout.
     */
    public String getPaymentProcessingError() {
        BroadleafRequestContext blcContext = BroadleafRequestContext.getBroadleafRequestContext();
        HttpServletRequest request = blcContext.getRequest();

        return request.getParameter(PaymentGatewayAbstractController.PAYMENT_PROCESSING_ERROR);
    }

}
