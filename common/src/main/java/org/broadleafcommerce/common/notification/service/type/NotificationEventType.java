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

import org.broadleafcommerce.common.BroadleafEnumerationType;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Nick Crum ncrum
 */
@Component
public class NotificationEventType implements Serializable, BroadleafEnumerationType {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final Map<String, NotificationEventType> TYPES = new LinkedHashMap<>();

    public static final NotificationEventType ADMIN_FORGOT_PASSWORD = new NotificationEventType("ADMIN_FORGOT_PASSWORD", "Admin Forgot Password");
    public static final NotificationEventType ADMIN_FORGOT_USERNAME = new NotificationEventType("ADMIN_FORGOT_USERNAME", "Admin Forgot Username");
    public static final NotificationEventType ORDER_CONFIRMATION = new NotificationEventType("ORDER_CONFIRMATION", "Order Confirmation");
    public static final NotificationEventType FORGOT_PASSWORD = new NotificationEventType("FORGOT_PASSWORD", "Forgot Password");
    public static final NotificationEventType FORGOT_USERNAME = new NotificationEventType("FORGOT_USERNAME", "Forgot Username");
    public static final NotificationEventType REGISTER_CUSTOMER = new NotificationEventType("REGISTER_CUSTOMER", "Register Customer");
    public static final NotificationEventType CONTACT_US = new NotificationEventType("CONTACT_US", "Contact Us");
    public static final NotificationEventType NOTIFY_ABANDONED_CART = new NotificationEventType("NOTIFY_ABANDONED_CART", "Notify Abandoned Cart");

    private String type;
    private String friendlyType;

    public NotificationEventType() {
        //do nothing
    }

    public NotificationEventType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }

    public static NotificationEventType getInstance(final String type) {
        return TYPES.get(type);
    }

    @Override
    public String getType() {
        return type;
    }

    protected void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
        }
    }

    @Override
    public String getFriendlyType() {
        return friendlyType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        NotificationEventType other = (NotificationEventType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
