/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.vendor.service.monitor.StatusHandler;
import org.broadleafcommerce.common.vendor.service.type.ServiceStatusType;

public class LogStatusHandler implements StatusHandler {

    private static final Log LOG = LogFactory.getLog(LogStatusHandler.class);

    public void handleStatus(String serviceName, ServiceStatusType status) {
        if (status.equals(ServiceStatusType.DOWN)) {
            LOG.error(serviceName + " is reporting a status of DOWN");
        } else if (status.equals(ServiceStatusType.PAUSED)) {
            LOG.warn(serviceName + " is reporting a status of PAUSED");
        } else {
            LOG.info(serviceName + " is reporting a status of UP");
        }
    }

}
