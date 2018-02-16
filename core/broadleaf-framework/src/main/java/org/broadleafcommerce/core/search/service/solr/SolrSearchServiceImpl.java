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
package org.broadleafcommerce.core.search.service.solr;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.dao.ProductDao;
import org.broadleafcommerce.core.catalog.dao.SkuDao;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.search.dao.FieldDao;
import org.broadleafcommerce.core.search.dao.IndexFieldDao;
import org.broadleafcommerce.core.search.dao.SearchFacetDao;
import org.broadleafcommerce.core.search.domain.CategorySearchFacet;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.domain.IndexField;
import org.broadleafcommerce.core.search.domain.IndexFieldType;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.domain.SearchFacetRange;
import org.broadleafcommerce.core.search.domain.SearchResult;
import org.broadleafcommerce.core.search.domain.solr.FieldType;
import org.broadleafcommerce.core.search.service.SearchService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Resource;

/**
 * An implementation of SearchService that uses Solr.
 * 
 * Note that prior to 2.2.0, this class used to contain all of the logic for interaction with Solr. Since 2.2.0, this class
 * has been refactored and parts of it have been split into the other classes you can find in this package.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class SolrSearchServiceImpl implements SearchService, DisposableBean {
    private static final Log LOG = LogFactory.getLog(SolrSearchServiceImpl.class);

    @Qualifier("blCatalogSolrConfiguration")
    @Autowired(required = false)
    protected SolrConfiguration solrConfiguration;

    @Value("${solr.index.use.sku}")
    protected boolean useSku;

    @Resource(name = "blProductDao")
    protected ProductDao productDao;

    @Resource(name = "blSkuDao")
    protected SkuDao skuDao;

    @Resource(name = "blFieldDao")
    protected FieldDao fieldDao;

    @Resource(name = "blSearchFacetDao")
    protected SearchFacetDao searchFacetDao;

    @Resource(name = "blSolrHelperService")
    protected SolrHelperService shs;

    @Resource(name = "blIndexFieldDao")
    protected IndexFieldDao indexFieldDao;

    @Resource(name = "blSolrSearchServiceExtensionManager")
    protected SolrSearchServiceExtensionManager extensionManager;

    @Autowired
    protected Environment environment;

    @Value("${solr.global.facets.category.search:false}")
    protected boolean globalFacetsForCategorySearch;

    /**
     * @return whether or not to enable debug query info for the SolrQuery
     */
    protected boolean shouldShowDebugQuery() {
        return environment.getProperty("solr.showDebugQuery", Boolean.class, false);
    }

    @Override
    public SearchResult findExplicitSearchResultsByCategory(Category category, SearchCriteria searchCriteria) throws ServiceException {
        searchCriteria.setSearchExplicitCategory(true);
        searchCriteria.setCategory(category);
        return findSearchResults(searchCriteria);
    }

    @Override
    @Deprecated
    public SearchResult findSearchResultsByCategory(Category category, SearchCriteria searchCriteria) throws ServiceException {
        searchCriteria.setCategory(category);
        return findSearchResults(searchCriteria);
    }

    @Override
    @Deprecated
    public SearchResult findSearchResultsByQuery(String query, SearchCriteria searchCriteria) throws ServiceException {
        searchCriteria.setQuery(query);
        return findSearchResults(searchCriteria);
    }

    @Override
    @Deprecated
    public SearchResult findSearchResultsByCategoryAndQuery(Category category, String query, SearchCriteria searchCriteria) throws ServiceException {
        searchCriteria.setCategory(category);
        searchCriteria.setQuery(query);
        return findSearchResults(searchCriteria);
    }

    @Override
    public SearchResult findSearchResults(SearchCriteria searchCriteria) throws ServiceException {
        List<SearchFacetDTO> facets = getSearchFacets(searchCriteria.getCategory());
        if (searchCriteria.getQuery() != null) {
            searchCriteria.setQuery("(" + sanitizeQuery(searchCriteria.getQuery()) + ")");
        } else {
            searchCriteria.setQuery("*:*");
        }

        return findSearchResults(searchCriteria.getQuery(), facets, searchCriteria, getDefaultSort(searchCriteria));
    }

    /**
     * @deprecated in favor of the other findSearchResults() method
     */
    @Deprecated
    protected SearchResult findSearchResults(String qualifiedSolrQuery, List<SearchFacetDTO> facets,
            SearchCriteria searchCriteria, String defaultSort) throws ServiceException {
        return findSearchResults(searchCriteria.getQuery(), facets, searchCriteria, defaultSort, (String[]) null);
    }
    
    /**
     * Given a qualified solr query string (such as "category:2002"), actually performs a solr search. It will
     * take into considering the search criteria to build out facets / pagination / sorting.
     *
     * @param searchCriteria
     * @param facets
     * @return the ProductSearchResult of the search
     * @throws ServiceException
     */
    protected SearchResult findSearchResults(String qualifiedSolrQuery, List<SearchFacetDTO> facets, SearchCriteria searchCriteria, String defaultSort, String... filterQueries)
            throws ServiceException  {
        Map<String, SearchFacetDTO> namedFacetMap = getNamedFacetMap(facets, searchCriteria);

        // Left here for backwards compatibility for this method signature
        if (searchCriteria.getQuery() == null && qualifiedSolrQuery != null) {
            searchCriteria.setQuery(qualifiedSolrQuery);
        }
        
        // Build the basic query
        // Solr queries with a 'start' parameter cannot be a negative number
        int start = (searchCriteria.getPage() <= 0) ? 0 : (searchCriteria.getPage() - 1);
        SolrQuery solrQuery = new SolrQuery()
                .setQuery(searchCriteria.getQuery())
                .setRows(searchCriteria.getPageSize())
                .setStart((start) * searchCriteria.getPageSize());

        //This is for SolrCloud.  We assume that we are always searching against a collection aliased as "PRIMARY"
        if (solrConfiguration.isSiteCollections()) {
            solrQuery.setParam("collection", solrConfiguration.getSiteAliasName(BroadleafRequestContext.getBroadleafRequestContext().getNonPersistentSite()));
        } else {
            solrQuery.setParam("collection", solrConfiguration.getPrimaryName()); //This should be ignored if not using SolrCloud
        }

        solrQuery.setFields(shs.getIndexableIdFieldName());
        if (filterQueries != null) {
            solrQuery.setFilterQueries(filterQueries);
        }

        // add category filter if applicable
        if (searchCriteria.getCategory() != null) {
            solrQuery.addFilterQuery(getCategoryFilter(searchCriteria));
        }

        solrQuery.addFilterQuery(shs.getNamespaceFieldName() + ":(\"" + solrConfiguration.getNamespace() + "\")");
        solrQuery.set("defType", "edismax");
        solrQuery.set("qf", buildQueryFieldsString(solrQuery, searchCriteria));

        // Attach additional restrictions
        attachActiveFacetFilters(solrQuery, namedFacetMap, searchCriteria);
        attachFacets(solrQuery, namedFacetMap, searchCriteria);
        
        modifySolrQuery(solrQuery, searchCriteria.getQuery(), facets, searchCriteria, defaultSort);

        // If there is a sort, remove all boosting that has been applied before we apply the sort clause.
        // We do this in order to support cases where we use boosting for enforcing a sort, specifically when sorting
        // on child documents.
        if (StringUtils.isNotBlank(defaultSort) || StringUtils.isNotBlank(searchCriteria.getSortQuery())) {
            solrQuery.remove("bq");
            solrQuery.remove("bf");
            solrQuery.remove("boost");
        }

        attachSortClause(solrQuery, searchCriteria, defaultSort);

        solrQuery.setShowDebugInfo(shouldShowDebugQuery());

        if (LOG.isTraceEnabled()) {
            try {
                LOG.trace(URLDecoder.decode(solrQuery.toString(), "UTF-8"));
            } catch (Exception e) {
                LOG.trace("Couldn't UTF-8 URL Decode: " + solrQuery.toString());
            }
        }

        // Query solr
        QueryResponse response;
        List<SolrDocument> responseDocuments;
        int numResults = 0;
        try {
            response = solrConfiguration.getServer().query(solrQuery, getSolrQueryMethod());
            responseDocuments = getResponseDocuments(response);
            numResults = (int) response.getResults().getNumFound();

            if (LOG.isTraceEnabled()) {
                LOG.trace(response.toString());

                for (SolrDocument doc : responseDocuments) {
                    LOG.trace(doc);
                }
            }
        } catch (SolrServerException e) {
            throw new ServiceException("Could not perform search", e);
        } catch (IOException e) {
            throw new ServiceException("Could not perform search", e);
        }

        // Get the facets
        setFacetResults(namedFacetMap, response);
        sortFacetResults(namedFacetMap);

        SearchResult result = new SearchResult();
        result.setFacets(facets);
        result.setQueryResponse(response);
        setPagingAttributes(result, numResults, searchCriteria);

        if (useSku) {
            List<Sku> skus = getSkus(responseDocuments);
            result.setSkus(skus);
        } else {
            // Get the products
            List<Product> products = getProducts(responseDocuments);
            result.setProducts(products);
        }

        return result;
    }

    protected String getDefaultSort(SearchCriteria criteria) {
        if (criteria.getCategory() != null) {
            return shs.getCategorySortFieldName(criteria.getCategory()) + " asc";
        }

        return null;
    }

    protected String getCategoryFilter(SearchCriteria searchCriteria) {
        String categoryFilterIds = StringUtils.join(shs.getCategoryFilterIds(searchCriteria.getCategory(), searchCriteria), "\" \"");
        
        String categoryFilterField = shs.getCategoryFieldName();
        if (searchCriteria.getSearchExplicitCategory()) {
            categoryFilterField = shs.getExplicitCategoryFieldName();
        }

        return categoryFilterField + ":(\"" + categoryFilterIds +  "\")";
    }

    public String getLocalePrefix() {
        if (BroadleafRequestContext.getBroadleafRequestContext() != null) {
            Locale locale = BroadleafRequestContext.getBroadleafRequestContext().getLocale();
            if (locale != null) {
                return locale.getLocaleCode() + "_";
            }
        }
        return "";
    }

    protected String buildQueryFieldsString(SolrQuery query, SearchCriteria searchCriteria) {
        StringBuilder queryBuilder = new StringBuilder();
        List<IndexField> fields = shs.getSearchableIndexFields();

        // we want to gather all the query fields into one list
        List<String> queryFields = new ArrayList<>();
        for (IndexField currentField : fields) {
            getQueryFields(query, queryFields, currentField, searchCriteria);
        }

        // we join our query fields to a single string to append to the solr query
        queryBuilder.append(StringUtils.join(queryFields, " "));

        return queryBuilder.toString();
    }

    /**
     * This helper method gathers the query fields for the given field and stores them in the List parameter.
     * @param currentField the current field
     * @param query
     * @param queryFields the query fields for this query
     * @param searchCriteria
     */
    protected void getQueryFields(SolrQuery query, final List<String> queryFields, IndexField indexField, SearchCriteria searchCriteria) {

        if (indexField != null && BooleanUtils.isTrue(indexField.getSearchable())) {
            List<IndexFieldType> fieldTypes = indexField.getFieldTypes();

            for (IndexFieldType indexFieldType : fieldTypes) {
                FieldType fieldType = indexFieldType.getFieldType();

                // this will hold the list of query fields for our given field
                ExtensionResultHolder<List<String>> queryFieldResult = new ExtensionResultHolder<>();
                queryFieldResult.setResult(queryFields);

                // here we try to get the query field's for this search field
                ExtensionResultStatusType result = extensionManager.getProxy().getQueryField(query, searchCriteria, indexFieldType, queryFieldResult);

                if (Objects.equals(ExtensionResultStatusType.NOT_HANDLED, result)) {
                    // if we didn't get any query fields we just add a default one
                    String solrFieldName = shs.getPropertyNameForIndexField(indexFieldType.getIndexField(), fieldType);
                    queryFields.add(solrFieldName);
                }
            }
        }
    }

    /**
     * Provides a hook point for implementations to modify all SolrQueries before they're executed.
     * Modules should leverage the extension manager method of the same name,
     * {@link SolrSearchServiceExtensionHandler#modifySolrQuery(SolrQuery, String, List, SearchCriteria, String)}
     * 
     * @param query
     * @param qualifiedSolrQuery
     * @param facets
     * @param searchCriteria
     * @param defaultSort
     */
    protected void modifySolrQuery(SolrQuery query, String qualifiedSolrQuery,
            List<SearchFacetDTO> facets, SearchCriteria searchCriteria, String defaultSort) {

        extensionManager.getProxy().modifySolrQuery(createSearchContextDTO(), query, qualifiedSolrQuery, facets, searchCriteria, defaultSort);
    }

    protected SearchContextDTO createSearchContextDTO() {
        SearchContextDTO searchContextDTO = new SearchContextDTO();

        BroadleafRequestContext ctx = BroadleafRequestContext.getBroadleafRequestContext();

        if (ctx != null) {
            Map<String, Object> ruleMap = (Map<String, Object>) ctx.getRequestAttribute("blRuleMap");

            if (MapUtils.isNotEmpty(ruleMap)) {
                searchContextDTO.setAttributes(ruleMap);
            }
        }

        return searchContextDTO;
    }

    protected List<SolrDocument> getResponseDocuments(QueryResponse response) {
        return shs.getResponseDocuments(response);
    }

    @Override
    public List<SearchFacetDTO> getSearchFacets() {
        List<SearchFacet> searchFacets = new ArrayList<>();
        ExtensionResultStatusType status = extensionManager.getProxy().getSearchFacets(searchFacets);

        if (Objects.equals(ExtensionResultStatusType.NOT_HANDLED, status)) {
            if (useSku) {
                return buildSearchFacetDTOs(searchFacetDao.readAllSearchFacets(FieldEntity.SKU));
            }
            return buildSearchFacetDTOs(searchFacetDao.readAllSearchFacets(FieldEntity.PRODUCT));
        }

        return buildSearchFacetDTOs(searchFacets);
    }

    @Override
    public List<SearchFacetDTO> getSearchFacets(Category category) {
        List<SearchFacetDTO> searchFacetDTOs = new ArrayList<>();

        if (category != null) {
            searchFacetDTOs.addAll(getCategoryFacets(category));
        }

        // if we aren't searching in a category, or globalFacetsForCategorySearch is true, include the global search facets
        if (globalFacetsForCategorySearch || category == null) {
            searchFacetDTOs.addAll(getSearchFacets());
        }

        return searchFacetDTOs;
    }

    @Override
    public List<SearchFacetDTO> getCategoryFacets(Category category) {
        List<SearchFacet> searchFacets = new ArrayList<>();
        ExtensionResultStatusType status = extensionManager.getProxy().getCategorySearchFacets(category, searchFacets);

        if (Objects.equals(ExtensionResultStatusType.NOT_HANDLED, status)) {
            List<CategorySearchFacet> categorySearchFacets = category.getCumulativeSearchFacets();
            for (CategorySearchFacet categorySearchFacet : categorySearchFacets) {
                searchFacets.add(categorySearchFacet.getSearchFacet());
            }
        }

        return buildSearchFacetDTOs(searchFacets);
    }

    /**
     * Sets up the sorting criteria. This will support sorting by multiple fields at a time
     * 
     * @param query
     * @param searchCriteria
     */
    protected void attachSortClause(SolrQuery query, SearchCriteria searchCriteria, String defaultSort) {
        shs.attachSortClause(query, searchCriteria, defaultSort);
        query.addSort("score", SolrQuery.ORDER.desc);
    }

    /**
     * Restricts the query by adding active facet filters.
     * 
     * @param query
     * @param namedFacetMap
     * @param searchCriteria
     */
    protected void attachActiveFacetFilters(SolrQuery query, Map<String, SearchFacetDTO> namedFacetMap,
            SearchCriteria searchCriteria) {
        shs.attachActiveFacetFilters(query, namedFacetMap, searchCriteria);
    }
    
    /**
     * Scrubs a facet value string for all Solr special characters, automatically adding escape characters
     * 
     * @param facetValue The raw facet value
     * @return The facet value with all special characters properly escaped, safe to be used in construction of a Solr query
     */
    protected String scrubFacetValue(String facetValue) {
        return shs.scrubFacetValue(facetValue);
    }

    /**
     * Notifies solr about which facets you want it to determine results and counts for
     * @param query
     * @param namedFacetMap
     * @param searchCriteria
     */
    protected void attachFacets(SolrQuery query, Map<String, SearchFacetDTO> namedFacetMap, SearchCriteria searchCriteria) {
        shs.attachFacets(query, namedFacetMap, searchCriteria);
    }

    /**
     * Builds out the DTOs for facet results from the search. This will then be used by the view layer to
     * display which values are available given the current constraints as well as the count of the values.
     * 
     * @param namedFacetMap
     * @param response
     */
    protected void setFacetResults(Map<String, SearchFacetDTO> namedFacetMap, QueryResponse response) {
        shs.setFacetResults(namedFacetMap, response);
    }

    /**
     * Invoked to sort the facet results. This method will use the natural sorting of the value attribute of the
     * facet (or, if value is null, the minValue of the facet result). Override this method to customize facet
     * sorting for your given needs.
     * 
     * @param namedFacetMap
     */
    protected void sortFacetResults(Map<String, SearchFacetDTO> namedFacetMap) {
        shs.sortFacetResults(namedFacetMap);
    }

    /**
     * Sets the total results, the current page, and the page size on the ProductSearchResult. Total results comes
     * from solr, while page and page size are duplicates of the searchCriteria conditions for ease of use.
     * 
     * @param result
     * @param response
     * @param searchCriteria
     */
    public void setPagingAttributes(SearchResult result, int numResults, SearchCriteria searchCriteria) {
        result.setTotalResults(numResults);
        result.setPage(searchCriteria.getPage());
        result.setPageSize(searchCriteria.getPageSize());
    }

    /**
     * Given a list of product IDs from solr, this method will look up the IDs via the productDao and build out
     * actual Product instances. It will return a Products that is sorted by the order of the IDs in the passed
     * in list.
     * 
     * @param response
     * @return the actual Product instances as a result of the search
     */
    protected List<Product> getProducts(List<SolrDocument> responseDocuments) {
        final List<Long> productIds = new ArrayList<>();
        for (SolrDocument doc : responseDocuments) {
            productIds.add((Long) doc.getFieldValue(shs.getIndexableIdFieldName()));
        }

        List<Product> products = productDao.readProductsByIds(productIds);

        extensionManager.getProxy().batchFetchCatalogData(products);

        // We have to sort the products list by the order of the productIds list to maintain sortability in the UI
        if (products != null) {
            Collections.sort(products, new Comparator<Product>() {
                @Override
                public int compare(Product o1, Product o2) {
                    Long o1id = shs.getIndexableId(o1);
                    Long o2id = shs.getIndexableId(o2);
                    return new Integer(productIds.indexOf(o1id)).compareTo(productIds.indexOf(o2id));
                }
            });
        }

        extensionManager.getProxy().modifySearchResults(responseDocuments, products);

        return products;
    }

    /**
     * Given a list of Sku IDs from solr, this method will look up the IDs via the skuDao and build out
     * actual Sku instances. It will return a Sku list that is sorted by the order of the IDs in the passed
     * in list.
     * 
     * @param response
     * @return the actual Sku instances as a result of the search
     */
    protected List<Sku> getSkus(List<SolrDocument> responseDocuments) {
        final List<Long> skuIds = new ArrayList<>();
        for (SolrDocument doc : responseDocuments) {
            skuIds.add((Long) doc.getFieldValue(shs.getIndexableIdFieldName()));
        }

        List<Sku> skus = skuDao.readSkusByIds(skuIds);

        // We have to sort the skus list by the order of the skuIds list to maintain sortability in the UI
        if (skus != null) {
            Collections.sort(skus, new Comparator<Sku>() {
                @Override
                public int compare(Sku o1, Sku o2) {
                    return new Integer(skuIds.indexOf(o1.getId())).compareTo(skuIds.indexOf(o2.getId()));
                }
            });
        }

        return skus;
    }

    /**
     * Create the wrapper DTO around the SearchFacet
     * 
     * @param searchFacets
     * @return the wrapper DTO
     */
    protected List<SearchFacetDTO> buildSearchFacetDTOs(List<SearchFacet> searchFacets) {
        return shs.buildSearchFacetDTOs(searchFacets);
    }

    /**
     * Checks to see if the requiredFacets condition for a given facet is met.
     * 
     * @param facet
     * @param request
     * @return whether or not the facet parameter is available 
     */
    protected boolean facetIsAvailable(SearchFacet facet, Map<String, String[]> params) {
        return shs.isFacetAvailable(facet, params);
    }

    /**
     * Perform any necessary query sanitation here. For example, we disallow open and close parentheses, colons, and we also
     * ensure that quotes are actual quotes (") and not the URL encoding (&quot;) so that Solr is able to properly handle
     * the user's intent.
     * 
     * @param query
     * @return the sanitized query
     */
    protected String sanitizeQuery(String query) {
        return shs.sanitizeQuery(query);
    }

    /**
     * Returns a fully composed solr field string. Given indexField = a, tag = ex, and a non-null range,
     * would produce the following String: {!tag=a frange incl=false l=minVal u=maxVal}a
     */
    protected String getSolrTaggedFieldString(String indexField, String tag, SearchFacetRange range) {
        return shs.getSolrTaggedFieldString(indexField, tag, range);
    }

    /**
     * Returns a solr field tag. Given indexField = a, tag = tag, would produce the following String:
     * {!tag=a}. if range is not null it will produce {!tag=a frange incl=false l=minVal u=maxVal} 
     */
    protected String getSolrFieldTag(String tagField, String tag, SearchFacetRange range) {
        return shs.getSolrFieldTag(tagField, tag, range);
    }

    protected String getSolrRangeString(String fieldName, BigDecimal minValue, BigDecimal maxValue) {
        return shs.getSolrRangeString(fieldName, minValue, maxValue);
    }

    /**
     * @param minValue
     * @param maxValue
     * @return a string representing a call to the frange solr function. it is not inclusive of lower limit, inclusive of upper limit
     */
    protected String getSolrRangeFunctionString(BigDecimal minValue, BigDecimal maxValue) {
        return shs.getSolrRangeFunctionString(minValue, maxValue);
    }

    /**
     * @param facets
     * @param searchCriteria
     * @return a map of fully qualified solr index field key to the searchFacetDTO object
     */
    protected Map<String, SearchFacetDTO> getNamedFacetMap(List<SearchFacetDTO> facets,
            final SearchCriteria searchCriteria) {
        return shs.getNamedFacetMap(facets, searchCriteria);
    }

    /**
     * Allows the user to choose the query method to use.  POST allows for longer, more complex queries with 
     * a higher number of facets.
     * 
     * Default value is POST.  Implementors can override this to use GET if they wish.
     * 
     * @return
     */
    protected METHOD getSolrQueryMethod() {
        return METHOD.POST;
    }

    @Override
    public void destroy() throws Exception {
        solrConfiguration.destroy();
    }

    @Override
    public boolean isActive() {
        return solrConfiguration != null;
    }
}
