/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2019 Broadleaf Commerce
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
package org.broadleafcommerce.common.extensibility.jpa;

import org.broadleafcommerce.common.extensibility.cache.DefaultJCacheUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

import java.util.Properties;

public class JCachePersistenceUnitPostProcessor implements PersistenceUnitPostProcessor {

    @Value("${jcache.disable.cache:false}")
    protected Boolean disableCache;

    @Override
    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
        Properties properties = pui.getProperties();
        if (disableCache) {
            properties.setProperty("hibernate.cache.use_second_level_cache", "false");
            properties.setProperty("hibernate.cache.use_query_cache", "false");
        }
        properties.setProperty("hibernate.javax.cache.uri", DefaultJCacheUtil.JCACHE_MERGED_XML_RESOUCE_URI.toString());
    }

}
