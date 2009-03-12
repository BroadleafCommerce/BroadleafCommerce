package org.broadleafcommerce.util.money;

import java.math.BigDecimal;

public final class BankersRounding {

    private static final int DEFAULT_SCALE = 2;

    private static final BigDecimal ZERO = setScale(0);

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