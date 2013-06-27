/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.time;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SystemTime {

    private static final TimeSource defaultTimeSource = new DefaultTimeSource();
    private static TimeSource globalTimeSource = null;
    private static final InheritableThreadLocal<TimeSource> localTimeSource = new InheritableThreadLocal<TimeSource>();

    private static TimeSource getTimeSource() {
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
