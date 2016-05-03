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
