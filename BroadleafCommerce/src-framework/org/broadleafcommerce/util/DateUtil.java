package org.broadleafcommerce.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

    private static Date overrideDate = null;

    public void setOverrideDate(Date overrideDate) {
        DateUtil.overrideDate = overrideDate;
    }

    public static Date getNow() {
        Date ret = overrideDate;
        if (ret == null) {
            ret = new Date();
        }
        return overrideDate;
    }

    public static Date getToday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getNow());
        return new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).getTime();
    }

    public static boolean isActive(Date startDate, Date endDate, boolean includeTime) {
        Date date = null;
        if (includeTime) {
            date = getNow();
        } else {
            date = getToday();
        }
        if (startDate == null || startDate.after(date) || (endDate != null && endDate.before(date))) {
            return false;
        }
        return true;
    }
}
