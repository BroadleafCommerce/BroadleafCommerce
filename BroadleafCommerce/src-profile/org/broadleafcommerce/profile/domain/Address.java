package org.broadleafcommerce.profile.domain;

public interface Address {

    public void setId(Long id);

    public Long getId();

    public void setAddressLine1(String addressLine1);

    public String getAddressLine1();

    public void setAddressLine2(String addressLine2);

    public String getAddressLine2();

    public void setCity(String city);

    public String getCity();

    public void setState(State state);

    public State getState();

    public void setPostalCode(String postalCode);

    public String getPostalCode();

    // This field is temporary and will be removed later
    public String getZipFour();

    // This field is temporary and will be removed later
    public void setZipFour(String zipFour);

    public void setCountry(Country country);

    public Country getCountry();

    public String getTokenizedAddress();

    public void setTokenizedAddress(String tAddress);

    public Boolean getStandardized();

    public void setStandardized(Boolean standardized);

    public String getCompanyName();

    public void setCompanyName(String companyName);

    public boolean isDefault();

    public void setDefault(boolean isDefault);

    public String getFirstName();

    public void setFirstName(String firstName);

    public String getLastName();

    public void setLastName(String lastName);

    public String getPrimaryPhone();

    public void setPrimaryPhone(String primaryPhone);

    public String getSecondaryPhone();

    public void setSecondaryPhone(String secondaryPhone);

    public boolean isBusiness();

    public void setBusiness(boolean isBusiness);
}
