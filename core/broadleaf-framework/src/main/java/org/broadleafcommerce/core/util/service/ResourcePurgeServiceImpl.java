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
package org.broadleafcommerce.core.util.service;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.notification.service.NotificationDispatcher;
import org.broadleafcommerce.common.notification.service.type.EmailNotification;
import org.broadleafcommerce.common.notification.service.type.NotificationEventType;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderImpl;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.util.dao.ResourcePurgeDao;
import org.broadleafcommerce.core.util.service.type.PurgeCartVariableNames;
import org.broadleafcommerce.core.util.service.type.PurgeCustomerVariableNames;
import org.broadleafcommerce.core.util.service.type.PurgeOrderHistoryVariableNames;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
 * }
 *
 * @author Jeff Fischer
 */
@Service("blResourcePurgeService")
public class ResourcePurgeServiceImpl implements ResourcePurgeService {

    protected static final Long BATCH_SIZE = 50L;
    protected static final Long PURGE_ERROR_CACHE_RETRY_SECONDS = System.currentTimeMillis() - 172800; //48 HOURS
    private static final Log LOG = LogFactory.getLog(ResourcePurgeServiceImpl.class);
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

    @Autowired
    @Qualifier("blNotificationDispatcher")
    protected NotificationDispatcher notificationDispatcher;

    @Resource(name = "blDeleteStatementGenerator")
    protected DeleteStatementGenerator deleteStatementGenerator;

