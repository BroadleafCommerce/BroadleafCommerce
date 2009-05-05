package org.broadleafcommerce.profile.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.profile.domain.listener.TemporalTimestampListener;

@Entity
@EntityListeners(value = { TemporalTimestampListener.class })
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ADDRESS")
public class AddressImpl implements Address {

    @Id
    @GeneratedValue(generator = "AddressId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "AddressId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "AddressImpl", allocationSize = 1)
    @Column(name = "ADDRESS_ID")
    private Long id;

    @Column(name = "ADDRESS_LINE1")
    private String addressLine1;

    @Column(name = "ADDRESS_LINE2")
    private String addressLine2;

    @Column(name = "CITY")
    private String city;

    @Column(name = "POSTAL_CODE")
    private String postalCode;

    @Column(name = "COUNTY")
    private String county;

    @ManyToOne(cascade = CascadeType.PERSIST, targetEntity = StateImpl.class)
    @JoinColumn(name = "STATE_PROV_REGION")
    private State state;

    @ManyToOne(cascade = CascadeType.PERSIST, targetEntity = CountryImpl.class)
    @JoinColumn(name = "COUNTRY")
    private Country country;

    @Column(name = "TOKENIZED_ADDRESS")
    private String tokenizedAddress;

    @Column(name = "STANDARDIZED")
    private Boolean standardized = Boolean.FALSE;

    // TODO - this field is temporary and will be removed later
    @Column(name = "ZIP_FOUR")
    private String zipFour;

    @Column(name = "COMPANY_NAME")
    private String companyName;

    @Column(name = "IS_DEFAULT")
    private boolean isDefault = false;

    @Column(name = "IS_ACTIVE")
    private boolean isActive = true;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "PRIMARY_PHONE")
    private String primaryPhone;

    @Column(name = "SECONDARY_PHONE")
    private String secondaryPhone;

    @Column(name = "IS_BUSINESS")
    private boolean isBusiness = false;

    @Column(name = "VERIFICATION_LEVEL")
    private String verificationLevel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getTokenizedAddress() {
        return tokenizedAddress;
    }

    public void setTokenizedAddress(String tokenizedAddress) {
        this.tokenizedAddress = tokenizedAddress;
    }

    public Boolean getStandardized() {
        return standardized;
    }

    public void setStandardized(Boolean standardized) {
        this.standardized = standardized;
    }

    public String getZipFour() {
        return zipFour;
    }

    public void setZipFour(String zipFour) {
        this.zipFour = zipFour;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPrimaryPhone() {
        return primaryPhone;
    }

    public void setPrimaryPhone(String primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    public void setSecondaryPhone(String secondaryPhone) {
        this.secondaryPhone = secondaryPhone;
    }

    public boolean isBusiness() {
        return isBusiness;
    }

    public void setBusiness(boolean isBusiness) {
        this.isBusiness = isBusiness;
    }

    public String getVerificationLevel() {
        return verificationLevel;
    }

    public void setVerificationLevel(String verificationLevel) {
        this.verificationLevel = verificationLevel;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || !(other instanceof AddressImpl)) return false;

        AddressImpl item = (AddressImpl) other;

        if (addressLine1 != null && item.addressLine1 != null ? !addressLine1.equals(item.addressLine1) : addressLine1 != item.addressLine1) return false;
        if (addressLine2 != null && item.addressLine2 != null ? !addressLine2.equals(item.addressLine2) : addressLine2 != item.addressLine2) return false;
        if (city != null && item.city != null ? !city.equals(item.city) : city != item.city) return false;
        if (postalCode != null && item.postalCode != null ? !postalCode.equals(item.postalCode) : postalCode != item.postalCode) return false;
        if (county != null && item.county != null ? !county.equals(item.county) : county != item.county) return false;
        if (state != null && item.state != null ? !state.equals(item.state) : state != item.state) return false;
        if (country != null && item.country != null ? !country.equals(item.country) : country != item.country) return false;
        if (companyName != null && item.companyName != null ? !companyName.equals(item.companyName) : companyName != item.companyName) return false;
        if (firstName != null && item.firstName != null ? !firstName.equals(item.firstName) : firstName != item.firstName) return false;
        if (lastName != null && item.lastName != null ? !lastName.equals(item.lastName) : lastName != item.lastName) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = addressLine1 != null ? addressLine1.hashCode() : 0;
        result = 31 * result + (addressLine2 != null ? addressLine2.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
        result = 31 * result + (county != null ? county.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (companyName != null ? companyName.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);

        return result;
    }
}
