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
package org.broadleafcommerce.common.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.TimeZone;

/**
 * Responsible for returning the timezone to use for the current request.
 *
 * @author Priyesh Patel
 */
@Component("blTimeZoneResolver")
public class BroadleafTimeZoneResolverImpl implements BroadleafTimeZoneResolver {
    private final Log LOG = LogFactory.getLog(BroadleafTimeZoneResolverImpl.class);
    
    /**
     * Parameter/Attribute name for the current language
     */
    public static String TIMEZONE_VAR = "blTimeZone";

    /**
     * Parameter/Attribute name for the current language
     */
    public static String TIMEZONE_CODE_PARAM = "blTimeZoneCode";

    @Override
    public TimeZone resolveTimeZone(WebRequest request) {
        TimeZone timeZone = null;

        // First check for request attribute
        timeZone = (TimeZone) request.getAttribute(TIMEZONE_VAR, WebRequest.SCOPE_REQUEST);

        // Second, check for a request parameter
        if (timeZone == null && BLCRequestUtils.getURLorHeaderParameter(request, TIMEZONE_CODE_PARAM) != null) {
            String timeZoneCode = BLCRequestUtils.getURLorHeaderParameter(request, TIMEZONE_CODE_PARAM);
            timeZone = TimeZone.getTimeZone(timeZoneCode);

            if (LOG.isTraceEnabled()) {
                LOG.trace("Attempt to find TimeZone by param " + timeZoneCode + " resulted in " + timeZone);
            }
        }

        // Third, check the session 
        if (timeZone == null && BLCRequestUtils.isOKtoUseSession(request)) {
            //@TODO verify if we should take this from global session
            timeZone = (TimeZone) request.getAttribute(TIMEZONE_VAR, WebRequest.SCOPE_GLOBAL_SESSION);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Attempt to find timezone from session resulted in " + timeZone);
            }
        }

        // Finally, use the default
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();

            if (LOG.isTraceEnabled()) {
                LOG.trace("timezone set to default timezone " + timeZone);
            }
        }

        if (BLCRequestUtils.isOKtoUseSession(request)) {
            request.setAttribute(TIMEZONE_VAR, timeZone, WebRequest.SCOPE_GLOBAL_SESSION);
        }
        return timeZone;
    }
}
