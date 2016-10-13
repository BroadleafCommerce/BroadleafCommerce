/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.persistence.IdOverrideTableGenerator;
import org.broadleafcommerce.common.util.dao.DynamicDaoHelperImpl;
import org.broadleafcommerce.openadmin.server.service.DynamicEntityRemoteService;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Clear the static entity metadata caches from {@code DynamicEntityDao}
 * upon recycling of the session factory.
 *
 * @author jfischer
 */
public class SessionFactoryChangeListener implements SessionFactoryObserver {
    
    private static final Log LOG = LogFactory.getLog(SessionFactoryChangeListener.class);

    @Override
    public void sessionFactoryClosed(SessionFactory factory) {
        //do nothing
    }

    @Override
    public void sessionFactoryCreated(SessionFactory factory) {
        synchronized (DynamicDaoHelperImpl.LOCK_OBJECT) {
            DynamicEntityDaoImpl.METADATA_CACHE.clear();
            DynamicDaoHelperImpl.POLYMORPHIC_ENTITY_CACHE.clear();
            try {
                Field metadataCache = DynamicEntityRemoteService.class.getDeclaredField("METADATA_CACHE");
                metadataCache.setAccessible(true);
                ((Map) metadataCache.get(null)).clear();
                LOG.info("Metadata cache cleared from recycling the session factory");
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            try {
                Field fieldCache = IdOverrideTableGenerator.class.getDeclaredField("FIELD_CACHE");
                fieldCache.setAccessible(true);
                ((Map) fieldCache.get(null)).clear();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

}
