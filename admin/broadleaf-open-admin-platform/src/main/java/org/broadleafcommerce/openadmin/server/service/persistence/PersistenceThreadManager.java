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
package org.broadleafcommerce.openadmin.server.service.persistence;

import org.broadleafcommerce.common.persistence.TargetModeType;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.springframework.stereotype.Service;

/**
 * @author Jeff Fischer
 */
@Service("blPersistenceThreadManager")
public class PersistenceThreadManager {

    public <T, G extends Throwable> T operation(TargetModeType targetModeType, Persistable<T, G> persistable) throws G {
        try {
            PersistenceManagerFactory.startPersistenceManager(targetModeType);
            return persistable.execute();
        } finally {
            PersistenceManagerFactory.endPersistenceManager();
        }
    }

    public <T, G extends Throwable> T operation(TargetModeType targetModeType, PersistencePackage pkg, Persistable<T, G> persistable) throws G {
        try {
            String pkgEntityClassName = pkg.getCeilingEntityFullyQualifiedClassname();
            PersistenceManagerFactory.startPersistenceManager(pkgEntityClassName, targetModeType);
            return persistable.execute();
        } finally {
            PersistenceManagerFactory.endPersistenceManager();
        }
    }
}
