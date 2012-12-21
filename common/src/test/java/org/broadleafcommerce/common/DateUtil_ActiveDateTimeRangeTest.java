package org.broadleafcommerce.common;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.broadleafcommerce.common.util.DateUtil;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DateUtil_ActiveDateTimeRangeTest extends TestCase {
    public void testDateRangeWithCurrentTime_ExpectActive(){
        Date startDate = new Date();
        Date endDate = null;
        boolean includeTime = true;
        boolean actual = DateUtil.isActive(startDate, endDate, includeTime);
        boolean expected = true;
        Assert.assertEquals(expected, actual);
    }
    
    public void testDateRangeWithLaterStartTime_ExpectInactive(){
        Date startDate = incrementByHours(new Date(), 2);
        Date endDate = null;
        boolean includeTime = true;
        boolean actual = DateUtil.isActive(startDate, endDate, includeTime);
        boolean expected = false;
        Assert.assertEquals(expected, actual);
    }
    
    public void testDateRangeWithEarlierStartTime_ExpectActive(){
        Date startDate = decrementByHours(new Date(), 2);
        Date endDate = null;
        boolean includeTime = true;
        boolean actual = DateUtil.isActive(startDate, endDate, includeTime);
        boolean expected = true;
        Assert.assertEquals(expected, actual);
    }
    
    public void testDateRangeWithLaterEndTime_ExpectActive(){
        Date startDate = new Date();
        Date endDate = incrementByHours(startDate, 1);
        boolean includeTime = true;
        boolean actual = DateUtil.isActive(startDate, endDate, includeTime);
        boolean expected = true;
        Assert.assertEquals(expected, actual);
    }
    
    public void testDateRangeWithEarlierEndTime_ExpectInactive(){
        Date now = new Date();
        Date startDate = decrementByHours(now, 2);
        Date endDate = decrementByHours(now, 1);
        boolean includeTime = true;
        boolean actual = DateUtil.isActive(startDate, endDate, includeTime);
        boolean expected = false;
        Assert.assertEquals(expected, actual);
    }
    
    public void testDateRangeWithCurrentEndTime_ExpectInactive(){
        Date now = new Date();
        Date startDate = decrementByHours(now, 2);
        Date endDate = now;
        boolean includeTime = true;
        boolean actual = DateUtil.isActive(startDate, endDate, includeTime);
        boolean expected = true;
        Assert.assertEquals(expected, actual);
    }

    private Date incrementByHours(Date date, int increment){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.roll(Calendar.HOUR, increment);
        return calendar.getTime();
    }
    
    private Date decrementByHours(Date date, int increment){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.roll(Calendar.HOUR, -1 * increment);
        return calendar.getTime();
    }
}
