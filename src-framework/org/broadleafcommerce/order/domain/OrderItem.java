package org.broadleafcommerce.order.domain;

import java.util.List;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.util.money.Money;

public interface OrderItem {

    public Long getId();

    public void setId(Long id);

    public Order getOrder();

    public void setOrder(Order order);

    public Money getRetailPrice();

    public void setRetailPrice(Money retailPrice);

    public Money getSalePrice();

    public void setSalePrice(Money salePrice);

    public Money getAdjustmentPrice();

    public void setAdjustmentPrice(Money adjustmentPrice);

    public Money getPrice();

    public void setPrice(Money price);


    public void assignFinalPrice();

    public Money getCurrentPrice();

    public int getQuantity();

    public void setQuantity(int quantity);

    public Category getCategory();

    public void setCategory(Category category);

    public List<CandidateItemOffer> getCandidateItemOffers();

    public void setCandidateItemOffers(List<CandidateItemOffer> candidateOffers);

    public List<CandidateItemOffer> addCandidateItemOffer(CandidateItemOffer candidateOffer);

    public void removeAllCandidateOffers();

    public boolean markForOffer();

    public int getMarkedForOffer();

    public boolean unmarkForOffer();

    public boolean isAllQuantityMarkedForOffer();

    public List<OrderItemAdjustment> getOrderItemAdjustments();

    public List<OrderItemAdjustment> addOrderItemAdjustment(OrderItemAdjustment orderItemAdjustment);

    public void setOrderItemAdjustments(List<OrderItemAdjustment> orderItemAdjustments);

    public void removeAllAdjustments();

    public PersonalMessage getPersonalMessage();

    public void setPersonalMessage(PersonalMessage personalMessage);

    public boolean isInCategory(String categoryName);

    public boolean getIsBundle();

    public GiftWrapOrderItem getGiftWrapOrderItem();

    public void setGiftWrapOrderItem(GiftWrapOrderItem giftWrapOrderItem);

    public String getOrderItemType();

    public void setOrderItemType(String orderItemType);

}
