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
package org.broadleafcommerce.common.money;

import java.math.BigDecimal;
import java.util.Currency;

public final class BankersRounding {

    public static final int DEFAULT_SCALE = 2;

    public static final BigDecimal ZERO = setScale(0);

    public static int getScaleForCurrency(Currency currency) {
        if (currency != null) {
            return currency.getDefaultFractionDigits();
        } else {
            return DEFAULT_SCALE;
        }
    }

    public static BigDecimal setScale(int scale, BigDecimal amount) {
        return amount.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
    }

    public static BigDecimal setScale(int scale, double amount) {
        return setScale(scale, new BigDecimal(amount));
    }

    public static double multiply(int scale, double multiplicand, double multiplier) {
        return setScale(scale, multiplicand).multiply(setScale(scale, multiplier)).doubleValue();
    }

    public static BigDecimal divide(int scale, BigDecimal dividend, BigDecimal divisor) {
        return dividend.divide(divisor, scale, BigDecimal.ROUND_HALF_EVEN);
    }

    public static double divide(int scale, double dividend, double divisor) {
        return divide(setScale(scale, dividend), setScale(scale, divisor)).doubleValue();
    }

    public static BigDecimal setScale(BigDecimal amount) {
        return setScale(DEFAULT_SCALE, amount);
    }
    
    public static BigDecimal setScale(BigDecimal amount, int scale) {
        return setScale(scale, amount);
    }

    public static BigDecimal setScale(double amount) {
        return setScale(DEFAULT_SCALE, new BigDecimal(amount));
    }

    public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
        return divide(DEFAULT_SCALE, dividend, divisor);
    }

    public static BigDecimal zeroAmount() {
        return ZERO;
    }
}
