/*
 * Copyright 2012 the original author or authors.
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

package org.broadleafcommerce.core.order.fulfillment.domain;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.core.order.domain.FulfillmentOptionImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 
 * @author Phillip Verheyden
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FULFILLMENT_OPTION_FIXED")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(friendlyName = "Fixed Price Fulfillment")
public class FixedPriceFulfillmentOptionImpl extends FulfillmentOptionImpl implements FixedPriceFulfillmentOption {

    private static final long serialVersionUID = 1L;

    @Column(name = "PRICE", precision=19, scale=5, nullable=false)
    protected BigDecimal price;
    
    @ManyToOne(targetEntity = BroadleafCurrencyImpl.class)
    @JoinColumn(name = "CURRENCY_CODE")
    @AdminPresentation(excluded = true)
    protected BroadleafCurrency currency;

    @Override
    public Money getPrice() {
        return price == null ? null : BroadleafCurrencyUtils.getMoney(price, getCurrency());
    }

    @Override
    public void setPrice(Money price) {
        this.price = Money.toAmount(price);
    }

    @Override
    public BroadleafCurrency getCurrency() {
        return currency;
    }

    @Override
    public void setCurrency(BroadleafCurrency currency) {
        this.currency = currency;
    }

}
