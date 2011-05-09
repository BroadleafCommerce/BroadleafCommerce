package org.broadleafcommerce.offer.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "BLC_OFFER_ITEM_CRITERIA")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
public class OfferItemCriteriaImpl implements OfferItemCriteria {
	
	public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "OfferItemCriteriaId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "OfferItemCriteriaId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "OfferItemCriteriaImpl", allocationSize = 50)
    @Column(name = "OFFER_ITEM_CRITERIA_ID")
    protected Long id;
    
    @Column(name = "RECEIVE_QUANTITY", nullable=false)
	protected Integer receiveQuantity;
    
    @Column(name = "REQUIRES_QUANTITY", nullable=false)
	protected Integer requiresQuantity;
    
    @Column(name = "ORDER_ITEM_MATCH_RULE", nullable=false)
	protected String orderItemMatchRule;
    
    @ManyToOne(targetEntity = OfferImpl.class)
    @JoinTable(name = "BLC_QUALIFIER_CRITERIA_OFFER_XREF", joinColumns = @JoinColumn(name = "OFFER_ITEM_CRITERIA_ID", referencedColumnName = "OFFER_ITEM_CRITERIA_ID"), inverseJoinColumns = @JoinColumn(name = "OFFER_ID", referencedColumnName = "OFFER_ID"))
    protected OfferImpl offer;

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.offer.domain.OfferItemCriteria#getId()
	 */
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.offer.domain.OfferItemCriteria#setId(java.lang.Long)
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.offer.domain.OfferItemCriteria#getReceiveQuantity()
	 */
	public Integer getReceiveQuantity() {
		return receiveQuantity;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.offer.domain.OfferItemCriteria#setReceiveQuantity(java.lang.Integer)
	 */
	public void setReceiveQuantity(Integer receiveQuantity) {
		this.receiveQuantity = receiveQuantity;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.offer.domain.OfferItemCriteria#getRequiresQuantity()
	 */
	public Integer getRequiresQuantity() {
		return requiresQuantity;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.offer.domain.OfferItemCriteria#setRequiresQuantity(java.lang.Integer)
	 */
	public void setRequiresQuantity(Integer requiresQuantity) {
		this.requiresQuantity = requiresQuantity;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.offer.domain.OfferItemCriteria#getOrderItemMatchRule()
	 */
	public String getOrderItemMatchRule() {
		return orderItemMatchRule;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.offer.domain.OfferItemCriteria#setOrderItemMatchRule(java.lang.String)
	 */
	public void setOrderItemMatchRule(String orderItemMatchRule) {
		this.orderItemMatchRule = orderItemMatchRule;
	}

	public OfferImpl getOffer() {
		return offer;
	}

	public void setOffer(OfferImpl offer) {
		this.offer = offer;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((orderItemMatchRule == null) ? 0 : orderItemMatchRule.hashCode());
		result = prime * result + ((receiveQuantity == null) ? 0 : receiveQuantity.hashCode());
		result = prime * result + ((requiresQuantity == null) ? 0 : requiresQuantity.hashCode());
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
		if (receiveQuantity == null) {
			if (other.receiveQuantity != null)
				return false;
		} else if (!receiveQuantity.equals(other.receiveQuantity))
			return false;
		if (requiresQuantity == null) {
			if (other.requiresQuantity != null)
				return false;
		} else if (!requiresQuantity.equals(other.requiresQuantity))
			return false;
		return true;
	}

}
