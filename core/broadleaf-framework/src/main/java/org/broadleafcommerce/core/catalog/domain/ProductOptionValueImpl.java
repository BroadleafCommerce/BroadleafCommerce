/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.i18n.service.DynamicTranslationProvider;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuPrices;
import org.broadleafcommerce.core.catalog.service.dynamic.SkuPricingConsiderationContext;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
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
@Table(name = "BLC_PRODUCT_OPTION_VALUE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blProducts")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class ProductOptionValueImpl implements ProductOptionValue, ProductOptionValueAdminPresentation {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "ProductOptionValueId")
    @GenericGenerator(
        name = "ProductOptionValueId",
        strategy = "org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
                @Parameter(name = "segment_value", value = "ProductOptionValueImpl"),
                @Parameter(name = "entity_name", value = "org.broadleafcommerce.core.catalog.domain.ProductOptionValueImpl")
        })
    @Column(name = "PRODUCT_OPTION_VALUE_ID")
    protected Long id;

    @Column(name = "ATTRIBUTE_VALUE")
    @AdminPresentation(friendlyName = "productOptionValue_attributeValue", 
            prominent = true, order = FieldOrder.value,
            translatable = true, gridOrder = FieldOrder.value,
            group = GroupName.General)
    protected String attributeValue;

    @Column(name = "DISPLAY_ORDER")
    @AdminPresentation(friendlyName = "productOptionValue_displayOrder", prominent = true,
            gridOrder =FieldOrder.order, order = FieldOrder.order,
            group = GroupName.General)
    protected Long displayOrder;

    @Column(name = "PRICE_ADJUSTMENT", precision = 19, scale = 5)
    @AdminPresentation(friendlyName = "productOptionValue_adjustment", fieldType = SupportedFieldType.MONEY,
            prominent = true, gridOrder = FieldOrder.adjustment, order = FieldOrder.adjustment,
            group = GroupName.General)
    protected BigDecimal priceAdjustment;

    @ManyToOne(targetEntity = ProductOptionImpl.class, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "PRODUCT_OPTION_ID")
    protected ProductOption productOption;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getAttributeValue() {
        return DynamicTranslationProvider.getValue(this, "attributeValue", attributeValue);
    }

    @Override
    public String getRawAttributeValue() {
        return attributeValue;
    }

    @Override
    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    @Override
    public Long getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void setDisplayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
    }

    @Override
    public Money getPriceAdjustment() {

        Money returnPrice = null;

        if (SkuPricingConsiderationContext.hasDynamicPricing()) {

            DynamicSkuPrices dynamicPrices = SkuPricingConsiderationContext.getSkuPricingService().getPriceAdjustment(this, priceAdjustment == null ? null : new Money(priceAdjustment), SkuPricingConsiderationContext.getSkuPricingConsiderationContext());
            returnPrice = dynamicPrices.getPriceAdjustment();

        } else {
            if (priceAdjustment != null) {
                returnPrice = new Money(priceAdjustment, Money.defaultCurrency());
            }
        }

        return returnPrice;
    }

    @Override
    public void setPriceAdjustment(Money priceAdjustment) {
        this.priceAdjustment = Money.toAmount(priceAdjustment);
    }

    @Override
    public ProductOption getProductOption() {
        return productOption;
    }

    @Override
    public void setProductOption(ProductOption productOption) {
        this.productOption = productOption;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        ProductOptionValueImpl other = (ProductOptionValueImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (getAttributeValue() == null) {
            if (other.getAttributeValue() != null) {
                return false;
            }
        } else if (!getAttributeValue().equals(other.getAttributeValue())) {
            return false;
        }
        return true;
    }

    @Override
    public <G extends ProductOptionValue> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        ProductOptionValue cloned = createResponse.getClone();
        cloned.setAttributeValue(attributeValue);
        cloned.setDisplayOrder(displayOrder);
        cloned.setPriceAdjustment(getPriceAdjustment());
        if (productOption != null) {
            cloned.setProductOption(productOption.createOrRetrieveCopyInstance(context).getClone());
        }
        
        return  createResponse;
    }
}
