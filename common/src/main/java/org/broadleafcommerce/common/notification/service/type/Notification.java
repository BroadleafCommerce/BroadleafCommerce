package org.broadleafcommerce.common.notification.service.type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nick Crum ncrum
 */
public abstract class Notification implements Serializable {

    protected String notificationType;
    protected Map<String, Object> context = new HashMap<>();

    public Notification() {
    }

    public Notification(NotificationEventType notificationEventType, Map<String, Object> context) {
        this.context = context;
        setType(notificationEventType);
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public NotificationEventType getType() {
        if (notificationType == null) {
            return null;
        }
        return NotificationEventType.getInstance(notificationType);
    }

    public void setType(NotificationEventType notificationEventType) {
        this.notificationType = notificationEventType.getType();
    }
}
