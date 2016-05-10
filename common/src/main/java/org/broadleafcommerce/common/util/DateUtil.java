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
package org.broadleafcommerce.common.util;

import org.broadleafcommerce.common.time.SystemTime;

import java.util.Date;

public class DateUtil {

    public static final long ONE_HOUR_MILLIS = 60 * 60 * 1000;
    public static final long ONE_DAY_MILLIS = ONE_HOUR_MILLIS * 24;
    public static final long ONE_WEEK_MILLIS = ONE_DAY_MILLIS * 7;

    public static final long ONE_HOUR_SECONDS = 60 * 60;
    public static final long ONE_DAY_SECONDS = ONE_HOUR_SECONDS * 24;
    public static final long ONE_WEEK_SECONDS = ONE_DAY_SECONDS * 7;
    public static final long SIX_MONTHS_SECONDS = ONE_DAY_SECONDS * 6 * 30;

    public static boolean isActive(Date startDate, Date endDate, boolean includeTime) {
        Long date = SystemTime.asMillis(includeTime);
        return !(startDate == null || startDate.getTime() >= date || (endDate != null && endDate.getTime() < date));
    }

    public static Date getCurrentDateAfterFactoringInDateResolution(Date cachedDate, Long currentDateResolution) {
        Date returnDate = SystemTime.getCurrentDateWithinTimeResolution(cachedDate, currentDateResolution);
        if (returnDate != cachedDate) {
            if (SystemTime.shouldCacheDate()) {
                cachedDate.setTime(returnDate.getTime());
            }
        }
        return returnDate;
    }

}
