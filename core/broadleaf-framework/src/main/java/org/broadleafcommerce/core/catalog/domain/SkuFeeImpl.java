/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.core.catalog.service.type.SkuFeeType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Phillip Verheyden
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_SKU_FEE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blProducts")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true)
})
public class SkuFeeImpl implements SkuFee {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(generator = "SkuFeeId")
    @GenericGenerator(
        name = "SkuFeeId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="SkuFeeImpl"),
            @Parameter(name = "entity_name", value = "org.broadleafcommerce.core.order.domain.SkuFeeImpl")
        }
    )
    @Column(name = "SKU_FEE_ID")
    protected Long id;

    @Column(name = "NAME")
    protected String name;
    
    @Column(name = "DESCRIPTION")
    protected String description;
    
    @Column(name ="AMOUNT", precision=19, scale=5, nullable=false)
    protected BigDecimal amount;
    
    @Column(name = "TAXABLE")
    protected Boolean taxable = Boolean.FALSE;
    
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    @Column(name = "EXPRESSION", length = Integer.MAX_VALUE - 1)
    protected String expression;

    @Column(name = "FEE_TYPE")
    @AdminPresentation(fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.catalog.service.type.SkuFeeType")
    protected String feeType = SkuFeeType.FULFILLMENT.getType();
    
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = SkuImpl.class)
    @JoinTable(name = "BLC_SKU_FEE_XREF",
            joinColumns = @JoinColumn(name = "SKU_FEE_ID", referencedColumnName = "SKU_FEE_ID", nullable = true),
            inverseJoinColumns = @JoinColumn(name = "SKU_ID", referencedColumnName = "SKU_ID", nullable = true))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    protected List<Sku> skus;
    
    @ManyToOne(targetEntity = BroadleafCurrencyImpl.class)
    @JoinColumn(name = "CURRENCY_CODE")
    @AdminPresentation(friendlyName = "TaxDetailImpl_Currency_Code", order=1, group = "FixedPriceFulfillmentOptionImpl_Details", prominent=true)
    protected BroadleafCurrency currency;




    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Money getAmount() {
        return BroadleafCurrencyUtils.getMoney(amount, getCurrency());
    }

    @Override
    public void setAmount(Money amount) {
        this.amount = Money.toAmount(amount);
    }

    @Override
    public Boolean getTaxable() {
        return taxable;
    }

    @Override
    public void setTaxable(Boolean taxable) {
        this.taxable = taxable;
    }

    @Override
    public String getExpression() {
        return expression;
    }

    @Override
    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public SkuFeeType getFeeType() {
        return SkuFeeType.getInstance(feeType);
    }

    @Override
    public void setFeeType(SkuFeeType feeType) {
        this.feeType = (feeType == null) ? null : feeType.getType();
    }
    
    @Override
    public List<Sku> getSkus() {
        return skus;
    }
    
    @Override
    public void setSkus(List<Sku> skus) {
        this.skus = skus;
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
