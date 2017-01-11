/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.core.util.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.util.dao.ResourcePurgeDao;
import org.broadleafcommerce.core.util.service.type.PurgeCartVariableNames;
import org.broadleafcommerce.core.util.service.type.PurgeCustomerVariableNames;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

/**
 * Service capable of deleting old or defunct entities from the persistence layer (e.g. Carts and anonymous Customers).
 * {@link ResourcePurgeService} for additional API documentation.
 * <p/>
 * A basic Quartz scheduled job configuration for calling this service can be configured as follows:
 * <p/>
 * {@code
 * <bean id="purgeCartConfig" class="org.springframework.beans.factory.config.MapFactoryBean">
 * <property name="sourceMap">
 * <map>
 * <entry key="SECONDS_OLD" value="2592000"/>
 * <entry key="STATUS" value="IN_PROCESS"/>
 * </map>
 * </property>
 * </bean>
 * <p/>
 * <bean id="purgeCartJobDetail" class="org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean">
 * <property name="targetObject" ref="blResourcePurgeService" />
 * <property name="targetMethod" value="purgeCarts" />
 * <property name="arguments">
 * <list>
 * <ref bean="purgeCartConfig"/>
 * </list>
 * </property>
 * </bean>
 * <p/>
 * <bean id="purgeCartTrigger" class="org.springframework.scheduling.quartz.SimpleTriggerFactoryBean">
 * <property name="jobDetail" ref="purgeCartJobDetail" />
 * <property name="startDelay" value="30000" />
 * <property name="repeatInterval" value="86400000" />
 * </bean>
 *}
 * @author Jeff Fischer
 */
@Service("blResourcePurgeService")
public class ResourcePurgeServiceImpl implements ResourcePurgeService {

    private static final Log LOG = LogFactory.getLog(ResourcePurgeServiceImpl.class);

    private static final Long BATCH_SIZE = 50L;
    private static final Long PURGE_ERROR_CACHE_RETRY_SECONDS = System.currentTimeMillis() - 172800; //48 HOURS

    protected PurgeErrorCache customerPurgeErrors = new PurgeErrorCache();
    protected PurgeErrorCache cartPurgeErrors = new PurgeErrorCache();

    @Resource(name = "blTransactionManager")
    protected PlatformTransactionManager transactionManager;
    
    @Resource(name = "blResourcePurgeDao")
    protected ResourcePurgeDao resourcePurgeDao;

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Resource(name = "blCustomerService")
    protected CustomerService customerService;

