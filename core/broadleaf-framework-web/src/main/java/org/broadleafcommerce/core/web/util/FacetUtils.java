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

package org.broadleafcommerce.core.web.util;

import org.broadleafcommerce.core.search.domain.SearchFacetResultDTO;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Provides static utility methods that facilitate interactions with SearchFacets
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class FacetUtils {
	
	public static boolean isActive(SearchFacetResultDTO result, Map<String, String[]> params) {
		for (Entry<String, String[]> entry : params.entrySet()) {
			String key = entry.getKey();
			if (key.equals(getKey(result))) {
				for (String val : entry.getValue()) {
					if (val.equals(getValue(result))) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static String getKey(SearchFacetResultDTO result) {
		return result.getFacet().getSearchFacet().getFieldName();
	}
	
	public static String getValue(SearchFacetResultDTO result) {
		String value = result.getValue();
		
		if (value == null) {
			value = "blcRange[" + result.getMinValue() + ":" + result.getMaxValue() + "]";
		}
		
		return value;
	}

}
