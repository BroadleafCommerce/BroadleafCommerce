/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.test.common.time;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.time.TimeSource;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

public class SystemTimeTest extends TestCase {
    private TimeSource mockTimeSource;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mockTimeSource = createMock(TimeSource.class);
    }

    @Override
    protected void tearDown() throws Exception {
        SystemTime.reset();
        super.tearDown();
    }

    /**
     * Test method for {@link SystemTime#setGlobalTimeSource(TimeSource)}.
     */
    public void testSetGlobalTimeSource() {
        expect(mockTimeSource.timeInMillis()).andReturn(100L).atLeastOnce();
        replay(mockTimeSource);
        SystemTime.setGlobalTimeSource(mockTimeSource);
        assertEquals(100L, SystemTime.asMillis());
        verify();
    }

    /**
     * Test method for {@link SystemTime#resetGlobalTimeSource()}.
     */
    public void testResetGlobalTimeSource() {
        expect(mockTimeSource.timeInMillis()).andReturn(200L).anyTimes();
        replay(mockTimeSource);
        SystemTime.setGlobalTimeSource(mockTimeSource);
        SystemTime.resetGlobalTimeSource();
        assertTrue(200L != SystemTime.asMillis());
        verify();
    }

    /**
     * Test method for {@link SystemTime#setLocalTimeSource(TimeSource)}.
     */
    public void testSetLocalTimeSource() {
        expect(mockTimeSource.timeInMillis()).andReturn(300L).atLeastOnce();
        replay(mockTimeSource);
        SystemTime.setLocalTimeSource(mockTimeSource);
        assertEquals(300L, SystemTime.asMillis());
        verify();
    }

    /**
     * Test method for {@link SystemTime#resetLocalTimeSource()}.
     */
    public void testResetLocalTimeSource() {
        expect(mockTimeSource.timeInMillis()).andReturn(400L).anyTimes();
        replay(mockTimeSource);
        SystemTime.setLocalTimeSource(mockTimeSource);
        SystemTime.resetLocalTimeSource();
        assertTrue(400L != SystemTime.asMillis());
        verify();
    }

    /**
     * Test method for {@link SystemTime#resetLocalTimeSource()}.
     */
    public void testLocalOverridesGlobal() {
        TimeSource mockLocalTimeSource = createMock(TimeSource.class);
        expect(mockTimeSource.timeInMillis()).andReturn(500L).anyTimes();
        expect(mockLocalTimeSource.timeInMillis()).andReturn(600L).atLeastOnce();
        replay(mockTimeSource, mockLocalTimeSource);
        SystemTime.setGlobalTimeSource(mockTimeSource);
        SystemTime.setLocalTimeSource(mockLocalTimeSource);
        assertEquals(600L, SystemTime.asMillis());
        SystemTime.resetLocalTimeSource();
        assertEquals(500L, SystemTime.asMillis());
        verify();
    }

    /**
     * Test method for {@link SystemTime#reset()}.
     */
    public void testReset() {
        TimeSource mockLocalTimeSource = createMock(TimeSource.class);
        expect(mockTimeSource.timeInMillis()).andReturn(700L).anyTimes();
        expect(mockLocalTimeSource.timeInMillis()).andReturn(800L).anyTimes();
        replay(mockTimeSource, mockLocalTimeSource);
        SystemTime.setGlobalTimeSource(mockTimeSource);
        SystemTime.setLocalTimeSource(mockLocalTimeSource);
        SystemTime.reset();
        assertTrue(SystemTime.asMillis() > 800L);
        verify();
    }

    /**
     * Test method for {@link SystemTime#asMillis()}.
     */
    public void testAsMillis() {
        expect(mockTimeSource.timeInMillis()).andReturn(1000L).atLeastOnce();
        replay(mockTimeSource);
        SystemTime.setGlobalTimeSource(mockTimeSource);
        assertEquals(1000L, SystemTime.asMillis());
        verify();
    }

    /**
     * Test method for {@link SystemTime#asDate()}.
     */
    public void testAsDate() {
        expect(mockTimeSource.timeInMillis()).andReturn(1100L).atLeastOnce();
        replay(mockTimeSource);
        SystemTime.setGlobalTimeSource(mockTimeSource);
        assertEquals(1100L, SystemTime.asDate().getTime());
        verify();
    }

    /**
     * Test method for {@link SystemTime#asCalendar()}.
     */
    public void testAsCalendar() {
        expect(mockTimeSource.timeInMillis()).andReturn(1200L).atLeastOnce();
        replay(mockTimeSource);
        SystemTime.setGlobalTimeSource(mockTimeSource);
        assertEquals(1200L, SystemTime.asCalendar().getTimeInMillis());
        verify();
    }

    /**
     * Test method for {@link SystemTime#asMillis(boolean)}.
     */
    public void testAsMillisBoolean() {
        Calendar cal = new GregorianCalendar(2010, 1, 2, 3, 4, 5);
        long timeInMillis = cal.getTimeInMillis() + 3; // Add a few milliseconds for good measure
        expect(mockTimeSource.timeInMillis()).andReturn(timeInMillis).atLeastOnce();
        replay(mockTimeSource);
        SystemTime.setGlobalTimeSource(mockTimeSource);
        Calendar calMidnight = new GregorianCalendar(2010, 1, 2, 0, 0, 0);
        calMidnight.set(Calendar.MILLISECOND, 0);
        assertEquals(calMidnight.getTimeInMillis(), SystemTime.asMillis(false));
        assertEquals(timeInMillis, SystemTime.asMillis(true));
        verify();
    }


    /**
     * Test method for {@link SystemTime#asCalendar(boolean)}.
     */
    public void testAsCalendarBoolean() {
        Calendar cal = new GregorianCalendar(2010, 1, 2, 3, 4, 5);
        cal.set(Calendar.MILLISECOND, 3); // Add a few milliseconds for good measure
        expect(mockTimeSource.timeInMillis()).andReturn(cal.getTimeInMillis()).atLeastOnce();
        replay(mockTimeSource);
        SystemTime.setGlobalTimeSource(mockTimeSource);
        Calendar calMidnight = new GregorianCalendar(2010, 1, 2, 0, 0, 0);
        calMidnight.set(Calendar.MILLISECOND, 0);
        assertEquals(calMidnight, SystemTime.asCalendar(false));
        assertEquals(cal, SystemTime.asCalendar(true));
        verify();
    }

    /**
     * Test method for {@link SystemTime#asDate(boolean)}.
     */
    public void testAsDateBoolean() {
        Calendar cal = new GregorianCalendar(2010, 1, 2, 3, 4, 5);
        cal.set(Calendar.MILLISECOND, 3); // Add a few milliseconds for good measure
        expect(mockTimeSource.timeInMillis()).andReturn(cal.getTimeInMillis()).atLeastOnce();
        replay(mockTimeSource);
        SystemTime.setGlobalTimeSource(mockTimeSource);
        Calendar calMidnight = new GregorianCalendar(2010, 1, 2, 0, 0, 0);
        calMidnight.set(Calendar.MILLISECOND, 0);
        assertEquals(calMidnight.getTimeInMillis(), SystemTime.asDate(false).getTime());
        assertEquals(cal.getTimeInMillis(), SystemTime.asDate(true).getTime());
        verify();
    }
}
