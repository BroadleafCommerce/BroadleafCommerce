package org.broadleafcommerce.profile.domain;

public interface ContactInfo {

    public Long getId();

    public void setId(Long id);

    public Customer getCustomer();

    public void setCustomer(Customer customer);

    public String getPrimaryPhone();

    public void setPrimaryPhone(String primaryPhone);

    public String getSecondaryPhone();

    public void setSecondaryPhone(String secondaryPhone);

    public String getEmail();

    public void setEmail(String email);

    public String getFax();

    public void setFax(String fax);
}
