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

import java.util.Currency;

public interface CurrencyConversionService {
    
    /**
     * Converts the given Money into the destination. The starting currency is determined by {@code source.getCurrency()}
     * 
     * @param source - the Money to convert
     * @param destinationCurrency - which Currency to convert to
     * @param destinationScale - the scale that the result will be in. If zero, this defaults to the scale of <b>source</b> 
     * and if that is zero, defaults to {@code BankersRounding.DEFAULT_SCALE}
     * @return a new Money in <b>destinationCurrency</b>. If the source and destination are the same currency, the original
     * source is returned unchanged
     */
    public Money convertCurrency(Money source, Currency destinationCurrency, int destinationScale);
    
}
