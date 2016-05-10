/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.common.vendor.service.monitor.handler;

import org.broadleafcommerce.common.vendor.service.monitor.StatusHandler;
import org.broadleafcommerce.common.vendor.service.type.ServiceStatusType;

import java.util.ArrayList;
import java.util.List;

public class CompositeStatusHandler implements StatusHandler {

    protected List<StatusHandler> handlers = new ArrayList<StatusHandler>();

    public void handleStatus(String serviceName, ServiceStatusType status) {
        for (StatusHandler statusHandler : handlers) {
            statusHandler.handleStatus(serviceName, status);
        }
    }

    public List<StatusHandler> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<StatusHandler> handlers) {
        this.handlers = handlers;
    }

}
