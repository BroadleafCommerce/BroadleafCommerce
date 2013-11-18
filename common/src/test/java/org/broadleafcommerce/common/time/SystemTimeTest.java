/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.time;

import junit.framework.TestCase;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class SystemTimeTest extends TestCase {
    private TimeSource mockTimeSource;

    protected void setUp() throws Exception {
        super.setUp();
        mockTimeSource = createMock(TimeSource.class);
    }

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
