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
package org.broadleafcommerce.util.money;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

@SuppressWarnings("unchecked")
public final class Money implements Serializable, Cloneable, Comparable, Externalizable {

    private static final long serialVersionUID = 1L;

    private BigDecimal amount;

    private final Currency currency;

    public Money() {
        this(BankersRounding.zeroAmount(), defaultCurrency());
    }

    public Money(BigDecimal amount) {
        this(amount, defaultCurrency());
    }

    public Money(double amount) {
        this(valueOf(amount), defaultCurrency());
    }

    public Money(int amount) {
        this(BigDecimal.valueOf(amount), defaultCurrency());
    }

    public Money(long amount) {
        this(BigDecimal.valueOf(amount), defaultCurrency());
    }

    public Money(String amount) {
        this(new BigDecimal(amount), defaultCurrency());
    }

    public Money(BigDecimal amount, String currencyCode) {
        this(amount, Currency.getInstance(currencyCode));
    }

    public Money(double amount, Currency currency) {
        this(valueOf(amount), currency);
    }

    public Money(double amount, String currencyCode) {
        this(valueOf(amount), Currency.getInstance(currencyCode));
    }

    public Money(int amount, Currency currency) {
        this(BigDecimal.valueOf(amount), currency);
    }

    public Money(int amount, String currencyCode) {
        this(BigDecimal.valueOf(amount), Currency.getInstance(currencyCode));
    }

    public Money(long amount, Currency currency) {
        this(BigDecimal.valueOf(amount), currency);
    }

    public Money(long amount, String currencyCode) {
        this(BigDecimal.valueOf(amount), Currency.getInstance(currencyCode));
    }

    public Money(String amount, Currency currency) {
        this(new BigDecimal(amount), currency);
    }

    public Money(String amount, String currencyCode) {
        this(new BigDecimal(amount), Currency.getInstance(currencyCode));
    }

    public Money(BigDecimal amount, Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException("currency cannot be null");
        }
        this.currency = currency;
        this.amount = BankersRounding.setScale(amount);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Money add(Money other) {
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        return new Money(amount.subtract(other.amount), currency);
    }

    public Money multiply(double amount) {
        return multiply(valueOf(amount));
    }

    public Money multiply(int amount) {
        return multiply(BigDecimal.valueOf(amount));
    }

    public Money multiply(BigDecimal multiplier) {
        return new Money(amount.multiply(multiplier), currency);
    }

    public Money divide(double amount) {
        return divide(valueOf(amount));
    }

    public Money divide(int amount) {
        return divide(BigDecimal.valueOf(amount));
    }

    public Money divide(BigDecimal divisor) {
        return new Money(BankersRounding.divide(amount, divisor), currency);
    }

    public Money abs() {
        return new Money(amount.abs(), currency);
    }

    public Money min(Money other) {
        if (other == null) { return this; }
        return lessThan(other) ? this : other;
    }

    public Money max(Money other) {
        if (other == null) { return this; }
        return greaterThan(other) ? this : other;
    }

    public Money negate() {
        return new Money(amount.negate(), currency);
    }

    public boolean isZero() {
        return amount.compareTo(BankersRounding.zeroAmount()) == 0;
    }

    public Money zero() {
        return Money.zero(currency);
    }

    public boolean lessThan(Money other) {
        return compareTo(other) < 0;
    }

    public boolean lessThan(BigDecimal value) {
        return amount.compareTo(value) < 0;
    }

    public boolean lessThanOrEqual(Money other) {
        return compareTo(other) <= 0;
    }

    public boolean lessThanOrEqual(BigDecimal value) {
        return amount.compareTo(value) <= 0;
    }

    public boolean greaterThan(Money other) {
        return compareTo(other) > 0;
    }

    public boolean greaterThan(BigDecimal value) {
        return amount.compareTo(value) > 0;
    }

    public boolean greaterThanOrEqual(Money other) {
        return compareTo(other) >= 0;
    }

    public boolean greaterThanOrEqual(BigDecimal value) {
        return amount.compareTo(value) >= 0;
    }

    public int compareTo(Money other) {
        return amount.compareTo(other.amount);
    }

    public int compareTo(BigDecimal value) {
        return amount.compareTo(value);
    }

    public int compareTo(Object other) {
        Money otherMoney = (Money) other;
        return compareTo(otherMoney);
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Money))
            return false;

        Money money = (Money) o;

        if (amount != null ? !amount.equals(money.amount) : money.amount != null)
            return false;
        if (currency != null ? !currency.equals(money.currency) : money.currency != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result = amount != null ? amount.hashCode() : 0;
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        return result;
    }

    public Object clone() {
        return new Money(amount, currency);
    }

    public String toString() {
        return amount.toString();
    }

    public double doubleValue() {
        try {
            return amount.doubleValue();
        } catch (NumberFormatException e) {
            // HotSpot bug in JVM < 1.4.2_06.
            if (e.getMessage().equals("For input string: \"0.00null\"")) {
                return amount.doubleValue();
            } else {
                throw e;
            }
        }
    }

    public String stringValue() {
        return amount.toString() + " " + currency.getCurrencyCode();
    }

    public static Money zero(String currencyCode) {
        return zero(Currency.getInstance(currencyCode));
    }

    public static Money zero(Currency currency) {
        return new Money(BankersRounding.zeroAmount(), currency);
    }

    public static Money abs(Money money) {
        return new Money(money.amount.abs(), money.currency);
    }

    public static Money min(Money left, Money right) {
        return left.min(right);
    }

    public static Money max(Money left, Money right) {
        return left.max(right);
    }

    public static BigDecimal toAmount(Money money) {
        return ((money == null) ? null : money.amount);
    }

    public static Currency toCurrency(Money money) {
        return ((money == null) ? null : money.currency);
    }

    /**
     * Ensures predictable results by converting the double into a string then calling the BigDecimal string constructor.
     * @param amount The amount
     * @return BigDecimal a big decimal with a predictable value
     */
    private static BigDecimal valueOf(double amount) {
        return new BigDecimal(String.valueOf(amount));
    }

    /**
     * Attempts to load a default currency by using the default locale. {@link Currency#getInstance(Locale)} uses the country component of the locale to resolve the currency. In some instances, the locale may not have a country component, in which case the default currency can be controlled with a
     * system property.
     * @return The default currency to use when none is specified
     */
    private static Currency defaultCurrency() {
        Locale locale = Locale.getDefault();
        if (locale.getCountry() != null && locale.getCountry().length() == 2) {
            return Currency.getInstance(locale);
        }
        return Currency.getInstance(System.getProperty("currency.default", "USD"));
    }

    public void readExternal(ObjectInput in) throws IOException,ClassNotFoundException {
        // Read in the server properties from the client representation.
        amount = new BigDecimal( in.readFloat());

    }

    public void writeExternal(ObjectOutput out) throws IOException {
        // Write out the client properties from the server representation.
        out.writeFloat(amount.floatValue());
        // out.writeObject(currency);
    }
}
