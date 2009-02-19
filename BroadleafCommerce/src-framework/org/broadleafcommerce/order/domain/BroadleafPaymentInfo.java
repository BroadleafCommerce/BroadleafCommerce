package org.broadleafcommerce.order.domain;

import java.io.Serializable;

import org.broadleafcommerce.profile.domain.Address;

public class BroadleafPaymentInfo implements PaymentInfo,Serializable {

	private Long id;
	private Order order;
	private Address address;
	private double amount;
	private String referenceNumber;
	
	@Override
	public double getAmount() {
		return amount;
	}

	@Override
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	
	
	
}
