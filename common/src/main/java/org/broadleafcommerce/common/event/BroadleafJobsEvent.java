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

import java.util.Map;

/**
 * A BroadleafApplicationEvent used so that we can communicate with the SystemJobsAndEvents module without having a dependency on it.
 * By publishing a Spring Event with this detail, the SystemJobsAndEvents module will listen for this event and create a corresponding
 * SystemEvent to be consumed. 
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
public class BroadleafJobsEvent extends BroadleafApplicationEvent {

    private static final long serialVersionUID = 1L;

    protected Map<String, BroadleafJobsEventDetail> detailMap;
    protected String type;

    /**
     * @see com.broadleafcommerce.jobsevents.service.type.EventScopeType
     * Use the "type" field and not the "friendlyType" field
     */
    protected String scopeType;

    /**
     * @see com.broadleafcommerce.jobsevents.service.type.EventScopeType
     * Use the "type" field and not the "friendlyType" field
     */
    protected String workerType;
    protected boolean universal;

    public BroadleafJobsEvent(String type, Map<String, BroadleafJobsEventDetail> detailMap, String scopeType, String workerType, boolean universal) {
        super(type);
        this.detailMap = detailMap;
        this.type = type;
        this.scopeType = scopeType;
        this.workerType = workerType;
        this.universal = universal;
    }

    public Map<String, BroadleafJobsEventDetail> getDetailMap() {
        return detailMap;
    }

    public void setDetailMap(Map<String, BroadleafJobsEventDetail> detailMap) {
        this.detailMap = detailMap;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScopeType() {
        return scopeType;
    }

    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }

    public String getWorkerType() {
        return workerType;
    }

    public void setWorkerType(String workerType) {
        this.workerType = workerType;
    }

    public boolean isUniversal() {
        return universal;
    }

    public void setUniversal(boolean universal) {
        this.universal = universal;
    }

}
