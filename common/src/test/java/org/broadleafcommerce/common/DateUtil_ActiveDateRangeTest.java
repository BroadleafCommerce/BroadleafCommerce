package org.broadleafcommerce.common;

import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.broadleafcommerce.common.util.DateUtil;

public class DateUtil_ActiveDateRangeTest extends TestCase {
    public void testDateRangeWithNullDates_ExpectInactive(){
        Date startDate = null;
        Date endDate = null;
        boolean includeTime = false;
        boolean actual = DateUtil.isActive(startDate, endDate, includeTime);
        boolean expected = false;
        Assert.assertEquals(expected, actual);
    }
    
    public void testDateRangeWithNullStartDate_ExpectInactive(){
        Date startDate = null;
        Date endDate = dateInGregorianCalendar(2012, 11, 31);
        boolean includeTime = false;
        boolean actual = DateUtil.isActive(startDate, endDate, includeTime);
        boolean expected = false;
        Assert.assertEquals(expected, actual);
    }
    
    public void testDateRangeWithTodayStartDateAndCurrentTime_ExpectInactive(){
        Date startDate = new Date();
        Date endDate = null;
        boolean includeTime = false;
        boolean actual = DateUtil.isActive(startDate, endDate, includeTime);
        boolean expected = false;
        Assert.assertEquals(expected, actual);
    }
    
    public void testDateRangeWithFutureStartDate_ExpectInactive(){
        Date startDate = dateInGregorianCalendar(2012, 11, 31);
        Date endDate = null;
        boolean includeTime = false;
        boolean actual = DateUtil.isActive(startDate, endDate, includeTime);
        boolean expected = false;
        Assert.assertEquals(expected, actual);
    }
    
    public void testDateRangeWithNullEndDate_ExpectActive(){
        Date startDate = dateInGregorianCalendar(2012, 9, 31);
        Date endDate = null;
        boolean includeTime = false;
        boolean actual = DateUtil.isActive(startDate, endDate, includeTime);
        boolean expected = true;
        Assert.assertEquals(expected, actual);
    }
    
    public void testDateRangeWithPastEndDate_ExpectInactive(){
        Date startDate = dateInGregorianCalendar(2012, 8, 31);
        Date endDate = dateInGregorianCalendar(2012, 9, 31);
        boolean includeTime = false;
        boolean actual = DateUtil.isActive(startDate, endDate, includeTime);
        boolean expected = false;
        Assert.assertEquals(expected, actual);
    }
    
    public void testDateRangeWithTodayEndDateAndCurrentTime_ExpectActive(){
        Date startDate = dateInGregorianCalendar(2012, 9, 31);
        Date endDate = new Date();
        boolean includeTime = false;
        boolean actual = DateUtil.isActive(startDate, endDate, includeTime);
        boolean expected = true;
        Assert.assertEquals(expected, actual);
    }
    
    public void testDateRangeWithFutureEndDate_ExpectActive(){
        Date startDate = dateInGregorianCalendar(2012, 9, 31);
        Date endDate = dateInGregorianCalendar(2012, 11, 31);
        boolean includeTime = false;
        boolean actual = DateUtil.isActive(startDate, endDate, includeTime);
        boolean expected = true;
        Assert.assertEquals(expected, actual);
    }
    
    private Date dateInGregorianCalendar(int year, int month, int day){
        return new GregorianCalendar(year, month, day).getTime();
    }
}
