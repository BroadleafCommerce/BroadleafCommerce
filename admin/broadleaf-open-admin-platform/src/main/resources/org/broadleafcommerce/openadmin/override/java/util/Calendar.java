/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java.util;

import java.io.Serializable;

/**
 * A simple, GWT compatible version of the Calendar class.
 * 
 * @author jfischer
 *
 */
public class Calendar implements Serializable {
    
    private static final long serialVersionUID = 1L;

    public static Calendar getInstance() {
        return new Calendar();
    }

    private Date calendar = new Date();

    public void add(int field, int amount) {
        long millis = System.currentTimeMillis();
        switch(field) {
        case Calendar.DATE: {
            calendar = new Date(millis + (amount * 1000 * 60 * 60 * 24));
            break;
        }
        case Calendar.HOUR: {
            calendar = new Date(millis + (amount * 1000 * 60 * 60));
            break;
        }
        case Calendar.MILLISECOND: {
            calendar = new Date(millis + (amount * 1000));
            break;
        }
        case Calendar.MINUTE: {
            calendar = new Date(millis + (amount * 1000 * 60));
            break;
        }
        case Calendar.YEAR: {
            calendar = new Date(millis);
            calendar.setYear(calendar.getYear() + amount);
            break;
        }
        case Calendar.MONTH: {
            int modulus = amount % 12;
            int yearAmount = amount/12;
            calendar = new Date(millis);
            calendar.setYear(calendar.getYear() + yearAmount);
            int month = calendar.getMonth();
            int year = calendar.getYear();
            int result = month + modulus;
            if (result > 11) {
                calendar.setMonth(result - 12);
                calendar.setYear(year + 1);
            } else if (result < 0) {
                calendar.setMonth(12 + result);
                calendar.setYear(year - 1);
            } else {
                calendar.setMonth(result);
            }
            break;
        }
        default: {
            throw new RuntimeException("Type not supported: " + field);
        }
        }
    }

    public boolean after(Object when) {
        Date test;
        if (when.getClass().getName().equals(Date.class.getName())) {
            test = (Date) when;
        } else {
            test = ((Calendar) when).getTime();
        }
        return calendar.getTime() > test.getTime();
    }

    public boolean before(Object when) {
        Date test;
        if (when.getClass().getName().equals(Date.class.getName())) {
            test = (Date) when;
        } else {
            test = ((Calendar) when).getTime();
        }
        return calendar.getTime() < test.getTime();
    }

    public final void clear() {
        calendar = new Date();
    }

    public final void clear(int field) {
        throw new RuntimeException("Not Supported");
    }

    public Object clone() {
        return calendar.clone();
    }

    public int compareTo(Calendar anotherCalendar) {
        return getTime().compareTo(anotherCalendar.getTime());
    }

    public boolean equals(Object obj) {
        return calendar.equals(obj);
    }

    @SuppressWarnings("deprecation")
    public int get(int field) {
        int response;
        switch(field) {
        case Calendar.SECOND: {
            response = calendar.getSeconds();
            break;
        }
        case Calendar.MINUTE: {
            response = calendar.getMinutes();
            break;
        }
        case Calendar.HOUR: {
            response = calendar.getHours();
            break;
        }
        case Calendar.DATE: {
            response = calendar.getDate();
            break;
        }
        case Calendar.MONTH: {
            response = calendar.getMonth();
            break;
        }
        case Calendar.YEAR: {
            response = calendar.getYear();
            break;
        }
        default: {
            throw new RuntimeException("Type not supported: " + field);
        }
        }
        
        return response;
    }

    public int getActualMaximum(int field) {
        throw new RuntimeException("Not Supported");
    }

    public int getActualMinimum(int field) {
        throw new RuntimeException("Not Supported");
    }

    public int getFirstDayOfWeek() {
        throw new RuntimeException("Not Supported");
    }

    public int getGreatestMinimum(int field) {
        throw new RuntimeException("Not Supported");
    }

    public int getLeastMaximum(int field) {
        throw new RuntimeException("Not Supported");
    }

