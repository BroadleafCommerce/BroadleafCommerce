package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.core.payment.domain.CustomerPayment;

import java.util.List;

public interface OrderCustomer {

    public Long getId();
    
    public Long getExternalId();
    
    public String getFirstName();
    
    public String getLastName();
    
    public String getEmailAddress();
    
    public List<CustomerPayment> getCustomerPayments();
    
    public boolean isAnonymous();

    public void setId(Long id);
    
    public void setExternalId(Long externalId);
    
    public void setEmailAddress(String emailAddress);

    public void setLastName(String lastName);

    public void setFirstName(String firstName);
    
    public void setCustomerPayments(List<CustomerPayment> customerPayments);
    
    public void setAnonymous(boolean anonymous);

}
