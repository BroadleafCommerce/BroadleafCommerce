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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.core.order.service.manipulation.OrderItemVisitor;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_BUNDLE_ORDER_ITEM")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
@AdminPresentationClass(friendlyName = "bundleOrderItem")
public class BundleOrderItemImpl extends OrderItemImpl implements BundleOrderItem {

    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "bundleOrderItem", targetEntity = DiscreteOrderItemImpl.class, cascade = {CascadeType.ALL})
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<DiscreteOrderItem> discreteOrderItems = new ArrayList<DiscreteOrderItem>();
    
    @OneToMany(mappedBy = "bundleOrderItem", targetEntity = BundleOrderItemFeePriceImpl.class, cascade = { CascadeType.ALL })
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    protected List<BundleOrderItemFeePrice> bundleOrderItemFeePrices = new ArrayList<BundleOrderItemFeePrice>();

    public List<DiscreteOrderItem> getDiscreteOrderItems() {
        return discreteOrderItems;
    }

    public void setDiscreteOrderItems(List<DiscreteOrderItem> discreteOrderItems) {
        this.discreteOrderItems = discreteOrderItems;
    }

    public List<BundleOrderItemFeePrice> getBundleOrderItemFeePrices() {
		return bundleOrderItemFeePrices;
	}

	public void setBundleOrderItemFeePrices(List<BundleOrderItemFeePrice> bundleOrderItemFeePrices) {
		this.bundleOrderItemFeePrices = bundleOrderItemFeePrices;
	}

	@Override
    public void removeAllCandidateItemOffers() {
        for (DiscreteOrderItem discreteOrderItem : discreteOrderItems) {
            discreteOrderItem.removeAllCandidateItemOffers();
        }
    }

    @Override
    public int removeAllAdjustments() {
    	int removedAdjustmentCount = 0;
        for (DiscreteOrderItem discreteOrderItem : discreteOrderItems) {
        	removedAdjustmentCount = removedAdjustmentCount + discreteOrderItem.removeAllAdjustments();
        }
        return removedAdjustmentCount;
    }

	@Override
    public void assignFinalPrice() {
        for (DiscreteOrderItem discreteOrderItem : discreteOrderItems) {
            discreteOrderItem.assignFinalPrice();
        }
        price = getCurrentPrice().getAmount();
    }

    @Override
    public Money getTaxablePrice() {
        Money currentBundleTaxablePrice = new Money();
        for (DiscreteOrderItem discreteOrderItem : discreteOrderItems) {
            Money currentItemTaxablePrice = discreteOrderItem.getTaxablePrice();
            currentBundleTaxablePrice = currentBundleTaxablePrice.add(new Money(currentItemTaxablePrice.doubleValue() * discreteOrderItem.getQuantity()));
        }
        for (BundleOrderItemFeePrice fee : getBundleOrderItemFeePrices()) {
        	if (fee.isTaxable()) {
        		currentBundleTaxablePrice = currentBundleTaxablePrice.add(fee.getAmount());
        	}
        }
        return currentBundleTaxablePrice;
    }

    @Override
    public Money getRetailPrice() {
        Money bundleRetailPrice = new Money();
        for (DiscreteOrderItem discreteOrderItem : discreteOrderItems) {
            Money itemRetailPrice = discreteOrderItem.getRetailPrice();
            bundleRetailPrice = bundleRetailPrice.add(new Money(itemRetailPrice.doubleValue() * discreteOrderItem.getQuantity()));
        }
        for (BundleOrderItemFeePrice fee : getBundleOrderItemFeePrices()) {
        	bundleRetailPrice = bundleRetailPrice.add(fee.getAmount());
        }
        return bundleRetailPrice;
    }


    @Override
    public Money getSalePrice() {
        Money bundleSalePrice = null;
        if (hasSaleItems()) {
            bundleSalePrice = new Money();
            for (DiscreteOrderItem discreteOrderItem : discreteOrderItems) {
                Money itemSalePrice = null;
                if (discreteOrderItem.getSalePrice() != null) {
                    itemSalePrice = discreteOrderItem.getSalePrice();
                } else {
                    itemSalePrice = discreteOrderItem.getRetailPrice();
                }
                bundleSalePrice = bundleSalePrice.add(new Money(itemSalePrice.doubleValue() * discreteOrderItem.getQuantity()));
            }
            for (BundleOrderItemFeePrice fee : getBundleOrderItemFeePrices()) {
            	bundleSalePrice = bundleSalePrice.add(fee.getAmount());
            }
        }
        return bundleSalePrice;
    }

    private boolean hasSaleItems() {
        for (DiscreteOrderItem discreteOrderItem : discreteOrderItems) {
            if (discreteOrderItem.getSalePrice() != null) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasAdjustedItems() {
        for (DiscreteOrderItem discreteOrderItem : discreteOrderItems) {
            if (discreteOrderItem.getAdjustmentValue().greaterThan(new Money(0D))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Money getCurrentPrice() {
        Money currentBundlePrice = new Money();
        for (DiscreteOrderItem discreteOrderItem : discreteOrderItems) {
            Money currentItemPrice = discreteOrderItem.getCurrentPrice();
            currentBundlePrice = currentBundlePrice.add(new Money(currentItemPrice.doubleValue() * discreteOrderItem.getQuantity()));
        }
        return currentBundlePrice;
    }
    
    @Override
    public boolean updatePrices() {
        boolean updated = false;
        for (DiscreteOrderItem discreteOrderItem : discreteOrderItems) {
            if (discreteOrderItem.updatePrices()){
                updated = true;
            }
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
        BundleOrderItemImpl other = (BundleOrderItemImpl) obj;
        
        if (!super.equals(obj)) {
        	return false;
        }

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public OrderItem clone() {
        BundleOrderItemImpl orderItem = (BundleOrderItemImpl) super.clone();
        if (getDiscreteOrderItems() != null) {
            for (DiscreteOrderItem discreteOrderItem : getDiscreteOrderItems()) {
                DiscreteOrderItem temp = (DiscreteOrderItem) discreteOrderItem.clone();
                temp.setBundleOrderItem(orderItem);
                orderItem.getDiscreteOrderItems().add(temp);
            }
        }
        if (getBundleOrderItemFeePrices() != null) orderItem.getBundleOrderItemFeePrices().addAll(getBundleOrderItemFeePrices());

        return orderItem;
    }

    @Override
    public int hashCode() {
        final int prime = super.hashCode();
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    
    @Override
    public void accept(OrderItemVisitor visitor) throws PricingException {
        visitor.visit(this);
    }
}
