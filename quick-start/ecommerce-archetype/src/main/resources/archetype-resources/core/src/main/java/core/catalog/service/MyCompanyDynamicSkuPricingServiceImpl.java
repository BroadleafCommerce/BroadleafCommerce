import java.util.HashMap;

#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${artifactId}.catalog.service;

import java.util.HashMap;

import org.broadleafcommerce.${artifactId}.catalog.domain.Sku;
import org.broadleafcommerce.${artifactId}.catalog.service.dynamic.DynamicSkuPrices;
import org.broadleafcommerce.${artifactId}.catalog.service.dynamic.DynamicSkuPricingService;
import org.broadleafcommerce.common.money.Money;
import org.springframework.stereotype.Service;

@Service("myCompanyDynamicSkuPricingService")
public class MyCompanyDynamicSkuPricingServiceImpl implements DynamicSkuPricingService {

	@Override
	public DynamicSkuPrices getSkuPrices(Sku sku, @SuppressWarnings("rawtypes") HashMap skuPricingConsiderations) {
		DynamicSkuPrices prices = new DynamicSkuPrices();
		prices.setRetailPrice(new Money(Math.random() * 100D));
		prices.setSalePrice(new Money(1D));
		
		return prices;
	}

}
