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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nick Crum ncrum
 */
@Component("blNotificationForgotUsernameEventListener")
public class NotificationForgotUsernameEventListener extends AbstractBroadleafApplicationEventListener<ForgotUsernameEvent> {

    protected static final String ACTIVE_USERNAMES_CONTEXT_KEY = "activeUsernames";
    protected final Log LOG = LogFactory.getLog(NotificationForgotUsernameEventListener.class);

    @Autowired
    @Qualifier("blNotificationDispatcher")
    protected NotificationDispatcher notificationDispatcher;

    @Override
    protected void handleApplicationEvent(ForgotUsernameEvent event) {
        Map<String, Object> context = createContext(event);

        try {
            notificationDispatcher.dispatchNotification(new EmailNotification(event.getEmailAddress(), NotificationEventType.FORGOT_USERNAME, context));
        } catch (ServiceException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to send a forgot username email for " + event.getEmailAddress(), e);
            }
        }

        try {
            notificationDispatcher.dispatchNotification(new SMSNotification(event.getPhoneNumber(), NotificationEventType.FORGOT_USERNAME, context));
        } catch (ServiceException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to send a forgot username sms for " + event.getPhoneNumber(), e);
            }
        }
    }

    protected Map<String, Object> createContext(ForgotUsernameEvent event) {
        Map<String, Object> context = new HashMap<>();
        context.put(ACTIVE_USERNAMES_CONTEXT_KEY, event.getActiveUsernames());
        return MapUtils.unmodifiableMap(context);
    }

    @Override
    public boolean isAsynchronous() {
        return true;
    }
}
