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
package org.broadleafcommerce.openadmin.server.service.persistence;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.springframework.core.Ordered;

/**
 * @author Jeff Fischer
 */
public interface PersistenceManagerEventHandler extends Ordered {

    PersistenceManagerEventHandlerResponse preInspect(PersistenceManager persistenceManager, PersistencePackage persistencePackage) throws ServiceException;

    PersistenceManagerEventHandlerResponse postInspect(PersistenceManager persistenceManager, DynamicResultSet resultSet, PersistencePackage persistencePackage) throws ServiceException;

    PersistenceManagerEventHandlerResponse preFetch(PersistenceManager persistenceManager, PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException;

    PersistenceManagerEventHandlerResponse postFetch(PersistenceManager persistenceManager, DynamicResultSet resultSet, PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException;

    PersistenceManagerEventHandlerResponse preAdd(PersistenceManager persistenceManager, PersistencePackage persistencePackage) throws ServiceException;

    PersistenceManagerEventHandlerResponse postAdd(PersistenceManager persistenceManager, Entity entity, PersistencePackage persistencePackage) throws ServiceException;

    PersistenceManagerEventHandlerResponse preUpdate(PersistenceManager persistenceManager, PersistencePackage persistencePackage) throws ServiceException;

    PersistenceManagerEventHandlerResponse postUpdate(PersistenceManager persistenceManager, Entity entity, PersistencePackage persistencePackage) throws ServiceException;

    PersistenceManagerEventHandlerResponse preRemove(PersistenceManager persistenceManager, PersistencePackage persistencePackage) throws ServiceException;

    PersistenceManagerEventHandlerResponse postRemove(PersistenceManager persistenceManager, PersistencePackage persistencePackage) throws ServiceException;

    PersistenceManagerEventHandlerResponse processValidationError(PersistenceManager persistenceManager, Entity entity, PersistencePackage persistencePackage) throws ServiceException;
}
