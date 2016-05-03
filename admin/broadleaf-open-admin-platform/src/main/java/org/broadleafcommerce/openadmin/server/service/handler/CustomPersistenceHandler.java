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
package org.broadleafcommerce.openadmin.server.service.handler;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.core.Ordered;

/**
 * Custom Persistence Handlers provide a hook to override the normal persistence
 * behavior of the admin application. This is useful when an alternate pathway
 * for working with persisted data is desirable. For example, if you want to
 * work directly with a service API, rather than go through the standard
 * admin persistence pipeline. In such a case, you can use Spring to inject
 * an instance of your service into your custom persistence handler and
 * utilize that service to work with your entity. The implementation is responsible
 * for converting domain object into the return type required by the admin. Helper
 * classes are passed in to assist with conversion operations.
 *
 * @author Jeff Fischer
 */
public interface CustomPersistenceHandler extends Ordered {

    public static final int DEFAULT_ORDER = Integer.MAX_VALUE;

    /**
     * Is this persistence handler capable of dealing with an inspect request from the admin
     *
     * @param persistencePackage details about the inspect operation
     * @return whether or not this handler supports inspects
     */
    public Boolean canHandleInspect(PersistencePackage persistencePackage);

    /**
     * Is this persistence handler capable of dealing with an fetch request from the admin
     *
     * @param persistencePackage details about the fetch operation
     * @return whether or not this handler supports fetches
     */
    public Boolean canHandleFetch(PersistencePackage persistencePackage);

    /**
     * Is this persistence handler capable of dealing with an add request from the admin
     *
     * @param persistencePackage details about the add operation
     * @return whether or not this handler supports adds
     */
    public Boolean canHandleAdd(PersistencePackage persistencePackage);

    /**
     * Is this persistence handler capable of dealing with a remove request from the admin
     *
     * @param persistencePackage details about the remove operation
     * @return whether or not this handler supports remove
     */
    public Boolean canHandleRemove(PersistencePackage persistencePackage);

    /**
     * Is this persistence handler capable of dealing with an update request from the admin
     *
     * @param persistencePackage details about the update operation
     * @return whether or not this handler supports updatess
     */
    public Boolean canHandleUpdate(PersistencePackage persistencePackage);
    public Boolean willHandleSecurity(PersistencePackage persistencePackage);
    
    public DynamicResultSet inspect(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException;

    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException;
    
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException;
    
    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException;
    
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException;
    
}
