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
package org.broadleafcommerce.core.search.service.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.broadleafcommerce.common.extension.ExtensionHandler;
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
 * @author Andre Azzolini (apazzolini), bpolster
 */                                      
public interface SolrSearchServiceExtensionHandler extends ExtensionHandler {

    /**
     * Returns a prefix if required for the passed in searchable field. 
     */
    ExtensionResultStatusType buildPrefixListForIndexField(IndexField field, FieldType fieldType,
            List<String> prefixList);

    /**
     * Builds the search facet ranges for the provided dto.
     * 
     * @param context
     * @param dto
     * @param ranges
     */
    ExtensionResultStatusType filterSearchFacetRanges(SearchFacetDTO dto, List<SearchFacetRange> ranges);

    /**
     * Provides an extension point to modify the SolrQuery.
     * @param context
     * @param query
     * @param qualifiedSolrQuery
     * @param facets
     * @param searchCriteria
     * @param defaultSort
     */
    ExtensionResultStatusType modifySolrQuery(SolrQuery query, String qualifiedSolrQuery,
            List<SearchFacetDTO> facets, SearchCriteria searchCriteria, String defaultSort);

    /**
     * In certain scenarios, the requested category id might not be the one that should be used in Solr.
     * If this method returns {@link ExtensionResultStatusType#HANDLED}, the value placed in the 0th element
     * in the returnContainer should be used.
     * 
     * @param category
     * @param returnContainer
     * @return the extension result status type
     */
    ExtensionResultStatusType getCategoryId(Category category, Long[] returnContainer);

    /**
     * <p>
     * Finds and adds the query fields for the given search field and searchable field type. This method should only ADD
     * to the list within the <b>queryFieldsResult</b> parameters.
     *
     * <p>
     * Most implementations of this will need to invoke {@link SolrHelperService#getPropertyNameForIndexField(IndexField, FieldType)}
     * in order to return the right value to populate in the <b>queryFieldsResult</b>. If the returned result is
     * {@link ExtensionResultStatusType#NOT_HANDLED} then the default behavior is to only do that.
     *
     *
     * @param query
     * @param searchCriteria
     *@param indexFieldType the field type of the field
     * @param queryFieldsResult the binding result that contains the list of query fields, only add to this   @return the result of the handler, if NOT_HANDLED, then no query fields were added
     * @see {@link SolrHelperService#getPropertyNameForIndexField(IndexField, FieldType)}
     */
    ExtensionResultStatusType getQueryField(SolrQuery query, SearchCriteria searchCriteria, IndexFieldType indexFieldType, ExtensionResultHolder<List<String>> queryFieldsResult);

    /**
     * <p>Modifies the product search results from a Solr query</p>
     *
     * <p>The parameters passed into this method should be assumed to be sorted identically and match one to one.</p>
     *
     * @param responseDocuments the response documents from Solr
     * @param products the products that tie to the response documents
     * @return the result of the handler, if NOT_HANDLED, then no changes where made
     */
    ExtensionResultStatusType modifySearchResults(List<SolrDocument> responseDocuments, List<Product> products);

    /**
     * Populates the List of SearchFacet's, or else returns NOT_HANDLED
     *
     * @param searchFacets the List to populate
     * @return the result of the handler
     */
    ExtensionResultStatusType getSearchFacets(List<SearchFacet> searchFacets);

    /**
     * Attaches the given dto to the given query, if possible
     *
     * @param query the SolrQuery to attach the facet to
     * @param key the key
     * @param dto
     * @return the result of the handler
     */
    ExtensionResultStatusType attachFacet(SolrQuery query, String indexField, SearchFacetDTO dto);

    /**
     * Attaches any additional facet results to the namedFacetMap if they exist. This should only attach facets if they do
     * not already have result DTOs
     *
     * @param namedFacetMap
     * @param response
     * @return the result of the handler
     */
    ExtensionResultStatusType setFacetResults(Map<String, SearchFacetDTO> namedFacetMap, QueryResponse response);

    /**
     * Builds the active facet filter query string for the given entity type and values. Typically this is only used when you
     * are doing faceting on nested child documents.
     *
     * @param entityType
     * @param solrKey
     * @param selectedValues
     * @param valueStrings
     * @return
     */
    ExtensionResultStatusType buildActiveFacetFilter(FieldEntity entityType, String solrKey, String[] selectedValues, List<String> valueStrings);

    /**
     * Adds any additional category ids to filter by when category browsing or searching.
     *
     * @param category the current Category we are browsing or searching on
     * @param searchCriteria the criteria for the current query
     * @param categoryIds the category IDs we are going to filter on (this already includes the current category's ID)
     * @return NOT_HANDLED if no IDs were added, and HANDLED_CONTINUE if there were
     */
    ExtensionResultStatusType addAdditionalCategoryIds(Category category, SearchCriteria searchCriteria, List<Long> categoryIds);

    /**
     * Populates the List of SearchFacet's for the given Category, or else returns NOT_HANDLED
     *
     * @param category
     * @param searchFacets
     * @return
     */
    ExtensionResultStatusType getCategorySearchFacets(Category category, List<SearchFacet> searchFacets);

    /**
     * Populated the List of searchable IndexField's that will be used in building the query fields (qf) for a Solr query.
     * It is assumed that if the result of this call is NOT_HANDLED, then SolrSearchService will follow it's default behavior
     * for populating IndexFields.
     *
     * @param fields the List to be populated.
     * @return HANDLED_CONTINUE if it added field, NOT_HANDLED otherwise
     */
    ExtensionResultStatusType getSearchableIndexFields(List<IndexField> fields);

    /**
     * Batch fetch important collections for the entire list of products in single batch fetch queries. In general, this is intended
     * to be used for search results and category landing page results. For batch fetching during solr indexing, see
     * {@link #startBatchEvent(List)}.
     *
     * @param products
     * @return
     */
    ExtensionResultStatusType batchFetchCatalogData(List<Product> products);

}
