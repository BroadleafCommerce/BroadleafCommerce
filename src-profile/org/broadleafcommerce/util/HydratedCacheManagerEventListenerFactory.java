/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.util;

import java.util.Properties;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.event.CacheManagerEventListener;
import net.sf.ehcache.event.CacheManagerEventListenerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HydratedCacheManagerEventListenerFactory extends CacheManagerEventListenerFactory {

    private static final Log LOG = LogFactory.getLog(HydratedCacheManagerEventListenerFactory.class);

    @Override
    public CacheManagerEventListener createCacheManagerEventListener(CacheManager arg0, Properties props) {
        String cacheNames = props.getProperty("cacheNames");
        if (cacheNames == null) {
            throw new RuntimeException("Must specify a cacheNames property with a semi-colon delimitted list of cache names.");
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("adding hydrated cache objects for registered cache names: " + cacheNames);
        }
        String[] names = cacheNames.split(";");
        for (String name : names) {
            HydratedCache cache = new HydratedCache(name.trim());
            HydratedCacheManager.getInstance().addHydratedCache(cache);
        }
        return HydratedCacheManager.getInstance();
    }

}
