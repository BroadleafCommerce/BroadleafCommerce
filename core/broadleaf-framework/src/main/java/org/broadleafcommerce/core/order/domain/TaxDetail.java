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

package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The Interface TaxDetail. A TaxDetail object stores relevant tax information 
 * including a tax type, amount, and rate.
 *
 */
public interface TaxDetail extends Serializable {

    /**
     * Gets the id.
     * 
     * @return the id
     */
    Long getId();

    /**
     * Sets the id.
     * 
     * @param id the new id
     */
    void setId(Long id);
    
    /**
     * Gets the tax type
     * 
     * @return the tax type
     */
    TaxType getType();

    /**
     * Sets the tax type
     * 
     * @param type the tax type
     */
    void setType(TaxType type);

    /**
     * Gets the tax amount
     * 
     * @return the tax amount
     */
    Money getAmount();

    /**
     * Sets the tax amount
     * 
     * @param amount the tax amount
     */
    void setAmount(Money amount);   

    /**
     * Gets the tax rate
     * 
     * @return the rate
     */
    BigDecimal getRate();

    /**
     * Sets the tax rate.
     * 
     * @param name the tax rate
     */
    void setRate(BigDecimal rate);
    
    public BroadleafCurrency getCurrency();

    public void setCurrency(BroadleafCurrency currency);
}
