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
package org.broadleafcommerce.common.extension;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Extension handler (generally for DAO usage) that allows contribution to a query (presumably from another module). The primary
 * use case is to handle querying for sparsely populated caches in multitenant scenarios. Take for example, a price list scenario
 * where there may be thousands of standard sites all inheriting from a template catalog containing some basic pricelist information.
 * Rather than having a cache of these same basic price lists for every site, it is advantageous to have a single cache of the common
 * pricelists. Then, if one or more standard sites override one of these pricelists, then a sparse cache is maintained per site
 * with that site's overrides only.
 * </p>
 * This functionality is achieved by running several versions of a query based on the desired
 * {@link org.broadleafcommerce.common.extension.ResultType}. Requesting results for a ResultType of STANDARD drives results filtered
 * specifically to a standard site, which is multitenant module concept. A ResultType of TEMPLATE drives results specifically
 * for template site catalog or profile (also a multitenant concept). In the absence of the multitenant module, ExtensionManager
 * instance of this type should have no effect.
 *
 * @see org.broadleafcommerce.common.extension.ResultType
 * @author Jeff Fischer
 */
public interface SparselyPopulatedQueryExtensionHandler extends ExtensionHandler {

    /**
     * Add additional restrictions to the fetch query
     *
     * @param type the class type for the query
     * @param resultType pass a ResultType of IGNORE to explicitly ignore refineRetrieve, even if the multitenant module is loaded
     * @param builder
     * @param criteria
     * @param root
     * @param restrictions any additional JPA criteria restrictions should be added here
     * @return the status of the extension operation
     */
    ExtensionResultStatusType refineRetrieve(Class<?> type, ResultType resultType, CriteriaBuilder builder, CriteriaQuery criteria, Root root, List<Predicate> restrictions);

    /**
     * Add additional restrictions to the fetch query. Uses parameters, rather than embedding values directly in the query. This is more
     * efficient from a Hibernate statement cache and database prepared statement cache perspective. Use in conjunction with
     * {@link #refineQuery(Class, ResultType, TypedQuery)} to pass the actual parameter values before retrieving the query
     * results.
     *
     * @param type the class type for the query
     * @param resultType pass a ResultType of IGNORE to explicitly ignore refineRetrieve, even if the multitenant module is loaded
     * @param builder
     * @param criteria
     * @param root
     * @param restrictions any additional JPA criteria restrictions should be added here
     * @return the status of the extension operation
     */
    ExtensionResultStatusType refineParameterRetrieve(Class<?> type, ResultType resultType, CriteriaBuilder builder, CriteriaQuery criteria, Root root, List<Predicate> restrictions);

    /**
     * Finish the query - possibly setting parameters
     *
     * @param type the class type for the query
     * @param resultType pass a ResultType of IGNORE to explicitly ignore refineQuery, even if the multitenant module is loaded
     * @param query the final Query instance to embellish
     * @return
     */
    ExtensionResultStatusType refineQuery(Class<?> type, ResultType resultType, TypedQuery query);

    /**
     * Perform any setup operations. This is usually done before executing the query and can serve to prepare the BroadleafRequestContext (if applicable).
     *
     * @param type the class type for the query
     * @param resultType pass a ResultType of IGNORE to explicitly ignore setup, even if the multitenant module is loaded
     * @return the status of the extension operation
     */
    ExtensionResultStatusType setup(Class<?> type, ResultType resultType);

    /**
     * Perform any breakdown operations. This is usually done after executing the query and can serve to reset the BroadleafRequestContext (if applicable)
     *
     * @param type the class type for the query
     * @param resultType pass a ResultType of IGNORE to explicitly ignore breakdown, even if the multitenant module is loaded
     * @return the status of the extension operation
     */
    ExtensionResultStatusType breakdown(Class<?> type, ResultType resultType);

    /**
     * Add sorting to the fetch query
     *
     * @param type the class type for the query
     * @param resultType
     * @param builder
     * @param criteria
     * @param root
     * @param sorts any additional JPA order expressions should be added here
     * @return the status of the extension operation
     */
    ExtensionResultStatusType refineOrder(Class<?> type, ResultType resultType, CriteriaBuilder builder, CriteriaQuery criteria, Root root, List<Order> sorts);

    /**
     * Filter the results from the database in Java
     *
     * @param type the class type for the query
     * @param resultType
     * @param queryResults the results of the fetch query from the database
     * @param response the container for the filtered results
     * @return the status of the extension operation
     */
    ExtensionResultStatusType refineResults(Class<?> type, ResultType resultType, List queryResults, ExtensionResultHolder<List> response);

    /**
     * By examining the multitenant information related to a test object, return whether the object is related to a standard
     * site, or a template profile or catalog, if applicable.
     *
     * @param testObject the multitenant object to test
     * @param response the response container
     * @return the status of the extension operation
     */
    ExtensionResultStatusType getResultType(Object testObject, ExtensionResultHolder<ResultType> response);

    /**
     * Build the cache key to be used for either the STANDARD or TEMPLATE style cache, driven by the resultType.
     *
     * @param testObject object to examine for a portion of the cache key
     * @param qualifier the suffix for the cache key
     * @param resultType the type of cache key to create (STANDARD or TEMPLATE)
     * @param response the response container
     * @return the status of the extension operation
     */
    ExtensionResultStatusType getCacheKey(Object testObject, String qualifier, ResultType resultType, ExtensionResultHolder<String> response);

    /**
     * Build the cache key to be used for either the STANDARD or TEMPLATE style cache, driven by the resultType.
     *
     * @param qualifier the suffix for the cache key
     * @param resultType the type of cache key to create (STANDARD or TEMPLATE)
     * @param response the response container
     * @return the status of the extension operation
     */
    ExtensionResultStatusType getCacheKey(String qualifier, ResultType resultType, ExtensionResultHolder<String> response);

    /**
     * Build a list of cache keys that are related to a TEMPLATE template site
     *
     * @param qualifier the suffix for the cache key
     * @param response the response container
     * @return the status of the extension operation
     */
    ExtensionResultStatusType getCacheKeyListForTemplateSite(String qualifier, ExtensionResultHolder<List<String>> response);

    /**
     * Convert the list of query results into a list that denotes not only the query results, but also whether or not each member
     * represents a deleted/archived item, or an active/normal item.
     *
     * @param type the class type for the query
     * @param queryResults the results of the fetch query from the database
     * @param response the response container - the list is sorted with deleted item appearing first
     * @return the status of the extension operation
     */
    ExtensionResultStatusType buildStatus(Class<?> type, List queryResults, ExtensionResultHolder<List<StandardCacheItem>> response);

    /**
     * Determine if the current thread is in a valid state for sparse cache handling
     *
     * @param response
     * @return
     */
    ExtensionResultStatusType isValidState(ExtensionResultHolder<Boolean> response);

    /**
     * Get a common id for an object that is consistent for a standard site (whether or not the test object is overridden in the standard site)
     *
     * @param testObject the object whose id is normalized
     * @param response the container for the normalized id
     * @return the status of the extension operation
     */
    ExtensionResultStatusType getNormalizedId(Object testObject, ExtensionResultHolder<Long> response);

}
