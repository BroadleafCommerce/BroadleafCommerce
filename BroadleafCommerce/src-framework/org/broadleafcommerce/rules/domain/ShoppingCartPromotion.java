package org.broadleafcommerce.rules.domain;

import java.io.Serializable;
import java.util.Date;

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

//	@Column(name = "DESCRIPTION")
	private String description;

//	@Column(name = "CATEGORY")
	private String category;

//	@Column(name = "ACTIVATION_DATE")
	private Date activationDate;

//	@Column(name = "EXPIRATION_DATE")
	private Date expirationDate;

//	@Column(name = "CREATED")
	private Date created;

//	@Column(name = "MODIFIED")
	private Date modified;
	
//	@Column(name = "PRIORITY")
	private int priority;
	
//	@Column(name = "COUPON_CODE")
	private String couponCode;
	
//	@Column(name = "CUSTOMER_REDEMPTION_LIMIT")
	private int customerRedemptionLimit;
	
//	@Column(name = "COUPON_REDEMPTION_LIMIT")
	private int couponRedemptionLimit;

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

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getCreated() {
		return created;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Date getModified() {
		return modified;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCustomerRedemptionLimit(int customerRedemptionLimit) {
		this.customerRedemptionLimit = customerRedemptionLimit;
	}

	public int getCustomerRedemptionLimit() {
		return customerRedemptionLimit;
	}

	public void setCouponRedemptionLimit(int couponRedemptionLimit) {
		this.couponRedemptionLimit = couponRedemptionLimit;
	}

	public int getCouponRedemptionLimit() {
		return couponRedemptionLimit;
	}

}
