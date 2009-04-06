package org.broadleafcommerce.profile.domain;

public interface Phone {

    public Long getId();

    public void setId(Long id);

    public String getPhoneNumber();

    public void setPhoneNumber(String phoneNumber);

    public boolean isDefault();

    public void setDefault(boolean isDefault);

    public boolean isActive();

    public void setActive(boolean isActive);
}
