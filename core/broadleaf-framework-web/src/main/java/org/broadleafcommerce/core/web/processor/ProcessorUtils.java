/*
 * Copyright 2012 the original author or authors.
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

package org.broadleafcommerce.core.web.processor;

import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.catalog.service.RelatedProductsService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.openadmin.server.service.ExploitProtectionService;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.Arguments;
import org.thymeleaf.spring3.context.SpringWebContext;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author apazzolini
 *
 * Utility class for Thymeleaf Processors
 */
public class ProcessorUtils {
	
	protected static Map<String, Object> cachedBeans = new HashMap<String, Object>();
	
	/**
	 * Gets the "blRelatedProductsService" bean via the Spring Web Application Context
	 * @param arguments the Thymeleaf arguments that's part of the request
	 * @return "blRelatedProductsService" bean instance
	 */
	public static RelatedProductsService getRelatedProductsService(Arguments arguments) {
		String key = "blRelatedProductsService";
		RelatedProductsService relatedProductsService = (RelatedProductsService) cachedBeans.get(key);
		if (relatedProductsService == null) { 
			final ApplicationContext appCtx = ((SpringWebContext) arguments.getContext()).getApplicationContext(); 
			relatedProductsService = (RelatedProductsService) appCtx.getBean(key);
			cachedBeans.put(key, relatedProductsService);
		}
		return relatedProductsService;
	}

	/**
	 * Gets the "blCatalogService" bean via the Spring Web Application Context
	 * @param arguments the Thymeleaf arguments that's part of the request
	 * @return "blCatalogService" bean instance
	 */
	public static CatalogService getCatalogService(Arguments arguments) {
		String key = "blCatalogService";
		CatalogService catalogService = (CatalogService) cachedBeans.get(key);
		if (catalogService == null) { 
			final ApplicationContext appCtx = ((SpringWebContext) arguments.getContext()).getApplicationContext(); 
			catalogService = (CatalogService) appCtx.getBean(key);
			cachedBeans.put(key, catalogService);
		}
		return catalogService;
	}

    /**
     * Gets the "blOrderService" bean via the Spring Web Application Context
     * @param arguments the Thymeleaf arguments that's part of the request
     * @return "blOrderService" bean instance
     */
    public static OrderService getOrderService(Arguments arguments) {
        String key = "blOrderService";
        OrderService orderService = (OrderService) cachedBeans.get(key);
        if (orderService == null) {
            final ApplicationContext appCtx = ((SpringWebContext) arguments.getContext()).getApplicationContext();
            orderService = (OrderService) appCtx.getBean(key);
            cachedBeans.put(key, orderService);
        }
        return orderService;
    }
	
	/**
	 * Gets the "blExploitProtectionService" bean via the Spring Web Application Context
	 * @param arguments the Thymeleaf arguments that's part of the request
	 * @return "blExploitProtectionService" bean instance
	 */
	public static ExploitProtectionService getExploitProtectionService(Arguments arguments) {
		String key = "blExploitProtectionService";
		ExploitProtectionService exploitProtectionService = (ExploitProtectionService) cachedBeans.get(key);
		if (exploitProtectionService == null) { 
			final ApplicationContext appCtx = ((SpringWebContext) arguments.getContext()).getApplicationContext(); 
			exploitProtectionService = (ExploitProtectionService) appCtx.getBean(key);
			cachedBeans.put(key, exploitProtectionService);
		}
		return exploitProtectionService;
	}
	
	/**
	 * Gets a UTF-8 URL encoded URL based on the current URL as well as the specified map 
	 * of query string parameters
	 * 
	 * @param baseUrl
	 * @param parameters
	 * @return the built URL
	 */
	public static String getUrl(String baseUrl, Map<String, String[]> parameters) {
		if (baseUrl.contains("?")) {
			throw new IllegalArgumentException("baseUrl contained a ? indicating it is not a base url");
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(baseUrl);
		
		if (parameters == null || parameters.size() == 0) {
			return sb.toString();
		} else {
			sb.append("?");
		}
		
		for (Entry<String, String[]> entry : parameters.entrySet()) {
			String key = entry.getKey();
			for (String value : entry.getValue()) {
				StringBuilder parameter = new StringBuilder();
				try {
					parameter.append(URLEncoder.encode(key, "UTF-8"));
					parameter.append("=");
					parameter.append(URLEncoder.encode(value, "UTF-8"));
					parameter.append("&");
				} catch (UnsupportedEncodingException e) {
					parameter = null;
				}
				sb.append(parameter);
			}
		}
		
		String url = sb.toString();
		if (url.charAt(url.length() - 1) == '&') {
			url = url.substring(0, url.length() - 1);
		}
		
		return url;
	}

}
