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
package org.broadleafcommerce.order.domain;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.ProductImpl;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.domain.SkuImpl;
import org.broadleafcommerce.util.money.Money;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_DISCRETE_ORDER_ITEM")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
public class DiscreteOrderItemImpl extends OrderItemImpl implements DiscreteOrderItem {

    private static final long serialVersionUID = 1L;

    @ManyToOne(targetEntity = SkuImpl.class, optional = false)
    @JoinColumn(name = "SKU_ID", nullable = false)
    protected Sku sku;

    @ManyToOne(targetEntity = ProductImpl.class)
    @JoinColumn(name = "PRODUCT_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    protected Product product;

    @ManyToOne(targetEntity = BundleOrderItemImpl.class)
    @JoinColumn(name = "BUNDLE_ORDER_ITEM_ID")
    protected BundleOrderItem bundleOrderItem;

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
        if (sku.getRetailPrice() != null) {
            this.retailPrice = sku.getRetailPrice().getAmount();
        }
        if (sku.getSalePrice() != null) {
            this.salePrice = sku.getSalePrice().getAmount();
        }
    }

    public Money getTaxablePrice() {
        Money taxablePrice = new Money(0D);
        if (sku.isTaxable()) {
            taxablePrice = getPrice();
        }
        return taxablePrice;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BundleOrderItem getBundleOrderItem() {
        return bundleOrderItem;
    }

    public void setBundleOrderItem(BundleOrderItem bundleOrderItem) {
        this.bundleOrderItem = bundleOrderItem;
    }

    public String getName() {
        return getSku().getName();
    }

    @Override
    public boolean updatePrices() {
        boolean updated = false;
        // use the sku prices - the retail and sale prices could be null
        if (!getSku().getRetailPrice().equals(getRetailPrice())) {
            setRetailPrice(getSku().getRetailPrice());
            updated = true;
        }
        if (getSku().getSalePrice() != null && !getSku().getSalePrice().equals(getSalePrice())) {
            setSalePrice(getSku().getSalePrice());
            updated = true;
        }
        return updated;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DiscreteOrderItemImpl other = (DiscreteOrderItemImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (bundleOrderItem == null) {
            if (other.bundleOrderItem != null)
                return false;
        } else if (!bundleOrderItem.equals(other.bundleOrderItem))
            return false;
        if (sku == null) {
            if (other.sku != null)
                return false;
        } else if (!sku.equals(other.sku))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bundleOrderItem == null) ? 0 : bundleOrderItem.hashCode());
        result = prime * result + ((sku == null) ? 0 : sku.hashCode());
        return result;
    }

}
