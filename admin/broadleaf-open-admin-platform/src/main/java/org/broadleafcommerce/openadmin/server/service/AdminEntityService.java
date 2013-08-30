/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.service;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;

import java.util.Map;

/**
 * @author Andre Azzolini (apazzolini)
 */
public interface AdminEntityService {

    /**
     * Returns class metadata for the given request object
     * 
     * @param request
     * @return ClassMetadata for the given request
     * @throws ServiceException
     */
    public ClassMetadata getClassMetadata(PersistencePackageRequest request)
            throws ServiceException;

    /**
     * Returns the DynamicResultSet containing the total records for the query and the currently fetched Entity[]
     * 
     * @param request
     * @return DynamicResultSet 
     * @throws ServiceException
     */
    public DynamicResultSet getRecords(PersistencePackageRequest request)
            throws ServiceException;

    /**
     * Returns a specific record for the given request and primary key id/property
     * 
     * @param request
     * @param id
     * @param cmd
     * @param isCollectionRequest whether or not this record request was initiated from a collection on a parent entity
     * @return the Entity
     * @throws ServiceException
     */
    public Entity getRecord(PersistencePackageRequest request, String id, ClassMetadata cmd, boolean isCollectionRequest)
            throws ServiceException;

    /**
     * Updates the given entity along with all of the dynamic forms associated with this entity. If there is a validation
     * problem when saving the entity (or any of the dynamic forms), this method will throw a {@link ValidationException}
     * in order to rollback the transaction.
     *  
     * @param entityForm
     * @param customCriteria
     * @return the persisted Entity
     * @throws ServiceException
     */
    public Entity addEntity(EntityForm entityForm, String[] customCriteria)
            throws ServiceException;

    /**
     * Updates the given entity along with all of the dynamic forms associated with this entity. If there is a validation
     * problem when saving the entity (or any of the dynamic forms), this method will throw a {@link ValidationException}
     * in order to rollback the transaction.
     * 
     * @param entityForm
     * @param customCriteria
     * @return the persisted Entity
     * @throws ServiceException
     */
    public Entity updateEntity(EntityForm entityForm, String[] customCriteria)
            throws ServiceException;

    /**
     * Removes the given entity
     * 
     * @param entityForm
     * @param customCriteria
     * @throws ServiceException
     */
    public void removeEntity(EntityForm entityForm, String[] customCriteria)
            throws ServiceException;

    /**
     * Gets an Entity representing a specific collection item
     * 
     * @param containingClassMetadata
     * @param containingEntity
     * @param collectionProperty
     * @param collectionItemId
     * @return the Entity
     * @throws ServiceException
     */
    public Entity getAdvancedCollectionRecord(ClassMetadata containingClassMetadata, Entity containingEntity,
            Property collectionProperty, String collectionItemId)
            throws ServiceException;

    /**
     * Returns the DynamicResultSet representing the records that belong to the specified collectionProperty for the 
     * given containingClass and the primary key for the containingClass
     * 
     * @param containingClassMetadata
     * @param containingEntity
     * @param collectionProperty
     * @param fascs
     * @param startIndex
     * @param maxIndex
     * @return the DynamicResultSet
     * @throws ServiceException
     */
    public DynamicResultSet getRecordsForCollection(ClassMetadata containingClassMetadata, Entity containingEntity, 
            Property collectionProperty, FilterAndSortCriteria[] fascs, Integer startIndex, Integer maxIndex)
            throws ServiceException;
    
    /**
     * The same as the other getRecordsForCollection method, except that this one expects allow the caller to explicitly
     * set the id value that will be used in the fetch instead of delegating to {@link #getContextSpecificRelationshipId()}
     * 
     * @param containingClassMetadata
     * @param containingEntity
     * @param collectionProperty
     * @param fascs
     * @param startIndex
     * @param maxIndex
     * @param idValueOverride
     * @return the DynamicResultSet
     * @throws ServiceException
     */
    public DynamicResultSet getRecordsForCollection(ClassMetadata containingClassMetadata, Entity containingEntity, 
            Property collectionProperty, FilterAndSortCriteria[] fascs, Integer startIndex, Integer maxIndex, 
            String idValueOverride) throws ServiceException;
    /**
     * Returns all records for all subcollections of the specified request and its primary key
     * 
     * @param ppr
     * @param containingEntity
     * @return all Entity[] for all collections for the specified containingClass
     * @throws ServiceException
     * 
     * @see #getRecordsForCollection(ClassMetadata, String, Property)
     */
    public Map<String, DynamicResultSet> getRecordsForAllSubCollections(PersistencePackageRequest ppr, 
            Entity containingEntity)
            throws ServiceException;

    /**
     * Adds an item into the specified collection
     * 
     * @param entityForm
     * @param mainMetadata
     * @param field
     * @param parentEntity
     * @return the persisted Entity
     * @throws ServiceException
     * @throws ClassNotFoundException
     */
    public Entity addSubCollectionEntity(EntityForm entityForm, ClassMetadata mainMetadata, Property field, 
            Entity parentEntity)
            throws ServiceException, ClassNotFoundException;

    /**
     * Updates the specified collection item
     * 
     * @param entityForm
     * @param mainMetadata
     * @param field
     * @param parentEntity
     * @param collectionItemId
     * @return the persisted Entity
     * @throws ServiceException
     * @throws ClassNotFoundException
     */
    public Entity updateSubCollectionEntity(EntityForm entityForm, ClassMetadata mainMetadata, Property field,
            Entity parentEntity, String collectionItemId)
            throws ServiceException, ClassNotFoundException;

    /**
     * Removes the given item from the specified collection.
     * 
     * @param mainMetadata
     * @param field
     * @param parentId
     * @param itemId
     * @param priorKey - only needed for Map type collections
     * @throws ServiceException
     */
    public void removeSubCollectionEntity(ClassMetadata mainMetadata, Property field, Entity parentEntity, String itemId,
            String priorKey)
            throws ServiceException;

    /**
     * Returns the appropriate id to use for the given entity/metadata and prefix when dealing with collections. For
     * example, on the Product screen, we display associated media. However, this media is actually owned by the Sku entity,
     * which means its property name is "defaultSku.skuMedia". In this case, when wanting to look up media for this product,
     * we cannot use the id of the product. Instead, we need to use the id of the sku.
     * 
     * @param cmd
     * @param entity
     * @param propertyName
     * @return the id to be used for this relationship
     */
    public String getContextSpecificRelationshipId(ClassMetadata cmd, Entity entity, String propertyName);

    /**
     * Returns the name of the property in this ClassMetadata that has field type set to {@link SupportedFieldType#ID}
     * 
     * @param cmd
     * @return the id property name
     * @throws ServiceException
     */
    public String getIdProperty(ClassMetadata cmd) throws ServiceException;


}