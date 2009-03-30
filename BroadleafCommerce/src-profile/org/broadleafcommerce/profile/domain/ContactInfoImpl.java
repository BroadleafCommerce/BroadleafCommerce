package org.broadleafcommerce.profile.domain;

import java.io.Serializable;

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

import org.broadleafcommerce.profile.domain.listener.TemporalTimestampListener;

@Entity
@EntityListeners(value = { TemporalTimestampListener.class })
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CONTACT_INFO")
public class ContactInfoImpl implements ContactInfo, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "CONTACT_ID")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = CustomerImpl.class)
    @JoinColumn(name = "CUSTOMER_ID")
    private Customer customer;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = AddressImpl.class)
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;

    @Column(name = "PRIMARY_PHONE")
    private String primaryPhone;

    @Column(name = "SECONDARY_PHONE")
    private String secondaryPhone;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "FAX")
    private String fax;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }
}
