/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 *
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.service;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.security.service.ExploitProtectionService;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.domain.SearchFacetResultDTO;
import org.broadleafcommerce.core.web.catalog.CategoryHandlerMapping;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service("blSearchFacetDTOService")
public class SearchFacetDTOServiceImpl implements SearchFacetDTOService {

    @Resource(name = "blExploitProtectionService")
    protected ExploitProtectionService exploitProtectionService;

    protected int getDefaultPageSize() {
        return BLCSystemProperty.resolveIntSystemProperty("web.defaultPageSize");
    }

    protected int getMaxPageSize() {
        return BLCSystemProperty.resolveIntSystemProperty("web.maxPageSize");
    }

    @Override
    public SearchCriteria buildSearchCriteria(HttpServletRequest request) {
        SearchCriteria searchCriteria = createSearchCriteria();
        searchCriteria.setPageSize(getDefaultPageSize());

        Map<String, String[]> facets = new HashMap<>();

        for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String key = entry.getKey();

            if (Objects.equals(key, SearchCriteria.SORT_STRING)) {
                searchCriteria.setSortQuery(StringUtils.join(entry.getValue(), ","));
            } else if (Objects.equals(key, SearchCriteria.PAGE_NUMBER)) {
                searchCriteria.setPage(Integer.parseInt(entry.getValue()[0]));
            } else if (Objects.equals(key, SearchCriteria.PAGE_SIZE_STRING)) {
                int requestedPageSize = Integer.parseInt(entry.getValue()[0]);
                int maxPageSize = getMaxPageSize();
                searchCriteria.setPageSize(Math.min(requestedPageSize, maxPageSize));
            } else if (Objects.equals(key, SearchCriteria.QUERY_STRING)) {
                String query = request.getParameter(SearchCriteria.QUERY_STRING);
                try {
                    if (StringUtils.isNotEmpty(query)) {
                        query = exploitProtectionService.cleanString(StringUtils.trim(query));
                    }
                } catch (ServiceException e) {
                    query = null;
                }
                searchCriteria.setQuery(query);
            } else if (Objects.equals(key, SearchCriteria.REQUEST_HANDLER)) {
                String requestHandler = entry.getValue()[0];
                if (!requestHandler.startsWith("/")) {
                    requestHandler = "/" + requestHandler;
                }
                searchCriteria.setRequestHandler(requestHandler);
            } else {
                facets.put(key, entry.getValue());
            }
        }

        searchCriteria.setFilterCriteria(facets);
        searchCriteria.setCategory((Category) request.getAttribute(CategoryHandlerMapping.CURRENT_CATEGORY_ATTRIBUTE_NAME));

        return searchCriteria;
    }

    protected SearchCriteria createSearchCriteria() {
        return new SearchCriteria();
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
