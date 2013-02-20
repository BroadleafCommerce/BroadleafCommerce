/*
 * Copyright 2008-2012 the original author or authors.
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

package org.broadleafcommerce.openadmin.server.service;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;

import com.gwtincubator.security.exception.ApplicationSecurityException;

import java.util.Map;

/**
 * @author Andre Azzolini (apazzolini)
 */
public interface AdminEntityService {

    /**
     * Convenience method to return class metadata for a class name
     * 
     * @param className
     * @return ClassMetadata for the given class
     * @throws ServiceException
     * @throws ApplicationSecurityException
     * 
     * @see #getClassMetadata(PersistencePackageRequest)
     */
    public ClassMetadata getClassMetadata(String className)
            throws ServiceException, ApplicationSecurityException;

    /**
     * Returns class metadata for the given request object
     * 
     * @param request
     * @return ClassMetadata for the given request
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
    public ClassMetadata getClassMetadata(PersistencePackageRequest request)
            throws ServiceException, ApplicationSecurityException;

    /**
     * Returns an Entity[] representing the records that were found for the given request.
     * 
     * @param request
     * @return the Entity[]
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
    public Entity[] getRecords(PersistencePackageRequest request)
            throws ServiceException, ApplicationSecurityException;

    /**
     * Returns a specific record for the given className and primary key id
     * 
     * @param className
     * @param id
     * @return the Entity
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
    public Entity getRecord(String className, String id)
            throws ServiceException, ApplicationSecurityException;

    /**
     * Returns the Entity[] representing the records that belong to the specified collectionProperty for the 
     * given containingClass and the primary key for the containingClass
     * 
     * @param containingClassMetadata
     * @param containingEntityId
     * @param collectionProperty
     * @return the Entity[]
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
    public Entity[] getRecordsForCollection(ClassMetadata containingClassMetadata, String containingEntityId,
            Property collectionProperty)
            throws ServiceException, ApplicationSecurityException;

    /**
     * Returns all records for all subcollections of the specified containingClass and its primary key
     * @param containingClassName
     * @param containingEntityId
     * @return all Entity[] for all collections for the specified containingClass
     * @throws ServiceException
     * @throws ApplicationSecurityException
     * 
     * @see #getRecordsForCollection(ClassMetadata, String, Property)
     */
    public Map<String, Entity[]> getRecordsForAllSubCollections(String containingClassName, String containingEntityId)
            throws ServiceException, ApplicationSecurityException;

    public Entity addSubCollectionEntity(EntityForm entityForm, ClassMetadata mainMetadata, Property field, String parentId)
            throws ServiceException, ApplicationSecurityException, ClassNotFoundException;

    /**
     * Updates the given entity
     * 
     * @param entityForm
     * @param className
     * @return the persisted Entity
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
    public Entity updateEntity(EntityForm entityForm, String className)
            throws ServiceException, ApplicationSecurityException;

    public void removeSubCollectionEntity(ClassMetadata mainMetadata, Property field, String parentId, String itemId)
            throws ServiceException, ApplicationSecurityException;


}