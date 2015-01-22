/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.util.ErrorHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Base abstract ApplicationEvent that indicates if the event should be an asynchronous event. 
 * The asynchronicity of this event will be honored by event listeners that understand this type 
 * of event.
 * 
 * When using asynchronous events, consider things like EntityManager, Transactions, thread pool size, 
 * database connection pool size, etc. 
 * These are all things that may need to be considered for the given environment.
 * 
 * @see <code>org.broadleafcommerce.common.event.BroadleafApplicationEventMultiCaster</code>
 * @see <code>org.broadleafcommerce.common.event.BroadleafApplicationListener</code>
 * 
 * @author Kelly Tisdell
 *
 */
public abstract class BroadleafApplicationEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;
	
    protected final boolean asynchronous;
	
	protected transient final Map<String, Object> context = Collections.synchronizedMap(new HashMap<String, Object>());
	
	protected transient final ErrorHandler errorHandler;
	
	/**
	 * Instantiates this with the required source. The asynchronous property is false and the errorHandler is null.
	 * @param source
	 */
	public BroadleafApplicationEvent(Object source) {
		this(source, false, null);
	}
	
	/**
	 * Instantiates this with the required source. The asynchronous property is false.
	 * @param source
	 * @param errorHandler
	 */
	public BroadleafApplicationEvent(Object source, ErrorHandler errorHandler) {
		this(source, false, errorHandler);
	}
	
	/**
	 * Instantiates this with the required source and asynchronous flag. The errorHandler is null.
	 * @param source
	 * @param asynchronous
	 */
	public BroadleafApplicationEvent(Object source, boolean asynchronous) {
		this(source, asynchronous, null);
	}
	
	/**
	 * Instantiates this with the required source, asynchronous flag, and errorHandler.
	 * @param source
	 * @param asynchronous
	 * @param errorHandler
	 */
	public BroadleafApplicationEvent(Object source, boolean asynchronous, ErrorHandler errorHandler) {
		super(source);
		this.asynchronous = asynchronous;
		this.errorHandler = errorHandler;
	}

	/**
	 * Indicates whether this event should be fired asynchronously, if possible.  Actual invocation will be 
	 * dependent on the ApplicationEventMulticaster implementation.
	 * @return
	 */
	public boolean isAsynchronous() {
		return asynchronous;
	}
	
	/**
	 * Context map that allows generic objects / properties to be passed around on events. This map is synchronized.
	 * @return
	 */
	public Map<String, Object> getConext() {
		return context;
	}

	/**
	 * Returns an error handler for this event.  May be null.
	 * @return
	 */
	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}
}
