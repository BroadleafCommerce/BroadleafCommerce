package org.broadleafcommerce.profile.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.broadleafcommerce.profile.domain.listener.TemporalTimestampListener;

@Entity
@EntityListeners(value = { TemporalTimestampListener.class })
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CUSTOMER_ADDRESS", uniqueConstraints = @UniqueConstraint(columnNames = { "CUSTOMER_ID", "ADDRESS_NAME" }))
public class AddressImpl implements Address {

    @Id
    @GeneratedValue
    @Column(name = "ADDRESS_ID")
    private Long id;

    @Column(name = "ADDRESS_NAME")
    private String addressName;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = CustomerImpl.class)
    @JoinColumn(name = "CUSTOMER_ID")
    private Customer customer;

    @Column(name = "ADDRESS_LINE1")
    private String addressLine1;

    @Column(name = "ADDRESS_LINE2")
    private String addressLine2;

    @Column(name = "CITY")
    private String city;

    @Column(name = "COUNTRY")
    private CountryEnums.Country country;

    @Column(name = "postalCode")
    private String postalCode;

    @Column(name = "stateProvRegion")
    private String stateProvRegion;

    @Column(name = "TOKENIZED_ADDRESS")
    private String tokenizedAddress;

    @Column(name = "STANDARDIZED")
    private Boolean standardized = Boolean.FALSE;

    // This field is temporary and will be removed later
    @Column(name = "zipFour")
    private String zipFour;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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

    public CountryEnums.Country getCountry() {
        return country;
    }

    public void setCountry(CountryEnums.Country country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStateProvRegion() {
        return stateProvRegion;
    }

    public void setStateProvRegion(String stateProvRegion) {
        this.stateProvRegion = stateProvRegion;
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
}
