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
package org.broadleafcommerce.core.offer.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemImpl;
import org.broadleafcommerce.money.Money;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

@Entity
@Table(name = "BLC_CANDIDATE_ITEM_OFFER")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
public class CandidateItemOfferImpl extends CandidateQualifiedOfferImpl implements CandidateItemOffer {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "CandidateItemOfferId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "CandidateItemOfferId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "CandidateItemOfferImpl", allocationSize = 50)
    @Column(name = "CANDIDATE_ITEM_OFFER_ID")
    protected Long id;

    @ManyToOne(targetEntity = OrderItemImpl.class, optional=false)
    @JoinColumn(name = "ORDER_ITEM_ID")
    @Index(name="CANDIDATE_ITEM_INDEX", columnNames={"ORDER_ITEM_ID"})
    protected OrderItem orderItem;

    @ManyToOne(targetEntity = OfferImpl.class, optional=false)
    @JoinColumn(name = "OFFER_ID")
    @Index(name="CANDIDATE_ITEMOFFER_INDEX", columnNames={"OFFER_ID"})
    protected Offer offer;

    @Column(name = "DISCOUNTED_PRICE")
    @Deprecated
    private BigDecimal discountedPrice;
    
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = OrderItemImpl.class)
    @JoinTable(name = "BLC_QUALIFIER_ITEM_XREF", joinColumns = @JoinColumn(name = "CANDIDATE_ITEM_OFFER_ID", referencedColumnName = "CANDIDATE_ITEM_OFFER_ID"), inverseJoinColumns = @JoinColumn(name = "ORDER_ITEM_ID", referencedColumnName = "ORDER_ITEM_ID"))
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<OrderItem> candidateQualifierItems = new ArrayList<OrderItem>();
    
    @Transient
    protected Money potentialSavings; 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public int getPriority() {
        return offer.getPriority();
    }

    public Offer getOffer() {
        return offer;
    }

    public List<OrderItem> getCandidateQualifierItems() {
		return candidateQualifierItems;
	}

	public void setCandidateQualifierItems(List<OrderItem> candidateQualifierItems) {
		this.candidateQualifierItems = candidateQualifierItems;
	}
	
	public Money getPotentialSavings() {
		if (potentialSavings == null) {
			potentialSavings = calculatePotentialSavings();
		}
		return potentialSavings;
	}

	/**
	 * This method determines how much the customer might save using this promotion for the
	 * purpose of sorting promotions with the same priority. The assumption is that any possible
	 * target specified for BOGO style offers are of equal or lesser value. We are using
	 * a calculation based on the qualifiers here strictly for rough comparative purposes.
	 *  
	 * If two promotions have the same priority, the one with the highest potential savings
	 * will be used as the tie-breaker to determine the order to apply promotions.
	 * 
	 * This method makes a good approximation of the promotion value as determining the exact value
	 * would require all permutations of promotions to be run resulting in a costly 
	 * operation.
	 * 
	 * @return
	 */
	protected Money calculatePotentialSavings() {
		Money savings = new Money(0);
		int maxUses = calculateMaximumNumberOfUses();
		int appliedCount = 0;
		
		for (OrderItem chgItem : candidateTargets) {
			int qtyToReceiveSavings = Math.min(chgItem.getQuantity(), maxUses);
			savings = calculateSavingsForOrderItem(chgItem, qtyToReceiveSavings);

			appliedCount = appliedCount + qtyToReceiveSavings;
			if (appliedCount >= maxUses) {
				return savings;
			}
		}
		
		return savings;
	}
	
	/**
	 * Determines the maximum number of times this promotion can be used based on the
	 * ItemCriteria and promotion's maxQty setting.
	 */
	protected int calculateMaximumNumberOfUses() {		
		int maxMatchesFound = 9999; // set arbitrarily high / algorithm will adjust down	
		
		int numberOfUsesForThisItemCriteria = calculateMaxUsesForItemCriteria(getOffer().getTargetItemCriteria(), getOffer());
		maxMatchesFound = Math.min(maxMatchesFound, numberOfUsesForThisItemCriteria);

		return Math.min(maxMatchesFound, getOffer().getMaxUses());
	}
	
	protected int calculateMaxUsesForItemCriteria(OfferItemCriteria itemCriteria, Offer promotion) {
		int numberOfTargets = 0;
		int numberOfUsesForThisItemCriteria = 9999;
		
		if (candidateTargets != null && itemCriteria != null) {
			for(OrderItem potentialTarget : candidateTargets) {
				numberOfTargets += potentialTarget.getQuantityAvailableToBeUsedAsTarget(promotion);
			}
			numberOfUsesForThisItemCriteria = numberOfTargets / itemCriteria.getQuantity();
		}
		
		return numberOfUsesForThisItemCriteria;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((discountedPrice == null) ? 0 : discountedPrice.hashCode());
        result = prime * result + ((offer == null) ? 0 : offer.hashCode());
        result = prime * result + ((orderItem == null) ? 0 : orderItem.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CandidateItemOfferImpl other = (CandidateItemOfferImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (discountedPrice == null) {
            if (other.discountedPrice != null)
                return false;
        } else if (!discountedPrice.equals(other.discountedPrice))
            return false;
        if (offer == null) {
            if (other.offer != null)
                return false;
        } else if (!offer.equals(other.offer))
            return false;
        if (orderItem == null) {
            if (other.orderItem != null)
                return false;
        } else if (!orderItem.equals(other.orderItem))
            return false;
        return true;
    }

}
