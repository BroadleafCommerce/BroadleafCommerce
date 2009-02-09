package org.broadleafcommerce.order.domain;

import java.io.Serializable;

import org.broadleafcommerce.catalog.domain.SellableItem;

//@Entity
//@Table(name = "SC_ORDER_ITEM")
public class OrderItem implements Serializable {

	private static final long serialVersionUID = 1L;

//	@Id
//	@GeneratedValue
//	@Column(name = "ORDER_ITEM_ID")
	private Long id;
	
//	@ManyToOne
//	@JoinColumn(name = "SELLABLE_ITEM_ID", nullable=false)
	private SellableItem sellableItem;
	
//	@ManyToOne
//	@JoinColumn(name = "SC_ORDER_ID", nullable = false)
	private BroadleafOrder order;
	
//	@Column(name = "FINAL_PRICE")
	private double finalPrice;
	
//	@Column(name = "QUANTITY")
	private int quantity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SellableItem getSellableItem() {
		return sellableItem;
	}

	public void setSellableItem(SellableItem sellableItem) {
		this.sellableItem = sellableItem;
	}

	public BroadleafOrder getOrder() {
		return order;
	}

	public void setOrder(BroadleafOrder order) {
		this.order = order;
	}

	public double getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(double finalPrice) {
		this.finalPrice = finalPrice;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	
	
}
