/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.web.catalog;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;

import org.broadleafcommerce.core.catalog.service.dynamic.DynamicSkuPricingService;
import org.springframework.stereotype.Component;

/**
 * @author ppatel
 *
 */
@Component("priceListDynamicSkuPricingFilter")
public class PriceListDynamicSkuPricingFilter extends AbstractDynamicSkuPricingFilter {
	
	@Resource(name="blPriceListDynamicSkuPricingService")
	protected DynamicSkuPricingService skuPricingService;
	
	
	@Override
    public DynamicSkuPricingService getDynamicSkuPricingService(ServletRequest arg0) {
		return skuPricingService;
	}

	@Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public HashMap getPricingConsiderations(ServletRequest request) {
		HashMap pricingConsiderations = new HashMap();
		pricingConsiderations.put("x","xx");
		return pricingConsiderations;
	}

}
