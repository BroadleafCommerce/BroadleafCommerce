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
package org.broadleafcommerce.core.order.domain;

import java.io.Serializable;

/**
 * Domain object used to synchronize {@link Order} operations.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface OrderLock extends Serializable {

    /**
     * @return the id of the {@link Order} associated with this OrderLock
     */
    public Long getOrderId();

    /**
     * Sets the id of the {@link Order} associated with this OrderLock
     * 
     * @param orderId
     */
    public void setOrderId(Long orderId);

    /**
     * @return whether or not this OrderLock is currently locked
     */
    public Boolean getLocked();

    /**
     * Sets the lock state of this OrderLock
     * 
     * @param locked
     */
    public void setLocked(Boolean locked);

    /**
     * @return the last time this lock record was successfully altered
     */
    Long getLastUpdated();

    /**
     * Set the time of alteration
     *
     * @param lastUpdated
     */
    void setLastUpdated(Long lastUpdated);

    /**
     * @return the key used to identify the creator of the lock
     */
    String getKey();

    /**
     * Set a key identifying the creator of the lock
     *
     * @param nodeKey
     */
    void setKey(String nodeKey);
}
