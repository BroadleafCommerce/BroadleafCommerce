package org.broadleafcommerce.core.order.service.util;

import java.util.List;
import java.util.Map;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.discount.PromotionQualifier;
import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemFeePrice;
import org.broadleafcommerce.core.order.domain.GiftWrapOrderItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.PersonalMessage;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import org.broadleafcommerce.money.Money;

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

	public Money getAdjustmentPrice() {
		return discreteOrderItem.getAdjustmentPrice();
	}

	public void setAdjustmentPrice(Money adjustmentPrice) {
		discreteOrderItem.setAdjustmentPrice(adjustmentPrice);
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

	public void addCandidateItemOffer(CandidateItemOffer candidateItemOffer) {
		discreteOrderItem.addCandidateItemOffer(candidateItemOffer);
	}

	public void removeAllCandidateItemOffers() {
		discreteOrderItem.removeAllCandidateItemOffers();
	}

	public boolean markForOffer() {
		return discreteOrderItem.markForOffer();
	}

	public int getMarkedForOffer() {
		return discreteOrderItem.getMarkedForOffer();
	}

	public boolean unmarkForOffer() {
		return discreteOrderItem.unmarkForOffer();
	}

	public boolean isAllQuantityMarkedForOffer() {
		return discreteOrderItem.isAllQuantityMarkedForOffer();
	}

	public List<OrderItemAdjustment> getOrderItemAdjustments() {
		return discreteOrderItem.getOrderItemAdjustments();
	}

	public void addOrderItemAdjustment(OrderItemAdjustment orderItemAdjustment) {
		discreteOrderItem.addOrderItemAdjustment(orderItemAdjustment);
	}

	public int removeAllAdjustments() {
		return discreteOrderItem.removeAllAdjustments();
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

	public boolean isNotCombinableOfferApplied() {
		return discreteOrderItem.isNotCombinableOfferApplied();
	}

	public boolean isHasOrderItemAdjustments() {
		return discreteOrderItem.isHasOrderItemAdjustments();
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

	public List<PromotionDiscount> getPromotionDiscounts() {
		return discreteOrderItem.getPromotionDiscounts();
	}

	public void setPromotionDiscounts(List<PromotionDiscount> promotionDiscounts) {
		discreteOrderItem.setPromotionDiscounts(promotionDiscounts);
	}

	public List<PromotionQualifier> getPromotionQualifiers() {
		return discreteOrderItem.getPromotionQualifiers();
	}

	public void setPromotionQualifiers(
			List<PromotionQualifier> promotionQualifiers) {
		discreteOrderItem.setPromotionQualifiers(promotionQualifiers);
	}

	public int getQuantityAvailableToBeUsedAsQualifier(Offer promotion) {
		return discreteOrderItem
				.getQuantityAvailableToBeUsedAsQualifier(promotion);
	}

	public int getQuantityAvailableToBeUsedAsTarget(Offer promotion) {
		return discreteOrderItem
				.getQuantityAvailableToBeUsedAsTarget(promotion);
	}

	public Money getPriceBeforeAdjustments(boolean allowSalesPrice) {
		return discreteOrderItem.getPriceBeforeAdjustments(allowSalesPrice);
	}

	public void addPromotionQualifier(CandidateItemOffer candidatePromotion,
			OfferItemCriteria itemCriteria, int quantity) {
		discreteOrderItem.addPromotionQualifier(candidatePromotion,
				itemCriteria, quantity);
	}

	public void addPromotionDiscount(CandidateItemOffer candidatePromotion,
			OfferItemCriteria itemCriteria, int quantity) {
		discreteOrderItem.addPromotionDiscount(candidatePromotion,
				itemCriteria, quantity);
	}

	public void clearAllNonFinalizedQuantities() {
		discreteOrderItem.clearAllNonFinalizedQuantities();
	}

	public void finalizeQuantities() {
		discreteOrderItem.finalizeQuantities();
	}

	public OrderItem clone() {
		return discreteOrderItem.clone();
	}

	public List<OrderItem> split() {
		return discreteOrderItem.split();
	}

	public void clearAllDiscount() {
		discreteOrderItem.clearAllDiscount();
	}

	public void clearAllQualifiers() {
		discreteOrderItem.clearAllQualifiers();
	}
}
