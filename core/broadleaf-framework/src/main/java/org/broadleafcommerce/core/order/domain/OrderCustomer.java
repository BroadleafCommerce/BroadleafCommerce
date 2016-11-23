package org.broadleafcommerce.core.order.domain;


public interface OrderCustomer {

    public Long getId();
    
    public String getFirstName();
    
    public String getLastName();
    
    public String getEmailAddress();

    public void setEmailAddress(String emailAddress);

    public void setLastName(String lastName);

    public void setFirstName(String firstName);

    public void setId(Long id);
}