    @Autowired
    protected Environment env;

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blResourcePurgeExtensionManager")
    protected ResourcePurgeExtensionManager extensionManager;

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
        synchronized (cartPurgeErrors) {
            Set<Long> failedCartIds = getCartsInErrorToIgnore(purgeParams);
            batchCount = getCartsToPurgeLength(purgeParams, new ArrayList<>(failedCartIds)).intValue();
            List<Order> carts = getCartsToPurge(purgeParams, 0, batchCount, new ArrayList<>(failedCartIds));
            for (Order cart : carts) {
                TransactionStatus status = TransactionUtils.createTransaction("Cart Purge",
                        TransactionDefinition.PROPAGATION_REQUIRED, transactionManager, false);
                try {
                    deleteCart(cart);
                    TransactionUtils.finalizeTransaction(status, transactionManager, false);
                    processedCount++;
                } catch (Exception e) {
                    if (!status.isCompleted()) {
                        TransactionUtils.finalizeTransaction(status, transactionManager, true);
                    }
                    LOG.error(String.format("Not able to purge Cart ID: %d", cart.getId()), e);
                    cartPurgeErrors.add(cart.getId());
                }
            }
        }
        LOG.info(String.format("Cart purge batch processed.  Purged %d from total batch size of %d, %d failures cached",
                processedCount, batchCount, cartPurgeErrors.size()));
    }

    @Override
    public void notifyCarts(final Map<String, String> config) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Notifying carts of purge");
        }
        if (MapUtils.isEmpty(config)) {
            throw new IllegalArgumentException("Cannot notify carts of purge since there was no configuration provided. "
                    + "In the absence of config params, all carts would be candidates for deletion.");
        }
        CartPurgeParams purgeParams = new CartPurgeParams(config).invoke();
        int batchCount = 0;
        Set<Long> failedCartIds = getCartsInErrorToIgnore(purgeParams);
        batchCount = getCartsToPurgeLength(purgeParams, new ArrayList<>(failedCartIds)).intValue();
        List<Order> carts = getCartsToPurge(purgeParams, 0, batchCount, new ArrayList<>(failedCartIds));
        for (Order cart : carts) {
            notifyCart(cart);
        }
    }

    public void purgeOrderHistory(
            Class<?> rootType,
            String rootTypeIdValue,
            Map<String, List<DeleteStatementGeneratorImpl.PathElement>> depends,
            final Map<String, Integer> config
    ) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Purging historical orders");
        }

        String enablePurge = env.getProperty("enable.purge.order.history");

        if (!Boolean.parseBoolean(enablePurge)) {
            LOG.info("Save protection. Purging history is off. Please set property enable.purge.order.history to true.");
            return;
        }

        Integer daysCount = config.get(PurgeOrderHistoryVariableNames.OLDER_THAN_DAYS.toString());
        Integer batchSize = config.get(PurgeOrderHistoryVariableNames.BATCH_SIZE.toString());

        List<Order> oldOrders = orderService.findOrdersByDaysCount(daysCount, batchSize);
        Map<String, List<DeleteStatementGeneratorImpl.PathElement>> dependencies = new HashMap<>(depends);

        List<DeleteStatementGeneratorImpl.PathElement> orderDependencies = new ArrayList<>();

        orderDependencies.add(new DeleteStatementGeneratorImpl.PathElement("BLC_ORDER_LOCK", "ORDER_ID", "ORDER_ID"));

        dependencies.put("BLC_ORDER", orderDependencies);

        ArrayList<DeleteStatementGeneratorImpl.PathElement> orderItemDependencies = new ArrayList<>();
        orderDependencies.add(new DeleteStatementGeneratorImpl.PathElement(
                "BLC_ORDER_MULTISHIP_OPTION", "ORDER_MULTISHIP_OPTION_ID", "ORDER_ITEM_ID"
        ));
        orderDependencies.add(new DeleteStatementGeneratorImpl.PathElement(
                "BLC_GIFTWRAP_ORDER_ITEM", "ORDER_ITEM_ID", "ORDER_ITEM_ID"
        ));
        dependencies.put("BLC_ORDER_ITEM", orderItemDependencies);
        dependencies.put(
                "BLC_ORDER_PAYMENT",
                Collections.singletonList(new DeleteStatementGeneratorImpl.PathElement(
                        "BLC_PAYMENT_LOG", "ORDER_PAYMENT_ID", "ORDER_PAYMENT_ID"
                ))
        );
        extensionManager.getProxy().addPurgeDependencies(dependencies);
        Set<String> exclusions = new HashSet<>();
        exclusions.add("BLC_ADMIN_USER");
        extensionManager.getProxy().addPurgeExclusions(exclusions);
        Map<String, String> deleteStatement = deleteStatementGenerator.generateDeleteStatementsForType(
                OrderImpl.class, "?", dependencies, exclusions
        );
        for (Order order : oldOrders) {
            TransactionStatus status = TransactionUtils.createTransaction(
                    "Cart Purge",
                    TransactionDefinition.PROPAGATION_REQUIRED,
                    transactionManager,
                    false
            );
            try {
                em.unwrap(Session.class).doWork(new Work() {
                    @Override
                    public void execute(Connection connection) throws SQLException {
                        Statement statement = connection.createStatement();
                        for (String value : deleteStatement.values()) {
                            String sql = value.replace("?", String.valueOf(order.getId()));
                            LOG.debug(sql);
                            statement.addBatch(sql);
                        }
                        extensionManager.getProxy().addPurgeStatements(statement, rootTypeIdValue);
                        statement.executeBatch();
                    }
                });
                TransactionUtils.finalizeTransaction(status, transactionManager, false);
            } catch (Exception e) {
                if (!status.isCompleted()) {
                    TransactionUtils.finalizeTransaction(status, transactionManager, true);
                }
                LOG.error(String.format("Not able to purge Order ID: %d", order.getId()), e);
            }
        }

        LOG.info("Finished purging historical orders.");
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
        synchronized (customerPurgeErrors) {
            Set<Long> failedCustomerIds = getCustomersInErrorToIgnore(purgeParams);
            batchCount = getCustomersToPurgeLength(purgeParams, new ArrayList<>(failedCustomerIds)).intValue();
            List<Customer> customers = getCustomersToPurge(
                    purgeParams, 0, batchCount, new ArrayList<>(failedCustomerIds)
            );
            for (Customer customer : customers) {
                TransactionStatus status = TransactionUtils.createTransaction("Customer Purge",
                        TransactionDefinition.PROPAGATION_REQUIRED, transactionManager, false);
                try {
                    deleteCustomer(customer);
                    TransactionUtils.finalizeTransaction(status, transactionManager, false);
                    processedCount++;
                } catch (Exception e) {
                    if (!status.isCompleted()) {
                        TransactionUtils.finalizeTransaction(status, transactionManager, true);
                    }
                    LOG.error(String.format("Not able to purge Customer ID: %d", customer.getId()), e);
                    customerPurgeErrors.add(customer.getId());
                }
            }
        }
        LOG.info(String.format("Customer purge batch processed.  Purged %d from total batch size of %d, %d failures cached",
                processedCount, batchCount, customerPurgeErrors.size()));
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
     * @param purgeParams  configured parameters for the Cart purge process
     * @param cartsInError list of cart ids to be ignored/excluded from the query
     * @return list of carts to delete
     */
    protected List<Order> getCartsToPurge(
            CartPurgeParams purgeParams,
            int startPos,
            int length,
            List<Long> cartsInError
    ) {
        String[] nameArray = purgeParams.getNameArray();
        OrderStatus[] statusArray = purgeParams.getStatusArray();
        Date dateCreatedMinThreshold = purgeParams.getDateCreatedMinThreshold();
        Boolean isPreview = purgeParams.getIsPreview();
        return resourcePurgeDao.findCarts(
                nameArray, statusArray, dateCreatedMinThreshold, isPreview, startPos, length, cartsInError
        );
    }

    /**
     * Get the count of carts to delete from the database. Subclasses may override for custom cart retrieval logic.
     *
     * @param purgeParams  configured parameters for the Customer purge process used in the query
     * @param cartsInError list of cart ids to ignore/exclude from the next purge run
     * @return count of carts to delete
     */
    protected Long getCartsToPurgeLength(CartPurgeParams purgeParams, List<Long> cartsInError) {
        String[] nameArray = purgeParams.getNameArray();
        OrderStatus[] statusArray = purgeParams.getStatusArray();
        Date dateCreatedMinThreshold = purgeParams.getDateCreatedMinThreshold();
        Boolean isPreview = purgeParams.getIsPreview();
        Long cartBatchSize = purgeParams.getBatchSize();
        Long orderCount = resourcePurgeDao.findCartsCount(
                nameArray, statusArray, dateCreatedMinThreshold, isPreview, cartsInError
        );
        //return the lesser of the parameter batch size of the count of the orders to purge
        return cartBatchSize != null && cartBatchSize < orderCount ? cartBatchSize : orderCount;
    }

    /**
     * Notify the cart's owner of a pending purge of their cart.
     *
     * @param cart the cart
     */
    protected void notifyCart(Order cart) {
        String emailAddress = getEmailForCart(cart);
        if (emailAddress != null) {
            Map<String, Object> context = new HashMap<>();
            context.put("cart", cart);
            context.put("customer", cart.getCustomer());
            context.put("emailAddress", emailAddress);

            try {
                notificationDispatcher.dispatchNotification(
                        new EmailNotification(emailAddress, NotificationEventType.NOTIFY_ABANDONED_CART, context)
                );
            } catch (ServiceException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Failure to send email notification", e);
                }
            }
        }
    }

    protected String getEmailForCart(Order cart) {
        if (cart.getEmailAddress() != null) {
            return cart.getEmailAddress();
        }

        if (cart.getCustomer() != null && cart.getCustomer().getEmailAddress() != null) {
            return cart.getCustomer().getEmailAddress();
        }

        return null;
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
     * @param purgeParams      configured parameters for the Customer purge process
     * @param customersInError list of customer ids to be ignored/excluded from the query
     * @return list of customers to delete
     */
    protected List<Customer> getCustomersToPurge(
            CustomerPurgeParams purgeParams,
            int startPos,
            int length,
            List<Long> customersInError
    ) {
        Boolean isRegistered = purgeParams.getIsRegistered();
        Boolean isDeactivated = purgeParams.getIsDeactivated();
        Date dateCreatedMinThreshold = purgeParams.getDateCreatedMinThreshold();
        Boolean isPreview = purgeParams.getIsPreview();
        return resourcePurgeDao.findCustomers(
                dateCreatedMinThreshold, isRegistered, isDeactivated, isPreview, startPos, length, customersInError
        );
    }

    /**
     * Get the count of customers to delete from the database. Subclasses may override for custom customer retrieval logic.
     *
     * @param purgeParams      configured parameters for the Customer purge process
     * @param customersInError list of customer ids to be ignored/excluded from the query
     * @return
     */
    protected Long getCustomersToPurgeLength(CustomerPurgeParams purgeParams, List<Long> customersInError) {
        Boolean isRegistered = purgeParams.getIsRegistered();
        Boolean isDeactivated = purgeParams.getIsDeactivated();
        Date dateCreatedMinThreshold = purgeParams.getDateCreatedMinThreshold();
        Boolean isPreview = purgeParams.getIsPreview();
        Long customerBatchSize = purgeParams.getBatchSize();
        Long customersCount = resourcePurgeDao.findCustomersCount(
                dateCreatedMinThreshold, isRegistered, isDeactivated, isPreview, customersInError
        );
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

    protected class CartPurgeParams {

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

    protected class CustomerPurgeParams {

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

    public class PurgeErrorCache {

        protected Map<Long, Long> cache = new HashMap<Long, Long>();
        
        public Long add(Long entry) {
            if (! cache.containsKey(entry)) {
                return cache.put(entry, Long.valueOf(System.currentTimeMillis()));
            }
            return null;
        }

        public Set<Long> getEntriesSince(long expiredTime) {
            cache.entrySet().removeIf(entry -> entry.getValue() < expiredTime);
            return cache.keySet();
        }

        public int size() {
            return cache.size();
        }

    }

}
