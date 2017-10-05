package org.broadleafcommerce.common.notification.service.type;

import java.util.Map;

/**
 * @author Nick Crum ncrum
 */
public class EmailNotification extends Notification {

    protected String emailAddress;

    public EmailNotification() {
        super();
    }

    public EmailNotification(NotificationEventType notificationEventType, Map<String, Object> context) {
        super(notificationEventType, context);
    }

    public EmailNotification(String emailAddress, NotificationEventType notificationEventType, Map<String, Object> context) {
        super(notificationEventType, context);
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
