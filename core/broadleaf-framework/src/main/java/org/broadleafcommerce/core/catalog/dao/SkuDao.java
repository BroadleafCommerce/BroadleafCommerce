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
package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.catalog.domain.SkuFee;

import java.util.List;

/**
 * {@code SkuDao} provides persistence access to {@code Sku} instances
 *
 * @author Jeff Fischer
 */
public interface SkuDao {

    /**
     * Retrieve a {@code Sku} instance by its primary key
     *
     * @param skuId the primary key of the sku
     * @return the sku at the primary key
     */
    Sku readSkuById(Long skuId);

    /**
     * Queries for a {@code Sku} instance by its Universal Product Code (UPC).
     * @param upc
     * @return
     */
    Sku readSkuByUpc(String upc);

    /**
     * Retrieve a {@link Sku} instance by its external id
     *
     * @param externalId the external id of the sku
     * @return the sku with this external id
     */
    Sku readSkuByExternalId(String externalId);

    /**
     * Persist a {@code Sku} instance to the datastore
     *
     * @param sku the sku to persist
     * @return the saved state of the passed in sku
     */
    Sku save(Sku sku);
    
    SkuFee saveSkuFee(SkuFee fee);

    /**
     * Retrieve the {@code Sku} instance whose primary key is the smallest
     * of all skus in the datastore
     *
     * @return the sku with the smallest primary key
     */
    Sku readFirstSku();

    /**
     * Retrieve all {@code Sku} instances from the datastore
     *
     * @return the list of all skus
     */
    List<Sku> readAllSkus();

    /**
     * Retrieve all {@code Sku} instances from the datastore
     *
     * @param offset the starting offset of the query
     * @param limit the maximum number of Skus to gather
     * @return the list of all skus in the given range
     */
    List<Sku> readAllSkus(int offset, int limit);

    /**
     * Find all the {@code Sku} instances whose primary key matches
     * one of the values from the passed in list
     *
     * @param ids the list of primary key values
     * @return the list of skus that match the list of primary key values
     */
    List<Sku> readSkusByIds(List<Long> ids);

    /**
     * Remove the {@code Sku} instance from the datastore
     *
     * @param sku the sku to remove
     */
    void delete(Sku sku);    

    /**
     * Create a new {@code Sku} instance. The system will use the configuration in
     * {@code /BroadleafCommerce/core/BroadleafCommerceFramework/src/main/resources/bl-framework-applicationContext-entity.xml}
     * to determine which polymorphic version of {@code Sku} to instantiate. To make Broadleaf instantiate your
     * extension of {@code Sku} by default, include an entity configuration bean in your application context xml similar to:
     * <p>
     * {@code
     *     <bean id="blEntityConfiguration" class="org.broadleafcommerce.common.persistence.EntityConfiguration">
     *          <property name="entityContexts">
     *              <list>
     *                  <value>classpath:myCompany-applicationContext-entity.xml</value>
     *              </list>
     *          </property>
     *      </bean>
     * }
     * </p>
     * Declare the same key for your desired entity in your entity xml that is used in the Broadleaf entity xml, but change the value to the fully
     * qualified classname of your entity extension.
     *
     * @return a {@code Sku} instance based on the Broadleaf entity configuration.
     */
    Sku create();
    
    /**
     * Returns the number of Skus that are currently active.
     * 
     * @return the number of currently active Skus
     */
    Long readCountAllActiveSkus();

    /**
     * Reads all Skus from the database that are currently active. This method utilizes database paging.
     * 
     * It will fetch results in pages. For example, if page = 3 and pageSize = 25, this method would
     * return rows 75-99 from the database.
     * 
     * @param page - the number of the page to get (0 indexed)
     * @param pageSize - the number of results per page
     * @return a list of active Skus for the given page
     */
    List<Sku> readAllActiveSkus(int page, int pageSize);

    /**
     * Reads all skus from the database that are currently active. This method utilizes efficient
     * paging to retrieve a subset of records. This approach does not use an offset technique (like {@link #readAllActiveSkus(int, int)},
     * but rather limits the retrieved records to those greater than the given id and returns a max results of pageSize. This
     * is more efficient that using an offset, since the database will not have to retrieve all the records from the beginning
     * of the table and trim the offset.
     *
     * @param pageSize the number of results per page
     * @param lastId the last id from the previous page - can be null if this is the first page request
     * @return a list of active skus for the given page
     */
    List<Sku> readAllActiveSkus(Integer pageSize, Long lastId);

    /**
     * Returns the number of milliseconds that the current date/time will be cached for queries before refreshing.
     * This aids in query caching, otherwise every query that utilized current date would be different and caching
     * would be ineffective.
     *
     * @return the milliseconds to cache the current date/time
     */
    Long getCurrentDateResolution();

    /**
     * Sets the number of milliseconds that the current date/time will be cached for queries before refreshing.
     * This aids in query caching, otherwise every query that utilized current date would be different and caching
     * would be ineffective.
     *
     * @param currentDateResolution the milliseconds to cache the current date/time
     */
    void setCurrentDateResolution(Long currentDateResolution);

    /**
     * Look up a sku that matches the given URI
     * 
     * @param uri - the relative URL to look up the sku by
     * @return List of skus that match the passed in URI.
     * 
     */
    List<Sku> findSkuByURI(String uri);
    
}
