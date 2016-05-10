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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "BLC_ORDER_LOCK")
@Inheritance(strategy = InheritanceType.JOINED)
public class OrderLockImpl implements OrderLock {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    OrderLockPk orderLockPK = new OrderLockPk();

    @Column(name = "LOCKED")
    protected Character locked = 'N';

    @Column(name = "LAST_UPDATED")
    protected Long lastUpdated;

    @Override
    public Long getOrderId() {
        return orderLockPK.getOrderId();
    }
    
    @Override
    public void setOrderId(Long orderId) {
        this.orderLockPK.setOrderId(orderId);
    }

    @Override
    public Boolean getLocked() {
        if (locked == null || locked == 'N') {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setLocked(Boolean locked) {
        if (locked == null || locked == false) {
            this.locked = 'N';
        } else {
            this.locked = 'Y';
        }
    }

    @Override
    public Long getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String getKey() {
        return orderLockPK.getKey();
    }

    @Override
    public void setKey(String nodeKey) {
        this.orderLockPK.setKey(nodeKey);
    }

    @Embeddable
    public static class OrderLockPk implements Serializable {

        private static final long serialVersionUID = 1L;

        @Column(name = "ORDER_ID")
        protected Long orderId;

        @Column(name = "LOCK_KEY")
        protected String key;

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj != null && getClass().isAssignableFrom(obj.getClass())) {
                OrderLockPk other = (OrderLockPk) obj;
                return new EqualsBuilder()
                    .append(orderId, other.orderId)
                    .append(key, other.key)
                    .build();
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                .append(orderId)
                .append(key)
                .build();
        }
    }
}
