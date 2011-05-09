package org.broadleafcommerce.core.catalog.service.dynamic;

import java.util.HashMap;

import org.broadleafcommerce.core.catalog.domain.Sku;
import org.springframework.stereotype.Service;

@Service("blDynamicSkuPricingService")
public class DefaultDynamicSkuPricingServiceImpl implements DynamicSkuPricingService {

	public DynamicSkuPrices getSkuPrices(Sku sku, @SuppressWarnings("rawtypes") HashMap skuPricingConsiderations) {
		//the default behavior is to ignore the pricing considerations and return the retail and sale price from the sku
		DynamicSkuPrices prices = new DynamicSkuPrices();
		prices.setRetailPrice(sku.getRetailPrice());
		prices.setSalePrice(sku.getSalePrice());
		
		return prices;
	}

}
