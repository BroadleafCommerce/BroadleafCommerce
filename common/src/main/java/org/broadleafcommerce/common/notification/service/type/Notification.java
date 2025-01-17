/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
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
