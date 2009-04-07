package org.broadleafcommerce.pricing.module;

import org.broadleafcommerce.order.domain.Order;

public class SimpleTaxModule implements TaxModule {
	
	public static final String MODULENAME = "simpleTaxModule";
	
	protected String name = MODULENAME;
	protected double factor = 0.05D;

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.pricing.module.TaxModule#calculateTaxForOrder(org.broadleafcommerce.order.domain.Order)
	 */
	@Override
	public Order calculateTaxForOrder(Order order) {
		/*
		 * TODO does Order need a member variable to hold the specific
		 * tax amount?
		 */
		order.setTotal(order.getSubTotal().multiply(factor+1D));
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
	public double getFactor() {
		return factor;
	}

	/**
	 * @param factor the factor to set
	 */
	public void setFactor(double factor) {
		this.factor = factor;
	}

}
