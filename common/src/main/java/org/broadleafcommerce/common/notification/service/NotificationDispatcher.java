package org.broadleafcommerce.common.notification.service;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.notification.service.type.Notification;

/**
 * @author Nick Crum ncrum
 */
public interface NotificationDispatcher {

    /**
     * This method is responsible for dispatching the given notification to any relevant services.
     *
     * @param notification the Notification
     */
    void dispatchNotification(Notification notification)  throws ServiceException;
}
