package org.broadleafcommerce.profile.domain;

public interface Phone {

    public Long getId();

    public void setId(Long id);

    public Customer getCustomer();

    public void setCustomer(Customer customer);

    public String getPhoneNumber();

    public void setPhoneNumber(String phoneNumber);

    public String getPhoneName();

    public void setPhoneName(String phoneName);
}
