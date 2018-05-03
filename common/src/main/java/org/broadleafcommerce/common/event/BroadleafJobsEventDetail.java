/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2018 Broadleaf Commerce
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
package org.broadleafcommerce.common.event;

import java.io.Serializable;

/**
 * Effectively a copy of com.broadleafcommerce.jobsevents.domain.dto.SystemEventDetailDTO
 * to be used when creating a org.broadleafcommerce.common.event.BroadleafJobsEvent
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
public class BroadleafJobsEventDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String name;
    protected String friendlyName;
    protected String value;
    protected String longValue;
    protected Serializable blob;

    public BroadleafJobsEventDetail(String name, String friendlyName, String value) {
        this(name, friendlyName);
        this.value = value;
    }

    public BroadleafJobsEventDetail(String name, String friendlyName, Long longValue) {
        this(name, friendlyName);
        this.longValue = longValue.toString();
    }

    public BroadleafJobsEventDetail(String name, String friendlyName, Serializable blob) {
        this(name, friendlyName);
        this.blob = blob;
    }

    protected BroadleafJobsEventDetail(String name, String friendlyName) {
        this.name = name;
        this.friendlyName = friendlyName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLongValue() {
        return longValue;
    }

    public void setLongValue(String longValue) {
        this.longValue = longValue;
    }

    public Serializable getBlob() {
        return blob;
    }

    public void setBlob(Serializable blob) {
        this.blob = blob;
    }
}
