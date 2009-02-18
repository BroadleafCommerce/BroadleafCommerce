package org.broadleafcommerce.order.domain;

import java.util.List;

import org.broadleafcommerce.profile.domain.Address;

public interface FullfillmentGroup {

	public Long getId();
	
	public void setId(Long id);
	
	public Long getOrderId();
	
	public void setOrderId(Long orderId);
	
	public Address getAddress();
	
	public void setAddress(Address address);		
	
	public List<FullfillmentGroupItem> getFullfillmentGroupItems();
	
	public void setFullfillmentGroupItems(List<FullfillmentGroupItem> fullfillmentGroupItems);
	
	public Object getMethod();
	
	public void setMethod(Object fullfillmentMethod);
	
	public double getCost();
	
	public void setCost(double fullfillmentCost);
	
	public String getReferenceNumber();
	
	public void setReferenceNumber(String referenceNumber);

	public String getType();

	public void setType(String type);
	
}
