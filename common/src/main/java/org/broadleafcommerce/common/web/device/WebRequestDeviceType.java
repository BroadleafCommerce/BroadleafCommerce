/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.common.web.device;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Nathan Moore (nathanmoore).
 */
public class WebRequestDeviceType implements BroadleafEnumerationType, Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final Map<String, WebRequestDeviceType> TYPES = new LinkedHashMap<>();
    
    public static final WebRequestDeviceType UNKNOWN = new WebRequestDeviceType("UNKNOWN", "Unknown");
    public static final WebRequestDeviceType NORMAL =  new WebRequestDeviceType("NORMAL", "Normal");
    public static final WebRequestDeviceType MOBILE =  new WebRequestDeviceType("MOBILE", "Mobile");
    public static final WebRequestDeviceType TABLET =  new WebRequestDeviceType("TABLET", "Tablet");
    
    private String type;
    private String friendlyType;
    
    WebRequestDeviceType(){}
    
    WebRequestDeviceType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }
    
    private void setType(final String type) {
        this.type = type;
        
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
        }
    }

    public static WebRequestDeviceType getInstance(final String type) {
        return TYPES.get(type);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getFriendlyType() {
        return friendlyType;
    }

    @Override
    public String toString() {
        return getFriendlyType();
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && getClass().isAssignableFrom(o.getClass())) {
            final WebRequestDeviceType other = (WebRequestDeviceType) o;
            
            return new EqualsBuilder()
                    .append(getType(), other.getType())
                    .append(getFriendlyType(), other.getFriendlyType())
                    .build();
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getFriendlyType())
                .append(getType())
                .build();
    }
}