    @Override
    public void purgeCarts(final Map<String, String> config) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Purging carts");
        }
        if (MapUtils.isEmpty(config)) {
            throw new IllegalArgumentException("Cannot purge carts since there was no configuration provided. " +
                    "In the absence of config params, all carts would be candidates for deletion.");
        }
        CartPurgeParams purgeParams = new CartPurgeParams(config).invoke();
        int processedCount = 0, batchCount = 0;
        synchronized(cartPurgeErrors) {
            Set<Long> failedCartIds = getCartsInErrorToIgnore(purgeParams);
            batchCount = getCartsToPurgeLength(purgeParams, new ArrayList<Long>(failedCartIds)).intValue();
            List<Order> carts = getCartsToPurge(purgeParams, 0, batchCount, new ArrayList<Long>(failedCartIds));
            for (Order cart : carts) {
                TransactionStatus status = TransactionUtils.createTransaction("Cart Purge",
                        TransactionDefinition.PROPAGATION_REQUIRED, transactionManager, false);
                try {
                    deleteCart(cart);
                    TransactionUtils.finalizeTransaction(status, transactionManager, false);
                    processedCount++;
                } catch (Exception e) {
                    if (! status.isCompleted()) {
                        TransactionUtils.finalizeTransaction(status, transactionManager, true);
                    }
                    LOG.error(String.format("Not able to purge Cart ID: %d", cart.getId()), e);
                    cartPurgeErrors.add(cart.getId());
                }
            }
        }
        LOG.info(String.format("Cart purge batch processed.  Purged %d from total batch size of %d, %d failures cached", processedCount, batchCount, cartPurgeErrors.size()));
    }

    @Override
    public void purgeCustomers(final Map<String, String> config) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Purging customers");
        }
        if (MapUtils.isEmpty(config)) {
            throw new IllegalArgumentException("Cannot purge customers since there was no configuration provided. " +
                    "In the absence of config params, all customers would be candidates for deletion.");
        }
        CustomerPurgeParams purgeParams = new CustomerPurgeParams(config).invoke();
        int processedCount = 0, batchCount = 0;
        synchronized(customerPurgeErrors) {
            Set<Long> failedCustomerIds = getCustomersInErrorToIgnore(purgeParams);
            batchCount = getCustomersToPurgeLength(purgeParams, new ArrayList<Long>(failedCustomerIds)).intValue();
            List<Customer> customers = getCustomersToPurge(purgeParams, 0, batchCount, new ArrayList<Long>(failedCustomerIds));
            for (Customer customer : customers) {
                TransactionStatus status = TransactionUtils.createTransaction("Customer Purge",
                        TransactionDefinition.PROPAGATION_REQUIRED, transactionManager, false);
                try {
                    deleteCustomer(customer);
                    TransactionUtils.finalizeTransaction(status, transactionManager, false);
                    processedCount++;
                } catch (Exception e) {
                    if (! status.isCompleted()) {
                        TransactionUtils.finalizeTransaction(status, transactionManager, true);
                    }
                    LOG.error(String.format("Not able to purge Customer ID: %d", customer.getId()), e);
                    customerPurgeErrors.add(customer.getId());
                }
            }
        }
        LOG.info(String.format("Customer purge batch processed.  Purged %d from total batch size of %d, %d failures cached", processedCount, batchCount, customerPurgeErrors.size()));
    }

    /**
     * Get the Carts Ids from cache that should be ignored due to errors in previous purge attempts.  Expired cached errors removed.
     * 
     * @param purgeParams configured parameters for the cart purge process
     * @return set of cart ids to ignore/exclude from the next purge run
     */
    protected Set<Long> getCartsInErrorToIgnore(CartPurgeParams purgeParams) {
        long ignoreFailedExpiration = purgeParams.getFailedRetryTime().longValue();
        Set<Long> ignoreFailedCartIds = cartPurgeErrors.getEntriesSince(ignoreFailedExpiration);
        return ignoreFailedCartIds;
    }
    
    /**
     * Get the list of carts to delete from the database. Subclasses may override for custom cart retrieval logic.
     *
     * @param purgeParams configured parameters for the Cart purge process
     * @param cartsInError list of cart ids to be ignored/excluded from the query
     * @return list of carts to delete
     */
    protected List<Order> getCartsToPurge(CartPurgeParams purgeParams, int startPos, int length, List<Long> cartsInError) {
        String[] nameArray = purgeParams.getNameArray();
        OrderStatus[] statusArray = purgeParams.getStatusArray();
        Date dateCreatedMinThreshold = purgeParams.getDateCreatedMinThreshold();
        Boolean isPreview = purgeParams.getIsPreview();
        return resourcePurgeDao.findCarts(nameArray, statusArray, dateCreatedMinThreshold, isPreview, startPos, length, cartsInError);
    }

    /**
     * Get the count of carts to delete from the database. Subclasses may override for custom cart retrieval logic.
     *
     * @param purgeParams configured parameters for the Customer purge process used in the query
     * @param cartsInError list of cart ids to ignore/exclude from the next purge run
     * @return count of carts to delete
     */
    /**
     * 
     */
    protected Long getCartsToPurgeLength(CartPurgeParams purgeParams, List<Long> cartsInError) {
        String[] nameArray = purgeParams.getNameArray();
        OrderStatus[] statusArray = purgeParams.getStatusArray();
        Date dateCreatedMinThreshold = purgeParams.getDateCreatedMinThreshold();
        Boolean isPreview = purgeParams.getIsPreview();
        Long cartBatchSize = purgeParams.getBatchSize(); 
        Long orderCount = resourcePurgeDao.findCartsCount(nameArray, statusArray, dateCreatedMinThreshold, isPreview, cartsInError);
        //return the lesser of the parameter batch size of the count of the orders to purge
        return cartBatchSize != null && cartBatchSize < orderCount ? cartBatchSize : orderCount; 
    }

    /**
     * Remove the cart from the persistence layer. Subclasses may override for custom cart retrieval logic.
     *
     * @param cart the cart to remove
     */
    protected void deleteCart(Order cart) {
        //We delete the order this way (rather than with a delete query) in order to ensure the cascades take place
        orderService.deleteOrder(cart);
    }

    /**
     * Get the Customer Ids from cache that should be ignored due to errors in previous purge attempts
     * 
     * @param purgeParams configured parameters for the Customer purge process
     * @return set of customer ids to ignore/exclude from the next purge run
     */
    protected Set<Long> getCustomersInErrorToIgnore(CustomerPurgeParams purgeParams) {
        long ignoreFailedExpiration = purgeParams.getFailedRetryTime().longValue();
        Set<Long> ignoreFailedCustomerIds = customerPurgeErrors.getEntriesSince(ignoreFailedExpiration);
        return ignoreFailedCustomerIds;
    }
    
    /**
     * Get the list of carts to delete from the database. Subclasses may override for custom cart retrieval logic.
     *
     * @param purgeParams configured parameters for the Customer purge process
     * @param customersInError list of customer ids to be ignored/excluded from the query
     * @return list of customers to delete
     */
    protected List<Customer> getCustomersToPurge(CustomerPurgeParams purgeParams, int startPos, int length, List<Long> customersInError) {
        Boolean isRegistered = purgeParams.getIsRegistered();
        Boolean isDeactivated = purgeParams.getIsDeactivated();
        Date dateCreatedMinThreshold = purgeParams.getDateCreatedMinThreshold();
        Boolean isPreview = purgeParams.getIsPreview();
        return resourcePurgeDao.findCustomers(dateCreatedMinThreshold, isRegistered, isDeactivated, isPreview, startPos, length, customersInError);
    }

    /**
     * Get the count of customers to delete from the database. Subclasses may override for custom customer retrieval logic.
     *
     * @param purgeParams configured parameters for the Customer purge process
     * @param customersInError list of customer ids to be ignored/excluded from the query
     * @return
     */
    protected Long getCustomersToPurgeLength(CustomerPurgeParams purgeParams, List<Long> customersInError) {
        Boolean isRegistered = purgeParams.getIsRegistered();
        Boolean isDeactivated = purgeParams.getIsDeactivated();
        Date dateCreatedMinThreshold = purgeParams.getDateCreatedMinThreshold();
        Boolean isPreview = purgeParams.getIsPreview();
        Long customerBatchSize = purgeParams.getBatchSize(); 
        Long customersCount = resourcePurgeDao.findCustomersCount(dateCreatedMinThreshold, isRegistered, isDeactivated, isPreview, customersInError);
        //return the lesser of the parameter batch size of the count of the customers to purge
        return customerBatchSize != null && customerBatchSize < customersCount ? customerBatchSize : customersCount;
    }

    /**
     * Remove the cart from the persistence layer. Subclasses may override for custom cart retrieval logic.
     *
     * @param customer the customer to remove
     */
    protected void deleteCustomer(Customer customer) {
        //We delete the customer this way (rather than with a delete query) in order to ensure the cascades take place
        customerService.deleteCustomer(customer);
    }

    private class CartPurgeParams {

        private Map<String, String> config;
        private String[] nameArray;
        private OrderStatus[] statusArray;
        private Date dateCreatedMinThreshold;
        private Boolean isPreview;
        private Long batchSize;
        private Long failedRetryTime;

        public CartPurgeParams(Map<String, String> config) {
            this.config = config;
        }

        public String[] getNameArray() {
            return nameArray;
        }

        public OrderStatus[] getStatusArray() {
            return statusArray;
        }

        public Date getDateCreatedMinThreshold() {
            return dateCreatedMinThreshold;
        }

        public Boolean getIsPreview() {
            return isPreview;
        }

        public Long getBatchSize() {
            return batchSize;
        }

        public Long getFailedRetryTime() {
            return failedRetryTime;
        }

        public CartPurgeParams invoke() {
            nameArray = null;
            statusArray = null;
            dateCreatedMinThreshold = null;
            isPreview = null;
            batchSize = ResourcePurgeServiceImpl.BATCH_SIZE;
            failedRetryTime = ResourcePurgeServiceImpl.PURGE_ERROR_CACHE_RETRY_SECONDS;
            
            for (Map.Entry<String, String> entry : config.entrySet()) {
                if (PurgeCartVariableNames.STATUS.toString().equals(entry.getKey())) {
                    String[] temp = entry.getValue().split(",");
                    statusArray = new OrderStatus[temp.length];
                    int index = 0;
                    for (String name : temp) {
                        OrderStatus orderStatus = OrderStatus.getInstance(name);
                        statusArray[index] = orderStatus;
                        index++;
                    }
                }
                if (PurgeCartVariableNames.NAME.toString().equals(entry.getKey())) {
                    nameArray = entry.getValue().split(",");
                }
                if (PurgeCartVariableNames.SECONDS_OLD.toString().equals(entry.getKey())) {
                    Long secondsOld = Long.parseLong(entry.getValue());
                    dateCreatedMinThreshold = new Date(SystemTime.asMillis() - (secondsOld * 1000));
                }
                if (PurgeCartVariableNames.IS_PREVIEW.toString().equals(entry.getKey())) {
                    isPreview = Boolean.parseBoolean(entry.getValue());
                }
                if (PurgeCartVariableNames.BATCH_SIZE.toString().equals(entry.getKey())) {
                    batchSize = Long.parseLong(entry.getValue());
                }
                if (PurgeCartVariableNames.RETRY_FAILED_SECONDS.toString().equals(entry.getKey())) {
                    failedRetryTime = System.currentTimeMillis() - (Long.parseLong(entry.getValue()) * 1000);
                }
            }
            return this;
        }
    }

    private class CustomerPurgeParams {

        private Map<String, String> config;
        private Date dateCreatedMinThreshold;
        private Boolean isPreview;
        private Boolean isRegistered;
        private Boolean isDeactivated;
        private Long batchSize;
        private Long failedRetryTime;

        public CustomerPurgeParams(Map<String, String> config) {
            this.config = config;
        }

        public Date getDateCreatedMinThreshold() {
            return dateCreatedMinThreshold;
        }

        public Boolean getIsPreview() {
            return isPreview;
        }

        public Boolean getIsRegistered() {
            return isRegistered;
        }

        public Boolean getIsDeactivated() {
            return isDeactivated;
        }
        
        public Long getBatchSize() {
            return batchSize;
        }

        public Long getFailedRetryTime() {
            return failedRetryTime;
        }

        public CustomerPurgeParams invoke() {
            isRegistered = null;
            isDeactivated = null;
            dateCreatedMinThreshold = null;
            isPreview = null;
            batchSize = ResourcePurgeServiceImpl.BATCH_SIZE;
            failedRetryTime = ResourcePurgeServiceImpl.PURGE_ERROR_CACHE_RETRY_SECONDS;

            for (Map.Entry<String, String> entry : config.entrySet()) {
                if (PurgeCustomerVariableNames.SECONDS_OLD.toString().equals(entry.getKey())) {
                    Long secondsOld = Long.parseLong(entry.getValue());
                    dateCreatedMinThreshold = new Date(SystemTime.asMillis() - (secondsOld * 1000));
                }
                if (PurgeCustomerVariableNames.IS_REGISTERED.toString().equals(entry.getKey())) {
                    isRegistered = Boolean.parseBoolean(entry.getValue());
                }
                if (PurgeCustomerVariableNames.IS_DEACTIVATED.toString().equals(entry.getKey())) {
                    isDeactivated = Boolean.parseBoolean(entry.getValue());
                }
                if (PurgeCustomerVariableNames.IS_PREVIEW.toString().equals(entry.getKey())) {
                    isPreview = Boolean.parseBoolean(entry.getValue());
                }
                if (PurgeCustomerVariableNames.BATCH_SIZE.toString().equals(entry.getKey())) {
                    batchSize = Long.parseLong(entry.getValue());
                }
                if (PurgeCustomerVariableNames.RETRY_FAILED_SECONDS.toString().equals(entry.getKey())) {
                    failedRetryTime = System.currentTimeMillis() - (Long.parseLong(entry.getValue()) * 1000);
                }
            }
            return this;
        }
    }
    
    private class PurgeErrorCache {

        private Map<Long, Long> cache = new HashMap<Long, Long>();
        
        public Long add(Long entry) {
            if (! cache.containsKey(entry)) {
                return cache.put(entry, new Long(System.currentTimeMillis()));
            }
            return null;
        }
        
        public Set<Long> getEntriesSince(long expiredTime) {
            for(Iterator<Map.Entry<Long, Long>> item = cache.entrySet().iterator(); item.hasNext(); ) {
                Map.Entry<Long, Long> entry = item.next();
                if(entry.getValue().longValue() < expiredTime) {
                  item.remove();
                }
            }
            return cache.keySet();
        }

        public int size() {
            return cache.size();
        }
        
    }
    
}
