package org.broadleafcommerce.order.domain;

import java.io.Serializable;

//@Entity
//@DiscriminatorColumn(name="TYPE")
//@DiscriminatorValue("SUBMITTED")
public class SubmittedOrderImpl extends OrderImpl implements SubmittedOrder, Serializable {
    private static final long serialVersionUID = 1L;

}
