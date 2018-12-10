/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2018 Broadleaf Commerce
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

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.FlushEntityEvent;
import org.hibernate.jpa.event.internal.core.JpaFlushEntityEventListener;

/**
 * Support {@link DirtyFallback} dirty state detection during flush.
 *
 * @author Jeff Fischer
 */
public class FrameworkFlushEntityEventListener extends JpaFlushEntityEventListener {

    @Override
    protected void dirtyCheck(FlushEntityEvent event) throws HibernateException {
        super.dirtyCheck(event);
        boolean alreadyDirty = !ArrayUtils.isEmpty(event.getDirtyProperties());
        if (alreadyDirty) {
            return;
        }
        boolean isDirtyFallback = event.getEntity() instanceof DirtyFallback;
        if (isDirtyFallback && ((DirtyFallback) event.getEntity()).isDirty()) {
            int[] dirtyProperties = {};
            for (String dirty : ((DirtyFallback) event.getEntity()).getDirtyProperties()) {
                int count = 0;
                for (String property : event.getEntityEntry().getPersister().getPropertyNames()) {
                    if (property.equals(dirty)) {
                        dirtyProperties = ArrayUtils.add(dirtyProperties, count);
                        break;
                    }
                    count++;
                }
            }
            if (!ArrayUtils.isEmpty(dirtyProperties)) {
                event.setDirtyProperties(dirtyProperties);
            }
        }
    }
}
