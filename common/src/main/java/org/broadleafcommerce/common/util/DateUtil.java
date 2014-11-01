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
package org.broadleafcommerce.common.util;

import org.broadleafcommerce.common.time.SystemTime;

import java.util.Date;

public class DateUtil {

    public static final long ONE_HOUR_MILLIS = 60 * 60 * 1000;
    public static final long ONE_DAY_MILLIS = ONE_HOUR_MILLIS * 24;
    public static final long ONE_WEEK_MILLIS = ONE_DAY_MILLIS * 7;

    public static final long ONE_HOUR_SECONDS = 60 * 60;
    public static final long ONE_DAY_SECONDS = ONE_HOUR_SECONDS * 24;
    public static final long ONE_WEEK_SECONDS = ONE_DAY_SECONDS * 7;
    public static final long SIX_MONTHS_SECONDS = ONE_DAY_SECONDS * 6 * 30;

    public static boolean isActive(Date startDate, Date endDate, boolean includeTime) {
        Long date = SystemTime.asMillis(includeTime);
        return !(startDate == null || startDate.getTime() > date || (endDate != null && endDate.getTime() < date));
    }

}
