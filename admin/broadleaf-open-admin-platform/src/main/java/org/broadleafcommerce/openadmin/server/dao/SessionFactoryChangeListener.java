/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.persistence.IdOverrideTableGenerator;
import org.broadleafcommerce.common.util.BLCFieldUtils;
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
            BLCFieldUtils.FIELD_CACHE.clear();
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
