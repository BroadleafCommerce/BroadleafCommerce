package org.broadleafcommerce.common;

import java.io.Serializable;

/**
 * Created by bpolster.
 */
public class DummyTimeDTO implements TimeDTO, Serializable {

    private static final long serialVersionUID = 1L;

    private int hour;
    private int dayOfWeek;
    private int dayOfMonth;
    private int month;
    private double timeAsDecimal;

    /**
     * @return int representing the hour of day as 0 - 23
     */
    @Override
    public int getHour() {
        return hour;
    }

    /**
     * @return int representing the day of week using Calendar.DAY_OF_WEEK values.
     *         1 = Sunday, 7 = Saturday
     */
    @Override
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * @return the current day of the month (1-31).
     */
    @Override
    public int getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * @return int representing the current month (1-12)
     */
    @Override
    public int getMonth() {
        return month;
    }

    /**
     * @return the time as a decimal (e.g. 13.5  =  (1:30 PM) or more specifically,
     *         Calendar-HOUR_OF_DAY + (CALENDAR.MINUTE_OF_HOUR / 60).
     */
    @Override
    public double getTimeAsDecimal() {
        return timeAsDecimal;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setTimeAsDecimal(double timeAsDecimal) {
        this.timeAsDecimal = timeAsDecimal;
    }
}
