/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.web.BroadleafRequestContext;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Convenience class to facilitate date manipulation.
 *
 * @author Chris Kittrell (ckittrell)
 */
public class BLCDateUtils {

    public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.s";
    public static final String DISPLAY_DATE_FORMAT = "MMM d, Y @ hh:mma";
    private static final Log LOG = LogFactory.getLog(BLCDateUtils.class);

    public static String convertDateToUTC(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return dateFormat.format(date);
    }

    public static String formatDateAsString(Date date) {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        TimeZone timeZone = brc.getTimeZone();

        return formatDateAsString(date, timeZone);
    }

    public static String formatDateAsString(Date date, TimeZone timeZone) {
        SimpleDateFormat formatter = new SimpleDateFormat(DISPLAY_DATE_FORMAT);
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
        symbols.setAmPmStrings(new String[]{"am", "pm"});
        formatter.setDateFormatSymbols(symbols);
        formatter.setTimeZone(timeZone);

        return formatter.format(date);
    }

    public static Date parseStringToDate(String dateString) {
        return parseStringToDate(dateString, DEFAULT_DATE_FORMAT);
    }

    public static Date parseStringToDate(String dateString, String dateFormat) {
        Date parsedDate = null;
        try {
            if (StringUtils.isNotEmpty(dateString)) {
                SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
                parsedDate = formatter.parse(dateString);
            }
        } catch (ParseException e) {
            LOG.warn("The date string could not be parsed into the given format: " + dateFormat, e);
        }
        return parsedDate;
    }

    public static String formatSimpleDate(Date date) {
        return formatDate(date, SIMPLE_DATE_FORMAT);
    }

    public static String formatDate(Date date) {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        TimeZone timeZone = brc.getTimeZone();

        return formatDate(date, DEFAULT_DATE_FORMAT, timeZone);
    }

    public static String formatDate(Date date, String format) {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        TimeZone timeZone = brc.getTimeZone();

        return formatDate(date, format, timeZone);
    }

    public static String formatDate(Date date, String format, TimeZone timeZone) {
        if (date == null) {
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(timeZone);

        return dateFormat.format(date);
    }

    /**
     * Method to solve problem with thymeleaf if you somehow have java.sql.Timestamp if will not allow call methods for this class
     *
     * @param date1 - the first date to compare
     * @param date2 - the second date to compare
     * @return result of date1.compareTo(date2)
     */
    public static int compareTo(Date date1, Date date2) {
        return date1.compareTo(date2);
    }

}
