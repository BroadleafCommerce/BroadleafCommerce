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
package org.broadleafcommerce.common.vendor.service.monitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.broadleafcommerce.common.vendor.service.monitor.handler.LogStatusHandler;
import org.broadleafcommerce.common.vendor.service.type.ServiceStatusType;

import java.util.HashMap;
import java.util.Map;

public class ServiceMonitor {

    private static final Log LOG = LogFactory.getLog(ServiceMonitor.class);

    protected Map<ServiceStatusDetectable, StatusHandler> serviceHandlers = new HashMap<ServiceStatusDetectable, StatusHandler>();
    protected StatusHandler defaultHandler = new LogStatusHandler();
    protected Map<ServiceStatusDetectable, ServiceStatusType> statusMap = new HashMap<ServiceStatusDetectable, ServiceStatusType>();

    public synchronized void init() {
        for (ServiceStatusDetectable statusDetectable : serviceHandlers.keySet()) {
            checkService(statusDetectable);
        }
    }

    public Object checkServiceAOP(ProceedingJoinPoint call) throws Throwable {
        try {
            checkService((ServiceStatusDetectable) call.getThis());
        } catch (Throwable e) {
            LOG.error("Could not check service status", e);
        }
        return call.proceed();
    }

    public void checkService(ServiceStatusDetectable statusDetectable) {
        ServiceStatusType type = statusDetectable.getServiceStatus();
        if (!statusMap.containsKey(statusDetectable)) {
            statusMap.put(statusDetectable, type);
            if (type.equals(ServiceStatusType.DOWN)) {
                handleStatusChange(statusDetectable, type);
            }
        }
        if (!statusMap.get(statusDetectable).equals(type)) {
            handleStatusChange(statusDetectable, type);
            statusMap.put(statusDetectable, type);
        }
    }

    protected void handleStatusChange(ServiceStatusDetectable serviceStatus, ServiceStatusType serviceStatusType) {
        if (serviceHandlers.containsKey(serviceStatus)) {
            serviceHandlers.get(serviceStatus).handleStatus(serviceStatus.getServiceName(), serviceStatusType);
        } else {
            defaultHandler.handleStatus(serviceStatus.getServiceName(), serviceStatusType);
        }
    }

    public Map<ServiceStatusDetectable, StatusHandler> getServiceHandlers() {
        return serviceHandlers;
    }

    public void setServiceHandlers(Map<ServiceStatusDetectable, StatusHandler> serviceHandlers) {
        this.serviceHandlers = serviceHandlers;
    }

    public StatusHandler getDefaultHandler() {
        return defaultHandler;
    }

    public void setDefaultHandler(StatusHandler defaultHandler) {
        this.defaultHandler = defaultHandler;
    }
}
