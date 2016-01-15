/*
 * #%L
 * BroadleafCommerce Framework
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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

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
    
    protected static String CACHE_NAME = "blStandardElements";
    protected static String CACHE_KEY_PREFIX = "facet:";
    protected Cache cache = CacheManager.getInstance().getCache(CACHE_NAME);
    
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
    @SuppressWarnings("unchecked")
    public List<SearchFacetDTO> getSearchFacets() {
        List<SearchFacetDTO> facets = null;
        
        String cacheKey = CACHE_KEY_PREFIX + "blc-search";
        Element element = cache.get(cacheKey);
        if (element != null) {
            facets = (List<SearchFacetDTO>) element.getValue();
        }
        
        if (facets == null) {
            facets = buildSearchFacetDtos(searchFacetDao.readAllSearchFacets(FieldEntity.PRODUCT));
            element = new Element(cacheKey, facets);
            cache.put(element);
        }
        return facets;
    }

    @Override
    public List<SearchFacetDTO> getSearchFacets(Category category) {
        return getSearchFacets();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SearchFacetDTO> getCategoryFacets(Category category) {
        List<SearchFacetDTO> facets = null;
        
        String cacheKey = CACHE_KEY_PREFIX + "category:" + category.getId();
        Element element = cache.get(cacheKey);
        if (element != null) {
            facets = (List<SearchFacetDTO>) element.getValue();
        }
        
        if (facets == null) {
            List<CategorySearchFacet> categorySearchFacets = category.getCumulativeSearchFacets();
            List<SearchFacet> searchFacets = new ArrayList<SearchFacet>();
            for (CategorySearchFacet categorySearchFacet : categorySearchFacets) {
                searchFacets.add(categorySearchFacet.getSearchFacet());
            }
            facets = buildSearchFacetDtos(searchFacets);
            element = new Element(cacheKey, facets);
            cache.put(element);
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
    public void rebuildIndex() {
        throw new UnsupportedOperationException("Indexes are not supported by this implementation");
    }

}
