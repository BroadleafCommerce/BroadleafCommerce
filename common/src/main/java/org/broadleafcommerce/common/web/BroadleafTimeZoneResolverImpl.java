/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
