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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Base abstract ApplicationEvent that provides a marker for Broadleaf events and provides a default 
 * context map. 
 * 
 * @see <code>org.broadleafcommerce.common.event.BroadleafApplicationEventMultiCaster</code>
 * @see <code>org.broadleafcommerce.common.event.BroadleafApplicationListener</code>
 * 
 * @author Kelly Tisdell
 *
 */
public abstract class BroadleafApplicationEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;
	
	protected transient final Map<String, Object> context = Collections.synchronizedMap(new HashMap<String, Object>());
	
	/**
	 * Instantiates this with the required source. The asynchronous property is false and the errorHandler is null.
	 * @param source
	 */
	public BroadleafApplicationEvent(Object source) {
		super(source);
	}
	
	/**
	 * Context map that allows generic objects / properties to be passed around on events. This map is synchronized.
	 * @return
	 */
	public Map<String, Object> getConext() {
		return context;
	}
}
