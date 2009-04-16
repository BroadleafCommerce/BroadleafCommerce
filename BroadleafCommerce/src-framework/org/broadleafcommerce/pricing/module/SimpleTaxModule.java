package org.broadleafcommerce.pricing.module;

import java.util.Iterator;
import java.util.List;

import org.broadleafcommerce.order.domain.FulfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.util.money.Money;

/**
 * TODO investigate how taxware is used and how tax rate information is populated into the
 * database. We would like to have a taxware tax module.
 * 
 * @author jfischer
 *
 */
public class SimpleTaxModule implements TaxModule {

    public static final String MODULENAME = "simpleTaxModule";

    protected String name = MODULENAME;
    protected Double factor;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.pricing.module.TaxModule#calculateTaxForOrder(org.broadleafcommerce.order.domain.Order)
     */
    @Override
    public Order calculateTaxForOrder(Order order) {
        order.setTotalTax(new Money(0));

        List<FulfillmentGroup> groups = order.getFulfillmentGroups();
        Iterator<FulfillmentGroup> itr = groups.iterator();
        while(itr.hasNext()) {
            FulfillmentGroup group = itr.next();
            group.setTotalTax(group.getPrice().multiply(factor));
            order.setTotalTax(order.getTotalTax().add(group.getTotalTax()));
        }
        order.setTotal(order.getTotal().add(order.getTotalTax()));
        return order;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.pricing.module.TaxModule#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.pricing.module.TaxModule#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the factor
     */
    public Double getFactor() {
        return factor;
    }

    /**
     * @param factor the factor to set
     */
    public void setFactor(Double factor) {
        this.factor = factor;
    }

}
