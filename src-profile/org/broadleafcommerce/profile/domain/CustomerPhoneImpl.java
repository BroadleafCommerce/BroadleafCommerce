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
@Table(name = "BLC_CUSTOMER_PHONE", uniqueConstraints = @UniqueConstraint(columnNames = { "CUSTOMER_ID", "PHONE_NAME" }))
public class CustomerPhoneImpl implements CustomerPhone{

    @Id
    @GeneratedValue
    @Column(name = "CUSTOMER_PHONE_ID")
    private Long id;

    @Column(name = "PHONE_NAME")
    private String phoneName;

    @Column(name = "CUSTOMER_ID")
    private Long customerId;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = PhoneImpl.class)
    @JoinColumn(name = "PHONE_ID")
    private Phone phone;

    public CustomerPhoneImpl() {
    }

    public CustomerPhoneImpl(Long customerId) {
        this.customerId = customerId;
        this.phone = new PhoneImpl();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }
}
