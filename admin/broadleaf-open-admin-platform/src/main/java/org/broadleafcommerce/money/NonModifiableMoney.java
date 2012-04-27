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
package org.broadleafcommerce.money;

import org.broadleafcommerce.money.Money;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Immutable class for easy money comparisons.
 * Created by bpolster.
 */
public class NonModifiableMoney extends Money {


    public NonModifiableMoney() {
        super();
    }

    public NonModifiableMoney(BigDecimal amount) {
        super(amount);
    }

    public NonModifiableMoney(double amount) {
        super(amount);
    }

    public NonModifiableMoney(int amount) {
        super(amount);
    }

    public NonModifiableMoney(long amount) {
        super(amount);
    }

    public NonModifiableMoney(BigDecimal amount, String currencyCode) {
        super(amount, currencyCode);
    }

    public NonModifiableMoney(double amount, Currency currency) {
        super(amount, currency);
    }

    public NonModifiableMoney(double amount, String currencyCode) {
        super(amount, currencyCode);
    }

    public NonModifiableMoney(int amount, Currency currency) {
        super(amount, currency);
    }

    public NonModifiableMoney(int amount, String currencyCode) {
        super(amount, currencyCode);
    }

    public NonModifiableMoney(long amount, Currency currency) {
        super(amount, currency);
    }

    public NonModifiableMoney(long amount, String currencyCode) {
        super(amount, currencyCode);
    }

    public NonModifiableMoney(String amount, Currency currency) {
        super(amount, currency);
    }

    public NonModifiableMoney(String amount, String currencyCode) {
        super(amount, currencyCode);
    }

    public NonModifiableMoney(BigDecimal amount, Currency currency) {
        super(amount, currency);
    }

    public NonModifiableMoney(BigDecimal amount, Currency currency, int scale) {
        super(amount, currency, scale);
    }

    @Override
    public Money add(Money other) {
        throw new UnsupportedOperationException("Operation attempted on NonModifiableMoney");
    }

    @Override
    public Money subtract(Money other) {
        throw new UnsupportedOperationException("Operation attempted on NonModifiableMoney");
    }

    @Override
    public Money multiply(double amount) {
        throw new UnsupportedOperationException("Operation attempted on NonModifiableMoney");
    }

    @Override
    public Money multiply(int amount) {
        throw new UnsupportedOperationException("Operation attempted on NonModifiableMoney");
    }

    @Override
    public Money multiply(BigDecimal multiplier) {
        throw new UnsupportedOperationException("Operation attempted on NonModifiableMoney");
    }

    @Override
    public Money divide(double amount) {
        throw new UnsupportedOperationException("Operation attempted on NonModifiableMoney");
    }

    @Override
    public Money divide(int amount) {
        throw new UnsupportedOperationException("Operation attempted on NonModifiableMoney");
    }

    @Override
    public Money divide(BigDecimal divisor) {
        throw new UnsupportedOperationException("Operation attempted on NonModifiableMoney");
    }
}
