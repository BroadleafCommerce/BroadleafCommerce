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

import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * <p>
 * A BroadleafApplicationEvent used so that we can communicate with the ScheduledJobsAndEvents module without having a dependency on it.
 * By publishing a Spring Event with this detail, the ScheduledJobsAndEvents module will listen for this event and create a corresponding
 * com.broadleafcommerce.jobsevents.domain.SystemEvent to be consumed.
 *
 * <p>
 * To send an event, inject the {@link ApplicationContext} and publish the event:
 *
 * <pre>
 * {@literal @}Autowired
 * private ApplicationContext appCtx;
 *
 * ...
 *
 * appCtx.publishEvent(new BroadleafSystemEvent("CONSUMER_TYPE", BroadleafEventScopeType.VM, BroadleafEventWorkerType.SITE, true);
 * </pre>
 *
 * @see ApplicationContext#publishEvent(org.springframework.context.ApplicationEvent)
 * @author Jay Aisenbrey (cja769)
 */
public class BroadleafSystemEvent extends BroadleafApplicationEvent {

    private static final long serialVersionUID = 1L;

    protected Map<String, BroadleafSystemEventDetail> detailMap;
    protected String type;
    protected BroadleafEventScopeType scopeType;
    protected BroadleafEventWorkerType workerType;
    protected boolean universal;

    /**
     * @param type should match the com.broadleafcommerce.jobsevents.service.SystemEventConsumer#getType
     * @param detailMap details passed to the event consumer
     * @param scopeType how the event should be consumed
     * @param workerType what type of workers should consume it
     * @param universal used for a performance optimization when sending multiple events at the same time, usually <b>true</b>
     */
    public BroadleafSystemEvent(String type, Map<String, BroadleafSystemEventDetail> detailMap, BroadleafEventScopeType scopeType, BroadleafEventWorkerType workerType, boolean universal) {
        super(type);
        this.detailMap = detailMap;
        this.type = type;
        this.scopeType = scopeType;
        this.workerType = workerType;
        this.universal = universal;
    }

    /**
     * @see BroadleafSystemEvent#BroadleafSystemEvent(String, Map, BroadleafEventScopeType, BroadleafEventWorkerType, boolean)
     */
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

    /**
     * If this event is constructed the exact same way with the exact same data, we can gain
     * a performance increase by combining multiple events into 1.
     * @return
     */
    public boolean isUniversal() {
        return universal;
    }

    public void setUniversal(boolean universal) {
        this.universal = universal;
    }

    /**
     * Constant for how the event should be consumed
     *
     * @author Jay Aisenbrey (cja769)
     */
    public static enum BroadleafEventScopeType {
        /**
         * All nodes will execute
         */
        GLOBAL,

        /**
         * Limit execution to a single, arbitrary node
         */
        VM,

        /**
         * Consume the event locally only
         */
        LOCAL,

        /**
         * Consume the event locally only in the same thread
         */
        NON_ASYNC_LOCAL,

        /**
         * Consume the event locally only in the same thread and do not allow auto resume
         */
        NON_ASYNC_LOCAL_NO_RESUME,

        /**
         * Durable events are always global, but are not removed from the system, allowing them to be consumed across server restarts,
         * or when new nodes enter the cluster (VM level events already exhibit behavior that allow them to execute even after
         * system restarts and do not require additional "durable" classification). Supports 'playback' of a group of
         * events that need to 'catch up' to events that it might have missed
         */
        DURABLE_GLOBAL
    }

    /**
     * Which type of worker is qualified to handle the event
     *
     * @author Jay Aisenbrey (cja769)
     */
    public static enum BroadleafEventWorkerType {
        SITE, ADMIN, ANY
    }
}
