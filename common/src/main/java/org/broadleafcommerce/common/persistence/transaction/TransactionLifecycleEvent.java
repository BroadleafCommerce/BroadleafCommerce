/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.common.persistence.transaction;

import org.springframework.context.ApplicationEvent;

/**
 * Spring event published at key transaction lifecycle points.
 *
 * @see LifecycleAwareJpaTransactionManager
 * @see TransactionLifecycleMonitor
 * @author Jeff Fischer
 */
public class TransactionLifecycleEvent extends ApplicationEvent {

    protected TransactionLifecycle lifecycle;
    protected Object[] params;
    protected Throwable e;

    public TransactionLifecycleEvent(Object source, TransactionLifecycle lifecycle, Throwable e, Object... params) {
        super(source);
        this.lifecycle = lifecycle;
        this.e = e;
        this.params = params;
    }

    public Object[] getParams() {
        return params;
    }

    public TransactionLifecycle getLifecycle() {
        return lifecycle;
    }

    public Throwable getException() {
        return e;
    }
}
