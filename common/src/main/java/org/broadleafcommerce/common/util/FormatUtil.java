/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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

import org.broadleafcommerce.common.web.BroadleafRequestContext;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;

/**
 * @author Jeff Fischer
 */
public class FormatUtil {

    public static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";
    public static final String DATE_FORMAT_WITH_TIMEZONE = "yyyy.MM.dd HH:mm:ss Z";

    public static SimpleDateFormat getDateFormat() {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        formatter.setTimeZone(BroadleafRequestContext.getBroadleafRequestContext().getTimeZone());
        return formatter;
    }
    
    /**
     * Used with dates in rules since they are not stored as a Timestamp type (and thus not converted to a specific database
     * timezone on a save). In order to provide accurate information, the timezone must also be preserved in the MVEL rule
     * expression
     * 
     * @return
     */
    public static SimpleDateFormat getTimeZoneFormat() {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_WITH_TIMEZONE);
        formatter.setTimeZone(BroadleafRequestContext.getBroadleafRequestContext().getTimeZone());
        return formatter;
    }

    /**
     * Use to produce date Strings in the W3C date format
     * 
     * @param date
     * @return
     * @throws DatatypeConfigurationException 
     */
    public static String formatDateUsingW3C(Date date) {
        String w3cDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date);
        return w3cDate = w3cDate.substring(0, 22) + ":" + w3cDate.substring(22, 24);
    }

    /**
     * Use to produce date Strings with a given timezone
     *
     * @param date     incoming date as java.util.Date
     * @param timeZone timezone to correctly represent date information
     * @return string of date
     */
    public static String dateToSting(Date date, TimeZone timeZone) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, y @ hh:mma");
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
        symbols.setAmPmStrings(new String[] { "am", "pm" });
        formatter.setDateFormatSymbols(symbols);
        formatter.setTimeZone(timeZone);
        return formatter.format(date);
    }

}
