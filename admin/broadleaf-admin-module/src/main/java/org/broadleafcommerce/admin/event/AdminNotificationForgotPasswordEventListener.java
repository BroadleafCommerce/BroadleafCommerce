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
import org.broadleafcommerce.openadmin.server.security.dao.AdminUserDao;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.event.AdminForgotPasswordEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nick Crum ncrum
 */
@Component("blAdminNotificationForgotPasswordEventListener")
public class AdminNotificationForgotPasswordEventListener extends AbstractBroadleafApplicationEventListener<AdminForgotPasswordEvent> {

    protected static final String TOKEN_CONTEXT_KEY = "token";
    protected static final String RESET_PASSWORD_URL_CONTEXT_KEY = "resetPasswordUrl";
    protected static final String ADMIN_USER_CONTEXT_KEY = "adminUser";
    protected final Log LOG = LogFactory.getLog(AdminNotificationForgotPasswordEventListener.class);

    @Autowired
    @Qualifier("blAdminUserDao")
    protected AdminUserDao adminUserDao;

    @Autowired
    @Qualifier("blNotificationDispatcher")
    protected NotificationDispatcher notificationDispatcher;

    @Override
    protected void handleApplicationEvent(AdminForgotPasswordEvent event) {
        AdminUser adminUser = adminUserDao.readAdminUserById(event.getAdminUserId());

        if (adminUser != null) {
            Map<String, Object> context = createContext(event, adminUser);

            try {
                notificationDispatcher.dispatchNotification(new EmailNotification(adminUser.getEmail(), NotificationEventType.ADMIN_FORGOT_PASSWORD, context));
            } catch (ServiceException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Unable to send an admin forgot password email for " + adminUser.getEmail(), e);
                }
            }

            try {
                notificationDispatcher.dispatchNotification(new SMSNotification(adminUser.getPhoneNumber(), NotificationEventType.ADMIN_FORGOT_PASSWORD, context));
            } catch (ServiceException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Unable to send an admin forgot password email for " + adminUser.getEmail(), e);
                }
            }
        }
    }

    protected Map<String, Object> createContext(AdminForgotPasswordEvent event, AdminUser adminUser) {
        HashMap<String, Object> context = new HashMap<>();
        String resetPasswordUrl = event.getResetPasswordUrl();
        String token = event.getToken();
        context.put(TOKEN_CONTEXT_KEY, token);
        context.put(RESET_PASSWORD_URL_CONTEXT_KEY, resetPasswordUrl);
        context.put(ADMIN_USER_CONTEXT_KEY, adminUser);
        return MapUtils.unmodifiableMap(context);
    }

    @Override
    public boolean isAsynchronous() {
        return true;
    }
}
