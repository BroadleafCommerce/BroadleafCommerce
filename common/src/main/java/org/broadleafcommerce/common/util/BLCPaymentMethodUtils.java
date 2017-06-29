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

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.joda.time.DateTime;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Convenience class to payment method data for front-end display.
 * 
 * @author Chris Kittrell (ckittrell)
 */
public class BLCPaymentMethodUtils {

    /**
     * A helper method used to construct a list of Credit Card Expiration Months
     * Useful for expiration dropdown menus.
     * Will use locale to determine language if a locale is available.
     *
     * @return List containing expiration months of the form "01 - January"
     */
    public static List<String> getExpirationMonthOptions() {
        DateFormatSymbols dateFormatter;
        if (BroadleafRequestContext.hasLocale()) {
            Locale locale = BroadleafRequestContext.getBroadleafRequestContext().getJavaLocale();
            dateFormatter = new DateFormatSymbols(locale);
        } else {
            dateFormatter = new DateFormatSymbols();
        }
        List<String> expirationMonths = new ArrayList<>();
        NumberFormat formatter = new DecimalFormat("00");
        String[] months = dateFormatter.getMonths();
        for (int i = 1; i < months.length; i++) {
            expirationMonths.add(formatter.format(i) + " - " + months[i - 1]);
        }
        return expirationMonths;
    }

    /**
     * A helper method used to construct a list of Credit Card Expiration Years
     * Useful for expiration dropdown menus.
     *
     * @return List of the next ten years starting with the current year.
     */
    public static List<String> getExpirationYearOptions() {
        List<String> expirationYears = new ArrayList<>();
        DateTime dateTime = new DateTime();
        for (int i = 0; i < 10; i++) {
            expirationYears.add(dateTime.plusYears(i).getYear() + "");
        }
        return expirationYears;
    }
}
