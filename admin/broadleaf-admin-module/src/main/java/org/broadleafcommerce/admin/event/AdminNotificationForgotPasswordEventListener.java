package org.broadleafcommerce.admin.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.event.AbstractBroadleafApplicationEventListener;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.notification.service.type.Notification;
import org.broadleafcommerce.common.notification.service.NotificationService;
import org.broadleafcommerce.openadmin.server.security.dao.AdminUserDao;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.event.AdminForgotPasswordEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.broadleafcommerce.common.notification.service.type.NotificationType;
import java.util.HashMap;

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
    @Qualifier("blEmailNotificationService")
    protected NotificationService notificationService;

    @Override
    protected void handleApplicationEvent(AdminForgotPasswordEvent event) {
        AdminUser adminUser = adminUserDao.readAdminUserById(event.getAdminUserId());
        String resetPasswordUrl = event.getResetPasswordUrl();
        String token = event.getToken();

        try {
            HashMap<String, Object> context = new HashMap<>();
            context.put(TOKEN_CONTEXT_KEY, token);
            context.put(RESET_PASSWORD_URL_CONTEXT_KEY, resetPasswordUrl);
            context.put(ADMIN_USER_CONTEXT_KEY, adminUser);
            notificationService.sendNotification(new Notification(adminUser.getEmail(), NotificationType.ADMIN_FORGOT_PASSWORD, context));
        } catch (ServiceException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to send an admin forgot password email for " + adminUser.getEmail(), e);
            }
        }
    }

    @Override
    public boolean isAsynchronous() {
        return true;
    }
}
