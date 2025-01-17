/*-
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.core.event;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.event.AbstractBroadleafApplicationEventListener;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.notification.service.NotificationDispatcher;
import org.broadleafcommerce.common.notification.service.type.EmailNotification;
import org.broadleafcommerce.common.notification.service.type.NotificationEventType;
import org.broadleafcommerce.common.notification.service.type.SMSNotification;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nick Crum ncrum
 */
@Component("blNotificationForgotPasswordEventListener")
public class NotificationForgotPasswordEventListener extends AbstractBroadleafApplicationEventListener<ForgotPasswordEvent> {

    protected static final String TOKEN_CONTEXT_KEY = "token";
    protected static final String RESET_PASSWORD_URL_CONTEXT_KEY = "resetPasswordUrl";
    protected static final String CUSTOMER_CONTEXT_KEY = "customer";
    protected final Log LOG = LogFactory.getLog(NotificationForgotPasswordEventListener.class);

    @Autowired
    @Qualifier("blCustomerService")
    protected CustomerService customerService;

    @Autowired
    @Qualifier("blNotificationDispatcher")
    protected NotificationDispatcher notificationDispatcher;

    @Override
    protected void handleApplicationEvent(ForgotPasswordEvent event) {
        Customer customer = customerService.readCustomerById(event.getCustomerId());
        Map<String, Object> context = createContext(customer, event);

        try {
            notificationDispatcher.dispatchNotification(new EmailNotification(customer.getEmailAddress(), NotificationEventType.FORGOT_PASSWORD, context));
        } catch (ServiceException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to send a forgot password email for " + customer.getEmailAddress(), e);
            }
        }

        try {
            notificationDispatcher.dispatchNotification(new SMSNotification(NotificationEventType.FORGOT_PASSWORD, context));
        } catch (ServiceException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to send a forgot password sms" , e);
            }
        }
    }

    protected Map<String, Object> createContext(Customer customer, ForgotPasswordEvent event) {
        String resetPasswordUrl = event.getResetPasswordUrl();
        String token = event.getToken();
        HashMap<String, Object> context = new HashMap<>();
        context.put(TOKEN_CONTEXT_KEY, token);
        context.put(RESET_PASSWORD_URL_CONTEXT_KEY, resetPasswordUrl);
        context.put(CUSTOMER_CONTEXT_KEY, customer);
        return MapUtils.unmodifiableMap(context);
    }

    @Override
    public boolean isAsynchronous() {
        return true;
    }
}
