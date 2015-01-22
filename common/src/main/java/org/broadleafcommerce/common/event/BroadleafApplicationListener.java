/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.common.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Works in conjection with <code>org.broadleafcommerce.common.event.BroadleafApplicationEventMulticaster</code> except 
 * the event listener can indicate if the event should be run in a background thread.  If no TaskExecutor is 
 * configured on the BroadleafApplicationEventMulticaster then it will be executed synchronously, regardless of 
 * of whether an event listener is configured.
 * 
 * @author Kelly Tisdell
 *
 * @param <E>
 */
public interface BroadleafApplicationListener<E extends ApplicationEvent> extends ApplicationListener<E> {

    /**
     * Indicates if this application listener should be run in a background thread if a TasExecutor is configured.
     * @return
     */
    public boolean isAsynchronous();

}
