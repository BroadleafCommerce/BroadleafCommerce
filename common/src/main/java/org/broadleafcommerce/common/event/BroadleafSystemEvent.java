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
public class BroadleafSystemEvent extends BroadleafApplicationEvent {

    private static final long serialVersionUID = 1L;

    protected Map<String, BroadleafSystemEventDetail> detailMap;
    protected String type;
    protected BroadleafEventScopeType scopeType;
    protected BroadleafEventWorkerType workerType;
    protected boolean universal;

    public BroadleafSystemEvent(String type, Map<String, BroadleafSystemEventDetail> detailMap, BroadleafEventScopeType scopeType, BroadleafEventWorkerType workerType, boolean universal) {
        super(type);
        this.detailMap = detailMap;
        this.type = type;
        this.scopeType = scopeType;
        this.workerType = workerType;
        this.universal = universal;
    }

    public BroadleafSystemEvent(String type, BroadleafEventScopeType scopeType, BroadleafEventWorkerType workerType, boolean universal) {
        super(type);
        this.type = type;
        this.scopeType = scopeType;
        this.workerType = workerType;
        this.universal = universal;
    }

    public Map<String, BroadleafSystemEventDetail> getDetailMap() {
        return detailMap;
    }

    public void setDetailMap(Map<String, BroadleafSystemEventDetail> detailMap) {
        this.detailMap = detailMap;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BroadleafEventScopeType getScopeType() {
        return scopeType;
    }

    public void setScopeType(BroadleafEventScopeType scopeType) {
        this.scopeType = scopeType;
    }

    public BroadleafEventWorkerType getWorkerType() {
        return workerType;
    }

    public void setWorkerType(BroadleafEventWorkerType workerType) {
        this.workerType = workerType;
    }

    public boolean isUniversal() {
        return universal;
    }

    public void setUniversal(boolean universal) {
        this.universal = universal;
    }

    public static enum BroadleafEventScopeType {
        //All nodes will execute
        GLOBAL,

        //Limit execution to a single, arbitrary node
        VM,

        //Consume the event locally only
        LOCAL,

        //Consume the event locally only in the same thread
        NON_ASYNC_LOCAL,

        //Consume the event locally only in the same thread and do not allow auto resume
        NON_ASYNC_LOCAL_NO_RESUME,

        //Durable events are always global, but are not removed from the system, allowing them to be consumed across server restarts,
        //or when new nodes enter the cluster (VM level events already exhibit behavior that allow them to execute even after
        //system restarts and do not require additional "durable" classification)
        DURABLE_GLOBAL
    }

    public static enum BroadleafEventWorkerType {
        SITE, ADMIN, ANY
    }
}
