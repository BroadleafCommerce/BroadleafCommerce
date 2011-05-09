package org.broadleafcommerce.core.catalog.service.dynamic;

import java.util.HashMap;


public class SkuPricingConsiderationContext {
	
	private static final ThreadLocal<DynamicSkuPricingService> skuPricingService = new ThreadLocal<DynamicSkuPricingService>();
	
	@SuppressWarnings("rawtypes")
	private static final ThreadLocal<HashMap> skuPricingConsiderationContext = new ThreadLocal<HashMap>();
	
	@SuppressWarnings("rawtypes")
	public static HashMap getSkuPricingConsiderationContext() {
		return SkuPricingConsiderationContext.skuPricingConsiderationContext.get();
	}
	
	public static void setSkuPricingConsiderationContext(@SuppressWarnings("rawtypes") HashMap skuPricingConsiderationContext) {
		SkuPricingConsiderationContext.skuPricingConsiderationContext.set(skuPricingConsiderationContext);
	}
	
	public static DynamicSkuPricingService getSkuPricingService() {
		return SkuPricingConsiderationContext.skuPricingService.get();
	}
	
	public static void setSkuPricingService(DynamicSkuPricingService skuPricingService) {
		SkuPricingConsiderationContext.skuPricingService.set(skuPricingService);
	}
}
