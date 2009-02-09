package org.broadleafcommerce.order.domain;

import java.io.Serializable;

import org.broadleafcommerce.common.domain.Auditable;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.broadleafcommerce.profile.domain.Customer;

//@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "TYPE")
//@Table(name = "BLC_ORDER")
public class BroadleafOrder implements Serializable {

    private static final long serialVersionUID = 1L;

//    @Id
//    @GeneratedValue
//    @Column(name = "ORDER_ID")
    private Long id;

//    @Embedded
    private Auditable auditable;

    private String type;
    
//    @ManyToOne(targetEntity = BroadleafCustomer.class)
//    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private Customer customer;

//    @ManyToOne
//    @JoinColumn(name = "CONTACT_INFO_ID")
    private ContactInfo contactInfo;

//    @Column(name = "ORDER_STATUS")
    private String orderStatus;

//    @Column(name = "ORDER_TOTAL")
    private double orderTotal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Auditable getAuditable() {
        return auditable;
    }

    public void setAuditable(Auditable auditable) {
        this.auditable = auditable;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
