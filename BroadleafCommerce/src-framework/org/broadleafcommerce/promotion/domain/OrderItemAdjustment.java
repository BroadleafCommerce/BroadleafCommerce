package org.broadleafcommerce.promotion.domain;

import java.io.Serializable;

import org.broadleafcommerce.order.domain.OrderItem;

//@Entity
//@Table(name = "BLC_ORDER_ITEM_ADJUSTMENT")
public class OrderItemAdjustment implements Serializable {

	public static final long serialVersionUID = 1L;
	
//	@Id
//	@GeneratedValue
//	@Column(name = "ITEM_ADJUSTMENT_ID")
	private Long id;
	
//	@ManyToOne
//	@JoinColumn(name = "SALES_ORDER_ITEM_ID")
	private OrderItem orderItem;
	
//	@ManyToOne
//	@JoinColumn(name = "PROMOTION_ID")
	private Promotion promotion;

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

	public Promotion getPromotion() {
		return promotion;
	}

	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}
	
	
	
	
}
