/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.order.service.call;


import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;

import java.util.List;

public class ConfigurableOrderItemRequest extends AddToCartItem {

    protected Product product;
    protected Sku sku;
    protected List<ConfigurableOrderItemRequest> productChoices;

    protected Boolean isMultiSelect;
    protected Integer minQuantity;
    protected Integer maxQuantity;
    protected Money displayPrice;
    protected Integer orderItemIndex;

    protected Boolean hasOverridenPrice;
    protected Boolean hasConfigurationError;
    protected Boolean discountsAllowed;

    protected boolean expandable;
    protected boolean firstExpandable;
    protected boolean lastExpandable;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        super.setProductId(product.getId());
        this.product = product;
    }

    public Sku getSku() { return sku; }

    public void setSku(Sku sku) {
        super.setSkuId(sku.getId());
        this.sku = sku;
    }

    public List<ConfigurableOrderItemRequest> getProductChoices() {
        return productChoices;
    }

    public void setProductChoices(List<ConfigurableOrderItemRequest> productChoices) {
        this.productChoices = productChoices;
    }

    public Boolean getIsMultiSelect() {
        if (isMultiSelect == null) {
            isMultiSelect = false;
        }
        return isMultiSelect;
    }

    public void setIsMultiSelect(Boolean multiSelect) {
        isMultiSelect = multiSelect;
    }

    public Integer getQuantity() {
        if (super.getQuantity() == null) {
            return getMinQuantity();
        }
        return super.getQuantity();
    }

    public Integer getMinQuantity() {
        return minQuantity == null ? 0 : minQuantity;
    }

    public void setMinQuantity(Integer minQuantity) {
        this.minQuantity = minQuantity;
    }

    public Integer getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Integer maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public Money getDisplayPrice() {
        return displayPrice;
    }

    public void setDisplayPrice(Money displayPrice) {
        this.displayPrice = displayPrice;
    }

    public Integer getOrderItemIndex() {
        return orderItemIndex;
    }

    public void setOrderItemIndex(Integer orderItemIndex) {
        this.orderItemIndex = orderItemIndex;
    }

    public Boolean getHasOverridenPrice() {
        if (hasOverridenPrice == null) {
            return false;
        }
        return hasOverridenPrice;
    }

    public void setHasOverridenPrice(Boolean hasOverridenPrice) {
        this.hasOverridenPrice = hasOverridenPrice;
    }

    public Boolean getHasConfigurationError() {
        if (hasConfigurationError == null) {
            return false;
        }
        return hasConfigurationError;
    }

    public void setHasConfigurationError(Boolean hasConfigurationError) {
        this.hasConfigurationError = hasConfigurationError;
    }

    public Boolean getDiscountsAllowed() {
        if (discountsAllowed == null) {
            return false;
        }
        return discountsAllowed;
    }

    public void setDiscountsAllowed(Boolean discountsAllowed) {
        this.discountsAllowed = discountsAllowed;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public boolean isFirstExpandable() {
        return firstExpandable;
    }

    public void setFirstExpandable(boolean firstExpandable) {
        this.firstExpandable = firstExpandable;
    }

    public boolean isLastExpandable() {
        return lastExpandable;
    }

    public void setLastExpandable(boolean lastExpandable) {
        this.lastExpandable = lastExpandable;
    }
}
