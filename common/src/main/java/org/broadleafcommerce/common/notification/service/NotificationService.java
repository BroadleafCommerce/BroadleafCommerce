package org.broadleafcommerce.common.notification.service;

import org.broadleafcommerce.common.notification.service.type.Notification;

/**
 * @author Nick Crum ncrum
 */
public interface NotificationService {

    boolean canHandle(Class<? extends Notification> clazz);

    void sendNotification(Notification notification);

}
