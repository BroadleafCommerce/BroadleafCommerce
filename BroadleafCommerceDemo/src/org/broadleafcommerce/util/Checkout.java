package org.broadleafcommerce.util;

import java.util.List;

import org.broadleafcommerce.order.domain.BroadleafOrder;
import org.broadleafcommerce.order.domain.OrderItem;
import org.broadleafcommerce.order.domain.OrderPayment;
import org.broadleafcommerce.order.domain.OrderShipping;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.ContactInfo;

public class Checkout {
	private BroadleafOrder order;
	private List<OrderItem> orderItems;
	private List<ContactInfo> userContactInfo;
	private List<Address> addressList;
	private ContactInfo contactInfo;
	private String selectedContactInfoId;
	private String selectedShippingAddressId;
	private String selectedBillingAddressId;
	private OrderShipping orderShipping;
	private OrderPayment orderPayment;
	
	public BroadleafOrder getOrder() {
		return order;
	}
	public void setOrder(BroadleafOrder order) {
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
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}
	public List<Address> getAddressList() {
		return addressList;
	}
	public void setAddressList(List<Address> addressList) {
		this.addressList = addressList;
	}
	public String getSelectedShippingAddressId() {
		return selectedShippingAddressId;
	}
	public void setSelectedShippingAddressId(String selectedShippingAddressId) {
		this.selectedShippingAddressId = selectedShippingAddressId;
	}
	public String getSelectedBillingAddressId() {
		return selectedBillingAddressId;
	}
	public void setSelectedBillingAddressId(String selectedBillingAddressId) {
		this.selectedBillingAddressId = selectedBillingAddressId;
	}
	
}
