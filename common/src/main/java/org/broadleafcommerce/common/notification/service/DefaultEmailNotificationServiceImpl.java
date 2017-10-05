package org.broadleafcommerce.common.notification.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.notification.service.type.EmailNotification;
import org.broadleafcommerce.common.notification.service.type.Notification;
import org.springframework.stereotype.Service;

/**
 * @author Nick Crum ncrum
 */
@Service("blEmailNotificationService")
public class DefaultEmailNotificationServiceImpl implements NotificationService {

    protected final Log LOG = LogFactory.getLog(DefaultEmailNotificationServiceImpl.class);

    @Override
    public boolean canHandle(Class<? extends Notification> clazz) {
        return EmailNotification.class.isAssignableFrom(clazz);
    }

    @Override
    public void sendNotification(Notification notification) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Attempt to send email notification of type " + notification.getType().getFriendlyType());
        }
    }
}
