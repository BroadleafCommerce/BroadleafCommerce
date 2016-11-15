/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.payment.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.payment.dto.PaymentRequestDTO;
import org.broadleafcommerce.common.payment.service.CurrentOrderPaymentRequestService;
import org.broadleafcommerce.common.vendor.service.exception.PaymentException;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderAttribute;
import org.broadleafcommerce.core.order.domain.OrderAttributeImpl;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.payment.service.OrderToPaymentRequestDTOService;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.order.CartState;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@Service("blDefaultCurrentPaymentRequestService")
public class DefaultCurrentOrderPaymentRequestService implements CurrentOrderPaymentRequestService {

    private static final Log LOG = LogFactory.getLog(DefaultCurrentOrderPaymentRequestService.class);

    @Resource(name = "blOrderToPaymentRequestDTOService")
    protected OrderToPaymentRequestDTOService paymentRequestDTOService;

    @Resource(name = "blOrderService")
    protected OrderService orderService;
    
    @Override
    public PaymentRequestDTO getPaymentRequestFromCurrentOrder() {
        Order currentCart = CartState.getCart();
        PaymentRequestDTO request = paymentRequestDTOService.translateOrder(currentCart);
        return request;
    }

    @Override
    public void addOrderAttributeToCurrentOrder(String orderAttributeKey, String orderAttributeValue) throws PaymentException {
        addOrderAttributeToOrder(null, orderAttributeKey, orderAttributeValue);
    }

    @Override
    public void addOrderAttributeToOrder(Long orderId, String orderAttributeKey, String orderAttributeValue) throws PaymentException {
        Order currentCart = CartState.getCart();
        Long currentCartId = currentCart.getId();
        
        if (orderId != null && !currentCartId.equals(orderId)) {
            logWarningIfCartMismatch(currentCartId, orderId);
            currentCart = orderService.findOrderById(orderId);
        }

        OrderAttribute orderAttribute = new OrderAttributeImpl();
        orderAttribute.setName(orderAttributeKey);
        orderAttribute.setValue(orderAttributeValue);
        orderAttribute.setOrder(currentCart);
        currentCart.getOrderAttributes().put(orderAttributeKey, orderAttribute);

        try {
            orderService.save(currentCart, false);
        } catch (PricingException e) {
            throw new PaymentException(e);
        }
    }
    
    protected void logWarningIfCartMismatch(Long currentCartId, Long orderId) {
        if (LOG.isWarnEnabled()) {
            LOG.warn(String.format("The current cart resolved from cart state [%s] is not the same as the requested order ID [%s]. Session may have expired or local cart state was lost. This may need manual review.", currentCartId, orderId));
        }
    }

    @Override
    public String retrieveOrderAttributeFromCurrentOrder(String orderAttributeKey) {
        return retrieveOrderAttributeFromOrder(null, orderAttributeKey);
    }

    @Override
    public String retrieveOrderAttributeFromOrder(Long orderId, String orderAttributeKey) {
        Order currentCart = CartState.getCart();
        Long currentCartId = currentCart.getId();
        
        if (orderId != null && !currentCartId.equals(orderId)) {
            logWarningIfCartMismatch(currentCartId, orderId);
            currentCart = orderService.findOrderById(orderId);
        }

        if (currentCart.getOrderAttributes().containsKey(orderAttributeKey)) {
            return currentCart.getOrderAttributes().get(orderAttributeKey).getValue();
        }

        return null;
    }

}
