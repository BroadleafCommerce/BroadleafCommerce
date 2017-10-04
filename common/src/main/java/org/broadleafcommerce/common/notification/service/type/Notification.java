package org.broadleafcommerce.common.notification.service.type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Nick Crum ncrum
 */
public class Notification implements Serializable {

    protected String target;
    protected String notificationType;
    protected Map<String, Object> context = new HashMap<>();

    public Notification() {
    }

    public Notification(String target, NotificationType notificationType, Map<String, Object> context) {
        this.target = target;
        this.context = context;
        setType(notificationType);
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public NotificationType getType() {
        return NotificationType.getInstance(notificationType);
    }

    public void setType(NotificationType notificationType) {
        this.notificationType = notificationType.getType();
    }
}
