/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.catalog.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.persistence.IdOverrideTableGenerator;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.core.catalog.service.type.SkuFeeType;
import org.hibernate.Length;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.descriptor.jdbc.LongVarcharJdbcType;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * @author Phillip Verheyden
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SKU_FEE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blProductRelationships")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps = true)
})
public class SkuFeeImpl implements SkuFee {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SkuFeeId")
    @GenericGenerator(
            name = "SkuFeeId",
            type = IdOverrideTableGenerator.class,
            parameters = {
                    @Parameter(name = "segment_value", value = "SkuFeeImpl"),
                    @Parameter(name = "entity_name", value = "org.broadleafcommerce.core.order.domain.SkuFeeImpl")
            }
    )
    @Column(name = "SKU_FEE_ID")
    protected Long id;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "DESCRIPTION")
    protected String description;

    @Column(name = "AMOUNT", precision = 19, scale = 5, nullable = false)
    protected BigDecimal amount;

    @Column(name = "TAXABLE")
    protected Boolean taxable = Boolean.FALSE;

    @Lob
    @JdbcType(LongVarcharJdbcType.class)
    @Column(name = "EXPRESSION", length = Length.LONG32 - 1)
    protected String expression;

    @Column(name = "FEE_TYPE")
    @AdminPresentation(fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.core.catalog.service.type.SkuFeeType")
    protected String feeType = SkuFeeType.FULFILLMENT.getType();

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = SkuImpl.class)
    @JoinTable(name = "BLC_SKU_FEE_XREF",
            joinColumns = @JoinColumn(name = "SKU_FEE_ID", referencedColumnName = "SKU_FEE_ID", nullable = true),
            inverseJoinColumns = @JoinColumn(name = "SKU_ID", referencedColumnName = "SKU_ID", nullable = true))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blProductRelationships")
    protected List<Sku> skus;

    @ManyToOne(targetEntity = BroadleafCurrencyImpl.class)
    @JoinColumn(name = "CURRENCY_CODE")
    @AdminPresentation(friendlyName = "TaxDetailImpl_Currency_Code", order = 1,
            group = "FixedPriceFulfillmentOptionImpl_Details", prominent = true)
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

    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }
        if (obj == null || !getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        SkuFeeImpl rhs = (SkuFeeImpl) obj;
        return new EqualsBuilder()
                .append(this.id, rhs.id)
                .append(this.name, rhs.name)
                .append(this.description, rhs.description)
                .append(this.amount, rhs.amount)
                .append(this.taxable, rhs.taxable)
                .append(this.expression, rhs.expression)
                .append(this.feeType, rhs.feeType)
                .append(this.skus, rhs.skus)
                .append(this.currency, rhs.currency)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(name)
                .append(description)
                .append(amount)
                .append(taxable)
                .append(expression)
                .append(feeType)
                .append(skus)
                .append(currency)
                .toHashCode();
    }

}
