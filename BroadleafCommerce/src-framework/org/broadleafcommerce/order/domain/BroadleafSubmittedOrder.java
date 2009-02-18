package org.broadleafcommerce.order.domain;

import java.io.Serializable;

//@Entity
//@DiscriminatorColumn(name="TYPE")
//@DiscriminatorValue("SUBMITTED")
public class BroadleafSubmittedOrder extends BroadleafOrder implements SubmittedOrder, Serializable {
	private static final long serialVersionUID = 1L;

}
