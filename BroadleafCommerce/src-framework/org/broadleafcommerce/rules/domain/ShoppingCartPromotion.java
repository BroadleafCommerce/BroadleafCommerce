package org.broadleafcommerce.rules.domain;

import java.io.Serializable;

//@Entity
//@Table(name = "BLC_PROMOTION_RULE_DEFINITION")
public class ShoppingCartPromotion implements Serializable {

	private static final long serialVersionUID = 1L;

//	@Id
//	@GeneratedValue
//	@Column(name = "SHOPPING_CART_PROMOTION_RULE_ID")
	private Long id;

//	@Column(name = "NAME")
	private String name;

//	@Column(name = "COUPON_CODE")
	private String couponCode;

//  @Column(name = "ORDER_TOTAL")
	private double orderTotal;

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setOrderTotal(double orderTotal) {
		this.orderTotal = orderTotal;
	}

	public double getOrderTotal() {
		return orderTotal;
	}

}