    public int getMaximum(int field) {
        throw new RuntimeException("Not Supported");
    }

    public int getMinimalDaysInFirstWeek() {
        throw new RuntimeException("Not Supported");
    }

    public int getMinimum(int field) {
        throw new RuntimeException("Not Supported");
    }

    public final Date getTime() {
        return calendar;
    }

    public long getTimeInMillis() {
        return calendar.getTime();
    }

    public TimeZone getTimeZone() {
        throw new RuntimeException("Not Supported");
    }

    public int hashCode() {
        return calendar.hashCode();
    }

    public boolean isLenient() {
        throw new RuntimeException("Not Supported");
    }

    public final boolean isSet(int field) {
        throw new RuntimeException("Not Supported");
    }

    public void roll(int field, boolean up) {
        throw new RuntimeException("Not Supported");
    }

    public void roll(int field, int amount) {
        throw new RuntimeException("Not Supported");
    }

    @SuppressWarnings("deprecation")
    public final void set(int year, int month, int date, int hourOfDay, int minute, int second) {
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDate(date);
        calendar.setHours(hourOfDay);
        calendar.setMinutes(minute);
        calendar.setSeconds(second);
    }

    @SuppressWarnings("deprecation")
    public final void set(int year, int month, int date, int hourOfDay, int minute) {
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDate(date);
        calendar.setHours(hourOfDay);
        calendar.setMinutes(minute);
    }

    @SuppressWarnings("deprecation")
    public final void set(int year, int month, int date) {
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDate(date);
    }

    @SuppressWarnings("deprecation")
    public void set(int field, int value) {
        switch(field) {
        case Calendar.SECOND: {
            calendar.setSeconds(value);
            break;
        }
        case Calendar.MINUTE: {
            calendar.setMinutes(value);
            break;
        }
        case Calendar.HOUR: {
            calendar.setHours(value);
            break;
        }
        case Calendar.DATE: {
            calendar.setDate(value);
            break;
        }
        case Calendar.MONTH: {
            calendar.setMonth(value);
            break;
        }
        case Calendar.YEAR: {
            calendar.setYear(value);
            break;
        }
        default: {
            throw new RuntimeException("Type not supported: " + field);
        }
        }
    }

    public void setFirstDayOfWeek(int value) {
        throw new RuntimeException("Not Supported");
    }

    public void setLenient(boolean lenient) {
        throw new RuntimeException("Not Supported");
    }

    public void setMinimalDaysInFirstWeek(int value) {
        throw new RuntimeException("Not Supported");
    }

    public final void setTime(Date date) {
        calendar = date;
    }

    public void setTimeInMillis(long millis) {
        calendar = new Date(millis);
    }

    public void setTimeZone(TimeZone value) {
        throw new RuntimeException("Not Supported");
    }

