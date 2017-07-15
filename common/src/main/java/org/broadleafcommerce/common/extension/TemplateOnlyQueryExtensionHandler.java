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
 * @author Jeff Fischer
 */
public interface TemplateOnlyQueryExtensionHandler extends ExtensionHandler {

    /**
     * Finish the query - possibly setting parameters
     *
     * @param type the class type for the query
     * @param testObject supporting implementations may use this object to test for possible catalog query optimizations. This value can be null, in which case it is ignored.
     * @param query the final Query instance to embellish
     * @return
     */
    ExtensionResultStatusType refineQuery(Class<?> type, Object testObject, TypedQuery query);

    /**
     * Add additional restrictions to the fetch query. Use in conjunction with {@link #refineQuery(Class, Object, TypedQuery)} to set
     * actual parameter values before retrieving results.
     *
     * @param type the class type for the query
     * @param testObject supporting implementations may use this object to test for possible catalog query optimizations. This value can be null, in which case it is ignored.
     * @param builder
     * @param criteria
     * @param root
     * @param restrictions any additional JPA criteria restrictions should be added here
     * @return the status of the extension operation
     */
    ExtensionResultStatusType refineParameterRetrieve(Class<?> type, Object testObject, CriteriaBuilder builder,
                                             CriteriaQuery criteria, Root root, List<Predicate> restrictions);

    /**
     * Perform any setup operations. This is usually done before executing the query and can serve to prepare the BroadleafRequestContext (if applicable).
     *
     * @param type the class type for the query
     * @return the status of the extension operation
     */
    ExtensionResultStatusType setup(Class<?> type);

    /**
     * Perform any breakdown operations. This is usually done after executing the query and can serve to reset the BroadleafRequestContext (if applicable)
     *
     * @param type the class type for the query
     * @return the status of the extension operation
     */
    ExtensionResultStatusType breakdown(Class<?> type);

    /**
     * Add sorting to the fetch query
     *
     * @param type the class type for the query
     * @param builder
     * @param criteria
     * @param root
     * @param sorts any additional JPA order expressions should be added here
     * @return the status of the extension operation
     */
    ExtensionResultStatusType refineOrder(Class<?> type, CriteriaBuilder builder, CriteriaQuery criteria, Root root, List<Order> sorts);

    /**
     * Determine if the current thread is in a valid state for sparse cache handling
     *
     * @param response
     * @return
     */
    ExtensionResultStatusType isValidState(ExtensionResultHolder<Boolean> response);

    ExtensionResultStatusType buildStatus(Object entity, ExtensionResultHolder<ItemStatus> response);

}
