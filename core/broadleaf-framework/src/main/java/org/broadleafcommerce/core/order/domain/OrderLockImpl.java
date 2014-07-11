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
package org.broadleafcommerce.core.order.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "BLC_ORDER_LOCK")
@Inheritance(strategy = InheritanceType.JOINED)
public class OrderLockImpl implements OrderLock {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ORDER_ID")
    protected Long orderId;
    
    @Column(name = "LOCKED")
    protected Character locked = 'N';

    @Override
    public Long getOrderId() {
        return orderId;
    }
    
    @Override
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

}