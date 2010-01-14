package org.broadleafcommerce.vendor.cybersource.service.message;

import org.broadleafcommerce.util.money.Money;

public class CyberSourceItemRequest implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String shortDescription;
	private String description;
	private Money unitPrice;
	private Long quantity;
	
	public String getShortDescription() {
		return shortDescription;
	}
	
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Money getUnitPrice() {
		return unitPrice;
	}
	
	public void setUnitPrice(Money unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	public Long getQuantity() {
		return quantity;
	}
	
	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
