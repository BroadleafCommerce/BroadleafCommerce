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
package org.broadleafcommerce.openadmin.server.service;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceResponse;
import org.springframework.security.access.annotation.Secured;

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
    @Secured("PERMISSION_OTHER_DEFAULT")
    public PersistenceResponse inspect(PersistencePackage persistencePackage) throws ServiceException;

    @Secured("PERMISSION_OTHER_DEFAULT")
    public PersistenceResponse fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException;
    
    /**
     * This will throw a {@link ValidationException} and not attempt to swallow them and wrap any other exceptions within
     * a {@link ServiceException} that might have resulted in adding the given package.
     * 
     * @param persistencePackage
     * @return
     * @throws ServiceException
     * @see {@link AdminEntityService#add(org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest)}
     */
    @Secured("PERMISSION_OTHER_DEFAULT")
    public PersistenceResponse add(PersistencePackage persistencePackage) throws ServiceException;
    
    @Secured("PERMISSION_OTHER_DEFAULT")
    public PersistenceResponse update(PersistencePackage persistencePackage) throws ServiceException;
    
    @Secured("PERMISSION_OTHER_DEFAULT")
    public PersistenceResponse remove(PersistencePackage persistencePackage) throws ServiceException;
    
}
