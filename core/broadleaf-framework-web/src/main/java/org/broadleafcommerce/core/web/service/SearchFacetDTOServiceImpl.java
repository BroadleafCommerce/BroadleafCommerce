/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.web.service;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.domain.SearchFacetResultDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

@Service("blSearchFacetDTOService")
public class SearchFacetDTOServiceImpl implements SearchFacetDTOService {
    
    protected int getDefaultPageSize() {
        return BLCSystemProperty.resolveIntSystemProperty("web.defaultPageSize");
    }

    protected int getMaxPageSize() {
        return BLCSystemProperty.resolveIntSystemProperty("web.maxPageSize");
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public SearchCriteria buildSearchCriteria(HttpServletRequest request, List<SearchFacetDTO> availableFacets) {
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setPageSize(getDefaultPageSize());
        
        Map<String, String[]> facets = new HashMap<String, String[]>();
        
        for (Iterator<Entry<String,String[]>> iter = request.getParameterMap().entrySet().iterator(); iter.hasNext();){
            Map.Entry<String, String[]> entry = iter.next();
            String key = entry.getKey();
            
            if (key.equals(SearchCriteria.SORT_STRING)) {
                searchCriteria.setSortQuery(StringUtils.join(entry.getValue(), ","));
            } else if (key.equals(SearchCriteria.PAGE_NUMBER)) {
                searchCriteria.setPage(Integer.parseInt(entry.getValue()[0]));
            } else if (key.equals(SearchCriteria.PAGE_SIZE_STRING)) {
                int requestedPageSize = Integer.parseInt(entry.getValue()[0]);
                int maxPageSize = getMaxPageSize();
                searchCriteria.setPageSize(Math.min(requestedPageSize, maxPageSize));
            } else if (key.equals(SearchCriteria.QUERY_STRING)) {
                continue; // This is handled by the controller
            } else {
                facets.put(key, entry.getValue());
            }
        }
        
        searchCriteria.setFilterCriteria(facets);
        
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
            if (key.equals(getUrlKey(result))) {
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
    public String getUrlKey(SearchFacetResultDTO result) {
        return result.getFacet().getField().getAbbreviation();
    }
    
    @Override
    public String getValue(SearchFacetResultDTO result) {
        return result.getValueKey();
    }

    @Override
    public String getUrlKey(SearchFacetDTO result) {
        return result.getFacet().getField().getAbbreviation();
    }

}
