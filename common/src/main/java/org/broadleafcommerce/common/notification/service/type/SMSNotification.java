package org.broadleafcommerce.common.notification.service.type;

import java.util.Map;

/**
 * @author Nick Crum ncrum
 */
public class SMSNotification extends Notification {

    protected String phoneNumber;

    public SMSNotification() {
        super();
    }

    public SMSNotification(NotificationEventType notificationEventType, Map<String, Object> context) {
        super(notificationEventType, context);
    }

    public SMSNotification(String phoneNumber, NotificationEventType notificationEventType, Map<String, Object> context) {
        super(notificationEventType, context);
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
