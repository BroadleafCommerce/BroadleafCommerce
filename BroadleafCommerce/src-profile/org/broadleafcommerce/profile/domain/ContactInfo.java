package org.broadleafcommerce.profile.domain;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//@Entity
//@EntityListeners(value = { TemporalTimestampListener.class })
//@Table(name = "BLC_CONTACT_INFO")
public class ContactInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    //    @Transient
    protected Log logger = LogFactory.getLog(getClass());

    //    @Id
    //    @GeneratedValue
    //    @Column(name = "CONTACT_ID")
    private Long id;

    //    @ManyToOne(cascade = CascadeType.ALL, targetEntity = BroadleafCustomer.class)
    //    @JoinColumn(name = "CUSTOMER_ID")
    private Customer customer;

    //    @Column(name = "PRIMARY_PHONE")
    private String primaryPhone;

    //    @Column(name = "SECONDARY_PHONE")
    private String secondaryPhone;

    //    @Column(name = "EMAIL")
    private String email;

    //    @Column(name = "FAX")
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