    public String toString() {
        return calendar.toString();
    }
    
    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * era, e.g., AD or BC in the Julian calendar. This is a calendar-specific
     * value; see subclass documentation.
     *
     * @see GregorianCalendar#AD
     * @see GregorianCalendar#BC
     */
    public final static int ERA = 0;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * year. This is a calendar-specific value; see subclass documentation.
     */
    public final static int YEAR = 1;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * month. This is a calendar-specific value. The first month of the year is
     * <code>JANUARY</code> which is 0; the last depends on the number of months in a year.
     *
     * @see #JANUARY
     * @see #FEBRUARY
     * @see #MARCH
     * @see #APRIL
     * @see #MAY
     * @see #JUNE
     * @see #JULY
     * @see #AUGUST
     * @see #SEPTEMBER
     * @see #OCTOBER
     * @see #NOVEMBER
     * @see #DECEMBER
     * @see #UNDECIMBER
     */
    public final static int MONTH = 2;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * week number within the current year.  The first week of the year, as
     * defined by <code>getFirstDayOfWeek()</code> and
     * <code>getMinimalDaysInFirstWeek()</code>, has value 1.  Subclasses define
     * the value of <code>WEEK_OF_YEAR</code> for days before the first week of
     * the year.
     *
     * @see #getFirstDayOfWeek
     * @see #getMinimalDaysInFirstWeek
     */
    public final static int WEEK_OF_YEAR = 3;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * week number within the current month.  The first week of the month, as
     * defined by <code>getFirstDayOfWeek()</code> and
     * <code>getMinimalDaysInFirstWeek()</code>, has value 1.  Subclasses define
     * the value of <code>WEEK_OF_MONTH</code> for days before the first week of
     * the month.
     *
     * @see #getFirstDayOfWeek
     * @see #getMinimalDaysInFirstWeek
     */
    public final static int WEEK_OF_MONTH = 4;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * day of the month. This is a synonym for <code>DAY_OF_MONTH</code>.
     * The first day of the month has value 1.
     *
     * @see #DAY_OF_MONTH
     */
    public final static int DATE = 5;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * day of the month. This is a synonym for <code>DATE</code>.
     * The first day of the month has value 1.
     *
     * @see #DATE
     */
    public final static int DAY_OF_MONTH = 5;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the day
     * number within the current year.  The first day of the year has value 1.
     */
    public final static int DAY_OF_YEAR = 6;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the day
     * of the week.  This field takes values <code>SUNDAY</code>,
     * <code>MONDAY</code>, <code>TUESDAY</code>, <code>WEDNESDAY</code>,
     * <code>THURSDAY</code>, <code>FRIDAY</code>, and <code>SATURDAY</code>.
     *
     * @see #SUNDAY
     * @see #MONDAY
     * @see #TUESDAY
     * @see #WEDNESDAY
     * @see #THURSDAY
     * @see #FRIDAY
     * @see #SATURDAY
     */
    public final static int DAY_OF_WEEK = 7;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * ordinal number of the day of the week within the current month. Together
     * with the <code>DAY_OF_WEEK</code> field, this uniquely specifies a day
     * within a month.  Unlike <code>WEEK_OF_MONTH</code> and
     * <code>WEEK_OF_YEAR</code>, this field's value does <em>not</em> depend on
     * <code>getFirstDayOfWeek()</code> or
     * <code>getMinimalDaysInFirstWeek()</code>.  <code>DAY_OF_MONTH 1</code>
     * through <code>7</code> always correspond to <code>DAY_OF_WEEK_IN_MONTH
     * 1</code>; <code>8</code> through <code>14</code> correspond to
     * <code>DAY_OF_WEEK_IN_MONTH 2</code>, and so on.
     * <code>DAY_OF_WEEK_IN_MONTH 0</code> indicates the week before
     * <code>DAY_OF_WEEK_IN_MONTH 1</code>.  Negative values count back from the
     * end of the month, so the last Sunday of a month is specified as
     * <code>DAY_OF_WEEK = SUNDAY, DAY_OF_WEEK_IN_MONTH = -1</code>.  Because
     * negative values count backward they will usually be aligned differently
     * within the month than positive values.  For example, if a month has 31
     * days, <code>DAY_OF_WEEK_IN_MONTH -1</code> will overlap
     * <code>DAY_OF_WEEK_IN_MONTH 5</code> and the end of <code>4</code>.
     *
     * @see #DAY_OF_WEEK
     * @see #WEEK_OF_MONTH
     */
    public final static int DAY_OF_WEEK_IN_MONTH = 8;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating
     * whether the <code>HOUR</code> is before or after noon.
     * E.g., at 10:04:15.250 PM the <code>AM_PM</code> is <code>PM</code>.
     *
     * @see #AM
     * @see #PM
     * @see #HOUR
     */
    public final static int AM_PM = 9;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * hour of the morning or afternoon. <code>HOUR</code> is used for the
     * 12-hour clock (0 - 11). Noon and midnight are represented by 0, not by 12.
     * E.g., at 10:04:15.250 PM the <code>HOUR</code> is 10.
     *
     * @see #AM_PM
     * @see #HOUR_OF_DAY
     */
    public final static int HOUR = 10;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * hour of the day. <code>HOUR_OF_DAY</code> is used for the 24-hour clock.
     * E.g., at 10:04:15.250 PM the <code>HOUR_OF_DAY</code> is 22.
     *
     * @see #HOUR
     */
    public final static int HOUR_OF_DAY = 11;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * minute within the hour.
     * E.g., at 10:04:15.250 PM the <code>MINUTE</code> is 4.
     */
    public final static int MINUTE = 12;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * second within the minute.
     * E.g., at 10:04:15.250 PM the <code>SECOND</code> is 15.
     */
    public final static int SECOND = 13;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * millisecond within the second.
     * E.g., at 10:04:15.250 PM the <code>MILLISECOND</code> is 250.
     */
    public final static int MILLISECOND = 14;

