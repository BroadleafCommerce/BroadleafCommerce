package org.broadleafcommerce.order.domain;

import java.util.List;

import org.broadleafcommerce.profile.domain.Address;

public class BroadleafShippingInfo implements ShippingInfo {

	private List<OrderItem> orderItems;
	private Address address;
	private Object shippingMethod;
	private Object shippingPrice;
	
	
	
	@Override
	public ShippingInfo addOrderItemToShippingInfo(OrderItem orderItem) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	@Override
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
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
	public Object getShippingMethod() {
		return shippingMethod;
	}

	@Override
	public void setShippingMethod(Object shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

	@Override
	public Object getShippingPrice() {
		return shippingPrice;
	}

	@Override
	public void setShippingPrice(Object shippingPrice) {
		this.shippingPrice = shippingPrice;
	}
	
}
