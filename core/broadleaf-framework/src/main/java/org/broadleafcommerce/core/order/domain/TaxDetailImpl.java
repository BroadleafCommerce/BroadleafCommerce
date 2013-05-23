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

import org.broadleafcommerce.common.config.domain.AbstractModuleConfiguration;
import org.broadleafcommerce.common.config.domain.ModuleConfiguration;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_TAX_DETAIL")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blOrderElements")
@AdminPresentationClass(friendlyName = "TaxDetailImpl_baseTaxDetail")
public class TaxDetailImpl implements TaxDetail {
    
    private static final long serialVersionUID = -4036994446393527252L;

    @Id
    @GeneratedValue(generator = "TaxDetailId")
    @GenericGenerator(
        name="TaxDetailId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="TaxDetailImpl"),
            @Parameter(name="increment_size", value="150"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.TaxDetailImpl")
        }
    )
    @Column(name = "TAX_DETAIL_ID")
    protected Long id;
    
    @Column(name = "TYPE")
    @AdminPresentation(friendlyName = "TaxDetailImpl_Tax_Type", order=1, group = "TaxDetailImpl_Tax_Detail")
    protected String type;
    
    @Column(name = "AMOUNT", precision=19, scale=5)
    @AdminPresentation(friendlyName = "TaxDetailImpl_Tax_Amount", order=2, group = "TaxDetailImpl_Tax_Detail")
    protected BigDecimal amount;
    
    @Column(name = "RATE", precision=19, scale=5)
    @AdminPresentation(friendlyName = "TaxDetailImpl_Tax_Rate", order=1, group = "TaxDetailImpl_Tax_Detail")
    protected BigDecimal rate;
    
    @ManyToOne(targetEntity = BroadleafCurrencyImpl.class)
    @JoinColumn(name = "CURRENCY_CODE")
    @AdminPresentation(friendlyName = "TaxDetailImpl_Currency_Code", order=1, group = "FixedPriceFulfillmentOptionImpl_Details", prominent=true)
    protected BroadleafCurrency currency;

    @ManyToOne(targetEntity = AbstractModuleConfiguration.class)
    @JoinColumn(name = "MODULE_CONFIG_ID")
    protected ModuleConfiguration moduleConfiguation;
    
    public TaxDetailImpl() {
        
    }
    
    public TaxDetailImpl(TaxType type, Money amount, BigDecimal rate) {
        this.type = type.getType();
        this.amount = amount.getAmount();
        this.rate = rate;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public TaxType getType() {
        return TaxType.getInstance(this.type);
    }

    @Override
    public void setType(TaxType type) {
        this.type = type.getType();
    }

    @Override
    public Money getAmount() {
        return BroadleafCurrencyUtils.getMoney(amount, currency);
    }

    @Override
    public void setAmount(Money amount) {
        this.amount = amount.getAmount();
    }

    @Override
    public BigDecimal getRate() {
        return rate;
    }

    @Override
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public BroadleafCurrency getCurrency() {
        return currency;
    }

    @Override
    public void setCurrency(BroadleafCurrency currency) {
        this.currency = currency;
    }

    @Override
    public ModuleConfiguration getModuleConfiguration() {
        return this.moduleConfiguation;
    }

    @Override
    public void setModuleConfiguration(ModuleConfiguration config) {
        this.moduleConfiguation = config;
    }
    
}
