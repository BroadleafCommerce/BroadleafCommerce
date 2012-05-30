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

package org.broadleafcommerce.core.web.dialect;

import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.Arguments;
import org.thymeleaf.spring3.context.SpringWebContext;

/**
 * @author apazzolini
 *
 * Utility class for Thymeleaf Processors
 */
public class ProcessorUtils {
	
	protected static Map<String, Object> cachedBeans = new HashMap<String, Object>();

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

}
