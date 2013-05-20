package org.broadleafcommerce.core.pricing.service.tax.provider;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.pricing.service.exception.TaxException;

/**
 * Generic service to calculate taxes. Those implementing tax calculation logic should, more likely, 
 * use the default Broadleaf TaxService implementation, and implement TaxProvider.
 * 
 * @author Kelly Tisdell
 *
 */
public interface TaxService {

    public Order calculateTaxForOrder(Order order) throws TaxException;

}
