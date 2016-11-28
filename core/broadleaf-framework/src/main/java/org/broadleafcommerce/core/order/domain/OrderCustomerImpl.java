package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.core.order.domain.OrderCustomerAdminPresentation.FieldOrder;
import org.broadleafcommerce.core.order.domain.OrderCustomerAdminPresentation.GroupName;
import org.broadleafcommerce.core.order.domain.OrderCustomerAdminPresentation.TabName;
import org.broadleafcommerce.core.payment.domain.CustomerPayment;
import org.broadleafcommerce.core.payment.domain.CustomerPaymentImpl;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER_CUSTOMER")
public class OrderCustomerImpl implements OrderCustomer {

    @Id
    @Column(name = "CUSTOMER_ID")
    @AdminPresentation(friendlyName = "CustomerImpl_Customer_Id", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;
    
    @Column(name = "EXTERNAL_ID")
    @AdminPresentation(friendlyName = "CustomerImpl_External_Id",
            group = GroupName.Customer, order = FieldOrder.EXTERNAL_ID)
    protected Long externalId;
    
    @Column(name = "FIRST_NAME")
    @AdminPresentation(friendlyName = "CustomerImpl_First_Name",
            group = GroupName.Customer, order = FieldOrder.FIRST_NAME,
            prominent = true, gridOrder = 2000)
    protected String firstName;

    @Column(name = "LAST_NAME")
    @AdminPresentation(friendlyName = "CustomerImpl_Last_Name",
            group = GroupName.Customer, order = FieldOrder.LAST_NAME,
            prominent = true, gridOrder = 3000)
    protected String lastName;
    
    @Column(name = "EMAIL_ADDRESS")
    @Index(name="ORDER_EMAIL_INDEX", columnNames={"EMAIL_ADDRESS"})
    @AdminPresentation(friendlyName = "OrderImpl_Order_Email_Address", group = GroupName.Customer,
            order=FieldOrder.EMAIL)
    protected String emailAddress;
    
    @OneToMany(mappedBy = "customer", targetEntity = CustomerPaymentImpl.class, cascade = { CascadeType.ALL })
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
    @BatchSize(size = 50)
    @AdminPresentationCollection(friendlyName = "OrderCustomerImpl_Customer_Payments",
            tab = TabName.PaymentMethods, order = 1000,
            addType = AddMethodType.PERSIST,
            readOnly = true)
    protected List<CustomerPayment> customerPayments = new ArrayList<>();
    
    @Override
    public Long getId() {
        return id;
    }
    
    @Override
    public Long getExternalId() {
        return externalId;
    }
    
    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getEmailAddress() {
        return emailAddress;
    }
    
    @Override
    public List<CustomerPayment> getCustomerPayments() {
        return customerPayments;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }
    
    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    @Override
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    @Override
    public void setCustomerPayments(List<CustomerPayment> customerPayments) {
        this.customerPayments = customerPayments;
    }

}
