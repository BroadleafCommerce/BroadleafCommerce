/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Indexable;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.IndexField;
import org.broadleafcommerce.core.search.domain.SearchCriteria;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.core.search.domain.SearchFacetDTO;
import org.broadleafcommerce.core.search.domain.SearchFacetRange;
import org.broadleafcommerce.core.search.domain.solr.FieldType;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Andre Azzolini (apazzolini)
 */
public interface SolrHelperService {

    /**
     * Swaps the primary and reindex cores.
     * If the reindex core is null, we are operating in single core mode. In this scenario, no swap occurs.
     *
     * @throws ServiceException
     */
    void swapActiveCores(SolrConfiguration solrConfiguration) throws ServiceException;

    /**
     * This property is needed to be non-null to allow filtering by multiple facets at one time and have the results
     * be an AND of the facets. Apart from being non-empty, the actual value does not matter.
     *
     * @return the non-empty global facet tag field
     */
    String getGlobalFacetTagField();

    /**
     * Returns the property name for the given field, field type, and prefix
     *
     * @param field
     * @param fieldType
     * @param prefix
     * @return the property name for the field and fieldtype
     */
    String getPropertyNameForIndexField(IndexField field, FieldType fieldType, String prefix);

    /**
     * Returns the property name for the given field and field type. This will apply the global prefix to the field,
     * and it will also apply either the locale prefix or the pricelist prefix, depending on whether or not the field
     * type was set to FieldType.PRICE
     *
     * @param field
     * @param fieldType
     * @return the property name for the field and fieldtype
     */
    String getPropertyNameForIndexField(IndexField field, FieldType fieldType);

    /**
     * @return the Solr id of this indexable
     */
    String getSolrDocumentId(SolrInputDocument document, Indexable indexable);

    /**
     * @return the name of the field that keeps track what namespace this document belongs to
     */
    String getNamespaceFieldName();

    /**
     * @return the id field name, with the global prefix as appropriate
     */
    String getIdFieldName();

    /**
     * @return the id field name. Usually "productId"
     */
    String getIndexableIdFieldName();

    /**
     * @return the category field name, with the global prefix as appropriate
     */
    String getCategoryFieldName();

    /**
     * @return the explicit category field name, with the global prefix as appropriate
     */
    String getExplicitCategoryFieldName();

    /**
     * The field that should store which catalog the item is being indexed for
     */
    String getCatalogFieldName();

    /**
     * <p>
     * The field that the list of catalogs that have overridden this document are in. In a multitenant environment this
     * works like so:
     *
     * <p>
     * <ol>
     * <li>MASTER_CATALOG exists in a template site with ID 1 and contains {@link #getProductIdFieldName()} == 5</li>
     * <li>Product ID 5 is indexed in Solr with {@link #getCatalogFieldName()} == 1</li>
     * <li>A standard site is created whose default catalog id is 2</li>
     * <li>Catalog 1 is assigned to the standard site as EDITABLE</li>
     * <li>Because the standard site can edit the catalog, edits actually create a clone of the original catalog product
     *  with the changed fields. This new product that is created in the database via this process has an id of 100</li>
     * <li>Solr then creates a new document with {@link #getProductIdFieldName()} == 5</li>
     *  and {@link #getCatalogFieldName()} == 2 (note that it uses the MASTER_CATALOG product ID and not the cloned id)
     * <li>Solr looks up the original document for {@link #getCatalogFieldName()} == 1 and
     *  {@link #getProductIdFieldName()} == 5 and then adds to the list of {@link #getCatalogOverridesFieldName()}, id 2.</li>
     * <li>When querying Solr for a list of products within a catalog, documents whose {@link #getCatalogOverridesFieldName()}
     *  contain the current catalog are filtered out.</li>
     * </ol>
     */
    String getCatalogOverridesFieldName();

