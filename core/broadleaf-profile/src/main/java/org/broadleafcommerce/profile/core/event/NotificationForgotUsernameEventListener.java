package org.broadleafcommerce.profile.core.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.event.AbstractBroadleafApplicationEventListener;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.notification.service.type.Notification;
import org.broadleafcommerce.common.notification.service.type.NotificationType;
import org.broadleafcommerce.common.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.util.HashMap;

/**
 * @author Nick Crum ncrum
 */
@Component("blNotificationForgotUsernameEventListener")
public class NotificationForgotUsernameEventListener extends AbstractBroadleafApplicationEventListener<ForgotUsernameEvent> {

    public static final String ACTIVE_USERNAMES_CONTEXT_KEY = "activeUsernames";
    protected final Log LOG = LogFactory.getLog(NotificationForgotUsernameEventListener.class);

    @Autowired
    @Qualifier("blEmailNotificationService")
    protected NotificationService notificationService;

    @Override
    protected void handleApplicationEvent(ForgotUsernameEvent event) {
        try {
            HashMap<String, Object> context = new HashMap<>();
            context.put(ACTIVE_USERNAMES_CONTEXT_KEY, event.getActiveUsernames());
            notificationService.sendNotification(new Notification(event.getEmailAddress(), NotificationType.FORGOT_USERNAME, context));
        } catch (ServiceException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to send a forgot username email for " + event.getEmailAddress(), e);
            }
        }
    }

    @Override
    public boolean isAsynchronous() {
        return true;
    }
}
