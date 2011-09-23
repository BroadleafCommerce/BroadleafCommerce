package org.broadleafcommerce.common;

/**
 * Created by bpolster.
 */
public interface TimeDTO {

    /**
     * @return  int representing the hour of day as 0 - 23
     */
    public int getHour();

    /**
     * @return int representing the day of week using Calendar.DAY_OF_WEEK values.
     * 1 = Sunday, 7 = Saturday
     */
    public int getDayOfWeek();

    /**
     * @return the current day of the month (1-31).
     */
    public int getDayOfMonth();

    /**
     * @return int representing the current month (1-12)
     */
    public int getMonth();

    /**
     * @return the time as a decimal (e.g. 13.5  =  (1:30 PM) or more specifically,
     * Calendar-HOUR_OF_DAY + (CALENDAR.MINUTE_OF_HOUR / 60).
     */
    public double getTimeAsDecimal();

}
