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

import org.apache.solr.common.SolrInputDocument;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.sandbox.domain.SandBoxType;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.solr.FieldType;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

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
    public void swapActiveCores() throws ServiceException;

    /**
     * Determines the current namespace we are operating on. For example, if you have multiple sites set up, 
     * you may want to filter that here. 
     * 
     * <ul>
     *     <li>Note: This method should ALWAYS return a non-empty string.</li>
     * </ul>
     * 
     * @return the global namespace 
     */
    public String getCurrentNamespace();

    /**
     * This property is needed to be non-null to allow filtering by multiple facets at one time and have the results
     * be an AND of the facets. Apart from being non-empty, the actual value does not matter.
     * 
     * @return the non-empty global facet tag field
     */
    public String getGlobalFacetTagField();

    /**
     * Returns the property name for the given field, field type, and prefix
     * 
     * @param field
     * @param searchableFieldType
     * @param prefix
     * @return the property name for the field and fieldtype
     */
    public String getPropertyNameForFieldSearchable(Field field, FieldType searchableFieldType, String prefix);

    /**
     * Returns the property name for the given field, its configured facet field type, and the given prefix
     * 
     * @param field
     * @param prefix
     * @return the property name for the facet type of this field
     */
    public String getPropertyNameForFieldFacet(Field field, String prefix);
    
    /**
     * Returns the searchable field types for the given field. If there were none configured, will return
     * a list with TEXT FieldType.
     * 
     * @param field
     * @return the searchable field types for the given field
     */
    public List<FieldType> getSearchableFieldTypes(Field field);

    /**
     * Returns the property name for the given field and field type. This will apply the global prefix to the field,
     * and it will also apply either the locale prefix or the pricelist prefix, depending on whether or not the field
     * type was set to FieldType.PRICE
     * 
     * @param field
     * @param searchableFieldType
     * @return the property name for the field and fieldtype
     */
    public String getPropertyNameForFieldSearchable(Field field, FieldType searchableFieldType);

    /**
     * Returns the property name for the given field and its configured facet field type. This will apply the global prefix 
     * to the field, and it will also apply either the locale prefix or the pricelist prefix, depending on whether or not 
     * the field type was set to FieldType.PRICE
     * 
     * @param field
     * @return the property name for the facet type of this field
     */
    public String getPropertyNameForFieldFacet(Field field);
    
    /**
     * @param product
     * @return the Solr id of this product
     */
    public String getSolrDocumentId(SolrInputDocument document, Product product);

    /**
     * @param sku
     * @return the Solr id of this sku
     */
    public String getSolrDocumentId(SolrInputDocument document, Sku sku);

    /**
     * @return the name of the field that keeps track what namespace this document belongs to
     */
    public String getNamespaceFieldName();

    /**
     * @return the id field name, with the global prefix as appropriate
     */
    public String getIdFieldName();
    
    /**
     * @return the productId field name
     */
    public String getProductIdFieldName();

    /**
     * @return the skuId field name
     */
    public String getSkuIdFieldName();

    /**
     * @return the category field name, with the global prefix as appropriate
     */
    public String getCategoryFieldName();

    /**
     * @return the explicit category field name, with the global prefix as appropriate
     */
    public String getExplicitCategoryFieldName();
    
    /**
     * The field that should store which catalog the item is being indexed for
     */
    public String getCatalogFieldName();

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
    public String getCatalogOverridesFieldName();
    
    /**
     * The field that stores which sandbox the document is active for. Whenever a change is made to a new Solr document is
     * created with all of the changed field values and this field set to the sandbox the change is in so that searches
     * work when previewing but are filtered out in production.
     */
    public String getSandBoxFieldName();

    /**
     * Which level of the priority tree the sandbox field is in like approval or user
     * @see {@link SandBoxType}
     */
    public String getSandBoxPriorityFieldName();

    /**
     * Used for DELETE documents that are extra documents created in a sandbox when an item is deleted. These are eventually
     * cleaned up when deployed to production
     */
    public String getSandBoxChangeTypeFieldName();

    /**
     * @param category
     * @return the default sort field name for this category
     */
    public String getCategorySortFieldName(Category category);

    /**
     * @param categoryId
     * @return the default sort field name for this category
     */
    public String getCategorySortFieldName(Long categoryId);

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
    public String getLocalePrefix();

    /**
     * @return the default locale's prefix
     */
    public String getDefaultLocalePrefix();

    /**
     * Returns the default locale. Will cache the result for subsequent use.
     * 
     * Note: There is no currently configured cache invalidation strategy for the the default locale. 
     * Override this method to provide for one if you need it.
     * 
     * @return the default locale
     */
    public Locale getDefaultLocale();

    /**
     * In certain cases, the category id used for Solr indexing is different than the direct id on the product.
     * This method provides a hook to substitute the category id if necessary.
     * 
     * @param tentativeCategoryId
     * @return the category id to use
     */
    public Long getCategoryId(Long tentativeCategoryId);

    /**
     * In certain cases, the product id used for Solr indexing is different than the direct id on the product.
     * This method provides a hook to substitute the product id if necessary.
     * 
     * @param tentativeProductId
     * @return the product id to use
     */
    public Long getProductId(Long tentativeProductId);

    /**
     * In certain cases, the sku id used for Solr indexing is different than the direct id on the sku.
     * This method provides a hook to substitute the sku id if necessary.
     * 
     * @param tentativeSkuId
     * @return the sku id to use
     */
    public Long getSkuId(Long tentativeSkuId);

    /**
     * See getPropertyValue(Object, String)
     * @param object
     * @param field
     * @return
     */
    public Object getPropertyValue(Object object, Field field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

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
    public Object getPropertyValue(Object object, String propertyName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

}
