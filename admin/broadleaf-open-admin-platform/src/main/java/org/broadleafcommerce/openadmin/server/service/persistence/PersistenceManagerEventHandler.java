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
 * Interface for handling various lifecycle event for the {@link org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager}.
 * These events occur as part of the standard admin persistence lifecycle for entities.
 * <p/>
 * PersistenceManagerEventHandler instances are generally registered via the following approach in application
 * context xml
 * <p/>
 * {@code
 * <bean class="org.broadleafcommerce.common.extensibility.context.merge.LateStageMergeBeanPostProcessor">
 *      <property name="collectionRef" value="blSandBoxPersistenceManagerEventHandlers"/>
 *      <property name="targetRef" value="blPersistenceManagerEventHandlers"/>
 * </bean>
 * <bean id="blSandBoxPersistenceManagerEventHandlers" class="org.springframework.beans.factory.config.ListFactoryBean">
 *      <property name="sourceList">
 *          <list>
 *              <ref bean="blSandBoxPersistenceManagerEventHandler"/>
 *          </list>
*       </property>
 * </bean>
 * }
 *
 * @author Jeff Fischer
 */
public interface PersistenceManagerEventHandler extends Ordered {

    /**
     * Called prior to inspection for the entity described by persistencePackage
     *
     * @param persistenceManager the PersistenceManager instance making the call
     * @param persistencePackage the descriptive information for the call
     * @return the response containing any changes, status or additional data
     * @throws ServiceException
     */
    PersistenceManagerEventHandlerResponse preInspect(PersistenceManager persistenceManager, PersistencePackage persistencePackage) throws ServiceException;

    /**
     * Called after the inspection for the entity described by persistencePackage
     *
     * @param persistenceManager the PersistenceManager instance making the call
     * @param resultSet the inspection result data
     * @param persistencePackage the descriptive information for the call
     * @return the response containing any changes, status or additional data
     * @throws ServiceException
     */
    PersistenceManagerEventHandlerResponse postInspect(PersistenceManager persistenceManager, DynamicResultSet resultSet, PersistencePackage persistencePackage) throws ServiceException;

    /**
     * Called prior to a fetch, which is a request for one or more persisted entities
     *
     * @param persistenceManager the PersistenceManager instance making the call
     * @param persistencePackage the descriptive information for the call
     * @param cto the criteria describing the parameters of the fetch - converted into the where clause for the select query
     * @return the response containing any changes, status or additional data
     * @throws ServiceException
     */
    PersistenceManagerEventHandlerResponse preFetch(PersistenceManager persistenceManager, PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException;

    /**
     * Called after the fetch, which is a request for one or more persisted entities
     *
     * @param persistenceManager the PersistenceManager instance making the call
     * @param resultSet the fetch result data
     * @param persistencePackage the descriptive information for the call
     * @param cto the criteria describing the parameters of the fetch - converted into the where clause for the select query
     * @return the response containing any changes, status or additional data
     * @throws ServiceException
     */
    PersistenceManagerEventHandlerResponse postFetch(PersistenceManager persistenceManager, DynamicResultSet resultSet, PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException;

    /**
     * Called prior to an add
     *
     * @param persistenceManager the PersistenceManager instance making the call
     * @param persistencePackage the descriptive information for the call
     * @return the response containing any changes, status or additional data
     * @throws ServiceException
     */
    PersistenceManagerEventHandlerResponse preAdd(PersistenceManager persistenceManager, PersistencePackage persistencePackage) throws ServiceException;

    /**
     * Called after an add
     *
     * @param persistenceManager the PersistenceManager instance making the call
     * @param entity the result of the add
     * @param persistencePackage the descriptive information for the call
     * @return the response containing any changes, status or additional data
     * @throws ServiceException
     */
    PersistenceManagerEventHandlerResponse postAdd(PersistenceManager persistenceManager, Entity entity, PersistencePackage persistencePackage) throws ServiceException;

    /**
     * Called prior to an update
     *
     * @param persistenceManager the PersistenceManager instance making the call
     * @param persistencePackage the descriptive information for the call
     * @return the response containing any changes, status or additional data
     * @throws ServiceException
     */
    PersistenceManagerEventHandlerResponse preUpdate(PersistenceManager persistenceManager, PersistencePackage persistencePackage) throws ServiceException;

    /**
     * Called after an update
     *
     * @param persistenceManager the PersistenceManager instance making the call
     * @param entity the result of the update
     * @param persistencePackage the descriptive information for the call
     * @return the response containing any changes, status or additional data
     * @throws ServiceException
     */
    PersistenceManagerEventHandlerResponse postUpdate(PersistenceManager persistenceManager, Entity entity, PersistencePackage persistencePackage) throws ServiceException;

    /**
     * Called prior to a remove
     *
     * @param persistenceManager the PersistenceManager instance making the call
     * @param persistencePackage the descriptive information for the call
     * @return the response containing any changes, status or additional data
     * @throws ServiceException
     */
    PersistenceManagerEventHandlerResponse preRemove(PersistenceManager persistenceManager, PersistencePackage persistencePackage) throws ServiceException;

    /**
     * Called after a remove
     *
     * @param persistenceManager the PersistenceManager instance making the call
     * @param persistencePackage the descriptive information for the call
     * @return the response containing any changes, status or additional data
     * @throws ServiceException
     */
    PersistenceManagerEventHandlerResponse postRemove(PersistenceManager persistenceManager, PersistencePackage persistencePackage) throws ServiceException;

    /**
     * Called after a validation error. Validations occur on adds, updates and removes. The validation confirms the persistence request is
     * correct and does not have any errors. This event handling hook provides an opportunity to impact and/or modify
     * the results of validation errors. Errors are generally reviewed in this method by looking at
     * {@link org.broadleafcommerce.openadmin.dto.Entity#getValidationErrors()}
     *
     * @param persistenceManager the PersistenceManager instance making the call
     * @param entity the results of the persistence request
     * @param persistencePackage the descriptive information for the call
     * @return the response containing any changes, status or additional data
     * @throws ServiceException
     */
    PersistenceManagerEventHandlerResponse processValidationError(PersistenceManager persistenceManager, Entity entity, PersistencePackage persistencePackage) throws ServiceException;
}
