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

package org.broadleafcommerce.core.web.service;

import org.broadleafcommerce.core.search.domain.ProductSearchCriteria;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.domain.SearchFacetResultDTO;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Service("blSearchFacetDTOService")
public class SearchFacetDTOServiceImpl implements SearchFacetDTOService {
	
	@Override
	@SuppressWarnings("unchecked")
	public ProductSearchCriteria buildSearchCriteria(HttpServletRequest request, List<SearchFacetDTO> availableFacets) {
		ProductSearchCriteria searchCriteria = new ProductSearchCriteria();
		
		Map<String, String[]> convertedFacets = new HashMap<String, String[]>();
		
		for (Iterator<Entry<String,String[]>> iter = request.getParameterMap().entrySet().iterator(); iter.hasNext();){
			Map.Entry<String, String[]> entry = iter.next();
			String key = entry.getKey();
			
			if (key.equals(ProductSearchCriteria.SORT_STRING)) {
				searchCriteria.setSortQuery(entry.getValue()[0]);
			} else if (key.equals(ProductSearchCriteria.PAGE_NUMBER)) {
				searchCriteria.setPage(Integer.parseInt(entry.getValue()[0]));
			} else if (key.equals(ProductSearchCriteria.PAGE_SIZE_STRING)) {
				searchCriteria.setPageSize(Integer.parseInt(entry.getValue()[0]));
			} else if (key.equals(ProductSearchCriteria.QUERY_STRING)) {
				continue; // This is handled by the controller
			} else {
				String convertedKey = getConvertedKey(key, availableFacets);
				if (convertedKey != null) {
					convertedFacets.put(convertedKey, entry.getValue());
				}
			}
		}
		
		searchCriteria.setFilterCriteria(convertedFacets);
		
		return searchCriteria;
	}
	
	@Override
	public void setActiveFacetResults(List<SearchFacetDTO> facets, HttpServletRequest request) {
		if (facets != null) {
	    	for (SearchFacetDTO facet : facets) {
	    		for (SearchFacetResultDTO facetResult : facet.getFacetValues()) {
	    			facetResult.setActive(isActive(facetResult, request));
	    		}
	    	}
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean isActive(SearchFacetResultDTO result, HttpServletRequest request) {
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
	
	@Override
	public String getKey(SearchFacetResultDTO result) {
		return result.getFacet().getSearchFacet().getQueryStringKey();
	}
	
	@Override
	public String getValue(SearchFacetResultDTO result) {
		String value = result.getValue();
		
		if (value == null) {
			value = "range[" + result.getMinValue() + ":" + result.getMaxValue() + "]";
		}
		
		return value;
	}
	
	protected String getConvertedKey(String key, List<SearchFacetDTO> availableFacets) {
		for (SearchFacetDTO dto : availableFacets) {
			if (key.equals(dto.getFacet().getSearchFacet().getQueryStringKey())) {
				return dto.getFacet().getSearchFacet().getFieldName();
			}
		}
		return null;
	}

}
