package org.broadleafcommerce.order.domain;

import java.io.Serializable;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorColumn(name = "TYPE")
@DiscriminatorValue("SUBMITTED")
public class SubmittedOrderImpl extends OrderImpl implements SubmittedOrder, Serializable {

    private static final long serialVersionUID = 1L;
}
