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
    public Sku readSkuById(Long skuId);

    /**
     * Persist a {@code Sku} instance to the datastore
     *
     * @param sku the sku to persist
     * @return the saved state of the passed in sku
     */
    public Sku save(Sku sku);
    
    public SkuFee saveSkuFee(SkuFee fee);

    /**
     * Retrieve the {@code Sku} instance whose primary key is the smallest
     * of all skus in the datastore
     *
     * @return the sku with the smallest primary key
     */
    public Sku readFirstSku();

    /**
     * Retrieve all {@code Sku} instances from the datastore
     *
     * @return the list of all skus
     */
    public List<Sku> readAllSkus();

    /**
     * Find all the {@code Sku} instances whose primary key matches
     * one of the values from the passed in list
     *
     * @param ids the list of primary key values
     * @return the list of skus that match the list of primary key values
     */
    public List<Sku> readSkusByIds(List<Long> ids);

    /**
     * Remove the {@code Sku} instance from the datastore
     *
     * @param sku the sku to remove
     */
    public void delete(Sku sku);    

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
    public Sku create();
    
    /**
     * Returns the number of Skus that are currently active.
     * 
     * @return the number of currently active Skus
     */
    public Long readCountAllActiveSkus();

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
    public List<Sku> readAllActiveSkus(int page, int pageSize);

    /**
     * Returns the number of milliseconds that the current date/time will be cached for queries before refreshing.
     * This aids in query caching, otherwise every query that utilized current date would be different and caching
     * would be ineffective.
     *
     * @return the milliseconds to cache the current date/time
     */
    public Long getCurrentDateResolution();

    /**
     * Sets the number of milliseconds that the current date/time will be cached for queries before refreshing.
     * This aids in query caching, otherwise every query that utilized current date would be different and caching
     * would be ineffective.
     *
     * @param currentDateResolution the milliseconds to cache the current date/time
     */
    public void setCurrentDateResolution(Long currentDateResolution);

}
