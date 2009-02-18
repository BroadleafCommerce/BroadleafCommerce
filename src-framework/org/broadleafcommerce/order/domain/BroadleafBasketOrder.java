package org.broadleafcommerce.order.domain;

import java.io.Serializable;

//@Entity
//@DiscriminatorColumn(name="TYPE")
//@DiscriminatorValue("BASKET")
public class BroadleafBasketOrder extends BroadleafOrder implements BasketOrder, Serializable {

	private static final long serialVersionUID = 1L;

}
