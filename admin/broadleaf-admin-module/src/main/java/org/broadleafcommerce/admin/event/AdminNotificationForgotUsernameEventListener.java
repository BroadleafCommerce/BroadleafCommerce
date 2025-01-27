/*-
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.event;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.event.AbstractBroadleafApplicationEventListener;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.notification.service.NotificationDispatcher;
import org.broadleafcommerce.common.notification.service.type.EmailNotification;
import org.broadleafcommerce.common.notification.service.type.NotificationEventType;
import org.broadleafcommerce.common.notification.service.type.SMSNotification;
import org.broadleafcommerce.openadmin.server.security.event.AdminForgotUsernameEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nick Crum ncrum
 */
@Component("blAdminNotificationForgotUsernameEventListener")
public class AdminNotificationForgotUsernameEventListener extends AbstractBroadleafApplicationEventListener<AdminForgotUsernameEvent> {

    public static final String ACTIVE_USERNAMES_CONTEXT_KEY = "activeUsernames";
    protected final Log LOG = LogFactory.getLog(AdminNotificationForgotUsernameEventListener.class);

    @Autowired
    @Qualifier("blNotificationDispatcher")
    protected NotificationDispatcher notificationDispatcher;

    @Override
    protected void handleApplicationEvent(AdminForgotUsernameEvent event) {
        Map<String, Object> context = createContext(event);

        try {
            notificationDispatcher.dispatchNotification(new EmailNotification(
                    event.getEmailAddress(), NotificationEventType.ADMIN_FORGOT_USERNAME, context
            ));
        } catch (ServiceException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to send an admin forgot username email for " + event.getEmailAddress(), e);
            }
        }

        try {
            notificationDispatcher.dispatchNotification(new SMSNotification(
                    event.getPhoneNumber(), NotificationEventType.ADMIN_FORGOT_USERNAME, context
            ));
        } catch (ServiceException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to send an admin forgot username email for " + event.getEmailAddress(), e);
            }
        }
    }

    protected Map<String, Object> createContext(AdminForgotUsernameEvent event) {
        HashMap<String, Object> context = new HashMap<>();
        context.put(ACTIVE_USERNAMES_CONTEXT_KEY, event.getActiveUsernames());
        return MapUtils.unmodifiableMap(context);
    }

    @Override
    public boolean isAsynchronous() {
        return true;
    }

}
