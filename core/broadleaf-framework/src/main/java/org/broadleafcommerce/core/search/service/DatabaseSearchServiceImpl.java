/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.service;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.search.dao.FieldDao;
import org.broadleafcommerce.core.search.dao.SearchFacetDao;
import org.broadleafcommerce.core.search.domain.CategorySearchFacet;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.domain.SearchFacetRange;
import org.broadleafcommerce.core.search.domain.SearchFacetResultDTO;
import org.broadleafcommerce.core.search.domain.SearchResult;
import org.broadleafcommerce.core.search.service.solr.SolrSearchServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.cache.Cache;
import javax.cache.CacheManager;

/**
 * @deprecated Use {@link SolrSearchServiceImpl} 
 */
@Deprecated
@Service("blSearchService")
public class DatabaseSearchServiceImpl implements SearchService {

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;
    
    @Resource(name = "blSearchFacetDao")
    protected SearchFacetDao searchFacetDao;
    
    @Resource(name = "blFieldDao")
    protected FieldDao fieldDao;
    
    @Resource(name = "blCacheManager")
    protected CacheManager cacheManager;
    
    protected static String CACHE_NAME = "blStandardElements";
    protected static String CACHE_KEY_PREFIX = "facet:";
    protected Cache<String, List<SearchFacetDTO>> cache;
    
    @Override
    public SearchResult findExplicitSearchResultsByCategory(Category category, SearchCriteria searchCriteria) throws ServiceException {
        throw new UnsupportedOperationException("See findProductsByCategory or use the SolrSearchService implementation");
    }
    
    @Override
    public SearchResult findSearchResultsByCategoryAndQuery(Category category, String query, SearchCriteria searchCriteria) throws ServiceException {
        throw new UnsupportedOperationException("This operation is only supported by the SolrSearchService by default");
    }
    
    @Override
    public SearchResult findSearchResultsByCategory(Category category, SearchCriteria searchCriteria) {
        SearchResult result = new SearchResult();
        setQualifiedKeys(searchCriteria);
        List<Product> products = catalogService.findFilteredActiveProductsByCategory(category, searchCriteria);
        List<SearchFacetDTO> facets = getCategoryFacets(category);
        setActiveFacets(facets, searchCriteria);
        result.setProducts(products);
        result.setFacets(facets);
        result.setTotalResults(products.size());
        result.setPage(1);
        result.setPageSize(products.size());
        return result;
    }

    @Override
    public SearchResult findSearchResultsByQuery(String query, SearchCriteria searchCriteria) {
        SearchResult result = new SearchResult();
        setQualifiedKeys(searchCriteria);
        List<Product> products = catalogService.findFilteredActiveProductsByQuery(query, searchCriteria);
        List<SearchFacetDTO> facets = getSearchFacets();
        setActiveFacets(facets, searchCriteria);
        result.setProducts(products);
        result.setFacets(facets);
        result.setTotalResults(products.size());
        result.setPage(1);
        result.setPageSize(products.size());
        return result;
    }

    @Override
    public SearchResult findSearchResults(SearchCriteria searchCriteria) throws ServiceException {
        return findSearchResultsByQuery(searchCriteria.getQuery(), searchCriteria);
    }

    @Override
    public List<SearchFacetDTO> getSearchFacets() {
        String cacheKey = CACHE_KEY_PREFIX + "blc-search";
        List<SearchFacetDTO> facets = getCache().get(cacheKey);
        
        if (facets == null) {
            facets = buildSearchFacetDtos(searchFacetDao.readAllSearchFacets(FieldEntity.PRODUCT));
            getCache().put(cacheKey, facets);
        }
        return facets;
    }

    @Override
    public List<SearchFacetDTO> getSearchFacets(Category category) {
        return getSearchFacets();
    }

    @Override
    public List<SearchFacetDTO> getCategoryFacets(Category category) {
        String cacheKey = CACHE_KEY_PREFIX + "category:" + category.getId();
        List<SearchFacetDTO> facets = getCache().get(cacheKey);
        
        if (facets == null) {
            List<CategorySearchFacet> categorySearchFacets = category.getCumulativeSearchFacets();
            List<SearchFacet> searchFacets = new ArrayList<SearchFacet>();
            for (CategorySearchFacet categorySearchFacet : categorySearchFacets) {
                searchFacets.add(categorySearchFacet.getSearchFacet());
            }
            facets = buildSearchFacetDtos(searchFacets);
            getCache().put(cacheKey, facets);
        }
        return facets;
    }
    
