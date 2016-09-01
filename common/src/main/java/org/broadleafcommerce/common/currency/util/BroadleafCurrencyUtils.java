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
package org.broadleafcommerce.common.currency.util;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Utility methods for common currency operations
 *
 * @author Phillip Verheyden
 * @see {@link BroadleafCurrency}
 */
public class BroadleafCurrencyUtils {

    protected static final Map<String, NumberFormat> FORMAT_CACHE = new ConcurrentHashMap<String, NumberFormat>();

    public static final MathContext ROUND_FLOOR_MATH_CONTEXT = new MathContext(0, RoundingMode.FLOOR);

    public static Money getMoney(BigDecimal amount, BroadleafCurrency currency) {
        if (amount == null) {
            return null;
        }

        if (currency != null) {
            return new Money(amount, currency.getCurrencyCode());
        } else {
            return new Money(amount, Money.defaultCurrency());
        }
    }

    public static Money getMoney(BigDecimal amount) {
        return getMoney(amount, null);
    }

    public static Money getMoney(BroadleafCurrency currency) {
        if (currency != null) {
            return new Money(0, currency.getCurrencyCode());
        } else {
            return new Money();
        }
    }

    public static Currency getCurrency(Money money) {
        if (money == null) {
            return Money.defaultCurrency();
        }
        return (money.getCurrency() == null) ? Money.defaultCurrency() : money.getCurrency();
    }

    public static Currency getCurrency(BroadleafCurrency currency) {
        return (currency == null) ? Money.defaultCurrency() : Currency.getInstance(currency.getCurrencyCode());
    }

    /**
     * Returns the unit amount (e.g. .01 for US and all other 2 decimal currencies)
     *
     * @param difference
     * @return
     */
    public static Money getUnitAmount(Money difference) {
        Currency currency = BroadleafCurrencyUtils.getCurrency(difference);
        BigDecimal divisor = new BigDecimal(Math.pow(10, currency.getDefaultFractionDigits()));
        BigDecimal unitAmount = new BigDecimal("1").divide(divisor);

        if (difference.lessThan(BigDecimal.ZERO)) {
            unitAmount = unitAmount.negate();
        }
        return new Money(unitAmount, currency);
    }

    /**
     * Returns the unit amount (e.g. .01 for US and all other 2 decimal currencies)
     *
     * @param blCurrency
     * @return
     */
    public static Money getUnitAmount(BroadleafCurrency blCurrency) {
        Currency currency = getCurrency(blCurrency);
        BigDecimal divisor = new BigDecimal(Math.pow(10, currency.getDefaultFractionDigits()));
        BigDecimal unitAmount = new BigDecimal("1").divide(divisor);
        return new Money(unitAmount, currency);
    }

    /**
     * Returns the remainder amount if the passed in totalAmount was divided by the
     * quantity taking into account the normal unit of the currency (e.g. .01 for US).
     *
     * @param totalAmount
     * @param quantity
     * @return
     */
    public static int calculateRemainder(Money totalAmount, int quantity) {
        if (totalAmount == null || totalAmount.isZero() || quantity == 0) {
            return 0;
        }

        // Use this to convert to a whole number (e.g. 1.05 becomes 105 in US currency).
        BigDecimal multiplier = new BigDecimal(10).pow(totalAmount.getAmount().scale());
        BigDecimal amount = totalAmount.getAmount().multiply(multiplier);

        BigDecimal remainder = amount.remainder(new BigDecimal(quantity), ROUND_FLOOR_MATH_CONTEXT);
        return remainder.toBigInteger().intValue();
    }

    /**
     * Provides a cached approach for creating NumberFormat instances. More performant
     * than creating a new one each time.
     *
     * @param locale   the Locale
     * @param currency the Currency
     * @return either a new NumberFormat instance, or one taken from the cache
     */
    public static NumberFormat getNumberFormatFromCache(Locale locale, Currency currency) {
        String key = locale.toString() + currency.getCurrencyCode();
        if (!FORMAT_CACHE.containsKey(key)) {
            NumberFormat format = NumberFormat.getCurrencyInstance(locale);
            format.setCurrency(currency);
            FORMAT_CACHE.put(key, format);
        }
        return FORMAT_CACHE.get(key);
    }
}
