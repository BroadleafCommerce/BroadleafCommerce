package org.broadleafcommerce.common.notification.service;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.notification.service.type.Notification;

/**
 * @author Nick Crum ncrum
 */
public interface NotificationService {

    boolean sendNotification(Notification notification) throws ServiceException;
}
