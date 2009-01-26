package org.springcommerce.util;

import java.util.List;

import org.springcommerce.order.domain.Order;
import org.springcommerce.order.domain.OrderPayment;
import org.springcommerce.order.domain.OrderShipping;
import org.springcommerce.profile.domain.ContactInfo;

public class Checkout {
	private Order order;
	private List<ContactInfo> userContactInfo;
	private ContactInfo contactInfo;
	private String selectedContactInfoId;
	private OrderShipping orderShipping;
	private OrderPayment orderPayment;
	
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}

	public ContactInfo getContactInfo() {
		return contactInfo;
	}
	public void setContactInfo(ContactInfo contactInfo) {
		this.contactInfo = contactInfo;
	}
	public OrderShipping getOrderShipping() {
		return orderShipping;
	}
	public void setOrderShipping(OrderShipping orderShipping) {
		this.orderShipping = orderShipping;
	}
	public OrderPayment getOrderPayment() {
		return orderPayment;
	}
	public void setOrderPayment(OrderPayment orderPayment) {
		this.orderPayment = orderPayment;
	}
	public List<ContactInfo> getUserContactInfo() {
		return userContactInfo;
	}
	public void setUserContactInfo(List<ContactInfo> userContactInfo) {
		this.userContactInfo = userContactInfo;
	}
	public String getSelectedContactInfoId() {
		return selectedContactInfoId;
	}
	public void setSelectedContactInfoId(String selectedContactInfoId) {
		this.selectedContactInfoId = selectedContactInfoId;
	}
	
	
}
