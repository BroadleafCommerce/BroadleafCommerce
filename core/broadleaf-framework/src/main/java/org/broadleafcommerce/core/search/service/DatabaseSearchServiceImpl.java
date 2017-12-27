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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.search.dao.FieldDao;
import org.broadleafcommerce.core.search.dao.SearchFacetDao;
import org.broadleafcommerce.core.search.domain.*;
import org.broadleafcommerce.core.search.extension.PriceListSearchFacetRangeExtensionManager;
import org.broadleafcommerce.core.search.service.solr.SolrSearchServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

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

    @Resource(name = "priceListSearchFacetRangeExtensionManager")
    protected PriceListSearchFacetRangeExtensionManager extensionManager;

    @Value("${database.searchfacets.enabled}")
    protected boolean enabledFacets;

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
        sanitizeSearchCriteria(searchCriteria);
        List<Product> products = catalogService.findFilteredActiveProductsByCategory(category, searchCriteria);
        List<Product> pageProducts = this.getPageProducts(products, searchCriteria.getPageSize(), (searchCriteria.getPage() - 1) * searchCriteria.getPageSize());
        if (enabledFacets) {
            List<Long> productIds = new ArrayList<>();
            for (Product product : products) {
                productIds.add(product.getId());
            }
            List<SearchFacetDTO> facets = getCategoryFacets(category, productIds);
            setActiveFacets(facets, searchCriteria);
            result.setFacets(calculateQuantityForRangeFacetValues(facets, products));
        }
        result.setProducts(pageProducts);
        result.setTotalResults(products.size());
        result.setPage(searchCriteria.getPage());
        result.setPageSize(searchCriteria.getPageSize());
        return result;
    }

    @Override
    public SearchResult findSearchResultsByQuery(String query, SearchCriteria searchCriteria) {
        SearchResult result = new SearchResult();
        setQualifiedKeys(searchCriteria);
        sanitizeSearchCriteria(searchCriteria);
        List<Product> products = catalogService.findFilteredActiveProductsByQuery(query, searchCriteria);
        List<Product> pageProducts = this.getPageProducts(products, searchCriteria.getPageSize(), (searchCriteria.getPage() - 1) * searchCriteria.getPageSize());
        if (enabledFacets) {
            List<Long> productIds = new ArrayList<>();
            for (Product product : products) {
                productIds.add(product.getId());
            }
            List<SearchFacetDTO> facets = getSearchFacets(productIds);
            setActiveFacets(facets, searchCriteria);
            result.setFacets(calculateQuantityForRangeFacetValues(facets, products));
        }
        result.setProducts(pageProducts);
        result.setTotalResults(products.size());
        result.setPage(searchCriteria.getPage());
        result.setPageSize(searchCriteria.getPageSize());
        return result;
    }

    @Override
    public SearchResult findSearchResults(SearchCriteria searchCriteria) throws ServiceException {
        return searchCriteria.getQuery() == null ? findSearchResultsByCategory(searchCriteria.getCategory(), searchCriteria) : findSearchResultsByQuery(searchCriteria.getQuery(), searchCriteria);
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

    @SuppressWarnings("unchecked")
    public List<SearchFacetDTO> getSearchFacets(List<Long> productIds) {
        List<SearchFacetDTO> facets = null;

        String cacheKey = CACHE_KEY_PREFIX + "blc-search";
        Element element = cache.get(cacheKey);
        if (element != null) {
            facets = (List<SearchFacetDTO>) element.getValue();
        }

        if (facets == null) {
            facets = buildSearchFacetDtos(searchFacetDao.readAllSearchFacets(FieldEntity.PRODUCT), productIds);
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

    @SuppressWarnings("unchecked")
    public List<SearchFacetDTO> getCategoryFacets(Category category, List<Long> productIds) {
        List<SearchFacetDTO> facets = null;
        List<CategorySearchFacet> categorySearchFacets = category.getCumulativeSearchFacets();
        List<SearchFacet> searchFacets = new ArrayList<SearchFacet>();
        for (CategorySearchFacet categorySearchFacet : categorySearchFacets) {
            searchFacets.add(categorySearchFacet.getSearchFacet());
        }
        facets = buildSearchFacetDtos(searchFacets, productIds);
        return facets;
    }

    /**
     * Perform any necessary conversion of the key to be used by the search service
     *
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
            return qualifiedFieldName.replace("Product.", "");
        } else if (qualifiedFieldName.contains("defaultSku")) {
            return qualifiedFieldName.replace("Product.", "");
        } else if (qualifiedFieldName.contains("productOptionValuesMap")) {
            return qualifiedFieldName.replace("Product.", "");
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
     *
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

    protected List<SearchFacetDTO> buildSearchFacetDtos(List<SearchFacet> categoryFacets, List<Long> productIds) {
        List<SearchFacetDTO> facets = new ArrayList<SearchFacetDTO>();

        for (SearchFacet facet : categoryFacets) {
            SearchFacetDTO dto = new SearchFacetDTO();
            dto.setFacet(facet);
            dto.setShowQuantity(false);
            dto.setFacetValues(getFacetValues(facet, productIds));
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

    protected List<SearchFacetResultDTO> getFacetValues(SearchFacet facet, List<Long> productIds) {
        if (facet.getSearchFacetRanges().size() > 0) {
            return getRangeFacetValues(facet);
        } else {
            return getMatchFacetValues(facet, productIds);
        }
    }

    protected List<SearchFacetResultDTO> getRangeFacetValues(SearchFacet facet) {
        List<SearchFacetResultDTO> results = new ArrayList<SearchFacetResultDTO>();

        List<SearchFacetRange> ranges = facet.getSearchFacetRanges();
        ExtensionResultHolder<Long> contextPriceListId = new ExtensionResultHolder<>();
        extensionManager.getProxy().getContextPriceListId(BroadleafRequestContext.getBroadleafRequestContext(), contextPriceListId);

        Collections.sort(ranges, new Comparator<SearchFacetRange>() {
            @Override
            public int compare(SearchFacetRange o1, SearchFacetRange o2) {
                return o1.getMinValue().compareTo(o2.getMinValue());
            }
        });

        for (SearchFacetRange range : ranges) {
            ExtensionResultHolder<Long> rangePriceListId = new ExtensionResultHolder<>();
            extensionManager.getProxy().getSearchFacetRangePriceListId(range, rangePriceListId);
            if (Objects.equals(contextPriceListId.getResult(), rangePriceListId.getResult())) {

                SearchFacetResultDTO dto = new SearchFacetResultDTO();
                dto.setMinValue(range.getMinValue());
                dto.setMaxValue(range.getMaxValue());
                dto.setFacet(facet);
                results.add(dto);
            }
        }
        return results;
    }

    protected List<SearchFacetResultDTO> getMatchFacetValues(SearchFacet facet) {
        List<SearchFacetResultDTO> results = new ArrayList<SearchFacetResultDTO>();

        String qualifiedFieldName = facet.getField().getQualifiedFieldName();
        qualifiedFieldName = getDatabaseQualifiedFieldName(qualifiedFieldName);
        List<Tuple> values = searchFacetDao.readDistinctValuesForField(qualifiedFieldName, null);

        for (Tuple value : values) {
            SearchFacetResultDTO dto = new SearchFacetResultDTO();
            dto.setValue((String) value.get(0));
            dto.setQuantity(Integer.parseInt(value.get(1).toString()));
            dto.setFacet(facet);
            results.add(dto);
        }

        return results;
    }

    protected List<SearchFacetResultDTO> getMatchFacetValues(SearchFacet facet, List<Long> productIds) {
        List<SearchFacetResultDTO> results = new ArrayList<SearchFacetResultDTO>();

        String qualifiedFieldName = facet.getField().getQualifiedFieldName();
        qualifiedFieldName = getDatabaseQualifiedFieldName(qualifiedFieldName);
        List<Tuple> values = searchFacetDao.readDistinctValuesForField(qualifiedFieldName, productIds);

        for (Tuple value : values) {
            SearchFacetResultDTO dto = new SearchFacetResultDTO();
            dto.setValue((String) value.get(0));
            dto.setQuantity(Integer.parseInt(value.get(1).toString()));
            dto.setFacet(facet);
            results.add(dto);
        }

        return results;
    }

    protected List<SearchFacetDTO> calculateQuantityForRangeFacetValues(List<SearchFacetDTO> facetDtos, List<Product> products) {
        BigDecimal maxValue = new BigDecimal(Integer.MAX_VALUE);
        for (SearchFacetDTO facetDto : facetDtos) {
            List<SearchFacetResultDTO> facetValues = facetDto.getFacetValues();
            for (SearchFacetResultDTO facetValue : facetValues) {
                if (facetValue.getMinValue() != null || facetValue.getMaxValue() != null) {
                    int quantity = 0;
                    for (Product product : products) {
                        if (product.getSalePrice() != null) {
                            Money price = product.getSalePrice();
                            BigDecimal rangeMax = facetValue.getMaxValue() == null ? maxValue : facetValue.getMaxValue();
                            if ((price.getAmount().compareTo(facetValue.getMinValue()) > 0) && (price.getAmount().compareTo(rangeMax) < 0))
                                quantity++;
                        }
                    }
                    if (quantity > 0)
                        facetValue.setQuantity(quantity);
                }
            }
        }
        return facetDtos;
    }


    protected void sanitizeSearchCriteria(SearchCriteria searchCriteria) {
        Map<String, String> keysMap = new HashMap<>();
        if (searchCriteria.getSortQuery() != null && searchCriteria.getSortQuery().contains("defaultSku.price"))
            searchCriteria.setSortQuery(searchCriteria.getSortQuery().replace("price", "retailPrice"));
        if (searchCriteria.getFilterCriteria().size() > 0) {
            for (String filterKey : searchCriteria.getFilterCriteria().keySet()) {
                String newFilterKey = filterKey;
                if (filterKey.contains("Product")) {
                    newFilterKey = filterKey.replace("Product", "product");
                }
                if (filterKey.contains("productAttributes(")) {
                    newFilterKey = filterKey.replace("(", ".").replace(").value", "");
                }
                if (filterKey.contains("defaultSku.price")) {
                    newFilterKey = filterKey.replace("price", "retailPrice");
                }
                if (!filterKey.equals(newFilterKey)) {
                    keysMap.put(filterKey, newFilterKey);
                }
            }
            for (String key : keysMap.keySet()) {
                searchCriteria.getFilterCriteria().put(keysMap.get(key), searchCriteria.getFilterCriteria().get(key));
                searchCriteria.getFilterCriteria().remove(key);
            }
        }
    }

    protected List<Product> getPageProducts(List<Product> products, int limit, int offset) {
        if (products.isEmpty()) {
            return products;
        } else {
            int toIndex = offset + limit;
            if (toIndex > products.size()) {
                toIndex = products.size();
            }
            if (offset > toIndex) {
                offset = toIndex;
            }
            return products.subList(offset, toIndex);
        }
    }

    @Override
    public boolean isActive() {
        return true;
    }

}
