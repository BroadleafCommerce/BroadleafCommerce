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

import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.util.BLCPaymentMethodUtils;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.broadleafcommerce.common.web.payment.controller.PaymentGatewayAbstractController;
import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
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

    public boolean orderContainsThirdPartyPayment() {
        return cartStateService.orderContainsThirdPartyPayment();
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

    protected boolean isNullOrder(Order cart) {
        return cart == null || (cart instanceof NullOrderImpl);
    }

}
