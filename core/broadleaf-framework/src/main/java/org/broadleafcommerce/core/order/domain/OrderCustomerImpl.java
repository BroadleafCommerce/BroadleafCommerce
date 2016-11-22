package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.profile.core.domain.CustomerAdminPresentation.FieldOrder;
import org.broadleafcommerce.profile.core.domain.CustomerAdminPresentation.GroupName;

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
    
    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

}