    /**
     * Field number for <code>get</code> and <code>set</code>
     * indicating the raw offset from GMT in milliseconds.
     * <p>
     * This field reflects the correct GMT offset value of the time
     * zone of this <code>Calendar</code> if the
     * <code>TimeZone</code> implementation subclass supports
     * historical GMT offset changes.
     */
    public final static int ZONE_OFFSET = 15;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * daylight savings offset in milliseconds.
     * <p>
     * This field reflects the correct daylight saving offset value of
     * the time zone of this <code>Calendar</code> if the
     * <code>TimeZone</code> implementation subclass supports
     * historical Daylight Saving Time schedule changes.
     */
    public final static int DST_OFFSET = 16;

    /**
     * The number of distinct fields recognized by <code>get</code> and <code>set</code>.
     * Field numbers range from <code>0..FIELD_COUNT-1</code>.
     */
    public final static int FIELD_COUNT = 17;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Sunday.
     */
    public final static int SUNDAY = 1;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Monday.
     */
    public final static int MONDAY = 2;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Tuesday.
     */
    public final static int TUESDAY = 3;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Wednesday.
     */
    public final static int WEDNESDAY = 4;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Thursday.
     */
    public final static int THURSDAY = 5;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Friday.
     */
    public final static int FRIDAY = 6;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Saturday.
     */
    public final static int SATURDAY = 7;

    /**
     * Value of the {@link #MONTH} field indicating the
     * first month of the year.
     */
    public final static int JANUARY = 0;

    /**
     * Value of the {@link #MONTH} field indicating the
     * second month of the year.
     */
    public final static int FEBRUARY = 1;

    /**
     * Value of the {@link #MONTH} field indicating the
     * third month of the year.
     */
    public final static int MARCH = 2;

    /**
     * Value of the {@link #MONTH} field indicating the
     * fourth month of the year.
     */
    public final static int APRIL = 3;

    /**
     * Value of the {@link #MONTH} field indicating the
     * fifth month of the year.
     */
    public final static int MAY = 4;

    /**
     * Value of the {@link #MONTH} field indicating the
     * sixth month of the year.
     */
    public final static int JUNE = 5;

    /**
     * Value of the {@link #MONTH} field indicating the
     * seventh month of the year.
     */
    public final static int JULY = 6;

    /**
     * Value of the {@link #MONTH} field indicating the
     * eighth month of the year.
     */
    public final static int AUGUST = 7;

    /**
     * Value of the {@link #MONTH} field indicating the
     * ninth month of the year.
     */
    public final static int SEPTEMBER = 8;

    /**
     * Value of the {@link #MONTH} field indicating the
     * tenth month of the year.
     */
    public final static int OCTOBER = 9;

    /**
     * Value of the {@link #MONTH} field indicating the
     * eleventh month of the year.
     */
    public final static int NOVEMBER = 10;

    /**
     * Value of the {@link #MONTH} field indicating the
     * twelfth month of the year.
     */
    public final static int DECEMBER = 11;

    /**
     * Value of the {@link #MONTH} field indicating the
     * thirteenth month of the year. Although <code>GregorianCalendar</code>
     * does not use this value, lunar calendars do.
     */
    public final static int UNDECIMBER = 12;

    /**
     * Value of the {@link #AM_PM} field indicating the
     * period of the day from midnight to just before noon.
     */
    public final static int AM = 0;

    /**
     * Value of the {@link #AM_PM} field indicating the
     * period of the day from noon to just before midnight.
     */
    public final static int PM = 1;
}
