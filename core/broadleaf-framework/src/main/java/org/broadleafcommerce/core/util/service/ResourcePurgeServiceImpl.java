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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.util.StreamCapableTransactionalOperationAdapter;
import org.broadleafcommerce.common.util.StreamingTransactionCapableUtil;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.util.dao.ResourcePurgeDao;
import org.broadleafcommerce.core.util.service.type.PurgeCartVariableNames;
import org.broadleafcommerce.core.util.service.type.PurgeCustomerVariableNames;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.springframework.stereotype.Service;

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

    @Resource(name="blStreamingTransactionCapableUtil")
    protected StreamingTransactionCapableUtil transUtil;

    @Resource(name = "blResourcePurgeDao")
    protected ResourcePurgeDao resourcePurgeDao;

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Resource(name = "blCustomerService")
    protected CustomerService customerService;

    protected Integer pageSize = 10;

    @PostConstruct
    public void init() {
        if (pageSize != null) {
            transUtil.setPageSize(pageSize);
        }
    }

    @Override
    public void purgeCarts(final Map<String, String> config) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Purging carts");
        }
        if (MapUtils.isEmpty(config)) {
            throw new IllegalArgumentException("Cannot purge carts since there was no configuration provided. " +
                    "In the absence of config params, all carts would be candidates for deletion.");
        }
        try {
            //The removal will be performed in chunks based on page size. This minimizes transaction times.
            transUtil.runStreamingTransactionalOperation(new StreamCapableTransactionalOperationAdapter() {
                @Override
                public void pagedExecute(Object[] param) throws Throwable {
                    List<Order> orders = (List<Order>) param[0];
                    for (Order cart : orders) {
                        deleteCart(cart);
                    }
                }

                @Override
                public Object[] retrievePage(int startPos, int pageSize) {
                    List<Order> results = getCartsToPurge(config, startPos, pageSize);
                    return new Object[]{results};
                }

                @Override
                public Long retrieveTotalCount() {
                    return getCartsToPurgeLength(config);
                }

                @Override
                public boolean shouldRetryOnTransactionLockAcquisitionFailure() {
                    return true;
                }
            }, RuntimeException.class);
        } catch (Exception e) {
            LOG.error("Unable to purge carts", e);
        }
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
        try {
            //The removal will be performed in chunks based on page size. This minimizes transaction times.
            transUtil.runStreamingTransactionalOperation(new StreamCapableTransactionalOperationAdapter() {
                @Override
                public void pagedExecute(Object[] param) throws Throwable {
                    List<Customer> customers = (List<Customer>) param[0];
                    for (Customer customer : customers) {
                        deleteCustomer(customer);
                    }
                }

                @Override
                public Object[] retrievePage(int startPos, int pageSize) {
                    List<Customer> results = getCustomersToPurge(config, startPos, pageSize);
                    return new Object[]{results};
                }

                @Override
                public Long retrieveTotalCount() {
                    return getCustomersToPurgeLength(config);
                }

                @Override
                public boolean shouldRetryOnTransactionLockAcquisitionFailure() {
                    return true;
                }
            }, RuntimeException.class);
        } catch (Exception e) {
            LOG.error("Unable to purge customers", e);
        }
    }

    @Override
    public Integer getPageSize() {
        return pageSize;
    }

    @Override
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Get the list of carts to delete from the database. Subclasses may override for custom cart retrieval logic.
     *
     * @param config params for the query
     * @return list of carts to delete
     */
    protected List<Order> getCartsToPurge(Map<String, String> config, int startPos, int length) {
        CartPurgeParams purgeParams = new CartPurgeParams(config).invoke();
        String[] nameArray = purgeParams.getNameArray();
        OrderStatus[] statusArray = purgeParams.getStatusArray();
        Date dateCreatedMinThreshold = purgeParams.getDateCreatedMinThreshold();
        Boolean isPreview = purgeParams.getIsPreview();
        return resourcePurgeDao.findCarts(nameArray, statusArray, dateCreatedMinThreshold, isPreview, startPos, length);
    }

    /**
     * Get the count of carts to delete from the database. Subclasses may override for custom cart retrieval logic.
     *
     * @param config params for the query
     * @return count of carts to delete
     */
    protected Long getCartsToPurgeLength(Map<String, String> config) {
        CartPurgeParams purgeParams = new CartPurgeParams(config).invoke();
        String[] nameArray = purgeParams.getNameArray();
        OrderStatus[] statusArray = purgeParams.getStatusArray();
        Date dateCreatedMinThreshold = purgeParams.getDateCreatedMinThreshold();
        Boolean isPreview = purgeParams.getIsPreview();
        return resourcePurgeDao.findCartsCount(nameArray, statusArray, dateCreatedMinThreshold, isPreview);
    }

    /**
     * Remove the cart from the persistence layer. Subclasses may override for custom cart retrieval logic.
     *
     * @param cart the cart to remove
     */
    protected void deleteCart(Order cart) {
        //We delete the order this way (rather than with a delete query) in order to ensure the cascades take place
        try {
            orderService.deleteOrder(cart);
        } catch (Exception e) {
            LOG.error("Unable to purge a cart", e);
        }
    }

    /**
     * Get the list of carts to delete from the database. Subclasses may override for custom cart retrieval logic.
     *
     * @param config params for the query
     * @return list of carts to delete
     */
    protected List<Customer> getCustomersToPurge(Map<String, String> config, int startPos, int length) {
        CustomerPurgeParams purgeParams = new CustomerPurgeParams(config).invoke();
        Boolean isRegistered = purgeParams.getIsRegistered();
        Boolean isDeactivated = purgeParams.getIsDeactivated();
        Date dateCreatedMinThreshold = purgeParams.getDateCreatedMinThreshold();
        Boolean isPreview = purgeParams.getIsPreview();
        return resourcePurgeDao.findCustomers(dateCreatedMinThreshold, isRegistered, isDeactivated, isPreview, startPos, length);
    }

    /**
     * Get the count of carts to delete from the database. Subclasses may override for custom cart retrieval logic.
     *
     * @param config params for the query
     * @return count of carts to delete
     */
    protected Long getCustomersToPurgeLength(Map<String, String> config) {
        CustomerPurgeParams purgeParams = new CustomerPurgeParams(config).invoke();
        Boolean isRegistered = purgeParams.getIsRegistered();
        Boolean isDeactivated = purgeParams.getIsDeactivated();
        Date dateCreatedMinThreshold = purgeParams.getDateCreatedMinThreshold();
        Boolean isPreview = purgeParams.getIsPreview();
        return resourcePurgeDao.findCustomersCount(dateCreatedMinThreshold, isRegistered, isDeactivated, isPreview);
    }

    /**
     * Remove the cart from the persistence layer. Subclasses may override for custom cart retrieval logic.
     *
     * @param customer the customer to remove
     */
    protected void deleteCustomer(Customer customer) {
        //We delete the customer this way (rather than with a delete query) in order to ensure the cascades take place
        try {
            customerService.deleteCustomer(customer);
        } catch (Exception e) {
            LOG.error("Unable to purge a customer", e);
        }
    }

    private class CartPurgeParams {

        private Map<String, String> config;
        private String[] nameArray;
        private OrderStatus[] statusArray;
        private Date dateCreatedMinThreshold;
        private Boolean isPreview;

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

        public CartPurgeParams invoke() {
            nameArray = null;
            statusArray = null;
            dateCreatedMinThreshold = null;
            isPreview = null;
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

        public CustomerPurgeParams invoke() {
            isRegistered = null;
            isDeactivated = null;
            dateCreatedMinThreshold = null;
            isPreview = null;
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
            }
            return this;
        }
    }
}
