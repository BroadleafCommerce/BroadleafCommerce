package org.broadleafcommerce.time;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
        return getTimeSource().timeInMillis();
    }

    public static Date asDate() {
        return new Date(asMillis());
    }

    public static Calendar asCalendar() {
        return asCalendar(Locale.getDefault(), TimeZone.getDefault());
    }

    public static Calendar asCalendar(Locale locale) {
        return asCalendar(locale, TimeZone.getDefault());
    }

    public static Calendar asCalendar(TimeZone timeZone) {
        return asCalendar(Locale.getDefault(), timeZone);
    }

    public static Calendar asCalendar(Locale locale, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone, locale);
        // Use intentionally as setTimeInMillis() is not available pre 1.4
        calendar.setTime(asDate());
        return calendar;
    }

    public static long asMillis(boolean includeTime) {
        Calendar cal = Calendar.getInstance();
        cal.getTime().setTime(getTimeSource().timeInMillis());
        Calendar calToday = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        return calToday.getTimeInMillis();
    }

    public static Calendar asCalendar(boolean includeTime) {
        Calendar calendar = asCalendar(Locale.getDefault(), TimeZone.getDefault());
        if (!includeTime) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }
        return calendar;
    }

    @SuppressWarnings("deprecation")
    public static Date asDate(boolean includeTime) {
        Date date = new Date(asMillis());
        if (!includeTime) {
            date.setHours(0);
            date.setMinutes(0);
            date.setSeconds(0);
        }
        return date;
    }
}
