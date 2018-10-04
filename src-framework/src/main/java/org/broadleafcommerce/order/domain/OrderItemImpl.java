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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.CategoryImpl;
import org.broadleafcommerce.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.offer.domain.CandidateItemOfferImpl;
import org.broadleafcommerce.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.offer.domain.OrderItemAdjustmentImpl;
import org.broadleafcommerce.order.service.type.OrderItemType;
import org.broadleafcommerce.util.money.Money;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER_ITEM")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
public class OrderItemImpl implements OrderItem {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "OrderItemId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "OrderItemId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "OrderItemImpl", allocationSize = 50)
    @Column(name = "ORDER_ITEM_ID")
    protected Long id;

    @ManyToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "CATEGORY_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    protected Category category;

    @ManyToOne(targetEntity = OrderImpl.class)
    @JoinColumn(name = "ORDER_ID")
    protected Order order;

    @Column(name = "RETAIL_PRICE")
    protected BigDecimal retailPrice;

    @Column(name = "SALE_PRICE")
    protected BigDecimal salePrice;

    @Column(name = "PRICE")
    protected BigDecimal price;

    @Column(name = "QUANTITY", nullable = false)
    protected int quantity;

    @Transient
    protected BigDecimal adjustmentPrice; // retailPrice with adjustments

    @ManyToOne(targetEntity = PersonalMessageImpl.class, cascade = { CascadeType.ALL })
    @JoinColumn(name = "PERSONAL_MESSAGE_ID")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    protected PersonalMessage personalMessage;

    @ManyToOne(targetEntity = GiftWrapOrderItemImpl.class, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinColumn(name = "GIFT_WRAP_ITEM_ID", nullable = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    protected GiftWrapOrderItem giftWrapOrderItem;

    @OneToMany(mappedBy = "orderItem", targetEntity = OrderItemAdjustmentImpl.class, cascade = { CascadeType.ALL })
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    protected List<OrderItemAdjustment> orderItemAdjustments = new ArrayList<OrderItemAdjustment>();

    @OneToMany(mappedBy = "orderItem", targetEntity = CandidateItemOfferImpl.class, cascade = { CascadeType.ALL })
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    protected List<CandidateItemOffer> candidateItemOffers = new ArrayList<CandidateItemOffer>();

    @Transient
    protected int markedForOffer = 0;

    @Transient
    protected boolean notCombinableOfferApplied = false;

    @Transient
    protected boolean hasOrderItemAdjustments = false;

    @Column(name = "ORDER_ITEM_TYPE")
    protected String orderItemType;

    public Money getRetailPrice() {
        return retailPrice == null ? null : new Money(retailPrice);
    }

    public void setRetailPrice(Money retailPrice) {
        this.retailPrice = Money.toAmount(retailPrice);
    }

    public Money getSalePrice() {
        return salePrice == null ? null : new Money(salePrice);
    }

    public void setSalePrice(Money salePrice) {
        this.salePrice = Money.toAmount(salePrice);
    }

    public Money getPrice() {
        return price == null ? null : new Money(price);
    }

    public void setPrice(Money finalPrice) {
        this.price = Money.toAmount(finalPrice);
    }

    public void assignFinalPrice() {
        price = getCurrentPrice().getAmount();
    }

    public Money getTaxablePrice() {
        return getPrice();
    }

    public Money getCurrentPrice() {
        updatePrices();
        Money currentPrice = null;
        if (adjustmentPrice != null) {
            currentPrice = new Money(adjustmentPrice);
        } else if (salePrice != null) {
            currentPrice = new Money(salePrice);
        } else {
            currentPrice = new Money(retailPrice);
        }
        return currentPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<CandidateItemOffer> getCandidateItemOffers() {
        return candidateItemOffers;
    }

    public void setCandidateItemOffers(List<CandidateItemOffer> candidateItemOffers) {
        this.candidateItemOffers = candidateItemOffers;
    }

    public void addCandidateItemOffer(CandidateItemOffer candidateItemOffer) {
        // TODO: if stacked, add all of the items to the persisted structure and
        // add just the stacked version
        // to this collection
        this.candidateItemOffers.add(candidateItemOffer);
    }

    public void removeAllCandidateItemOffers() {
        if (candidateItemOffers != null) {
            for (CandidateItemOffer candidate : candidateItemOffers) {
                candidate.setOrderItem(null);
            }
            candidateItemOffers.clear();
        }
    }

    public boolean markForOffer() {
        if (markedForOffer >= quantity) {
            return false;
        }
        markedForOffer++;
        return true;
    }

    public int getMarkedForOffer() {
        return markedForOffer;
    }

    public boolean unmarkForOffer() {
        if (markedForOffer < 1) {
            return false;
        }
        markedForOffer--;
        return true;
    }

    public boolean isAllQuantityMarkedForOffer() {
        if (markedForOffer >= quantity) {
            return true;
        }
        return false;
    }

    public PersonalMessage getPersonalMessage() {
        return personalMessage;
    }

    public void setPersonalMessage(PersonalMessage personalMessage) {
        this.personalMessage = personalMessage;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isInCategory(String categoryName) {
        Category currentCategory = category;
        if (currentCategory != null) {
            if (currentCategory.getName().equals(categoryName)) {
                return true;
            }
            while ((currentCategory = currentCategory.getDefaultParentCategory()) != null) {
                if (currentCategory.getName().equals(categoryName)) {
                    return true;
                }
            }
        }
        return false;

    }

    public List<OrderItemAdjustment> getOrderItemAdjustments() {
        return this.orderItemAdjustments;
    }

    /*
     * Adds the adjustment to the order item's adjustment list an discounts the
     * order item's adjustment price by the value of the adjustment.
     */
    public void addOrderItemAdjustment(OrderItemAdjustment orderItemAdjustment) {
        if (this.orderItemAdjustments.size() == 0) {
            adjustmentPrice = retailPrice;
        }
        adjustmentPrice = adjustmentPrice.subtract(orderItemAdjustment.getValue().getAmount());
        this.orderItemAdjustments.add(orderItemAdjustment);
        if (!orderItemAdjustment.getOffer().isCombinableWithOtherOffers()) {
            notCombinableOfferApplied = true;
        }
        hasOrderItemAdjustments = true;
    }

    public int removeAllAdjustments() {
        int removedAdjustmentCount = 0;
        if (orderItemAdjustments != null) {
            for (OrderItemAdjustment adjustment : orderItemAdjustments) {
                adjustment.setOrderItem(null);
            }
            removedAdjustmentCount = orderItemAdjustments.size();
            orderItemAdjustments.clear();
        }
        adjustmentPrice = null;
        notCombinableOfferApplied = false;
        hasOrderItemAdjustments = false;
        return removedAdjustmentCount;
    }

    protected void setOrderItemAdjustments(List<OrderItemAdjustment> orderItemAdjustments) {
        this.orderItemAdjustments = orderItemAdjustments;
        if ((orderItemAdjustments == null) || (orderItemAdjustments.size() == 0)) {
            removeAllAdjustments();
        } else {
            for (OrderItemAdjustment orderItemAdjustment : orderItemAdjustments) {
                if (!notCombinableOfferApplied) {
                    addOrderItemAdjustment(orderItemAdjustment);
                } else {
                    break;
                }
            }
        }
    }

    public Money getAdjustmentValue() {
        Money adjustmentValue = new Money(0);
        for (OrderItemAdjustment itemAdjustment : orderItemAdjustments) {
            adjustmentValue = adjustmentValue.add(itemAdjustment.getValue());
        }
        return adjustmentValue;
    }

    public Money getAdjustmentPrice() {
        return adjustmentPrice == null ? null : new Money(adjustmentPrice);
    }

    public void setAdjustmentPrice(Money adjustmentPrice) {
        this.adjustmentPrice = Money.toAmount(adjustmentPrice);
    }

    public GiftWrapOrderItem getGiftWrapOrderItem() {
        return giftWrapOrderItem;
    }

    public void setGiftWrapOrderItem(GiftWrapOrderItem giftWrapOrderItem) {
        this.giftWrapOrderItem = giftWrapOrderItem;
    }

    public OrderItemType getOrderItemType() {
        return orderItemType == null ? null : OrderItemType.getInstance(orderItemType);
    }

    public void setOrderItemType(OrderItemType orderItemType) {
        this.orderItemType = orderItemType.getType();
    }

    public boolean getIsOnSale() {
        return !getSalePrice().equals(getRetailPrice());
    }

    public boolean getIsDiscounted() {
        return !getPrice().equals(getRetailPrice());
    }

    public boolean isNotCombinableOfferApplied() {
        return notCombinableOfferApplied;
    }

    public boolean isHasOrderItemAdjustments() {
        return hasOrderItemAdjustments;
    }

    public boolean updatePrices() {
        return false;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((adjustmentPrice == null) ? 0 : adjustmentPrice.hashCode());
        result = prime * result + ((candidateItemOffers == null) ? 0 : candidateItemOffers.hashCode());
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((giftWrapOrderItem == null) ? 0 : giftWrapOrderItem.hashCode());
        result = prime * result + markedForOffer;
        result = prime * result + ((order == null) ? 0 : order.hashCode());
        result = prime * result + ((orderItemAdjustments == null) ? 0 : orderItemAdjustments.hashCode());
        result = prime * result + ((orderItemType == null) ? 0 : orderItemType.hashCode());
        result = prime * result + ((personalMessage == null) ? 0 : personalMessage.hashCode());
        result = prime * result + ((price == null) ? 0 : price.hashCode());
        result = prime * result + quantity;
        result = prime * result + ((retailPrice == null) ? 0 : retailPrice.hashCode());
        result = prime * result + ((salePrice == null) ? 0 : salePrice.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OrderItemImpl other = (OrderItemImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (adjustmentPrice == null) {
            if (other.adjustmentPrice != null)
                return false;
        } else if (!adjustmentPrice.equals(other.adjustmentPrice))
            return false;
        if (candidateItemOffers == null) {
            if (other.candidateItemOffers != null)
                return false;
        } else if (!candidateItemOffers.equals(other.candidateItemOffers))
            return false;
        if (category == null) {
            if (other.category != null)
                return false;
        } else if (!category.equals(other.category))
            return false;
        if (giftWrapOrderItem == null) {
            if (other.giftWrapOrderItem != null)
                return false;
        } else if (!giftWrapOrderItem.equals(other.giftWrapOrderItem))
            return false;
        if (markedForOffer != other.markedForOffer)
            return false;
        if (order == null) {
            if (other.order != null)
                return false;
        } else if (!order.equals(other.order))
            return false;
        if (orderItemAdjustments == null) {
            if (other.orderItemAdjustments != null)
                return false;
        } else if (!orderItemAdjustments.equals(other.orderItemAdjustments))
            return false;
        if (orderItemType == null) {
            if (other.orderItemType != null)
                return false;
        } else if (!orderItemType.equals(other.orderItemType))
            return false;
        if (personalMessage == null) {
            if (other.personalMessage != null)
                return false;
        } else if (!personalMessage.equals(other.personalMessage))
            return false;
        if (price == null) {
            if (other.price != null)
                return false;
        } else if (!price.equals(other.price))
            return false;
        if (quantity != other.quantity)
            return false;
        if (retailPrice == null) {
            if (other.retailPrice != null)
                return false;
        } else if (!retailPrice.equals(other.retailPrice))
            return false;
        if (salePrice == null) {
            if (other.salePrice != null)
                return false;
        } else if (!salePrice.equals(other.salePrice))
            return false;
        return true;
    }

}
