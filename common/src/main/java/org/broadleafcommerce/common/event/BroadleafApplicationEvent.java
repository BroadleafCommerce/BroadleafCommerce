/*
 * #%L
 * BroadleafCommerce Common Libraries
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
