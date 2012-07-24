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

import org.broadleafcommerce.core.search.domain.ProductSearchCriteria;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.domain.SearchFacetResultDTO;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Provides static utility methods that facilitate interactions with SearchFacets
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class FacetUtils {
	
	@SuppressWarnings("unchecked")
	public static ProductSearchCriteria buildSearchCriteria(HttpServletRequest request) {
		ProductSearchCriteria searchCriteria = new ProductSearchCriteria();
		
		Map<String, String[]> params = new HashMap<String, String[]>(request.getParameterMap());
		for (Iterator<Map.Entry<String,String[]>> iter = params.entrySet().iterator(); iter.hasNext();){
			Map.Entry<String, String[]> entry = iter.next();
			String key = entry.getKey();
			
			if (key.equals(ProductSearchCriteria.SORT_STRING)) {
				searchCriteria.setSortQuery(entry.getValue()[0]);
				iter.remove();
			}
			
			if (key.equals(ProductSearchCriteria.PAGE_NUMBER)) {
				searchCriteria.setPage(Integer.parseInt(entry.getValue()[0]));
				iter.remove();
			}
			
			if (key.equals(ProductSearchCriteria.PAGE_SIZE_STRING)) {
				searchCriteria.setPageSize(Integer.parseInt(entry.getValue()[0]));
				iter.remove();
			}
			
			// This is handled specifically by the controller and we do not need to deal with it here
			if (key.equals(ProductSearchCriteria.QUERY_STRING)) {
				iter.remove();
			}
		}
		
		searchCriteria.setFilterCriteria(params);
		
		return searchCriteria;
	}
	
	public static void setActiveFacetResults(List<SearchFacetDTO> facets, HttpServletRequest request) {
		if (facets != null) {
	    	for (SearchFacetDTO facet : facets) {
	    		for (SearchFacetResultDTO facetResult : facet.getFacetValues()) {
	    			facetResult.setActive(FacetUtils.isActive(facetResult, request));
	    		}
	    	}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isActive(SearchFacetResultDTO result, HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
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
