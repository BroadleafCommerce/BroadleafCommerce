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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.CandidateItemOfferImpl;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustmentImpl;
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount;
import org.broadleafcommerce.core.offer.service.discount.PromotionQualifier;
import org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import org.broadleafcommerce.gwt.client.presentation.SupportedFieldType;
import org.broadleafcommerce.money.Money;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER_ITEM")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
public class OrderItemImpl implements OrderItem, Cloneable {

	private static final Log LOG = LogFactory.getLog(OrderItemImpl.class);
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "OrderItemId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "OrderItemId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "OrderItemImpl", allocationSize = 50)
    @Column(name = "ORDER_ITEM_ID")
    protected Long id;

    @ManyToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "CATEGORY_ID")
    @Index(name="ORDERITEM_CATEGORY_INDEX", columnNames={"CATEGORY_ID"})
    @NotFound(action = NotFoundAction.IGNORE)
    protected Category category;

    @ManyToOne(targetEntity = OrderImpl.class)
    @JoinColumn(name = "ORDER_ID")
    @Index(name="ORDERITEM_ORDER_INDEX", columnNames={"ORDER_ID"})
    protected Order order;

    @Column(name = "RETAIL_PRICE")
    @AdminPresentation(friendlyName="Item Retail Price", order=2, group="Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal retailPrice;

    @Column(name = "SALE_PRICE")
    @AdminPresentation(friendlyName="Item Sale Price", order=3, group="Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal salePrice;

    @Column(name = "PRICE")
    @AdminPresentation(friendlyName="Item Price", order=4, group="Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal price;

    @Column(name = "QUANTITY", nullable=false)
    @AdminPresentation(friendlyName="Item Quantity", order=5, group="Pricing")
    protected int quantity;
    
    @Column(name = "NAME")
    @AdminPresentation(friendlyName="Item Name", order=1, group="Description", prominent=true)
    protected String name;

    @ManyToOne(targetEntity = PersonalMessageImpl.class, cascade = { CascadeType.ALL })
    @JoinColumn(name = "PERSONAL_MESSAGE_ID")
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @Index(name="ORDERITEM_MESSAGE_INDEX", columnNames={"PERSONAL_MESSAGE_ID"})
    protected PersonalMessage personalMessage;

    @ManyToOne(targetEntity = GiftWrapOrderItemImpl.class, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinColumn(name = "GIFT_WRAP_ITEM_ID", nullable = true)
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @Index(name="ORDERITEM_GIFT_INDEX", columnNames={"GIFT_WRAP_ITEM_ID"})
    protected GiftWrapOrderItem giftWrapOrderItem;

    @OneToMany(mappedBy = "orderItem", targetEntity = OrderItemAdjustmentImpl.class, cascade = { CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<OrderItemAdjustment> orderItemAdjustments = new ArrayList<OrderItemAdjustment>();

    @OneToMany(mappedBy = "orderItem", targetEntity = CandidateItemOfferImpl.class, cascade = { CascadeType.ALL })
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<CandidateItemOffer> candidateItemOffers = new ArrayList<CandidateItemOffer>();

    @Transient
    protected int markedForOffer = 0;  
    
    @Transient
    //TODO move these transient elements to a promotion management interface since they are not intended to be permanent facets of the API
    protected BigDecimal adjustmentPrice; // retailPrice with adjustments
    
    @Transient
    //TODO move these transient elements to a promotion management interface since they are not intended to be permanent facets of the API
    protected List<PromotionDiscount> promotionDiscounts = new ArrayList<PromotionDiscount>();
    
    @Transient
    //TODO move these transient elements to a promotion management interface since they are not intended to be permanent facets of the API
    protected List<PromotionQualifier> promotionQualifiers = new ArrayList<PromotionQualifier>();

    @Column(name = "ORDER_ITEM_TYPE")
    @Index(name="ORDERITEM_TYPE_INDEX", columnNames={"ORDER_ITEM_TYPE"})
    @AdminPresentation(friendlyName="Item Type", order=6, group="Description", fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.order.service.type.OrderItemType")
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
    
    public Money getPriceBeforeAdjustments(boolean allowSalesPrice) {
    	updatePrices();
        Money currentPrice = null;
        if (salePrice != null && allowSalesPrice) {
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

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
        return Collections.unmodifiableList(this.orderItemAdjustments);
    }

    public void addOrderItemAdjustment(OrderItemAdjustment orderItemAdjustment) {
        if (this.orderItemAdjustments.size() == 0) {
            adjustmentPrice = retailPrice;
        }
        adjustmentPrice = adjustmentPrice.subtract(orderItemAdjustment.getValue().getAmount());
        this.orderItemAdjustments.add(orderItemAdjustment);
        getOrder().resetTotalitarianOfferApplied();
    }

    public int removeAllAdjustments() {
    	int removedAdjustmentCount = 0;
        if (orderItemAdjustments != null) {
        	removedAdjustmentCount = orderItemAdjustments.size();
            orderItemAdjustments.clear();
        }
        adjustmentPrice = null;
        if (getOrder() != null) {
        	getOrder().resetTotalitarianOfferApplied();
        }
        if (promotionDiscounts != null) {
        	promotionDiscounts.clear();
        }
        if (promotionQualifiers != null) {
        	promotionQualifiers.clear();
        }
        assignFinalPrice();
        return removedAdjustmentCount;
    }

    public void setOrderItemAdjustments(List<OrderItemAdjustment> orderItemAdjustments) {    	
        this.orderItemAdjustments = orderItemAdjustments;
    }

    public Money getAdjustmentValue() {
        Money adjustmentValue = new Money(0);
        for (OrderItemAdjustment itemAdjustment : orderItemAdjustments) {
            adjustmentValue = adjustmentValue.add(itemAdjustment.getValue());
        }
        return adjustmentValue;
    }

    @Deprecated
    public Money getAdjustmentPrice() {
        return adjustmentPrice == null ? null : new Money(adjustmentPrice);
    }

    @Deprecated
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
        return OrderItemType.getInstance(orderItemType);
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
    	for (OrderItemAdjustment orderItemAdjustment : orderItemAdjustments) {
    		boolean notCombineableApplied = !orderItemAdjustment.getOffer().isCombinableWithOtherOffers() || (orderItemAdjustment.getOffer().isTotalitarianOffer() != null && orderItemAdjustment.getOffer().isTotalitarianOffer());
    		if (notCombineableApplied) return true;
    	}
    	
    	return false;
	}

	public boolean isHasOrderItemAdjustments() {
		return orderItemAdjustments != null && orderItemAdjustments.size() > 0;
	}

	public boolean updatePrices() {
        return false;
    }
	
	public List<PromotionDiscount> getPromotionDiscounts() {
		return promotionDiscounts;
	}

	public void setPromotionDiscounts(List<PromotionDiscount> promotionDiscounts) {
		this.promotionDiscounts = promotionDiscounts;
	}

	public List<PromotionQualifier> getPromotionQualifiers() {
		return promotionQualifiers;
	}

	public void setPromotionQualifiers(List<PromotionQualifier> promotionQualifiers) {
		this.promotionQualifiers = promotionQualifiers;
	}

	public int getQuantityAvailableToBeUsedAsQualifier(Offer promotion) {
		int qtyAvailable = getQuantity();
		// Any quantities of this item that have already received the promotion are not eligible.
		for (PromotionDiscount promotionDiscount : promotionDiscounts) {
			if (promotionDiscount.getPromotion().equals(promotion)) {
				qtyAvailable = qtyAvailable - promotionDiscount.getQuantity();
			} else {
				// Item's that receive discounts are also qualifiers
				OfferItemRestrictionRuleType qualifierType = promotionDiscount.getPromotion().getOfferItemTargetRuleType();
				if (OfferItemRestrictionRuleType.NONE.equals(qualifierType) || OfferItemRestrictionRuleType.TARGET.equals(qualifierType)) {
					qtyAvailable = qtyAvailable - promotionDiscount.getQuantity();
				}
			}
		}
		
		// Any quantities of this item that have already been used as a qualifier for this promotion or for 
		// another promotion that has a qualifier type of NONE or TARGET_ONLY cannot be used for this promotion
		for (PromotionQualifier promotionQualifier : promotionQualifiers) {
			if (promotionQualifier.getPromotion().equals(promotion)) {
				qtyAvailable = qtyAvailable - promotionQualifier.getQuantity();
			} else {
				OfferItemRestrictionRuleType qualifierType = promotionQualifier.getPromotion().getOfferItemQualifierRuleType();
				if (OfferItemRestrictionRuleType.NONE.equals(qualifierType) || OfferItemRestrictionRuleType.TARGET.equals(qualifierType)) {
					qtyAvailable = qtyAvailable - promotionQualifier.getQuantity();
				}
			}
		}
		return qtyAvailable;
	}
	
	public int getQuantityAvailableToBeUsedAsTarget(Offer promotion) {
		int qtyAvailable = getQuantity();
		
		// 1. Any quantities of this item that have already received the promotion are not eligible.
		// 2. If this promotion is not stackable then any quantities that have received discounts
		//    from other promotions cannot receive this discount
		// 3. If this promotion is stackable then any quantities that have received discounts from
		//    other stackable promotions are eligible to receive this discount as well
		boolean stackable = promotion.isStackable();
		
		// Any quantities of this item that have already received the promotion are not eligible.
		for (PromotionDiscount promotionDiscount : promotionDiscounts) {
			if (promotionDiscount.getPromotion().equals(promotion) || ! stackable) {
				qtyAvailable = qtyAvailable - promotionDiscount.getQuantity();
			} else if (promotionDiscount.getPromotion().isStackable()) {
				// The other promotion is Stackable, but we must make sure that the item qualifier also allows
				// it to be reused as a target.   
				OfferItemRestrictionRuleType qualifierType = promotionDiscount.getPromotion().getOfferItemTargetRuleType();
				if (OfferItemRestrictionRuleType.NONE.equals(qualifierType) || OfferItemRestrictionRuleType.QUALIFIER.equals(qualifierType)) {
					qtyAvailable = qtyAvailable - promotionDiscount.getQuantity();
				}
			}
		}
		
		// 4.  Any quantities of this item that have been used as a qualifier for this promotion are not eligible as targets
		// 5.  Any quantities of this item that have been used as a qualifier for another promotion that does not allow the qualifier to be reused
		//     must be deduced from the qtyAvailable.
		for (PromotionQualifier promotionQualifier : promotionQualifiers) {
			if (promotionQualifier.getPromotion().equals(promotion)) {
				qtyAvailable = qtyAvailable - promotionQualifier.getQuantity();
			} else {
				OfferItemRestrictionRuleType qualifierType = promotionQualifier.getPromotion().getOfferItemQualifierRuleType();
				if (OfferItemRestrictionRuleType.NONE.equals(qualifierType) || OfferItemRestrictionRuleType.QUALIFIER.equals(qualifierType)) {
					qtyAvailable = qtyAvailable - promotionQualifier.getQuantity();
				}
			}
		}
		
		return qtyAvailable;
	}
	
	public void addPromotionQualifier(CandidateItemOffer candidatePromotion, OfferItemCriteria itemCriteria, int quantity) {
		PromotionQualifier pq = lookupOrCreatePromotionQualifier(candidatePromotion);
		pq.incrementQuantity(quantity);
		pq.setItemCriteria(itemCriteria);
	}
	
	public void addPromotionDiscount(CandidateItemOffer candidatePromotion, OfferItemCriteria itemCriteria, int quantity) {
		PromotionDiscount pd = lookupOrCreatePromotionDiscount(candidatePromotion);
		if (pd == null) {
			return;
		}
		pd.incrementQuantity(quantity);
		pd.setItemCriteria(itemCriteria);
		pd.setCandidateItemOffer(candidatePromotion);
	}
	
	protected PromotionQualifier lookupOrCreatePromotionQualifier(CandidateItemOffer candidatePromotion) {
		Offer promotion = candidatePromotion.getOffer();
		for(PromotionQualifier pq : promotionQualifiers) {
			if (pq.getPromotion().equals(promotion)) {
				return pq;
			}
		}
		
		PromotionQualifier pq = new PromotionQualifier();
		pq.setPromotion(promotion);
		promotionQualifiers.add(pq);
		return pq;
	}
	
	protected PromotionDiscount lookupOrCreatePromotionDiscount(CandidateItemOffer candidatePromotion) {
		Offer promotion = candidatePromotion.getOffer();
		for(PromotionDiscount pd : promotionDiscounts) {
			if (pd.getPromotion().equals(promotion)) {
				return pd;
			}
		}
		
		PromotionDiscount pd = new PromotionDiscount();
		pd.setPromotion(promotion);
		
		promotionDiscounts.add(pd);
		return pd;
	}
	
	public void clearAllNonFinalizedQuantities() {
		clearAllNonFinalizedDiscounts();
		clearAllNonFinalizedQualifiers();
	}
	
	public void clearAllDiscount() {
		promotionDiscounts.clear();
	}
	
	public void clearAllQualifiers() {
		promotionQualifiers.clear();
	}
	
	protected void clearAllNonFinalizedDiscounts() {
		Iterator<PromotionDiscount> promotionDiscountIterator = promotionDiscounts.iterator();
		while (promotionDiscountIterator.hasNext()) {
			PromotionDiscount promotionDiscount = promotionDiscountIterator.next();
			if (promotionDiscount.getFinalizedQuantity() == 0) {
				// If there are no quantities of this item that are finalized, then remove the item.
				promotionDiscountIterator.remove();
			} else {
				// Otherwise, set the quantity to the number of finalized items.
				promotionDiscount.setQuantity(promotionDiscount.getFinalizedQuantity());
			}
		}	
	}
	
	protected void clearAllNonFinalizedQualifiers() {
		Iterator<PromotionQualifier> promotionQualifierIterator = promotionQualifiers.iterator();
		while (promotionQualifierIterator.hasNext()) {
			PromotionQualifier promotionQualifier = promotionQualifierIterator.next();
			if (promotionQualifier.getFinalizedQuantity() == 0) {
				// If there are no quantities of this item that are finalized, then remove the item.
				promotionQualifierIterator.remove();
			} else {
				// Otherwise, set the quantity to the number of finalized items.
				promotionQualifier.setQuantity(promotionQualifier.getFinalizedQuantity());
			}
		}	
	}
	
	public void finalizeQuantities() {
		for (PromotionDiscount promotionDiscount : promotionDiscounts) {
			promotionDiscount.setFinalizedQuantity(promotionDiscount.getQuantity());
		}
		for (PromotionQualifier promotionQualifier : promotionQualifiers) {
			promotionQualifier.setFinalizedQuantity(promotionQualifier.getQuantity());
		}
	}
	
	protected int getPromotionDiscountMismatchQuantity() {
		Iterator<PromotionDiscount> promotionDiscountIterator = promotionDiscounts.iterator();
		while (promotionDiscountIterator.hasNext()) {
			PromotionDiscount promotionDiscount = promotionDiscountIterator.next();
			if (promotionDiscount.getQuantity() != quantity) {
				return promotionDiscount.getQuantity();
			}
		}
		return 0;
	}
	
	public void checkCloneable(OrderItem orderItem) throws CloneNotSupportedException, SecurityException, NoSuchMethodException {
		Method cloneMethod = orderItem.getClass().getMethod("clone", new Class[]{});
		if (cloneMethod.getDeclaringClass().getName().startsWith("org.broadleafcommerce") && !orderItem.getClass().getName().startsWith("org.broadleafcommerce")) {
			//subclass is not implementing the clone method
			throw new CloneNotSupportedException("Custom extensions and implementations should implement clone in order to guarantee split and merge operations are performed accurately");
		}
	}
	
	public OrderItem clone() {
		//this is likely an extended class - instantiate from the fully qualified name via reflection
		OrderItem orderItem;
		try {
			orderItem = (OrderItem) Class.forName(this.getClass().getName()).newInstance();
			try {
				checkCloneable(orderItem);
			} catch (CloneNotSupportedException e) {
				LOG.warn("Clone implementation missing in inheritance hierarchy outside of Broadleaf: " + orderItem.getClass().getName(), e);
			}
			if (getCandidateItemOffers() != null) {
				for (CandidateItemOffer candidate : getCandidateItemOffers()) {
					CandidateItemOffer clone = candidate.clone();
					clone.setOrderItem(orderItem);
					orderItem.getCandidateItemOffers().add(clone);
				}
			}
			orderItem.setCategory(getCategory());
			orderItem.setGiftWrapOrderItem(getGiftWrapOrderItem());
			orderItem.setName(getName());
			orderItem.setOrder(getOrder());
			orderItem.setOrderItemType(getOrderItemType());
			orderItem.setPersonalMessage(getPersonalMessage());
			orderItem.setQuantity(getQuantity());
			orderItem.setRetailPrice(getRetailPrice());
			orderItem.setSalePrice(getSalePrice());
			orderItem.setAdjustmentPrice(getAdjustmentPrice());
			orderItem.setPrice(getPrice());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
 		
 		return orderItem;
	}
	
	public List<OrderItem> split() {	
		List<OrderItem> chargeableItems = null;
		if (getQuantity() != 1) {
			int discountQty = getPromotionDiscountMismatchQuantity();
			if (discountQty != 0) {
				// Item needs to be split.
				chargeableItems = new ArrayList<OrderItem>();
				OrderItem firstItem = clone();
				OrderItem secondItem = clone();
				chargeableItems.add(firstItem);
				chargeableItems.add(secondItem);
				
				// set the quantity
				int firstItemQty = discountQty;
				int secondItemQty = quantity - discountQty;
				firstItem.setQuantity(firstItemQty);
				secondItem.setQuantity(secondItemQty);
				
				// distribute the qualifiers
				for(PromotionQualifier pq : promotionQualifiers) {
					if (pq.getQuantity() > firstItemQty) {
						PromotionQualifier pq1 = pq.copy();
						pq1.resetQty(firstItemQty);
						firstItem.getPromotionQualifiers().add(pq1);
						
						PromotionQualifier pq2 = pq.copy();
						pq2.resetQty(pq.getQuantity() - firstItemQty);
						secondItem.getPromotionQualifiers().add(pq2);
						
					} else {
						firstItem.getPromotionQualifiers().add(pq);
					}
				}
				
				// distribute the promotions
				for(PromotionDiscount pd : promotionDiscounts) {
					if (pd.getQuantity() > firstItemQty) {
						PromotionDiscount pd1 = pd.copy();
						pd1.resetQty(firstItemQty);
						firstItem.getPromotionDiscounts().add(pd1);
						
						PromotionDiscount pd2 = pd.copy();
						pd2.resetQty(pd.getQuantity() - firstItemQty);
						secondItem.getPromotionDiscounts().add(pd2);
					} else {
						firstItem.getPromotionDiscounts().add(pd);
					}
				}
			}
		}
		return chargeableItems;
	}

	public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((adjustmentPrice == null) ? 0 : adjustmentPrice.hashCode());
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((giftWrapOrderItem == null) ? 0 : giftWrapOrderItem.hashCode());
        result = prime * result + markedForOffer;
        result = prime * result + ((order == null) ? 0 : order.hashCode());
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
