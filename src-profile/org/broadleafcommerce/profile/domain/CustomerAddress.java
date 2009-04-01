package org.broadleafcommerce.profile.domain;

public interface CustomerAddress {

    public void setId(Long id);

    public Long getId();

    public void setAddressName(String addressName);

    public String getAddressName();

    public Long getCustomerId();

    public void setCustomerId(Long customerId);

    public Address getAddress();

    public void setAddress(Address address);
}
