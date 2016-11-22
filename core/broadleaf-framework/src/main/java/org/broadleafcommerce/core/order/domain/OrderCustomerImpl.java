package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.core.order.domain.OrderCustomerAdminPresentation.FieldOrder;
import org.broadleafcommerce.core.order.domain.OrderCustomerAdminPresentation.GroupName;
import org.hibernate.annotations.Index;

import javax.persistence.Column;

public class OrderCustomerImpl implements OrderCustomer {

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

}
