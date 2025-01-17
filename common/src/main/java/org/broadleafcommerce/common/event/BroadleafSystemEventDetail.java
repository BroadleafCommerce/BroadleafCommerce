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
package org.broadleafcommerce.common.event;

import java.io.Serializable;

/**
 * Effectively a copy of com.broadleafcommerce.jobsevents.domain.dto.SystemEventDetailDTO
 * to be used when creating a org.broadleafcommerce.common.event.BroadleafSystemEvent
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
public class BroadleafSystemEventDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String friendlyName;
    protected String value;
    protected Serializable blob;

    public BroadleafSystemEventDetail(String friendlyName, String value) {
        this.friendlyName = friendlyName;
        this.value = value;
    }

    public BroadleafSystemEventDetail(String friendlyName, Serializable blob) {
        this.friendlyName = friendlyName;
        this.blob = blob;
    }

    public BroadleafSystemEventDetail(String value) {
        this.value = value;
    }

    public BroadleafSystemEventDetail(Serializable blob) {
        this.blob = blob;
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

    public Serializable getBlob() {
        return blob;
    }

    public void setBlob(Serializable blob) {
        this.blob = blob;
    }
}
