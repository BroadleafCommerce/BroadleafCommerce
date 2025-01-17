/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.order.event;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.event.AbstractBroadleafApplicationEventListener;
import org.broadleafcommerce.common.event.BroadleafApplicationEventMulticaster;
import org.broadleafcommerce.common.event.OrderSubmittedEvent;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.notification.service.NotificationDispatcher;
import org.broadleafcommerce.common.notification.service.type.EmailNotification;
import org.broadleafcommerce.common.notification.service.type.NotificationEventType;
import org.broadleafcommerce.common.notification.service.type.SMSNotification;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * This event listener is responsible for sending any notifications in response to an {@code OrderSubmittedEvent}. By
 * default, this listener will handle the event synchronously unless the {@link BroadleafApplicationEventMulticaster} is
 * injected and configured correctly to handle asynchronous events.
 *
 * @see org.broadleafcommerce.core.checkout.service.workflow.CompleteOrderActivity for where the event is typically published
 * @author Nick Crum ncrum
 */
@Component("blNotificationOrderSubmittedEventListener")
public class NotificationOrderSubmittedEventListener extends AbstractBroadleafApplicationEventListener<OrderSubmittedEvent> {

    protected static final String ORDER_CONTEXT_KEY = "order";
    protected static final String CUSTOMER_CONTEXT_KEY = "customer";
    protected final Log LOG = LogFactory.getLog(NotificationOrderSubmittedEventListener.class);

    @Autowired
    @Qualifier("blOrderService")
    protected OrderService orderService;

    @Autowired
    @Qualifier("blNotificationDispatcher")
    protected NotificationDispatcher notificationDispatcher;

    @Override
    protected void handleApplicationEvent(OrderSubmittedEvent event) {
        Order order = orderService.findOrderById(event.getOrderId());
        if (order != null) {
            Map<String, Object> context = createContext(order);

            try {
                notificationDispatcher.dispatchNotification(new EmailNotification(order.getEmailAddress(), NotificationEventType.ORDER_CONFIRMATION, context));
            } catch (ServiceException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Failure to dispatch order confirmation email notification", e);
                }
            }

            try {
                notificationDispatcher.dispatchNotification(new SMSNotification(NotificationEventType.ORDER_CONFIRMATION, context));
            } catch (ServiceException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Failure to dispatch order confirmation sms notification", e);
                }
            }
        }
    }

    protected Map<String, Object> createContext(Order order) {
        Map<String, Object> context = new HashMap<>();
        if (order != null) {
            context.put(ORDER_CONTEXT_KEY, order);
            context.put(CUSTOMER_CONTEXT_KEY, order.getCustomer());
        }
        return MapUtils.unmodifiableMap(context);
    }

    @Override
    public boolean isAsynchronous() {
        return true;
    }
}
