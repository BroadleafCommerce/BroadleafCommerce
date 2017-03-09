/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.search.service;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.domain.SearchResult;
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexService;

import java.io.IOException;
import java.util.List;

/**
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface SearchService {

    /**
     * Performs a search for search results in the given category, taking into consideration the SearchCriteria
     * 
     * This method will return search results that are in any sub-level of a given category. For example, if you had a 
     * "Routers" category and a "Enterprise Routers" sub-category, asking for search results in "Routers", would return
     * search results that are in the "Enterprise Routers" category. 
     * 
     * @see #findExplicitSearchResultsByCategory(Category, SearchCriteria)
     *
     * @param category
     * @param searchCriteria
     * @return the result of the search
     * @throws ServiceException
     * @deprecated use #findSearchResults(SearchCriteria)
     */
    @Deprecated
    public SearchResult findSearchResultsByCategory(Category category, SearchCriteria searchCriteria)
            throws ServiceException;
    
    /**
     * Performs a search for search results in the given category, taking into consideration the SearchCriteria
     * 
     * This method will NOT return search results that are in a sub-level of a given category. For example, if you had a 
     * "Routers" category and a "Enterprise Routers" sub-category, asking for search results in "Routers", would NOT return
     * search results that are in the "Enterprise Routers" category. 
     * 
     * @see #findSearchResultsByCategory(Category, SearchCriteria)
     * 
     * @param category
     * @param searchCriteria
     * @return
     * @throws ServiceException
     */
    public SearchResult findExplicitSearchResultsByCategory(Category category, SearchCriteria searchCriteria)
            throws ServiceException;
    
    /**
     * Performs a search for search results across all categories for the given query, taking into consideration
     * the SearchCriteria
     * 
     * @param query
     * @param searchCriteria
     * @return the result of the search
     * @throws ServiceException
     * @deprecated use #findSearchResults(SearchCriteria)
     */
    @Deprecated
    public SearchResult findSearchResultsByQuery(String query, SearchCriteria searchCriteria)
            throws ServiceException;
    
    /**
     * Performs a search for search results in the given category for the given query, taking into consideration 
     * the SearchCriteria
     * 
     * @param category
     * @param query
     * @param searchCriteria
     * @throws ServiceException
     * @deprecated use #findSearchResults(SearchCriteria)
     */
    @Deprecated
    public SearchResult findSearchResultsByCategoryAndQuery(Category category, String query, SearchCriteria searchCriteria) throws ServiceException;

    /**
     * Performs a search for search results based on the given SearchCriteria, if SearchCriteria has a category, the category
     * is considering for the search.
     *
     * @param searchCriteria contains the information about this given search
     * @return the SearchResult
     */
    public SearchResult findSearchResults(SearchCriteria searchCriteria) throws ServiceException;

    /**
     * Gets all available facets for search results page
     * 
     * @return the available facets
     */
    public List<SearchFacetDTO> getSearchFacets();

    /**
     * Gets all available facets for the given category and global search
     *
     * @param category
     * @return
     */
    public List<SearchFacetDTO> getSearchFacets(Category category);

    /**
     * Gets all available facets for a given category
     * 
     * @param category
     * @return the available facets
     */
    public List<SearchFacetDTO> getCategoryFacets(Category category);

    /**
     * Determines whether or not the necessary configuration is in place
     *
     * @return whether or not the necessary configuration is in place
     */
    public boolean isActive();

}
