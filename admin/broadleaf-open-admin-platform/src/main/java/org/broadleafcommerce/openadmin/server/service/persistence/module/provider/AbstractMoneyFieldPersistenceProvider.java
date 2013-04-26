/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Abstract persistence provider that provides a method to actually handle formatting moneys.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public abstract class AbstractMoneyFieldPersistenceProvider extends PersistenceProviderAdapter {
    
    public String getFormattedDisplayValue(BigDecimal value, Locale locale, Currency currency) {
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        format.setCurrency(currency);
        return format.format(value);
    }
    
    public String getFormattedValue(BigDecimal value) {
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        return format.format(value);
    }
    
}
