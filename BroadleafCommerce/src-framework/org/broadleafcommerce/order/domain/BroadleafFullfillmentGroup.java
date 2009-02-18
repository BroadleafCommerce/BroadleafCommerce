package org.broadleafcommerce.order.domain;

import java.util.List;

import org.broadleafcommerce.profile.domain.Address;

public class BroadleafFullfillmentGroup implements FullfillmentGroup {
	private Long id;
	private Long orderId;
	private String referenceNumber;
	private List<FullfillmentGroupItem> fullfillmentGroupItems;
	private Address address;
	private Object method;
	private double cost;
	private String type;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public List<FullfillmentGroupItem> getFullfillmentGroupItems() {
		return fullfillmentGroupItems;
	}

	public void setFullfillmentGroupItems(
			List<FullfillmentGroupItem> fullfillmentGroupItems) {
		this.fullfillmentGroupItems = fullfillmentGroupItems;
	}

	@Override
	public Address getAddress() {
		return address;
	}

	@Override
	public void setAddress(Address address) {
		this.address = address;
	}

	@Override
	public Object getMethod() {
		return method;
	}

	@Override
	public void setMethod(Object fullfillmentMethod) {
		this.method = fullfillmentMethod;
	}

	@Override
	public double getCost() {
		return cost;
	}

	@Override
	public void setCost(double fullfillmentCost) {
		this.cost = fullfillmentCost;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}
	
	
}
