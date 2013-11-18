/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.rulebuilder;

import org.broadleafcommerce.common.util.FormatUtil;
import org.broadleafcommerce.common.web.BroadleafRequestContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Work with dates in rule builder mvel
 *
 * @author Jeff Fischer
 */
public class RuleBuilderFormatUtil {

    public static final String COMPATIBILITY_FORMAT = "MM/dd/yy HH:mm a Z";
    public static final String DATE_FORMAT = "MM/dd/yyyy HH:mm";

    /**
     * Prepare date for display in the admin
     *
     * @param date the date to convert
     * @return the string value to show in the admin
     */
    public static String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        formatter.setTimeZone(BroadleafRequestContext.getBroadleafRequestContext().getTimeZone());
        return formatter.format(date);
    }

    /**
     * Parse the string value of the date stored in mvel
     *
     * @param date the mvel date value
     * @return the parsed Date instance
     */
    public static Date parseDate(String date) throws ParseException {
        Date parsedDate;
        try {
            parsedDate = FormatUtil.getTimeZoneFormat().parse(date);
        } catch (ParseException e) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(COMPATIBILITY_FORMAT);
                formatter.setTimeZone(BroadleafRequestContext.getBroadleafRequestContext().getTimeZone());
                parsedDate = formatter.parse(date);
            } catch (ParseException e1) {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
                    formatter.setTimeZone(BroadleafRequestContext.getBroadleafRequestContext().getTimeZone());
                    parsedDate = formatter.parse(date);
                } catch (ParseException e2) {
                    throw e;
                }
            }
        }
        return parsedDate;
    }
}
