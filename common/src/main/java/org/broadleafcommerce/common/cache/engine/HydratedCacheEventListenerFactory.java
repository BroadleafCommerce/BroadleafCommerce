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
package org.broadleafcommerce.common.cache.engine;

import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;

import java.lang.reflect.Method;
import java.util.Properties;

/**
 * 
 * @author jfischer
 *
 */
public class HydratedCacheEventListenerFactory extends CacheEventListenerFactory {

    private static HydratedCacheManager manager = null;

    @Override
    public CacheEventListener createCacheEventListener(Properties props) {
        try {
            if (props == null || props.isEmpty()) {
                manager = EhcacheHydratedCacheManagerImpl.getInstance();
            } else {
                String managerClass = props.getProperty("managerClass");
                Class<?> clazz = Class.forName(managerClass);
                Method method = clazz.getDeclaredMethod("getInstance");
                manager = (HydratedCacheManager) method.invoke(null);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to create a CacheEventListener instance", e);
        }
        return (CacheEventListener) manager;
    }

    public static HydratedCacheManager getConfiguredManager() {
        return manager;
    }
}
