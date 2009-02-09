package org.broadleafcommerce.promotion.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.broadleafcommerce.order.domain.BroadleafOrder;

@Entity
@Table(name = "ORDER_ADJUSTMENTS")
public class OrderAdjustment implements Serializable {

	public static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name = "ORDER_ADJUSTMENT_ID")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "SALES_ORDER_ID")
	private BroadleafOrder salesOrder;
	
	@ManyToOne
	@JoinColumn(name = "PROMOTIONAL_ID")
	private Promotion promotion;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BroadleafOrder getSalesOrder() {
		return salesOrder;
	}

	public void setSalesOrder(BroadleafOrder salesOrder) {
		this.salesOrder = salesOrder;
	}

	public Promotion getPromotion() {
		return promotion;
	}

	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}
	
	
	
}
