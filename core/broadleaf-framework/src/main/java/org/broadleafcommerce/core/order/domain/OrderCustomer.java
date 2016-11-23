package org.broadleafcommerce.core.order.domain;


public interface OrderCustomer {

    public Long getId();
    
    public Long getExternalId();
    
    public String getFirstName();
    
    public String getLastName();
    
    public String getEmailAddress();

    public void setId(Long id);
    
    public void setExternalId(Long externalId);
    
    public void setEmailAddress(String emailAddress);

    public void setLastName(String lastName);

    public void setFirstName(String firstName);

}
