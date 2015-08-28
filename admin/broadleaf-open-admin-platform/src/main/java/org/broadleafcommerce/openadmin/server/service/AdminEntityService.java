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

import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceResponse;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;

import java.util.List;
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
    public PersistenceResponse getClassMetadata(PersistencePackageRequest request)
            throws ServiceException;

    /**
     * Returns the DynamicResultSet containing the total records for the query and the currently fetched Entity[]
     * 
     * @param request
     * @return DynamicResultSet 
     * @throws ServiceException
     */
    public PersistenceResponse getRecords(PersistencePackageRequest request)
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
    public PersistenceResponse getRecord(PersistencePackageRequest request, String id, ClassMetadata cmd, boolean isCollectionRequest)
            throws ServiceException;

    /**
     * Persists the given entity
     * 
     * @param entityForm
     * @param customCriteria
     * @return the persisted Entity
     * @throws ServiceException
     */
    public PersistenceResponse addEntity(EntityForm entityForm, String[] customCriteria, List<SectionCrumb> sectionCrumb)
            throws ServiceException;

    /**
     * Updates the given entity
     * 
     * @param entityForm
     * @param customCriteria
     * @return the persisted Entity
     * @throws ServiceException
     */
    public PersistenceResponse updateEntity(EntityForm entityForm, String[] customCriteria, List<SectionCrumb> sectionCrumb)
            throws ServiceException;

    /**
     * Removes the given entity
     * 
     * @param entityForm
     * @param customCriteria
     * @throws ServiceException
     */
    public PersistenceResponse removeEntity(EntityForm entityForm, String[] customCriteria, List<SectionCrumb> sectionCrumb)
            throws ServiceException;

    /**
     * Gets the PersistencePackageRequest for the passed in EntityForm
     *
     * @param entityForm
     * @param customCriteria
     * @param sectionCrumbs
     */
    public PersistencePackageRequest getRequestForEntityForm(EntityForm entityForm, String[] customCriteria, List<SectionCrumb> sectionCrumbs);

    /**
     * Thin layer on top of {@link DynamicEntityService#add(org.broadleafcommerce.openadmin.dto.PersistencePackage)} that
     * swallows all {@link ValidationException}s that could be thrown and still just returns a {@link PersistenceResponse}
     * with the {@link Entity} that failed validation.
     *
     * @param request
     * @return
     * @throws ServiceException if there were exceptions other than a {@link ValidationException} that was thrown as a
     * result of the attempted add
     */
    public PersistenceResponse add(PersistencePackageRequest request) throws ServiceException;
    
    /**
     * Works the same as {@link #add(PersistencePackageRequest)} but you can optionally invoke the transactional version
     * of {@link DynamicEntityRemoteService#add(org.broadleafcommerce.openadmin.dto.PersistencePackage)} in situations
     * where you want to manage the transactions in a parent component
     * 
     * @param request
     * @param transactional
     * @return
     * @throws ServiceException
     */
    public PersistenceResponse add(PersistencePackageRequest request, boolean transactional) throws ServiceException;
    
    /**
     * Thin layer on top of {@link DynamicEntityService#update(org.broadleafcommerce.openadmin.dto.PersistencePackage)}
     * @param request
     * @return
     * @throws ServiceException if there were exceptions other than a {@link ValidationException} that was thrown as a
     * result of the attempted update
     */
    public PersistenceResponse update(PersistencePackageRequest request) throws ServiceException;
    
    /**
     * Works the same as {@link #update(PersistencePackageRequest)} but you can optionally invoke the transactional version
     * of {@link DynamicEntityRemoteService#update(org.broadleafcommerce.openadmin.dto.PersistencePackage)} in situations
     * where you want to manage the transactions in a parent component
     * 
     * @param request
     * @param transactional
     * @return
     * @throws ServiceException
     */
    public PersistenceResponse update(PersistencePackageRequest request, boolean transactional) throws ServiceException;

    /**
     * Thin layer on top of {@link DynamicEntityService#inspect(org.broadleafcommerce.openadmin.dto.PersistencePackage)}
     * @param request
     * @return
     * @throws ServiceException
     */
    public PersistenceResponse inspect(PersistencePackageRequest request) throws ServiceException;

    /**
     * Thin layer on top of {@link DynamicEntityService#remove(org.broadleafcommerce.openadmin.dto.PersistencePackage)}
     * @param request
     * @return
     * @throws ServiceException
     */
    public PersistenceResponse remove(PersistencePackageRequest request) throws ServiceException;
    
    /**
     * Works the same as {@link #remove(PersistencePackageRequest)} but you can optionally invoke the transactional version
     * of {@link DynamicEntityRemoteService#remove(org.broadleafcommerce.openadmin.dto.PersistencePackage)} in situations
     * where you want to manage the transactions in a parent component
     * 
     * @param request
     * @param transactional
     * @return
     * @throws ServiceException
     */
    public PersistenceResponse remove(PersistencePackageRequest request, boolean transactional) throws ServiceException;

    /**
     * Thin layer on top of {@link DynamicEntityService#fetch(org.broadleafcommerce.openadmin.dto.PersistencePackage, org.broadleafcommerce.openadmin.dto.CriteriaTransferObject)}.
     * This will glean and create a {@link CriteriaTransferObject} from {@link PersistencePackageRequest#getFilterAndSortCriteria()}
     * to pass to {@link DynamicEntityService}.
     * 
     * @param request
     * @return
     * @throws ServiceException
     */
    public PersistenceResponse fetch(PersistencePackageRequest request) throws ServiceException;

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
    public PersistenceResponse getAdvancedCollectionRecord(ClassMetadata containingClassMetadata, Entity containingEntity,
            Property collectionProperty, String collectionItemId, List<SectionCrumb> sectionCrumb, String alternateId)
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
    public PersistenceResponse getRecordsForCollection(ClassMetadata containingClassMetadata, Entity containingEntity,
            Property collectionProperty, FilterAndSortCriteria[] fascs, Integer startIndex, Integer maxIndex, List<SectionCrumb> sectionCrumb)
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
     * @return the PersistenceResponse
     * @throws ServiceException
     */
    public PersistenceResponse getRecordsForCollection(ClassMetadata containingClassMetadata, Entity containingEntity,
            Property collectionProperty, FilterAndSortCriteria[] fascs, Integer startIndex, Integer maxIndex, 
            String idValueOverride, List<SectionCrumb> sectionCrumb) throws ServiceException;
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
            Entity containingEntity, List<SectionCrumb> sectionCrumb)
            throws ServiceException;

    /**
     * overloading containing paging parameters
     * @param ppr
     * @param containingEntity
     * @param startIndex
     * @param maxIndex
     * @param sectionCrumb
     * @return
     * @throws ServiceException
     */
    public Map<String, DynamicResultSet> getRecordsForAllSubCollections(PersistencePackageRequest ppr,
            Entity containingEntity, Integer startIndex, Integer maxIndex, List<SectionCrumb> sectionCrumb)
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
    public PersistenceResponse addSubCollectionEntity(EntityForm entityForm, ClassMetadata mainMetadata, Property field,
            Entity parentEntity, List<SectionCrumb> sectionCrumb)
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
    public PersistenceResponse updateSubCollectionEntity(EntityForm entityForm, ClassMetadata mainMetadata, Property field,
            Entity parentEntity, String collectionItemId, List<SectionCrumb> sectionCrumb)
            throws ServiceException, ClassNotFoundException;

    /**
     * Updates the specified collection item
     *
     * @param entityForm
     * @param mainMetadata
     * @param field
     * @param parentEntity
     * @param collectionItemId
     * @param alternateId
     * @return the persisted Entity
     * @throws ServiceException
     * @throws ClassNotFoundException
     */
    public PersistenceResponse updateSubCollectionEntity(EntityForm entityForm, ClassMetadata mainMetadata, Property field,
            Entity parentEntity, String collectionItemId, String alternateId, List<SectionCrumb> sectionCrumb)
            throws ServiceException, ClassNotFoundException;

    /**
     * Removes the given item from the specified collection.
     * 
     * @param mainMetadata
     * @param field
     * @param parentEntity
     * @param itemId
     * @param priorKey - only needed for Map type collections
     * @throws ServiceException
     */
    public PersistenceResponse removeSubCollectionEntity(ClassMetadata mainMetadata, Property field, Entity parentEntity, String itemId,
            String priorKey, List<SectionCrumb> sectionCrumb)
            throws ServiceException;

    /**
     * Removes the given item from the specified collection.
     *
     * @param mainMetadata
     * @param field
     * @param parentEntity
     * @param itemId
     * @param alternateId
     * @param priorKey - only needed for Map type collections
     * @throws ServiceException
     */
    public PersistenceResponse removeSubCollectionEntity(ClassMetadata mainMetadata, Property field, Entity parentEntity,
            String itemId, String alternateId, String priorKey, List<SectionCrumb> sectionCrumb)
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

    /**
     * For the given class (which could be an interface) and id, finds the {@link AdminMainEntity} value for the 
     * foreign entity
     * 
     * @param owningClass
     * @param id
     * @return the friendly name for the given foreign entity
     */
    public String getForeignEntityName(String owningClass, String id);

    /**
     * Returns all records for selected tab of the specified request and its primary key
     * 
     * @param cmd
     * @param containingEntity
     * @return all Entity[] for selected tab for the specified containingClass
     * @throws ServiceException
     * 
     * @see #getRecordsForCollection(ClassMetadata, String, Property)
     */
    public Map<String, DynamicResultSet> getRecordsForSelectedTab(ClassMetadata cmd , Entity containingEntity,
            List<SectionCrumb> sectionCrumb, String currentTabName)
            throws ServiceException;
}
