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

package org.broadleafcommerce.core.order.service.manipulation;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuBundleItem;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemFeePrice;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemAttribute;
import org.broadleafcommerce.core.order.domain.PersonalMessage;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;

import java.util.List;
import java.util.Map;

public class DiscreteOrderItemDecorator implements DiscreteOrderItem {
	
	private static final long serialVersionUID = 1L;
	
	private int quantity;
	private DiscreteOrderItem discreteOrderItem;

	public DiscreteOrderItemDecorator(DiscreteOrderItem discreteOrderItem, int quantity) {
		this.discreteOrderItem = discreteOrderItem;
		this.quantity = quantity;
	}
	
	public Sku getSku() {
		return discreteOrderItem.getSku();
	}

	public void setSku(Sku sku) {
		discreteOrderItem.setSku(sku);
	}

	public Product getProduct() {
		return discreteOrderItem.getProduct();
	}

	public void setProduct(Product product) {
		discreteOrderItem.setProduct(product);
	}

	public BundleOrderItem getBundleOrderItem() {
		return discreteOrderItem.getBundleOrderItem();
	}

	public void setBundleOrderItem(BundleOrderItem bundleOrderItem) {
		discreteOrderItem.setBundleOrderItem(bundleOrderItem);
	}

    /**
     * If this item is part of a bundle that was created via a ProductBundle, then this
     * method returns a reference to the corresponding SkuBundleItem.
     * <p/>
     * For manually created
     * <p/>
     * For all others, this method returns null.
     *
     * @return
     */
    @Override
    public SkuBundleItem getSkuBundleItem() {
        return discreteOrderItem.getSkuBundleItem();
    }

    /**
     * Sets the associated skuBundleItem.
     *
     * @param skuBundleItem
     */
    @Override
    public void setSkuBundleItem(SkuBundleItem skuBundleItem) {
        discreteOrderItem.setSkuBundleItem(skuBundleItem);
    }

    public Money getTaxablePrice() {
		return discreteOrderItem.getTaxablePrice();
	}

	public Map<String, String> getAdditionalAttributes() {
		return discreteOrderItem.getAdditionalAttributes();
	}

	public void setAdditionalAttributes(Map<String, String> additionalAttributes) {
		discreteOrderItem.setAdditionalAttributes(additionalAttributes);
	}

	public Money getBaseRetailPrice() {
		return discreteOrderItem.getBaseRetailPrice();
	}

	public void setBaseRetailPrice(Money baseRetailPrice) {
		discreteOrderItem.setBaseRetailPrice(baseRetailPrice);
	}

	public Long getId() {
		return discreteOrderItem.getId();
	}

	public void setId(Long id) {
		discreteOrderItem.setId(id);
	}

	public Money getBaseSalePrice() {
		return discreteOrderItem.getBaseSalePrice();
	}

	public Order getOrder() {
		return discreteOrderItem.getOrder();
	}

	public void setBaseSalePrice(Money baseSalePrice) {
		discreteOrderItem.setBaseSalePrice(baseSalePrice);
	}

	public void setOrder(Order order) {
		discreteOrderItem.setOrder(order);
	}

	public Money getRetailPrice() {
		return discreteOrderItem.getRetailPrice();
	}

	public List<DiscreteOrderItemFeePrice> getDiscreteOrderItemFeePrices() {
		return discreteOrderItem.getDiscreteOrderItemFeePrices();
	}

	public void setRetailPrice(Money retailPrice) {
		discreteOrderItem.setRetailPrice(retailPrice);
	}

	public Money getSalePrice() {
		return discreteOrderItem.getSalePrice();
	}

	public void setDiscreteOrderItemFeePrices(
			List<DiscreteOrderItemFeePrice> orderItemFeePrices) {
		discreteOrderItem.setDiscreteOrderItemFeePrices(orderItemFeePrices);
	}

	public void setSalePrice(Money salePrice) {
		discreteOrderItem.setSalePrice(salePrice);
	}

	public Money getAdjustmentValue() {
		return discreteOrderItem.getAdjustmentValue();
	}

	public Money getPrice() {
		return discreteOrderItem.getPrice();
	}

	public void setPrice(Money price) {
		discreteOrderItem.setPrice(price);
	}

	public void assignFinalPrice() {
		discreteOrderItem.assignFinalPrice();
	}

