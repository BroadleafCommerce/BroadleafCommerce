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

package org.broadleafcommerce.common;

import org.broadleafcommerce.openadmin.time.SystemTime;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by bpolster.
 */
public class TimeDTO {
    private Calendar cal;
    private Integer hour;
    private Integer dayOfWeek;
    private Integer month;
    private Integer dayOfMonth;
    private Double timeAsDecimal;
    private Date date;

    public TimeDTO() {
        cal = SystemTime.asCalendar();
    }

    public TimeDTO(Calendar cal) {
        this.cal = cal;
    }


    /**
     * @return  int representing the hour of day as 0 - 23
     */
    public int getHour() {
        if (hour == null) {
            hour = cal.get(Calendar.HOUR_OF_DAY);
        }
        return hour;
    }

    /**
     * @return int representing the day of week using Calendar.DAY_OF_WEEK values.
     * 1 = Sunday, 7 = Saturday
     */
    public int getDayOfWeek() {
        if (dayOfWeek == null) {
            dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        }
        return dayOfWeek;
    }

    /**
     * @return the current day of the month (1-31).
     */
    public int getDayOfMonth() {
        if (dayOfMonth == null) {
            dayOfMonth =  cal.get(Calendar.DAY_OF_MONTH);
        }
        return dayOfMonth;
    }

    /**
     * @return int representing the current month (1-12)
     */
    public int getMonth() {
        if (month == null) {
            month = cal.get(Calendar.MONTH);
        }
        return month;
    }

    /**
     * @return the time as a decimal (e.g. 13.5  =  (1:30 PM) or more specifically,
     * Calendar-HOUR_OF_DAY + (CALENDAR.MINUTE_OF_HOUR / 60).
     */
    public double getTimeAsDecimal() {
        if (timeAsDecimal == null) {
            timeAsDecimal = cal.get(Calendar.HOUR_OF_DAY) + (cal.get(Calendar.MINUTE) / 60.0);
        }
        return timeAsDecimal;
    }

    public Date getDate() {
        if (date == null) {
            date = cal.getTime();
        }
        return date;
    }

    public void setCal(Calendar cal) {
        this.cal = cal;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public void setDayOfMonth(Integer dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public void setTimeAsDecimal(Double timeAsDecimal) {
        this.timeAsDecimal = timeAsDecimal;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
