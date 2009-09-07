package org.broadleafcommerce.cache;

import java.io.Serializable;

public interface HydratedCacheManager {

	public Object getHydratedCacheElementItem(String cacheName, Serializable elementKey, String elementItemName);

	public void addHydratedCacheElementItem(String cacheName, Serializable elementKey, String elementItemName, Object elementValue);
	
}