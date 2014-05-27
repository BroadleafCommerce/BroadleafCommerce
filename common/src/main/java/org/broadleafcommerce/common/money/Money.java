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
package org.broadleafcommerce.common.money;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.util.CurrencyAdapter;
import org.broadleafcommerce.common.util.xml.BigDecimalRoundingAdapter;
import org.broadleafcommerce.common.web.BroadleafRequestContext;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class Money implements Serializable, Cloneable, Comparable<Money>, Externalizable {
    
    private static final long serialVersionUID = 1L;

    @XmlElement
    @XmlJavaTypeAdapter(value = BigDecimalRoundingAdapter.class)
    private BigDecimal amount;

    @XmlElement
    @XmlJavaTypeAdapter(CurrencyAdapter.class)
    private final Currency currency;
    
    public static final Money ZERO = new Money(BigDecimal.ZERO);

    protected static String getCurrencyCode(BroadleafCurrency blCurrency) {
        if (blCurrency != null) {
            return blCurrency.getCurrencyCode();
        } else {
            return defaultCurrency().getCurrencyCode();
        }
    }

    public Money(Currency currency) {
        this(BankersRounding.zeroAmount(), currency);
    }

    public Money(BroadleafCurrency blCurrency) {
        this(0, Currency.getInstance(getCurrencyCode(blCurrency)));
    }

    public Money(BigDecimal amount, BroadleafCurrency blCurrency) {
        this(amount, Currency.getInstance(getCurrencyCode(blCurrency)));
    }

    public Money(BigDecimal amount, BroadleafCurrency blCurrency, int scale) {
        this(amount, Currency.getInstance(getCurrencyCode(blCurrency)), scale);
    }

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
        this(BigDecimal.valueOf(amount).setScale(BankersRounding.getScaleForCurrency(defaultCurrency()),
                RoundingMode.HALF_EVEN), defaultCurrency());
    }

    public Money(long amount) {
        this(BigDecimal.valueOf(amount).setScale(BankersRounding.getScaleForCurrency(defaultCurrency()),
                RoundingMode.HALF_EVEN), defaultCurrency());
    }

    public Money(String amount) {
        this(valueOf(amount), defaultCurrency());
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
        this(BigDecimal.valueOf(amount).setScale(BankersRounding.getScaleForCurrency(currency), RoundingMode.HALF_EVEN), currency);
    }

    public Money(int amount, String currencyCode) {
        this(BigDecimal.valueOf(amount).setScale(BankersRounding.getScaleForCurrency(Currency.getInstance(currencyCode)), RoundingMode.HALF_EVEN), Currency.getInstance(currencyCode));
    }

    public Money(long amount, Currency currency) {
        this(BigDecimal.valueOf(amount).setScale(BankersRounding.getScaleForCurrency(currency), RoundingMode.HALF_EVEN), currency);
    }

    public Money(long amount, String currencyCode) {
        this(BigDecimal.valueOf(amount).setScale(BankersRounding.getScaleForCurrency(Currency.getInstance(currencyCode)), RoundingMode.HALF_EVEN), Currency.getInstance(currencyCode));
    }

    public Money(String amount, Currency currency) {
        this(valueOf(amount), currency);
    }

    public Money(String amount, String currencyCode) {
        this(valueOf(amount), Currency.getInstance(currencyCode));
    }

    public Money(BigDecimal amount, Currency currency) {
        if (currency == null) {
            throw new IllegalArgumentException("currency cannot be null");
        }
        this.currency = currency;
        this.amount = BankersRounding.setScale(BankersRounding.getScaleForCurrency(currency), amount);
    }
    
    public Money(BigDecimal amount, Currency currency, int scale) {
        if (currency == null) {
            throw new IllegalArgumentException("currency cannot be null");
        }
        this.currency = currency;
        this.amount = BankersRounding.setScale(amount, scale);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Money add(Money other) {
        if (!other.getCurrency().equals(getCurrency())) {
            if (
                CurrencyConversionContext.getCurrencyConversionContext() != null &&
                CurrencyConversionContext.getCurrencyConversionContext().size() > 0 &&
                CurrencyConversionContext.getCurrencyConversionService() != null
                ) {
                other = CurrencyConversionContext.getCurrencyConversionService().convertCurrency(other, getCurrency(), amount.scale());
            } else {
                if (this == Money.ZERO) {
                    return other;
                } else if (other == Money.ZERO) {
                    return this;
                }
                throw new UnsupportedOperationException("No currency conversion service is registered, cannot add different currency " +
                        "types together (" + getCurrency().getCurrencyCode() + " " + other.getCurrency().getCurrencyCode() + ")");
            }
        }
        
        return new Money(amount.add(other.amount), currency, amount.scale() == 0 ? BankersRounding.getScaleForCurrency(currency) : amount.scale());
    }

    public Money subtract(Money other) {
        if (!other.getCurrency().equals(getCurrency())) {
            if (
                CurrencyConversionContext.getCurrencyConversionContext() != null &&
                CurrencyConversionContext.getCurrencyConversionContext().size() > 0 &&
                CurrencyConversionContext.getCurrencyConversionService() != null
                ) {
                other = CurrencyConversionContext.getCurrencyConversionService().convertCurrency(other, getCurrency(), amount.scale());
            } else {
                if (other == Money.ZERO) {
                    return this;
                } else if (this == Money.ZERO) {
                    return new Money(amount.subtract(other.amount), other.getCurrency(), amount.scale() == 0
                            ? BankersRounding.getScaleForCurrency(other.getCurrency()) : amount.scale());
                }
                throw new UnsupportedOperationException("No currency conversion service is registered, cannot subtract different currency " +
                        "types (" + getCurrency().getCurrencyCode() + ", " + other.getCurrency().getCurrencyCode() + ")");
            }
        }
        
        return new Money(amount.subtract(other.amount), currency, amount.scale() == 0 ? BankersRounding.getScaleForCurrency(currency) : amount.scale());
    }

    public Money multiply(double amount) {
        return multiply(valueOf(amount));
    }

    public Money multiply(int amount) {
        BigDecimal value = BigDecimal.valueOf(amount);
        value = value.setScale(BankersRounding.getScaleForCurrency(currency), RoundingMode.HALF_EVEN);
        return multiply(value);
    }

    public Money multiply(BigDecimal multiplier) {
        return new Money(amount.multiply(multiplier), currency, amount.scale() == 0 ? BankersRounding.getScaleForCurrency(currency) : amount.scale());
    }

    public Money divide(double amount) {
        return this.divide(amount, RoundingMode.HALF_EVEN);
    }

    public Money divide(double amount, RoundingMode roundingMode) {
        return divide(valueOf(amount), roundingMode);
    }

    public Money divide(int amount) {
        return this.divide(amount, RoundingMode.HALF_EVEN);
    }

    public Money divide(int amount, RoundingMode roundingMode) {
        BigDecimal value = BigDecimal.valueOf(amount);
        value = value.setScale(BankersRounding.getScaleForCurrency(currency), RoundingMode.HALF_EVEN);
        return divide(value, roundingMode);
    }

    public Money divide(BigDecimal divisor) {
        return this.divide(divisor, RoundingMode.HALF_EVEN);
    }

    public Money divide(BigDecimal divisor, RoundingMode roundingMode) {
        return new Money(amount.divide(divisor, amount.scale(), roundingMode), currency, amount.scale() == 0 ? BankersRounding.getScaleForCurrency(currency) : amount.scale());
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

    @Override
    public int compareTo(Money other) {
        return amount.compareTo(other.amount);
    }

    public int compareTo(BigDecimal value) {
        return amount.compareTo(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) return false;
        if (!getClass().isAssignableFrom(o.getClass())) {
            return false;
        }

        Money money = (Money) o;

        if (amount != null ? !amount.equals(money.amount) : money.amount != null) {
            return false;
        }

        if (isZero()) {
            return true;
        }

        if (currency != null ? !currency.equals(money.currency) : money.currency != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = amount != null ? amount.hashCode() : 0;
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        return result;
    }

    @Override
    public Object clone() {
        return new Money(amount, currency);
    }

    @Override
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
        return valueOf(String.valueOf(amount));
    }
    
    private static BigDecimal valueOf(String amount) {
        BigDecimal value = new BigDecimal(amount);
        if (value.scale() < 2) {
            value = value.setScale(BankersRounding.getScaleForCurrency(defaultCurrency()), RoundingMode.HALF_EVEN);
        }
        
        return value;
    }

    /**
     * Attempts to load a default currency by using the default locale. {@link Currency#getInstance(Locale)} uses the country component of the locale to resolve the currency. In some instances, the locale may not have a country component, in which case the default currency can be controlled with a
     * system property.
     * @return The default currency to use when none is specified
     */
    public static Currency defaultCurrency() {
        if (
            CurrencyConsiderationContext.getCurrencyConsiderationContext() != null &&
            CurrencyConsiderationContext.getCurrencyConsiderationContext().size() > 0 &&
            CurrencyConsiderationContext.getCurrencyDeterminationService() != null
        ) {
            return Currency.getInstance(CurrencyConsiderationContext.getCurrencyDeterminationService().getCurrencyCode(CurrencyConsiderationContext.getCurrencyConsiderationContext()));
        }

        // Check the BLC Thread
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();

        if (brc != null && brc.getBroadleafCurrency() != null) {
            assert brc.getBroadleafCurrency().getCurrencyCode()!=null;
            return Currency.getInstance(brc.getBroadleafCurrency().getCurrencyCode());
        }
        if (System.getProperty("currency.default") != null) {
            return Currency.getInstance(System.getProperty("currency.default"));
        }
        Locale locale = Locale.getDefault();
        if (locale.getCountry() != null && locale.getCountry().length() == 2) {
            return Currency.getInstance(locale);
        }
        return Currency.getInstance("USD");
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,ClassNotFoundException {
        // Read in the server properties from the client representation.
        amount = new BigDecimal( in.readFloat());

    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        // Write out the client properties from the server representation.
        out.writeFloat(amount.floatValue());
        // out.writeObject(currency);
    }
    
}
