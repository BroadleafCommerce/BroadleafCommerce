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
package org.broadleafcommerce.common.currency.util;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;


/**
 * Utility methods for common currency operations
 * 
 * @author Phillip Verheyden
 * @see {@link BroadleafCurrency}
 */
public class BroadleafCurrencyUtils {

    public static final MathContext ROUND_FLOOR_MATH_CONTEXT = new MathContext(0, RoundingMode.FLOOR);

    public static Money getMoney(BigDecimal amount, BroadleafCurrency currency) {
        if (amount == null) {
            return null;
        }
        
        if (currency != null) {
            return new Money(amount, currency.getCurrencyCode());
        } else {
            return new Money(amount);
        }
    }

    public static Money getMoney(BroadleafCurrency currency) {
        if (currency != null) {
            return new Money(0,currency.getCurrencyCode());
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
     * @param currency
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
     * @param currency
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
     * @param currency
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
}
