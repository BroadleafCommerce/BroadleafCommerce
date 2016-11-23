package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.core.order.domain.OrderCustomerAdminPresentation.FieldOrder;
import org.broadleafcommerce.core.order.domain.OrderCustomerAdminPresentation.GroupName;
import org.hibernate.annotations.Index;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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

}
