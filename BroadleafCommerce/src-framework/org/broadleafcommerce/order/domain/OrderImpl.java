package org.broadleafcommerce.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.broadleafcommerce.common.domain.Auditable;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.broadleafcommerce.profile.domain.Customer;

//@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "TYPE")
//@Table(name = "BLC_ORDER")
public class OrderImpl implements Order, Serializable {

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
    private String status;

    //    @Column(name = "ORDER_TOTAL")
    private BigDecimal total;

    private List<FullfillmentGroup> fullfillmentGroups;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String orderStatus) {
        this.status = orderStatus;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal orderTotal) {
        this.total = orderTotal;
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

    public List<FullfillmentGroup> getFullfillmentGroups() {
        return fullfillmentGroups;
    }

    public void setFullfillmentGroups(List<FullfillmentGroup> fullfillmentGroups) {
        this.fullfillmentGroups = fullfillmentGroups;
    }
}
