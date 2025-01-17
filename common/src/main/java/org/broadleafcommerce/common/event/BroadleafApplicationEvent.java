/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import org.broadleafcommerce.common.web.BroadleafRequestContext;
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

	public static final class ContextVars {
		public static final String SITE_ID = "_SITE_ID";
		public static final String CATALOG_ID = "_CATALOG_ID";
		public static final String PROFILE_ID = "_PROFILE_ID";
		public static final String LOCALE_CODE = "_LOCALE_CODE";
		public static final String CURRENCY_CODE = "_CURRENCY_CODE";
		public static final String TIMEZONE_ID = "_TIMEZONE_ID";
	}
	
	protected transient final Map<String, Object> context = Collections.synchronizedMap(new HashMap<String, Object>());

	public BroadleafApplicationEvent(Object source) {
		super(source);

		BroadleafRequestContext ctx = BroadleafRequestContext.getBroadleafRequestContext();
		if (ctx != null) {
			if (ctx.getNonPersistentSite() != null) {
				context.put(BroadleafApplicationEvent.ContextVars.SITE_ID, ctx.getNonPersistentSite().getId());
			}

			if (ctx.getCurrentCatalog() != null) {
				context.put(BroadleafApplicationEvent.ContextVars.CATALOG_ID, ctx.getCurrentCatalog().getId());
			}

			if (ctx.getCurrentProfile() != null) {
				context.put(BroadleafApplicationEvent.ContextVars.PROFILE_ID, ctx.getCurrentProfile().getId());
			}

			if (ctx.getLocale() != null) {
				context.put(BroadleafApplicationEvent.ContextVars.LOCALE_CODE, ctx.getLocale().getLocaleCode());
			}

			if (ctx.getBroadleafCurrency() != null) {
				context.put(BroadleafApplicationEvent.ContextVars.CURRENCY_CODE, ctx.getBroadleafCurrency().getCurrencyCode());
			}

			if (ctx.getTimeZone() != null) {
				context.put(BroadleafApplicationEvent.ContextVars.TIMEZONE_ID, ctx.getTimeZone().getID());
			}
		}
	}
	
	/**
	 * Context map that allows generic objects / properties to be passed around on events. This map is synchronized.
	 * @return
	 */
	public Map<String, Object> getContext() {
		return context;
	}

	public Long getSiteId() {
		return (Long) context.get(ContextVars.SITE_ID);
	}

	public Long getCatalogId() {
		return (Long) context.get(ContextVars.CATALOG_ID);
	}

	public Long getProfileId() {
		return (Long) context.get(ContextVars.PROFILE_ID);
	}

	public String getLocaleCode() {
		return (String) context.get(ContextVars.LOCALE_CODE);
	}

	public String getCurrencyCode() {
		return (String) context.get(ContextVars.CURRENCY_CODE);
	}

	public String getTimeZoneId() {
		return (String) context.get(ContextVars.TIMEZONE_ID);
	}

}
