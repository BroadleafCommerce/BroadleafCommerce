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
        return ret;
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
