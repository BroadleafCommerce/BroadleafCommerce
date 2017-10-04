package org.broadleafcommerce.common.notification.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.notification.service.type.Notification;
import org.springframework.stereotype.Service;

/**
 * @author Nick Crum ncrum
 */
@Service("blSMSNotificationService")
public class DefaultSMSNotificationServiceImpl implements NotificationService {

    protected final Log LOG = LogFactory.getLog(DefaultSMSNotificationServiceImpl.class);

    @Override
    public boolean sendNotification(Notification notification) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Attempt to send sms notification of type " + notification.getType().getFriendlyType());
        }
        return true;
    }
}
