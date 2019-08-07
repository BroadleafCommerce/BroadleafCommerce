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
import org.broadleafcommerce.common.money.BankersRounding;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.core.catalog.service.type.ProductBundlePricingModelType;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;


/**
 * @deprecated instead, use the ProductType Module's Product Add-Ons to build and configure bundles
 */
@Deprecated
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRODUCT_BUNDLE")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "ProductImpl_bundleProduct")
public class ProductBundleImpl extends ProductImpl implements ProductBundle {

    private static final long serialVersionUID = 1L;

    @Column(name = "PRICING_MODEL")
    @AdminPresentation(friendlyName = "productBundlePricingModel", 
        group = GroupName.Price, order = 1,
        helpText = "productBundlePricingModelHelp", 
        fieldType = SupportedFieldType.BROADLEAF_ENUMERATION, 
        broadleafEnumeration = "org.broadleafcommerce.core.catalog.service.type.ProductBundlePricingModelType",
        defaultValue = "ITEM_SUM",
        requiredOverride = RequiredOverride.REQUIRED)
    protected String pricingModel;

    @Column(name = "AUTO_BUNDLE")
    @AdminPresentation(excluded = true)
    protected Boolean autoBundle = false;

    @Column(name = "ITEMS_PROMOTABLE")
    @AdminPresentation(excluded = true)
    protected Boolean itemsPromotable = false;

    @Column(name = "BUNDLE_PROMOTABLE")
    @AdminPresentation(excluded = true)
    protected Boolean bundlePromotable = false;

    @Column(name = "BUNDLE_PRIORITY")
    @AdminPresentation(excluded = true, friendlyName = "productBundlePriority", group="productBundleGroup")
    protected Integer priority=99;

    @OneToMany(mappedBy = "bundle", targetEntity = SkuBundleItemImpl.class, cascade = { CascadeType.ALL })
    @OrderBy(value = "sequence")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blProducts")
    @BatchSize(size = 50)
    @AdminPresentationCollection(friendlyName = "skuBundleItemsTitle",
        sortProperty = "sequence",
        tab = TabName.General)
    protected List<SkuBundleItem> skuBundleItems = new ArrayList<SkuBundleItem>();
    
    @Override
    public boolean isOnSale() {
        Money retailPrice = getRetailPrice();
        Money salePrice = getSalePrice();
        return (salePrice != null && !salePrice.isZero() && salePrice.lessThan(retailPrice));
    }

    @Override
    public ProductBundlePricingModelType getPricingModel() {
        return ProductBundlePricingModelType.getInstance(pricingModel);
    }

    @Override
    public void setPricingModel(ProductBundlePricingModelType pricingModel) {
        this.pricingModel = pricingModel == null ? null : pricingModel.getType();
    }

    @Override
    public Money getRetailPrice() {
        if (ProductBundlePricingModelType.ITEM_SUM.equals(getPricingModel())) {
            return getBundleItemsRetailPrice();
        } else if (ProductBundlePricingModelType.BUNDLE.equals(getPricingModel())) {
            return super.getDefaultSku().getRetailPrice();
        }
        return null;
    }
    
    @Override
    public Money getSalePrice() {
        if (ProductBundlePricingModelType.ITEM_SUM.equals(getPricingModel())) {
            return getBundleItemsSalePrice();
        } else if (ProductBundlePricingModelType.BUNDLE.equals(getPricingModel())) {
            return super.getDefaultSku().getSalePrice();
        }
        return null;
    }

    @Override
    public Money getBundleItemsRetailPrice() {
        Money price = Money.ZERO;
        for (SkuBundleItem item : getSkuBundleItems()) {
            price = price.add(item.getRetailPrice().multiply(item.getQuantity()));
        }
        return price;
    }

    @Override
    public Money getBundleItemsSalePrice() {
        Money price = Money.ZERO;
        for (SkuBundleItem item : getSkuBundleItems()){
            if (item.getSalePrice() != null) {
                price = price.add(item.getSalePrice().multiply(item.getQuantity()));
            } else {
                price = price.add(item.getRetailPrice().multiply(item.getQuantity()));
            }
        }
        return price;
    }
    
    @Override
    public void clearDynamicPrices() {
        super.clearDynamicPrices();
        for (SkuBundleItem bundleItem : getSkuBundleItems()) {
            bundleItem.clearDynamicPrices();
        }
    }

    @Override
    public Boolean getAutoBundle() {
        return autoBundle == null ? false : autoBundle;
    }

    @Override
    public void setAutoBundle(Boolean autoBundle) {
        this.autoBundle = autoBundle;
    }

    @Override
    public Boolean getItemsPromotable() {
        return itemsPromotable == null ? false : itemsPromotable;
    }

    @Override
    public void setItemsPromotable(Boolean itemsPromotable) {
        this.itemsPromotable = itemsPromotable;
    }

    @Override
    public Boolean getBundlePromotable() {
        return bundlePromotable == null ? false : bundlePromotable;
    }

    @Override
    public void setBundlePromotable(Boolean bundlePromotable) {
        this.bundlePromotable = bundlePromotable;
    }

    @Override
    public List<SkuBundleItem> getSkuBundleItems() {
        return skuBundleItems;
    }

    @Override
    public void setSkuBundleItems(List<SkuBundleItem> skuBundleItems) {
        this.skuBundleItems = skuBundleItems;
    }

    @Override
    public Integer getPriority() {
        return priority;
    }

    @Override
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public BigDecimal getPotentialSavings() {

        if (skuBundleItems != null) {

            Money totalNormalPrice = new Money(BankersRounding.zeroAmount());
            Money totalBundlePrice = new Money(BankersRounding.zeroAmount());

            for (SkuBundleItem skuBundleItem : skuBundleItems) {

                Sku sku = skuBundleItem.getSku();

                if (sku != null && sku.getRetailPrice() != null) {
                    totalNormalPrice = totalNormalPrice.add(sku.getRetailPrice().multiply(skuBundleItem.getQuantity()));
                }

                if (ProductBundlePricingModelType.ITEM_SUM.equals(getPricingModel())) {
                    if (skuBundleItem.getSalePrice() != null) {
                        totalBundlePrice = totalBundlePrice.add(skuBundleItem.getSalePrice().multiply(skuBundleItem.getQuantity()));
                    } else {
                        totalBundlePrice = totalBundlePrice.add(skuBundleItem.getRetailPrice().multiply(skuBundleItem.getQuantity()));
                    }
                }

            }

            if (ProductBundlePricingModelType.BUNDLE.equals(getPricingModel())) {
                if (getSalePrice() != null) {
                    totalBundlePrice = getSalePrice();
                } else {
                    totalBundlePrice = getRetailPrice();
                }
            }

            return totalNormalPrice.subtract(totalBundlePrice).getAmount();

        }

        return BigDecimal.ZERO;

    }

    @Override
    public CreateResponse<ProductBundle> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws
            CloneNotSupportedException {
        CreateResponse<ProductBundle> createResponse = super.createOrRetrieveCopyInstance(context);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        ProductBundle cloned = createResponse.getClone();
        cloned.setAutoBundle(autoBundle);
        cloned.setBundlePromotable(bundlePromotable);
        cloned.setItemsPromotable(itemsPromotable);
        cloned.setPriority(priority);
        cloned.setPricingModel(getPricingModel());
        for (SkuBundleItem item : skuBundleItems) {
            cloned.getSkuBundleItems().add(item.createOrRetrieveCopyInstance(context).getClone());
        }
        return createResponse;
    }
}
