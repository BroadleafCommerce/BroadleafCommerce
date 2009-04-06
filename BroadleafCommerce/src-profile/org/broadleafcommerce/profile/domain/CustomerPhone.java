package org.broadleafcommerce.profile.domain;

public interface CustomerPhone {

    public void setId(Long id);

    public Long getId();

    public void setPhoneName(String phoneName);

    public String getPhoneName();

    public Long getCustomerId();

    public void setCustomerId(Long customerId);

    public Phone getPhone();

    public void setPhone(Phone phone);
}
