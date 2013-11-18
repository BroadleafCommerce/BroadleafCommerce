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
import org.hibernate.annotations.Cascade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRODUCT_BUNDLE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "ProductImpl_bundleProduct")
public class ProductBundleImpl extends ProductImpl implements ProductBundle {

    private static final long serialVersionUID = 1L;

    @Column(name = "PRICING_MODEL")
    @AdminPresentation(friendlyName = "productBundlePricingModel", 
            group = ProductImpl.Presentation.Group.Name.Price,
            order = 1,
        helpText = "productBundlePricingModelHelp", 
        fieldType = SupportedFieldType.BROADLEAF_ENUMERATION, 
        broadleafEnumeration = "org.broadleafcommerce.core.catalog.service.type.ProductBundlePricingModelType",
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
    protected int priority=99;

    @OneToMany(mappedBy = "bundle", targetEntity = SkuBundleItemImpl.class, cascade = { CascadeType.ALL })
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
    @BatchSize(size = 50)
    @AdminPresentationCollection(friendlyName = "skuBundleItemsTitle")
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
        Money price = new Money(BigDecimal.ZERO);
        for (SkuBundleItem item : getSkuBundleItems()) {
            price = price.add(item.getRetailPrice().multiply(item.getQuantity()));
        }
        return price;
    }

    @Override
    public Money getBundleItemsSalePrice() {
        Money price = new Money(BigDecimal.ZERO);
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

}