    /**
     * The field that stores which sandbox the document is active for. Whenever a change is made to a new Solr document is
     * created with all of the changed field values and this field set to the sandbox the change is in so that searches
     * work when previewing but are filtered out in production.
     */
    String getSandBoxFieldName();

    /**
     * Which level of the priority tree the sandbox field is in like approval or user
     *
     * @see {@link SandBoxType}
     */
    String getSandBoxPriorityFieldName();

    /**
     * Used for DELETE documents that are extra documents created in a sandbox when an item is deleted. These are eventually
     * cleaned up when deployed to production
     */
    String getSandBoxChangeTypeFieldName();

    /**
     * @param category
     * @return the default sort field name for this category
     */
    String getCategorySortFieldName(Category category);

    /**
     * @param categoryId
     * @return the default sort field name for this category
     */
    String getCategorySortFieldName(Long categoryId);

    /**
     * Determines if there is a locale prefix that needs to be applied to the given field for this particular request.
     * By default, a locale prefix is not applicable for category, explicitCategory, or fields that have type Price.
     * Also, it is not applicable for non-translatable fields
     *
     * <ul>
     *     <li>Note: This method should NOT return null. There must be a default locale configured.</li>
     * </ul>
     *
     * @return the global prefix if there is one, "" if there isn't
     */
    String getLocalePrefix();

    /**
     * @return the default locale's prefix
     */
    String getDefaultLocalePrefix();

    /**
     * Returns the default locale. Will cache the result for subsequent use.
     * <p>
     * Note: There is no currently configured cache invalidation strategy for the the default locale.
     * Override this method to provide for one if you need it.
     *
     * @return the default locale
     */
    Locale getDefaultLocale();

    /**
     * In certain cases, the category id used for Solr indexing is different than the direct id on the product.
     * This method provides a hook to substitute the category id if necessary.
     *
     * @param category
     * @return the category id to use
     */
    Long getCategoryId(Category category);

    /**
     * In certain cases, the category id used for Solr indexing is different than the direct id on the product.
     * This method provides a hook to substitute the category id if necessary.
     *
     * @param category
     * @return the category id to use
     */
    Long getCategoryId(Long category);

    /**
     * In certain cases, the sku id used for Solr indexing is different than the direct id on the sku.
     * This method provides a hook to substitute the sku id if necessary.
     *
     * @param indexable
     * @return the sku id to use
     */
    Long getIndexableId(Indexable indexable);

    /**
     * @return the type of the primary indexable document
     */
    String getPrimaryDocumentType();

