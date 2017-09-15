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

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.search.domain.FieldEntity;
import org.broadleafcommerce.core.search.domain.IndexField;
import org.broadleafcommerce.core.search.domain.IndexFieldType;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.domain.SearchFacetRange;
import org.broadleafcommerce.core.search.domain.solr.FieldType;

import java.util.List;
import java.util.Map;

/**
 * Implementors of the SolrSearchServiceExtensionHandler interface should extend this class so that if 
 * additional extension points are added which they don't care about, their code will not need to be
 * modified.
 * 
 * @author bpolster
 */                                      
public abstract class AbstractSolrSearchServiceExtensionHandler extends AbstractExtensionHandler
        implements SolrSearchServiceExtensionHandler {

    @Override
    public ExtensionResultStatusType buildPrefixListForIndexField(IndexField field, FieldType fieldType, List<String> prefixList) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType filterSearchFacetRanges(SearchFacetDTO dto, List<SearchFacetRange> ranges) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
    
    @Override
    public ExtensionResultStatusType modifySolrQuery(SolrQuery query, String qualifiedSolrQuery,
            List<SearchFacetDTO> facets, SearchCriteria searchCriteria, String defaultSort) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType modifySolrQuery(SearchContextDTO context, SolrQuery query, String qualifiedSolrQuery, List<SearchFacetDTO> facets, SearchCriteria searchCriteria, String defaultSort) {
        return modifySolrQuery(query, qualifiedSolrQuery, facets, searchCriteria, defaultSort);
    }

    @Override
    public ExtensionResultStatusType getQueryField(SolrQuery query, SearchCriteria searchCriteria, IndexFieldType indexFieldType, ExtensionResultHolder<List<String>> queryFieldsResult) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType modifySearchResults(List<SolrDocument> responseDocuments, List<Product> products) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType getSearchFacets(List<SearchFacet> searchFacets) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    /**
     *
     * @deprecated in favor of {@link #attachFacet(SolrQuery, String, SearchFacetDTO, SearchCriteria)}
     *
     */
    @Override
    @Deprecated
    public ExtensionResultStatusType attachFacet(SolrQuery query, String indexField, SearchFacetDTO dto) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType attachFacet(SolrQuery query, String indexField, SearchFacetDTO dto, SearchCriteria searchCriteria) {
        return attachFacet(query, indexField, dto);
    }

    @Override
    public ExtensionResultStatusType setFacetResults(Map<String, SearchFacetDTO> namedFacetMap, QueryResponse response) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    @Deprecated
    public ExtensionResultStatusType buildActiveFacetFilter(FieldEntity entityType, String solrKey, String[] selectedValues, List<String> valueStrings) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType buildActiveFacetFilter(SearchFacet facet, String[] selectedValues, List<String> valueStrings) {
        return buildActiveFacetFilter(facet.getField().getEntityType(), facet.getField().getAbbreviation(), selectedValues, valueStrings);
    }

    @Override
    public ExtensionResultStatusType addAdditionalCategoryIds(Category category, SearchCriteria searchCriteria, List<Long> categoryIds) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType getCategorySearchFacets(Category category, List<SearchFacet> searchFacets) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType getSearchableIndexFields(List<IndexField> fields) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType getCategoryId(Category category, Long[] returnContainer) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType batchFetchCatalogData(List<Product> products) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType attachSortField(SolrQuery solrQuery, String requestedSortFieldName, SolrQuery.ORDER order) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType getPropertyNameForIndexField(IndexField field, FieldType fieldType, String prefix, ExtensionResultHolder<String> erh) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
}
