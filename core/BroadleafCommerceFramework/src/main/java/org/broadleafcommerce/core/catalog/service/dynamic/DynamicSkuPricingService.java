package org.broadleafcommerce.core.catalog.service.dynamic;

import java.util.HashMap;

import org.broadleafcommerce.core.catalog.domain.Sku;

public interface DynamicSkuPricingService  {

	public DynamicSkuPrices getSkuPrices(Sku sku, @SuppressWarnings("rawtypes") HashMap skuPricingConsiderations);
	
	
}
