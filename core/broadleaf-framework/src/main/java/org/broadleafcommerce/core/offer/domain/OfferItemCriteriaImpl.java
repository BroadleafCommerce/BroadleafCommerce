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

import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * 
 * @author jfischer
 *
 */
@Entity
@Table(name = "BLC_OFFER_ITEM_CRITERIA")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(friendlyName = "OfferItemCriteriaImpl_baseOfferItemCriteria")
public class OfferItemCriteriaImpl implements OfferItemCriteria {
	
	public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "OfferItemCriteriaId")
    @GenericGenerator(
        name="OfferItemCriteriaId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="table_name", value="SEQUENCE_GENERATOR"),
            @Parameter(name="segment_column_name", value="ID_NAME"),
            @Parameter(name="value_column_name", value="ID_VAL"),
            @Parameter(name="segment_value", value="OfferItemCriteriaImpl"),
            @Parameter(name="increment_size", value="50"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.offer.domain.OfferItemCriteriaImpl")
        }
    )
    @Column(name = "OFFER_ITEM_CRITERIA_ID")
    @AdminPresentation(friendlyName = "OfferItemCriteriaImpl_Item_Criteria_Id", group = "OfferItemCriteriaImpl_Description", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;
    
    @Column(name = "QUANTITY", nullable=false)
    @AdminPresentation(friendlyName = "OfferItemCriteriaImpl_Quantity", group = "OfferItemCriteriaImpl_Description", visibility =VisibilityEnum.HIDDEN_ALL)
	protected Integer quantity;
    
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    @Column(name = "ORDER_ITEM_MATCH_RULE")
    @AdminPresentation(friendlyName = "OfferItemCriteriaImpl_Order_Item_Match_Rule", group = "OfferItemCriteriaImpl_Description", visibility = VisibilityEnum.HIDDEN_ALL)
	protected String orderItemMatchRule;
    
    @ManyToOne(targetEntity = OfferImpl.class)
    @JoinTable(name = "BLC_QUAL_CRIT_OFFER_XREF", joinColumns = @JoinColumn(name = "OFFER_ITEM_CRITERIA_ID"), inverseJoinColumns = @JoinColumn(name = "OFFER_ID"))
    protected Offer qualifyingOffer;

    @ManyToOne(targetEntity = OfferImpl.class)
    @JoinTable(name = "BLC_TAR_CRIT_OFFER_XREF", joinColumns = @JoinColumn(name = "OFFER_ITEM_CRITERIA_ID"), inverseJoinColumns = @JoinColumn(name = "OFFER_ID"))
    protected Offer targetOffer;

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.offer.domain.OfferItemCriteria#getId()
	 */
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.offer.domain.OfferItemCriteria#setId(java.lang.Long)
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.offer.domain.OfferItemCriteria#getReceiveQuantity()
	 */
	public Integer getQuantity() {
		return quantity;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.offer.domain.OfferItemCriteria#setReceiveQuantity(java.lang.Integer)
	 */
	public void setQuantity(Integer receiveQuantity) {
		this.quantity = receiveQuantity;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.offer.domain.OfferItemCriteria#getOrderItemMatchRule()
	 */
	public String getOrderItemMatchRule() {
		return orderItemMatchRule;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.core.offer.domain.OfferItemCriteria#setOrderItemMatchRule(java.lang.String)
	 */
	public void setOrderItemMatchRule(String orderItemMatchRule) {
		this.orderItemMatchRule = orderItemMatchRule;
	}

	public Offer getQualifyingOffer() {
		return qualifyingOffer;
	}

	public void setQualifyingOffer(Offer offer) {
		this.qualifyingOffer = offer;
	}

    public Offer getTargetOffer() {
        return targetOffer;
    }

    public void setTargetOffer(Offer targetOffer) {
        this.targetOffer = targetOffer;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((orderItemMatchRule == null) ? 0 : orderItemMatchRule.hashCode());
		result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());
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
		OfferItemCriteriaImpl other = (OfferItemCriteriaImpl) obj;
		
		if (id != null && other.id != null) {
            return id.equals(other.id);
        }
		
		if (orderItemMatchRule == null) {
			if (other.orderItemMatchRule != null)
				return false;
		} else if (!orderItemMatchRule.equals(other.orderItemMatchRule))
			return false;
		if (quantity == null) {
			if (other.quantity != null)
				return false;
		} else if (!quantity.equals(other.quantity))
			return false;
		return true;
	}

}
