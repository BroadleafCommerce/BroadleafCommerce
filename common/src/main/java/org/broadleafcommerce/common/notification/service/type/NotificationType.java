/*-
 * #%L
 * BroadleafCommerce Advanced CMS
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt).
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */
package org.broadleafcommerce.common.notification.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Nick Crum ncrum
 */
public class NotificationType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, NotificationType> TYPES = new LinkedHashMap<String, NotificationType>();

    public static final NotificationType ADMIN_FORGOT_PASSWORD = new NotificationType("ADMIN_FORGOT_PASSWORD", "Admin Forgot Password");
    public static final NotificationType ADMIN_FORGOT_USERNAME = new NotificationType("ADMIN_FORGOT_USERNAME", "Admin Forgot Username");
    public static final NotificationType ORDER_CONFIRMATION = new NotificationType("ORDER_CONFIRMATION", "Order Confirmation");
    public static final NotificationType FORGOT_PASSWORD = new NotificationType("FORGOT_PASSWORD", "Forgot Password");
    public static final NotificationType FORGOT_USERNAME = new NotificationType("FORGOT_USERNAME", "Forgot Username");
    public static final NotificationType REGISTER_CUSTOMER = new NotificationType("REGISTER_CUSTOMER", "Register Customer");

    public static NotificationType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public NotificationType() {
        //do nothing
    }

    public NotificationType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getFriendlyType() {
        return friendlyType;
    }

    private void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
        }
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
        NotificationType other = (NotificationType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