    /**
     * See getPropertyValue(Object, String)
     *
     * @param object
     * @param field
     * @return
     */
    Object getPropertyValue(Object object, Field field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    /**
     * <p>
     * This method is meant to behave in a similar way to Apache's PropertyUtils.getProperty(Object, String).
     * This is attempting to get the value or values for a property using the property name specified in field.getPropertyName().
     * The real difference with this method is that it iterates over Collections, Map values, and arrays until it reaches
     * end of the property name.  For example, consider a Product and the property name "defaultSku.fees.currency.currencyCode".
     *
     * <p>
     * The property "fees" is a collection of SkuFee objects on the Sku.  If an Product is passed to this method, with a field
     * defining a property name of "defaultSku.fees.currency.currencyCode", this method will return a Collection of Strings.
     * Specifically, it will return a Set of Strings.
     *
     * <p>
     * The point is, for Solr indexing, it is often desirable to specify all of the values associated with a product for a
     * given Solr field.  In this case, you are trying to get all of the unique currency codes associated with the collection
     * of fees associated with the default Sku for the given product.
     *
     * <p>
     * This works similarly for Maps, Collections, Dates, Strings, Integers, Longs, and other primitives.  Note, though, that this
     * will return complex objects as well, if you do not specify the more primitive property that you are trying to access.
     * For example, if you used "defaultSku.fees.currency" as a property name, you would get a collection of BroadleafCurrency
     * objects back.  Solr will not be happy if you try to index these.
     *
     * <p>
     * Note that, for arrays, this method only works with one dimensional arrays.
     *
     * <p>
     * For Maps, if a key is not specified, this method ignores the
     * key, and iterates over the values collection and treats the values the same way that it treats any other collection.
     * If they key is specified, then this method returns the keyed value rather than all of the values.
     *
     * <p>
     * So, for example, if you have a product and a property such as "productAttributes(heatRange).value", it will return
     * a single value if there is a ProductAttribute keyed by "heatRange", or null if there is not.  If you use the property
     * "productAttributes.value" then is will return a collection of the values associated with each of the values in the productAttributes map.
     *
     * <p>
     * In this regard it is quite different than PropertyUtils.getMappedProperty(Object, String).
     *
     * <p>
     * Keep in mind that, since this method returns either a Collection or a single object that is not a Map or Array, you
     * need to make sure that the field can handle such a value.  For example, if your field is intended to index
     * a collection of Strings, you need to make sure Solr's definition of this field (or dynamic field) is a multi-valued
     * type according to your Solr schema definition (e.g. _txt or _ss or _is, etc.).
     *
     * @param object
     * @param propertyName
     * @return
     */
    Object getPropertyValue(Object object, String propertyName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

    /**
     * Tells Solr to optimize the index.  This is an expensive operation and should be used rarely or never.
     *
     * @param collection The collection to operate on
     * @param server     The server to use to do the operation
     * @throws ServiceException
     * @throws IOException
     */
    void optimizeIndex(String collection, SolrClient server) throws ServiceException, IOException;

    /**
     * Tells Solr to optimize the index. This is an expensive operation and should be used rarely or never.
     *
     * @param server The server to use to do the operation
     * @throws ServiceException
     * @throws IOException
     * @deprecated Use {@link #optimizeIndex(String, SolrClient)} instead so that the collection that's being used can be customized otherwise the default collection will always be used. Generally use {@link SolrConfiguration#getQueryCollectionName()} or {@link SolrConfiguration#getReindexCollectionName()}
     */
    @Deprecated
    void optimizeIndex(SolrClient server) throws ServiceException, IOException;

    /**
     * @param facetValue
     * @return
     */
    String scrubFacetValue(String facetValue);

    /**
     * Strips out or replaces certain characters / substrings.
     *
     * @param query
     * @return
     */
    String sanitizeQuery(String query);

    /**
     * Builds a list of SearchFacetDTOs from a list of SearchFacets.
     *
     * @param searchFacets
     * @return
     */
    List<SearchFacetDTO> buildSearchFacetDTOs(List<SearchFacet> searchFacets);

    /**
     * Checks to see if the requiredFacets condition for a given facet is met.
     *
     * @param facet
     * @param params
     * @return
     */
    boolean isFacetAvailable(SearchFacet facet, Map<String, String[]> params);

    /**
     * Creates a range filter (e.g. field:[minValue TO maxValue])
     * <p>
     * If minValue == null or maxValue == null, they are replaced by an '*' for wildcard functionality.
     *
     * @param fieldName
     * @param minValue
     * @param maxValue
     * @return
     */
    String getSolrRangeString(String fieldName, BigDecimal minValue, BigDecimal maxValue);

    /**
     * Returns a string representing a call to the frange solr function. it is not inclusive of lower limit, inclusive of upper limit.
     *
     * @param minValue
     * @param maxValue
     * @return
     */
    String getSolrRangeFunctionString(BigDecimal minValue, BigDecimal maxValue);

    /**
     * Builds the value for a Solr param based on the given {@code fieldName} and {@code range} with the given local
     * {@code param} name.
     * <p>
     * If {@code range == null}, then this will produce a value for a {@code facet.field} param: {@code {!param=fieldName}}.
     * Else, then this will produce a value for a {@code facet.query} param:
     * {@code {!ex=fieldName param=fieldName[range#minValue:range#maxValue] frange incl=false l=range#minValue u=range#maxValue}}.
     *
     * @param fieldName Name of the index field
     * @param param     Name of the local param (e.g., key, ex, tag)
     * @param range     {@link SearchFacetRange} representing the range for which to create a {@code facet.query}.
     * @return the value for a Solr param based on the given {@code tagField} and {@code range} with the given local
     * {@code param} name.
     */
    String getSolrFieldTag(String fieldName, String param, SearchFacetRange range);

    /**
     * Builds out the DTOs for facet results from the search. This will then be used by the view layer to
     * display which values are available given the current constraints as well as the count of the values.
     *
     * @param namedFacetMap
     * @param response
     */
    void setFacetResults(Map<String, SearchFacetDTO> namedFacetMap, QueryResponse response);

    /**
     * Invoked to sort the facet results. This method will use the natural sorting of the value attribute of the
     * facet (or, if value is null, the minValue of the facet result). Override this method to customize facet
     * sorting for your given needs.
     *
     * @param namedFacetMap
     */
    void sortFacetResults(Map<String, SearchFacetDTO> namedFacetMap);

    /**
     * Notifies solr about which facets you want it to determine results and counts for.
     *
     * @param query
     * @param namedFacetMap
     * @deprecated use {@link SolrHelperService#attachFacets(SolrQuery, Map, SearchCriteria)}
     */
    @Deprecated
    void attachFacets(SolrQuery query, Map<String, SearchFacetDTO> namedFacetMap);

    /**
     * Notifies solr about which facets you want it to determine results and counts for.
     *
     * @param query
     * @param namedFacetMap
     * @param searchCriteria
     */
    void attachFacets(SolrQuery query, Map<String, SearchFacetDTO> namedFacetMap, SearchCriteria searchCriteria);

    /**
     * Returns a fully composed solr field string. Given indexField = a, tag = ex, and a non-null range,
     * would produce the following String: {!tag=a frange incl=false l=minVal u=maxVal}a
     *
     * @param indexField
     * @param tag
     * @param range
     * @return
     */
    String getSolrTaggedFieldString(String indexField, String tag, SearchFacetRange range);

    /**
     * Determines the list of SolrDocuments from the QueryResponse
     *
     * @param response
     * @return
     */
    List<SolrDocument> getResponseDocuments(QueryResponse response);

    /**
     * Sets up the sorting criteria. This will support sorting by multiple fields at a time
     *
     * @param query
     * @param searchCriteria
     * @param defaultSort
     */
    void attachSortClause(SolrQuery query, SearchCriteria searchCriteria, String defaultSort);

    /*
     * Builds a map of the fields with the abbreviation
     * @param searchCriteria
     * @param fields
     * @return
     */
    Map<String, String> getSolrFieldKeyMap(SearchCriteria searchCriteria, List<IndexField> fields);

    /**
     * Returns a map of fully qualified solr index field key to the searchFacetDTO object
     *
     * @param facets
     * @param searchCriteria
     * @return
     */
    Map<String, SearchFacetDTO> getNamedFacetMap(List<SearchFacetDTO> facets, SearchCriteria searchCriteria);

    /**
     * Restricts the query by adding active facet filters.
     *
     * @param query
     * @param namedFacetMap
     * @param searchCriteria
     */
    void attachActiveFacetFilters(SolrQuery query, Map<String, SearchFacetDTO> namedFacetMap, SearchCriteria searchCriteria);

    Long getCurrentProductId(Indexable indexable);

    Product getProductForIndexable(Indexable indexable);

    /**
     * Returns the type field name, usually 'type_s'
     *
     * @return
     */
    String getTypeFieldName();

    /**
     * Returns the type for the given Indexable. For Product's this is "product".
     *
     * @param indexable
     * @return
     */
    String getDocumentType(Indexable indexable);

    List<IndexField> getSearchableIndexFields();

    List<Long> getCategoryFilterIds(Category category, SearchCriteria searchCriteria);

}
