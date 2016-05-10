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
