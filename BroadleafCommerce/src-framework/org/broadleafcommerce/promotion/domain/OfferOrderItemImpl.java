package org.broadleafcommerce.promotion.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.OrderItemImpl;

@Entity
@Table(name = "BLC_OFFER_ORDERITEM")
public class OfferOrderItemImpl implements OfferOrderItem {

	public static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name = "OFFER_ORDERITEM_ID")
	private Long id;
	
	@ManyToOne(targetEntity = OfferImpl.class)
	Offer offer;
	
	@ManyToOne(targetEntity = OrderItemImpl.class)
	OrderItem orderItem;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
	}

	public OrderItem getOrderItem() {
		return orderItem;
	}

	public void setOrderItem(OrderItem orderItem) {
		this.orderItem = orderItem;
	}
	
	
}
