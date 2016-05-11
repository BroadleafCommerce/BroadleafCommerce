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
