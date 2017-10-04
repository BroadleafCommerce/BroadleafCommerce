/*-
 * #%L
 * BroadleafCommerce Advanced CMS
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt).
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */
package org.broadleafcommerce.core.order.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.event.AbstractBroadleafApplicationEventListener;
import org.broadleafcommerce.common.event.BroadleafApplicationEventMulticaster;
import org.broadleafcommerce.common.event.OrderSubmittedEvent;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.notification.service.type.Notification;
import org.broadleafcommerce.common.notification.service.type.NotificationType;
import org.broadleafcommerce.common.notification.service.NotificationService;
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
    @Qualifier("blEmailNotificationService")
    protected NotificationService notificationService;

    @Override
    protected void handleApplicationEvent(OrderSubmittedEvent event) {
        Order order = orderService.findOrderById(event.getOrderId());
        if (order != null) {
            try {
                String emailAddress = order.getEmailAddress() != null ? order.getEmailAddress() : order.getCustomer().getEmailAddress();

                Map<String, Object> context = new HashMap<>();
                context.put(ORDER_CONTEXT_KEY, order);
                context.put(CUSTOMER_CONTEXT_KEY, order.getCustomer());

                notificationService.sendNotification(new Notification(emailAddress, NotificationType.ORDER_CONFIRMATION, context));
            } catch (ServiceException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Unable to send an order confirmation email for order #" + order.getOrderNumber(), e);
                }
            }
        }
    }

    @Override
    public boolean isAsynchronous() {
        return true;
    }
}