	public Money getCurrentPrice() {
		return discreteOrderItem.getCurrentPrice();
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		//do nothing
	}

	public Category getCategory() {
		return discreteOrderItem.getCategory();
	}

	public void setCategory(Category category) {
		discreteOrderItem.setCategory(category);
	}

	public List<CandidateItemOffer> getCandidateItemOffers() {
		return discreteOrderItem.getCandidateItemOffers();
	}

	public void setCandidateItemOffers(
			List<CandidateItemOffer> candidateItemOffers) {
		discreteOrderItem.setCandidateItemOffers(candidateItemOffers);
	}

	public List<OrderItemAdjustment> getOrderItemAdjustments() {
		return discreteOrderItem.getOrderItemAdjustments();
	}

	public PersonalMessage getPersonalMessage() {
		return discreteOrderItem.getPersonalMessage();
	}

	public void setPersonalMessage(PersonalMessage personalMessage) {
		discreteOrderItem.setPersonalMessage(personalMessage);
	}

	public boolean isInCategory(String categoryName) {
		return discreteOrderItem.isInCategory(categoryName);
	}

	public GiftWrapOrderItem getGiftWrapOrderItem() {
		return discreteOrderItem.getGiftWrapOrderItem();
	}

	public void setGiftWrapOrderItem(GiftWrapOrderItem giftWrapOrderItem) {
		discreteOrderItem.setGiftWrapOrderItem(giftWrapOrderItem);
	}

	public OrderItemType getOrderItemType() {
		return discreteOrderItem.getOrderItemType();
	}

	public void setOrderItemType(OrderItemType orderItemType) {
		discreteOrderItem.setOrderItemType(orderItemType);
	}

	public boolean getIsOnSale() {
		return discreteOrderItem.getIsOnSale();
	}

	public boolean getIsDiscounted() {
		return discreteOrderItem.getIsDiscounted();
	}

	public boolean updatePrices() {
		return discreteOrderItem.updatePrices();
	}

	public String getName() {
		return discreteOrderItem.getName();
	}

	public void setName(String name) {
		discreteOrderItem.setName(name);
	}

	public Money getPriceBeforeAdjustments(boolean allowSalesPrice) {
		return discreteOrderItem.getPriceBeforeAdjustments(allowSalesPrice);
	}

	public OrderItem clone() {
		return discreteOrderItem.clone();
	}

	public void setOrderItemAdjustments(List<OrderItemAdjustment> orderItemAdjustments) {
		discreteOrderItem.setOrderItemAdjustments(orderItemAdjustments);
	}

	public void addCandidateItemOffer(CandidateItemOffer candidateItemOffer) {
		discreteOrderItem.addCandidateItemOffer(candidateItemOffer);
	}

	public void removeAllCandidateItemOffers() {
		discreteOrderItem.removeAllCandidateItemOffers();
	}

	public int removeAllAdjustments() {
		return discreteOrderItem.removeAllAdjustments();
	}

	public void accept(OrderItemVisitor visitor) throws PricingException {
		discreteOrderItem.accept(visitor);
	}

    /**
     * A list of arbitrary attributes added to this item.
     */
    @Override
    public Map<String, OrderItemAttribute> getOrderItemAttributes() {
        return discreteOrderItem.getOrderItemAttributes();
    }

    /**
     * Sets the map of order item attributes.
     *
     * @param orderItemAttributes
     */
    @Override
    public void setOrderItemAttributes(Map<String, OrderItemAttribute> orderItemAttributes) {
        discreteOrderItem.setOrderItemAttributes(orderItemAttributes);
    }

    @Override
    public Boolean isTaxable() {
        return discreteOrderItem.isTaxable();
    }

    @Override
    public void setTaxable(Boolean taxable) {
        discreteOrderItem.setTaxable(taxable);
    }

    /**
     * If the system automatically split an item to accommodate the promotion logic (e.g. buy one get one free),
     * then this value is set to the originalItemId.
     * <p/>
     * Returns null otherwise.
     *
     * @return
     */
    @Override
    public Long getSplitParentItemId() {
        return discreteOrderItem.getSplitParentItemId();
    }

    @Override
    public void setSplitParentItemId(Long id) {
        discreteOrderItem.setSplitParentItemId(id);
    }


}