    /**
     * Perform any necessary conversion of the key to be used by the search service
     * @param criteria
     */
    protected void setQualifiedKeys(SearchCriteria criteria) {
        // Convert the filter criteria url keys
        Map<String, String[]> convertedFilterCriteria = new HashMap<String, String[]>();
        for (Entry<String, String[]> entry : criteria.getFilterCriteria().entrySet()) {
            Field field = fieldDao.readFieldByAbbreviation(entry.getKey());
            if (field != null) {
                String qualifiedFieldName = getDatabaseQualifiedFieldName(field.getQualifiedFieldName());
                convertedFilterCriteria.put(qualifiedFieldName, entry.getValue());
            }
        }
        criteria.setFilterCriteria(convertedFilterCriteria);
        
        // Convert the sort criteria url keys
        if (StringUtils.isNotBlank(criteria.getSortQuery())) {
            StringBuilder convertedSortQuery = new StringBuilder();
            for (String sortQuery : criteria.getSortQuery().split(",")) {
                String[] sort = sortQuery.split(" ");
                if (sort.length == 2) {
                    String key = sort[0];
                    Field field = fieldDao.readFieldByAbbreviation(key);
                    String qualifiedFieldName = getDatabaseQualifiedFieldName(field.getQualifiedFieldName());
                    
                    if (convertedSortQuery.length() > 0) {
                        convertedSortQuery.append(",");
                    }
                    
                    convertedSortQuery.append(qualifiedFieldName).append(" ").append(sort[1]);
                }
            }
            criteria.setSortQuery(convertedSortQuery.toString());
        }
        
    }
    
    /**
     * From the Field's qualifiedName, build out the qualified name to be used by the ProductDao
     * to find the requested products.
     * 
     * @param qualifiedFieldName
     * @return the database qualified name
     */
    protected String getDatabaseQualifiedFieldName(String qualifiedFieldName) {
        if (qualifiedFieldName.contains("productAttributes")) {
            return qualifiedFieldName.replace("product.", "");
        } else if (qualifiedFieldName.contains("defaultSku")) {
            return qualifiedFieldName.replace("product.", "");
        } else {
            return qualifiedFieldName;
        }
    }
    
    
    protected void setActiveFacets(List<SearchFacetDTO> facets, SearchCriteria searchCriteria) {
        for (SearchFacetDTO facet : facets) {
            String qualifiedFieldName = getDatabaseQualifiedFieldName(facet.getFacet().getField().getQualifiedFieldName());
            for (Entry<String, String[]> entry : searchCriteria.getFilterCriteria().entrySet()) {
                if (qualifiedFieldName.equals(entry.getKey())) {
                    facet.setActive(true);
                }
            }
        }
    }
    
    
    /**
     * Create the wrapper DTO around the SearchFacet
     * @param categoryFacets
     * @return the wrapper DTO
     */
    protected List<SearchFacetDTO> buildSearchFacetDtos(List<SearchFacet> categoryFacets) {
        List<SearchFacetDTO> facets = new ArrayList<SearchFacetDTO>();
        
        for (SearchFacet facet : categoryFacets) {
            SearchFacetDTO dto = new SearchFacetDTO();
            dto.setFacet(facet);
            dto.setShowQuantity(false);
            dto.setFacetValues(getFacetValues(facet));
            dto.setActive(false);
            facets.add(dto);
        }
        
        return facets;
    }
    
    protected List<SearchFacetResultDTO> getFacetValues(SearchFacet facet) {
        if (facet.getSearchFacetRanges().size() > 0) {
            return getRangeFacetValues(facet);
        } else {
            return getMatchFacetValues(facet);
        }
    }
    
    protected List<SearchFacetResultDTO> getRangeFacetValues(SearchFacet facet) {
        List<SearchFacetResultDTO> results = new ArrayList<SearchFacetResultDTO>();
        
        List<SearchFacetRange> ranges = facet.getSearchFacetRanges();
        Collections.sort(ranges, new Comparator<SearchFacetRange>() {
            @Override
            public int compare(SearchFacetRange o1, SearchFacetRange o2) {
                return o1.getMinValue().compareTo(o2.getMinValue());
            }
        });
        
        for (SearchFacetRange range : ranges) {
            SearchFacetResultDTO dto = new SearchFacetResultDTO();
            dto.setMinValue(range.getMinValue());
            dto.setMaxValue(range.getMaxValue());
            dto.setFacet(facet);
            results.add(dto);
        }
        return results;
    }
    
    protected List<SearchFacetResultDTO> getMatchFacetValues(SearchFacet facet) {
        List<SearchFacetResultDTO> results = new ArrayList<SearchFacetResultDTO>();
        
        String qualifiedFieldName = facet.getField().getQualifiedFieldName();
        qualifiedFieldName = getDatabaseQualifiedFieldName(qualifiedFieldName);
        List<String> values = searchFacetDao.readDistinctValuesForField(qualifiedFieldName, String.class);
        
        Collections.sort(values);
        
        for (String value : values) {
            SearchFacetResultDTO dto = new SearchFacetResultDTO();
            dto.setValue(value);
            dto.setFacet(facet);
            results.add(dto);
        }
        
        return results;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    protected Cache<String, List<SearchFacetDTO>> getCache() {
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    cache = cacheManager.getCache(CACHE_NAME);
                }
            }
        }
        return cache;
    }
}
