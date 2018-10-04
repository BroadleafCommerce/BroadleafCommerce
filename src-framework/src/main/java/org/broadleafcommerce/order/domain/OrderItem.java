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

import java.io.Serializable;
import java.util.List;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.order.service.type.OrderItemType;
import org.broadleafcommerce.util.money.Money;

public interface OrderItem extends Serializable {

    public Long getId();

    public void setId(Long id);

    public Order getOrder();

    public void setOrder(Order order);

    public Money getRetailPrice();

    public void setRetailPrice(Money retailPrice);

    public Money getSalePrice();

    public void setSalePrice(Money salePrice);

    public Money getAdjustmentValue();

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

    public void setCandidateItemOffers(List<CandidateItemOffer> candidateItemOffers);

    public void addCandidateItemOffer(CandidateItemOffer candidateItemOffer);

    public void removeAllCandidateItemOffers();

    public boolean markForOffer();

    public int getMarkedForOffer();

    public boolean unmarkForOffer();

    public boolean isAllQuantityMarkedForOffer();

    public List<OrderItemAdjustment> getOrderItemAdjustments();

    public void addOrderItemAdjustment(OrderItemAdjustment orderItemAdjustment);

    //public void setOrderItemAdjustments(List<OrderItemAdjustment> orderItemAdjustments);

    public int removeAllAdjustments();

    public PersonalMessage getPersonalMessage();

    public void setPersonalMessage(PersonalMessage personalMessage);

    public boolean isInCategory(String categoryName);

    public GiftWrapOrderItem getGiftWrapOrderItem();

    public void setGiftWrapOrderItem(GiftWrapOrderItem giftWrapOrderItem);

    public OrderItemType getOrderItemType();

    public void setOrderItemType(OrderItemType orderItemType);

    public Money getTaxablePrice();

    public boolean getIsOnSale();

    public boolean getIsDiscounted();

    public boolean isNotCombinableOfferApplied();

    public boolean isHasOrderItemAdjustments();

    public boolean updatePrices();
}
