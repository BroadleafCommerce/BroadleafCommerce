package org.broadleafcommerce.core.catalog.service.dynamic;

import java.io.Serializable;

import org.broadleafcommerce.money.Money;

public class DynamicSkuPrices implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected Money retailPrice;
	protected Money salePrice;
	
	public Money getRetailPrice() {
		return retailPrice;
	}
	
	public void setRetailPrice(Money retailPrice) {
		this.retailPrice = retailPrice;
	}
	
	public Money getSalePrice() {
		return salePrice;
	}
	
	public void setSalePrice(Money salePrice) {
		this.salePrice = salePrice;
	}
	
}
