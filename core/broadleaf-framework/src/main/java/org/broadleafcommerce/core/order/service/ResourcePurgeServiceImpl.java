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
package org.broadleafcommerce.core.order.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.util.StreamCapableTransactionalOperationAdapter;
import org.broadleafcommerce.common.util.StreamingTransactionCapableUtil;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.order.service.type.PurgeCartVariableNames;
import org.springframework.stereotype.Service;

/**
 * Service capable of deleting old or defunct entities from the persistence layer (e.g. Carts and anonymous Customers).
 * {@link org.broadleafcommerce.core.order.service.ResourcePurgeService} for additional API documentation.
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

    @Resource(name = "blOrderService")
    protected OrderService orderService;

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
            LOG.debug("Purging carts (non-submitted orders)");
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
            }, RuntimeException.class);
        } catch (Exception e) {
            LOG.error("Unable to purge carts", e);
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
        CartPurgeParams cartPurgeParams = new CartPurgeParams(config).invoke();
        String[] nameArray = cartPurgeParams.getNameArray();
        OrderStatus[] statusArray = cartPurgeParams.getStatusArray();
        Date dateCreatedMinThreshold = cartPurgeParams.getDateCreatedMinThreshold();
        Boolean isPreview = cartPurgeParams.getIsPreview();
        return orderService.findCarts(nameArray, statusArray, dateCreatedMinThreshold, isPreview, startPos, length);
    }

    /**
     * Get the count of carts to delete from the database. Subclasses may override for custom cart retrieval logic.
     *
     * @param config params for the query
     * @return count of carts to delete
     */
    protected Long getCartsToPurgeLength(Map<String, String> config) {
        CartPurgeParams cartPurgeParams = new CartPurgeParams(config).invoke();
        String[] nameArray = cartPurgeParams.getNameArray();
        OrderStatus[] statusArray = cartPurgeParams.getStatusArray();
        Date dateCreatedMinThreshold = cartPurgeParams.getDateCreatedMinThreshold();
        Boolean isPreview = cartPurgeParams.getIsPreview();
        return orderService.findCartsCount(nameArray, statusArray, dateCreatedMinThreshold, isPreview);
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
}
