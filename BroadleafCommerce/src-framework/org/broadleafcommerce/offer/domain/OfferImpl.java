package org.broadleafcommerce.offer.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.broadleafcommerce.type.OfferDiscountType;
import org.broadleafcommerce.type.OfferScopeType;
import org.broadleafcommerce.type.OfferType;

@Entity
@Table(name = "BLC_OFFER")
public class OfferImpl implements Serializable, Offer {
	
	public static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name = "OFFER_ID")
	private Long id;
	
	@Column(name = "OFFER_NAME")
	private String name;
	
	@Column(name = "OFFER_TYPE")
	private OfferType type;
	
	@Column(name = "OFFER_DISCOUNT_TYPE")
	private OfferDiscountType discountType;
	
	@Column(name = "OFFER_SCOPE_TYPE")
	private OfferScopeType scopeType;
	
	@Column(name = "OFFER_VALUE")
	private BigDecimal value;
	
	@Column(name = "OFFER_PRIORITY")
	private int priority;
	
	@Column(name = "START_DATE")
	private Date startDate;
	
	@Column(name = "END_DATE")
	private Date endDate;
	
	@Column(name = "STACKABLE")
	private boolean stackable;
	
	@Column(name = "TARGET_SYSTEM")
	private boolean targetSystem;
	
	@OneToMany(mappedBy = "id", targetEntity = OfferOrderItemImpl.class)
	private List<OfferOrderItem> offerOrderItems;
	
	
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

	public OfferType getType() {
		return type;
	}

	public void setType(OfferType type) {
		this.type = type;
	}

	public OfferScopeType getScopeType() {
		return scopeType;
	}

	public void setScopeType(OfferScopeType useType) {
		this.scopeType = useType;
	}

	public OfferDiscountType getDiscountType() {
		return discountType;
	}

	@Override
	public void setDiscountType(OfferDiscountType type) {
		this.discountType = type;
		
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isStackable() {
		return stackable;
	}

	public void setStackable(boolean stackable) {
		this.stackable = stackable;
	}

	public boolean isTargetSystem() {
		return targetSystem;
	}

	public void setTargetSystem(boolean targetSystem) {
		this.targetSystem = targetSystem;
	}

	public List<OfferOrderItem> getOfferOrderItems() {
		return offerOrderItems;
	}

	public void setOfferOrderItems(List<OfferOrderItem> offerOrderItems) {
		this.offerOrderItems = offerOrderItems;
	}

	
	
}
