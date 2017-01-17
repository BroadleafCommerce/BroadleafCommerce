/*
 * #%L
 * BroadleafCommerce Export Module
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
package com.broadleafcommerce.export.event.factory;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.util.service.type.ContextVariableNames;

import com.broadleafcommerce.jobsevents.domain.SystemEvent;
import com.broadleafcommerce.jobsevents.domain.SystemEventDetail;
import com.broadleafcommerce.jobsevents.domain.SystemEventDetailImpl;
import com.broadleafcommerce.jobsevents.domain.SystemEventImpl;
import com.broadleafcommerce.jobsevents.service.type.EventScopeType;
import com.broadleafcommerce.jobsevents.service.type.EventWorkerType;

/**
 * Base class for creating an export event factory for a domain
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
public abstract class AbstractExportEventFactory {

    public static final String ENCODING = "ENCODING";
    public static final String FORMAT = "FORMAT";
    public static final String SHAREABLE = "SHAREABLE";
    public static final String ADMIN_USER = "ADMIN_USER";
    
    protected SystemEvent createSystemEvent(String eventType) {
        SystemEvent systemEvent = new SystemEventImpl();
        systemEvent.setEnabled(true);
        systemEvent.setScopeType(EventScopeType.VM);
        systemEvent.setType(eventType);
        systemEvent.setWorkerType(EventWorkerType.ADMIN);
        systemEvent.setUniversal(true);
        return systemEvent;
    }
    
    protected void createEventContext(SystemEvent systemEvent, String formatType, String encodingType, boolean shareable) {
        SystemEventDetail encodingDetail = new SystemEventDetailImpl();
        encodingDetail.setEvent(systemEvent);
        encodingDetail.setName(AbstractExportEventFactory.ENCODING);
        encodingDetail.setFriendlyName("Encoding");
        encodingDetail.setValue(encodingType);
        systemEvent.getEventDetails().put(encodingDetail.getName(), encodingDetail);
        
        SystemEventDetail formatDetail = new SystemEventDetailImpl();
        formatDetail.setEvent(systemEvent);
        formatDetail.setName(AbstractExportEventFactory.FORMAT);
        formatDetail.setFriendlyName("Format");
        formatDetail.setValue(formatType);
        systemEvent.getEventDetails().put(formatDetail.getName(), formatDetail);
        
        SystemEventDetail shareableDetail = new SystemEventDetailImpl();
        shareableDetail.setEvent(systemEvent);
        shareableDetail.setName(AbstractExportEventFactory.SHAREABLE);
        shareableDetail.setFriendlyName("Shareable");
        shareableDetail.setValue(String.valueOf(shareable));
        systemEvent.getEventDetails().put(shareableDetail.getName(), shareableDetail);
        
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        if (context.getAdminUserId() != null) {
            SystemEventDetail adminUserDetail = new SystemEventDetailImpl();
            adminUserDetail.setEvent(systemEvent);
            adminUserDetail.setName(AbstractExportEventFactory.ADMIN_USER);
            adminUserDetail.setFriendlyName("Admin User Id");
            adminUserDetail.setValue(String.valueOf(context.getAdminUserId()));
            systemEvent.getEventDetails().put(adminUserDetail.getName(), adminUserDetail);
        }
        
        if (context.getNonPersistentSite() != null && context.getNonPersistentSite().getId() != null) {
            SystemEventDetail siteDetail = new SystemEventDetailImpl();
            siteDetail.setEvent(systemEvent);
            siteDetail.setName(ContextVariableNames.SITE);
            siteDetail.setFriendlyName("Site");
            siteDetail.setValue(String.valueOf(context.getNonPersistentSite().getId()));
            systemEvent.getEventDetails().put(siteDetail.getName(), siteDetail);
        }
    }
}
