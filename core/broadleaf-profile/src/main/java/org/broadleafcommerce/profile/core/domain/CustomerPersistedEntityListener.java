/*
 * Copyright 2008-2013 the original author or authors.
 *
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
 */

package org.broadleafcommerce.profile.core.domain;

import org.broadleafcommerce.common.util.ApplicationContextHolder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;


/**
 * The main function of this entity listener is to publish a Spring event that the customer has been persisted. This is
 * necessary in order to update the current customer in the application
 *
 * @author Phillip Verheyden (phillipuniverse)
 * 
 * @see {@link ApplicationEventPublisher#publishEvent(org.springframework.context.ApplicationEvent)}
 * @see {@link CustomerPersistedEvent}
 * @see {@link org.broadleafcommerce.profile.web.core.CustomerPersistedEventListener}
 * @see {@link org.broadleafcommerce.profile.web.core.CustomerState}
 */
public class CustomerPersistedEntityListener {
    
    /**
     * Invoked on both the PostPersist and PostUpdate. The default implementation is to simply publish a Spring event
     * to the ApplicationContext to allow 
     * 
     * @param entity the newly-persisted Customer
     * @see CustomerPersistedEvent
     */
    @PostPersist
    @PostUpdate
    public void customerUpdated(final Object entity) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    ApplicationContextHolder.getApplicationContext().publishEvent(new CustomerPersistedEvent((Customer) entity));
                }
            });
        }
    }
    
}
