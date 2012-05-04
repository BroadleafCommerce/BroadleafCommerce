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

package org.broadleafcommerce.core.order.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_TAX_DETAIL")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(friendlyName = "baseTaxDetail")
public class TaxDetailImpl implements TaxDetail {
    
    private static final long serialVersionUID = -4036994446393527252L;

    @Id
    @GeneratedValue(generator = "TaxDetailId")
    @GenericGenerator(
        name="TaxDetailId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="table_name", value="SEQUENCE_GENERATOR"),
            @Parameter(name="segment_column_name", value="ID_NAME"),
            @Parameter(name="value_column_name", value="ID_VAL"),
            @Parameter(name="segment_value", value="TaxDetailImpl"),
            @Parameter(name="increment_size", value="50"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.catalog.domain.TaxDetailImpl")
        }
    )
    @Column(name = "TAX_DETAIL_ID")
    protected Long id;
    
    @Column(name = "TYPE")
    @AdminPresentation(friendlyName="TaxDetailImpl_Tax_Type", order=1, group="Tax Detail")
    protected TaxType type;
    
    @Column(name = "AMOUNT")
    @AdminPresentation(friendlyName="TaxDetailImpl_Tax_Amount", order=2, group="Tax Detail")
    protected Money amount;
    
    @Column(name = "RATE")
    @AdminPresentation(friendlyName="TaxDetailImpl_Tax_Rate", order=1, group="Tax Detail")
    protected BigDecimal rate;
    
    public TaxDetailImpl() {
        
    }
    
    public TaxDetailImpl(TaxType type, Money amount, BigDecimal rate) {
        this.type = type;
        this.amount = amount;
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
        return type;
    }

    @Override
    public void setType(TaxType type) {
        this.type = type;
    }

    @Override
    public Money getAmount() {
        return amount;
    }

    @Override
    public void setAmount(Money amount) {
        this.amount = amount;
    }

    @Override
    public BigDecimal getRate() {
        return rate;
    }

    @Override
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
    
}
