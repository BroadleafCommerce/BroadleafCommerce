package org.springcommerce.util;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springcommerce.profile.domain.Address;

public class CreateAddress implements Serializable {
    private static final long serialVersionUID = 1L;
    @Transient
    private final Log logger = LogFactory.getLog(getClass());
    private Address address;
    private List<Address> addressList;
    private String addressLine1;
    private String addressLine2;
    private String addressName;
    private String city;
    private String state;
    private String zipCode;

    public Address getAddress() {
        return address;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public String getAddressName() {
        return addressName;
    }

    public String getCity() {
        return city;
    }

    public Log getLogger() {
        return logger;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
