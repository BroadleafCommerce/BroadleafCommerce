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
package org.broadleafcommerce.common.time;

import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SystemTime {

    private static final TimeSource defaultTimeSource = new DefaultTimeSource();
    private static TimeSource globalTimeSource = null;
    private static final ThreadLocal<TimeSource> localTimeSource = ThreadLocalManager.createThreadLocal(TimeSource.class, false);

    public static TimeSource getTimeSource() {
        TimeSource applicableTimeSource;
        TimeSource localTS = localTimeSource.get();
        if (localTS != null) {
            applicableTimeSource = localTS;
        } else if (globalTimeSource != null) {
            applicableTimeSource = globalTimeSource;
        } else {
            applicableTimeSource = defaultTimeSource;
        }
        return applicableTimeSource;
    }

    public static void setGlobalTimeSource(final TimeSource globalTS) {
        SystemTime.globalTimeSource = globalTS;
    }

    public static void resetGlobalTimeSource() {
        setGlobalTimeSource(null);
    }

    public static void setLocalTimeSource(final TimeSource localTS) {
        SystemTime.localTimeSource.set(localTS);
    }

    public static void resetLocalTimeSource() {
        SystemTime.localTimeSource.remove();
    }

    public static void reset() {
        resetGlobalTimeSource();
        resetLocalTimeSource();
    }

    public static long asMillis() {
        return asMillis(true);
    }

    public static long asMillis(boolean includeTime) {
        if (includeTime) {
            return getTimeSource().timeInMillis();
        }
        return asCalendar(includeTime).getTimeInMillis();
    }

    public static Date asDate() {
        return asDate(true);
    }

    public static Date asDate(boolean includeTime) {
        if (includeTime) {
            return new Date(asMillis());
        }
        return asCalendar(includeTime).getTime();
    }

    public static Calendar asCalendar() {
        return asCalendar(true);
    }

    public static Calendar asCalendar(boolean includeTime) {
        return asCalendar(Locale.getDefault(), TimeZone.getDefault(), includeTime);
    }

    public static Calendar asCalendar(Locale locale) {
        return asCalendar(locale, TimeZone.getDefault(), true);
    }

    public static Calendar asCalendar(TimeZone timeZone) {
        return asCalendar(Locale.getDefault(), timeZone, true);
    }

    /**
     * Returns false if the current time source is a {@link FixedTimeSource} indicating that the 
     * time is being overridden.   For example to preview items in a later time.
     * 
     * @return
     */
    public static boolean shouldCacheDate() {
        if (SystemTime.getTimeSource() instanceof FixedTimeSource) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Many DAO objects in Broadleaf use a cached time concept.   Since most entities have an active
     * start and end date, the DAO may ask for a representation of "NOW" that is within some
     * threshold.   
     * 
     * By default, most entities cache active-date queries to every 10 seconds.    These DAO
     * classes can be overridden to extend or decrease this default.
     * 
     * @return
     */
    public static Date getCurrentDateWithinTimeResolution(Date cachedDate, Long dateResolutionMillis) {
        Date returnDate = SystemTime.asDate();
        if (cachedDate == null || (SystemTime.getTimeSource() instanceof FixedTimeSource)) {
            return returnDate;
        }

        if (returnDate.getTime() > (cachedDate.getTime() + dateResolutionMillis)) {
            return returnDate;
        } else {
            return cachedDate;
        }
    }

    public static Calendar asCalendar(Locale locale, TimeZone timeZone, boolean includeTime) {
        Calendar calendar = Calendar.getInstance(timeZone, locale);
        calendar.setTimeInMillis(asMillis());
        if (!includeTime) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }
        return calendar;
    }
}
