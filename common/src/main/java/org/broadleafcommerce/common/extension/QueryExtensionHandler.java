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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Extension handler (generally for DAO usage) that allows contribution to a query (presumably from another module).
 *
 * @author Jeff Fischer
 */
public interface QueryExtensionHandler extends ExtensionHandler {

    /**
     * Perform any setup operations. This is usually done before executing the query and can serve to prepare the BroadleafRequestContext (if applicable).
     *
     * @param type the class type for the query (can be null)
     * @param config pass information to the handler, perhaps to be used by the handler to determine suitability (can be null)
     * @return the status of the extension operation
     */
    public ExtensionResultStatusType setup(Class<?> type, String[] config);

    /**
     * Add additional restrictions to the fetch query
     *
     * @param type the class type for the query (can be null)
     * @param config pass information to the handler, perhaps to be used by the handler to determine suitability (can be null)
     * @param builder
     * @param criteria
     * @param root
     * @param restrictions any additional JPA criteria restrictions should be added here
     * @return the status of the extension operation
     */
    public ExtensionResultStatusType refineRetrieve(Class<?> type, String[] config, CriteriaBuilder builder, CriteriaQuery criteria, Root root, List<Predicate> restrictions);

    /**
     * Add sorting to the fetch query
     *
     * @param type the class type for the query (can be null)
     * @param config pass information to the handler, perhaps to be used by the handler to determine suitability (can be null)
     * @param builder
     * @param criteria
     * @param root
     * @param sorts any additional JPA order expressions should be added here
     * @return the status of the extension operation
     */
    public ExtensionResultStatusType refineOrder(Class<?> type, String[] config, CriteriaBuilder builder, CriteriaQuery criteria, Root root, List<Order> sorts);

    /**
     * Filter the results from the database in Java
     *
     * @param type the class type for the query (can be null)
     * @param config pass information to the handler, perhaps to be used by the handler to determine suitability (can be null)
     * @param queryResults the results of the fetch query from the database
     * @param response the container for the filtered results
     * @return the status of the extension operation
     */
    public ExtensionResultStatusType refineResults(Class<?> type, String[] config, List queryResults, ExtensionResultHolder<List> response);

    /**
     * Perform any breakdown operations. This is usually done after executing the query and can serve to reset the BroadleafRequestContext (if applicable)
     *
     * @param type the class type for the query (can be null)
     * @param config pass information to the handler, perhaps to be used by the handler to determine suitability (can be null)
     * @return the status of the extension operation
     */
    public ExtensionResultStatusType breakdown(Class<?> type, String[] config);

}
