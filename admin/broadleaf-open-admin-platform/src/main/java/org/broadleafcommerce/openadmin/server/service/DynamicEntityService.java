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
package org.broadleafcommerce.openadmin.server.service;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceResponse;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Rather than using this clas directly, it might be more appropraite to utilize {@link AdminEntityService} instead. The
 * methods in this class will not attempt to recover from things like validation problems whereas {@link AdminEntityService}
 * will.
 * 
 * @author jfischer
 */
public interface DynamicEntityService {
    
    /**
     * Builds all of the metadata associated with a particular request for an entity. The resulting {@link PersistenceResponse}
     * that is returned will not have the {@link PersistenceResponse#getEntity()} property set and this will return null.
     * Instead, this will populate {@link PersistenceResponse#getDynamicResultSet()}.
     * 
     * @param persistencePackage the package that should be passed through the admin pipeline to build the metadata
     * @return a {@link PersistenceResponse} with the {@link PersistenceResponse#getDynamicResultSet()} set with the 
     * metadata of the built properties for this particular entity
     * @throws ServiceException wraps whatever internal exception that might have occurred as a result of the inspect
     */
    @PreAuthorize("isAuthenticated()")
    PersistenceResponse inspect(PersistencePackage persistencePackage) throws ServiceException;

    @PreAuthorize("isAuthenticated()")
    PersistenceResponse nonTransactionalInspect(final PersistencePackage persistencePackage) throws ServiceException;

    @PreAuthorize("isAuthenticated()")
    PersistenceResponse fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException;

    @PreAuthorize("isAuthenticated()")
    PersistenceResponse nonTransactionalFetch(final PersistencePackage persistencePackage, final CriteriaTransferObject cto) throws ServiceException;
    
    /**
     * This will throw a {@link ValidationException} and not attempt to swallow them and wrap any other exceptions within
     * a {@link ServiceException} that might have resulted in adding the given package.
     * 
     * @param persistencePackage
     * @return
     * @throws ServiceException
     * @see {@link AdminEntityService#add(org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest)}
     */
    @PreAuthorize("isAuthenticated()")
    PersistenceResponse add(PersistencePackage persistencePackage) throws ServiceException;

    /**
     * The exact same as {@link #add(PersistencePackage)} except this is not bound to a transaction. This is useful when
     * transactions are handled by the caller that has its own rollback logic (like when batching multiple adds).
     * 
     * @param persistencePackage
     * @return
     * @throws ServiceException
     */
    @PreAuthorize("isAuthenticated()")
    PersistenceResponse nonTransactionalAdd(PersistencePackage persistencePackage) throws ServiceException;

    @PreAuthorize("isAuthenticated()")
    PersistenceResponse update(PersistencePackage persistencePackage) throws ServiceException;

    /**
     * The exact same as {@link #update(PersistencePackage)} except this is not bound to a transaction. This is useful when
     * transactions are handled by the caller that has its own rollback logic (like when batching multiple updates).
     * 
     * @param persistencePackage
     * @return
     * @throws ServiceException
     */
    @PreAuthorize("isAuthenticated()")
    PersistenceResponse nonTransactionalUpdate(PersistencePackage persistencePackage) throws ServiceException;

    @PreAuthorize("isAuthenticated()")
    PersistenceResponse remove(PersistencePackage persistencePackage) throws ServiceException;
    
    /**
     * The exact same as {@link #remove(PersistencePackage)} except this is not bound to a transaction. This is useful when
     * transactions are handled by the caller that has its own rollback logic (like when batching multiple removes).
     * 
     * @param persistencePackage
     * @return
     * @throws ServiceException
     */
    @PreAuthorize("isAuthenticated()")
    PersistenceResponse nonTransactionalRemove(PersistencePackage persistencePackage) throws ServiceException;
    
}
