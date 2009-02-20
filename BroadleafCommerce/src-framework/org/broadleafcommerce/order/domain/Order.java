package org.broadleafcommerce.order.domain;

import java.util.List;

import org.broadleafcommerce.common.domain.Auditable;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.broadleafcommerce.profile.domain.Customer;

public interface Order {

    public Long getId();

    public void setId(Long id);

    public Auditable getAuditable();

    public void setAuditable(Auditable auditable);
	
    public String getStatus();

    public void setStatus(String orderStatus);

    public double getTotalAmount();

    public void setTotalAmount(double totalAmount);

    public Customer getCustomer();

    public void setCustomer(Customer customer);

    public ContactInfo getContactInfo();

    public void setContactInfo(ContactInfo contactInfo);

	public String getType();

	public void setType(String type);
	
	public List<FullfillmentGroup> getFullfillmentGroups();
	
	public void setFullfillmentGroups(List<FullfillmentGroup> fullfillmentGroups);
	
}
