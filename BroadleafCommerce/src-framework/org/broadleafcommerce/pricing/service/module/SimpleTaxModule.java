package org.broadleafcommerce.pricing.service.module;

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
        Money totalTax = order.getSubTotal().multiply(factor);
        order.setTotalTax(totalTax);

        for(FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
            Money fgTotalTax = fulfillmentGroup.getShippingPrice().multiply(factor);
            fulfillmentGroup.setTotalTax(fgTotalTax);
            fulfillmentGroup.setCityTax(new Money(0D));
            fulfillmentGroup.setCountyTax(new Money(0D));
            fulfillmentGroup.setCountryTax(new Money(0D));
        }

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
