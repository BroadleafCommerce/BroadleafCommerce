/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeZoneWrapper extends TimeZone {

    private final TimeZone timeZone;

    public TimeZoneWrapper(TimeZone timeZone) {

        this.timeZone = timeZone;
    }

    public static TimeZone getTimeZone(String ID) {
        return TimeZone.getTimeZone(ID);
    }

    public static TimeZone getTimeZone(ZoneId zoneId) {
        return TimeZone.getTimeZone(zoneId);
    }

    public static String[] getAvailableIDs(int rawOffset) {
        return TimeZone.getAvailableIDs(rawOffset);
    }

    public static String[] getAvailableIDs() {
        return TimeZone.getAvailableIDs();
    }

    public static TimeZone getDefault() {
        return TimeZone.getDefault();
    }

    public static void setDefault(TimeZone zone) {
        TimeZone.setDefault(zone);
    }

    @Override
    public int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds) {
        return timeZone.getOffset(era, year, month, day, dayOfWeek, milliseconds);
    }

    @Override
    public int getOffset(long date) {
        return timeZone.getOffset(date);
    }

    @Override
    public int getRawOffset() {
        return timeZone.getRawOffset();
    }

    @Override
    public void setRawOffset(int offsetMillis) {
        timeZone.setRawOffset(offsetMillis);
    }

    @Override
    public String getID() {
        return timeZone.getID();
    }

    @Override
    public void setID(String ID) {
        timeZone.setID(ID);
    }

    @Override
    public String getDisplayName(boolean daylight, int style, Locale locale) {
        return timeZone.getDisplayName(daylight, style, locale);
    }

    @Override
    public int getDSTSavings() {
        return timeZone.getDSTSavings();
    }

    @Override
    public boolean useDaylightTime() {
        return timeZone.useDaylightTime();
    }

    @Override
    public boolean observesDaylightTime() {
        return timeZone.observesDaylightTime();
    }

    @Override
    public boolean inDaylightTime(Date date) {
        return timeZone.inDaylightTime(date);
    }

    @Override
    public ZoneId toZoneId() {
        return timeZone.toZoneId();
    }

    @Override
    public boolean hasSameRules(TimeZone other) {
        return timeZone.hasSameRules(other);
    }

    @Override
    public Object clone() {
        return timeZone.clone();
    }

    @Override
    public String toString() {
        return timeZone.toString();
    }

}
