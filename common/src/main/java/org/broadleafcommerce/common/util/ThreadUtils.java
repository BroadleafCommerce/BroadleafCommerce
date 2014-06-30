/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.common.util;

import java.util.Calendar;

/**
 * This utility class provides methods that simplify thread operations.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class ThreadUtils {
    
    /**
     * Sleeps the current thread until the specified future date. If the date is before the current time,
     * the thread will resume operation immediately.
     * 
     * @param date
     */
    public static void sleepUntil(int year, int month, int day, int hour, int min, int sec) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, min, sec);

        long msFuture = cal.getTime().getTime();
        long msNow = System.currentTimeMillis();
        long msSleep = msFuture - msNow;

        if (msSleep <= 0) {
            return;
        }

        try {
            Thread.sleep(msFuture - msNow);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
