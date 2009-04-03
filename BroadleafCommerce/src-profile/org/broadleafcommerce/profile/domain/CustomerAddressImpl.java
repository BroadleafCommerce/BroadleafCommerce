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
public class CustomerAddressImpl implements CustomerAddress {

    @Id
    @GeneratedValue
    @Column(name = "CUSTOMER_ADDRESS_ID")
    private Long id;

    @Column(name = "ADDRESS_NAME")
    private String addressName;

    @Column(name = "CUSTOMER_ID")
    private Long customerId;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = AddressImpl.class)
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;

    public CustomerAddressImpl() {
    }

    public CustomerAddressImpl(Long customerId) {
        this.customerId = customerId;
        this.address = new AddressImpl();
    }

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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
