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

package org.broadleafcommerce.openadmin.web.service;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.MapMetadata;
import org.broadleafcommerce.openadmin.client.dto.MapStructure;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;

import com.gwtincubator.security.exception.ApplicationSecurityException;

import java.util.Map;

/**
 * @author Andre Azzolini (apazzolini)
 */
public interface FormBuilderService {

    /**
     * Builds a list grid that is typically used at the top entity level to select an entity for modification.
     * 
     * Note that it can also be used in other places that require the same grid as the main entity search screen
     * provided the type on the returned ListGrid is set appropriately.
     * 
     * @param entities
     * @param cmd
     * @param sectionKey
     * @return the ListGrid
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
    public ListGrid buildMainListGrid(DynamicResultSet drs, ClassMetadata cmd, String sectionKey)
            throws ServiceException, ApplicationSecurityException;

    /**
     * Builds a list grid that is used to render a collection inline in an entity form.
     * 
     * Note that it can also be used in other places that require the same grid provided the type on the returned
     * ListGrid is set appropriately. 
     * 
     * @param containingEntityId
     * @param entities
     * @param field
     * @param sectionKey
     * @return the ListGrid
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
    public ListGrid buildCollectionListGrid(String containingEntityId, DynamicResultSet drs, Property field, String sectionKey)
            throws ServiceException, ApplicationSecurityException;

    /**
     * Loops through all of the fields that are specified in given class metadata and removes fields that
     * are not applicable for the given polymorphic entity type from the entity form.
     * 
     * @param cmd
     * @param entityForm
     * @param entityType
     */
    public void removeNonApplicableFields(ClassMetadata cmd, EntityForm entityForm, String entityType);

    /**
     * Builds an EntityForm that has all of the appropriate fields set up without any values.
     * 
     * @param cmd
     * @return the EntityForm
     * @throws ApplicationSecurityException 
     * @throws ServiceException 
     */
    public EntityForm buildEntityForm(ClassMetadata cmd) throws ServiceException, ApplicationSecurityException;

    /**
     * Builds an EntityForm that has all of the appropriate fields set up along with the values for those fields
     * from the given Entity.
     * 
     * @param cmd
     * @param entity
     * @return the EntityForm
     * @throws ApplicationSecurityException 
     * @throws ServiceException 
     */
    public EntityForm buildEntityForm(ClassMetadata cmd, Entity entity) 
            throws ServiceException, ApplicationSecurityException;
    
    /**
     * Builds an EntityForm that has all of the appropriate fields set up along with the values for thsoe fields
     * from the given Entity as well as all sub-collections of the given Entity that appear in the collectionRecords map.
     * 
     * @param cmd
     * @param entity
     * @param collectionRecords
     * @return the EntityForm
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
    public EntityForm buildEntityForm(ClassMetadata cmd, Entity entity, Map<String, DynamicResultSet> collectionRecords)
            throws ServiceException, ApplicationSecurityException;

    /**
     * Sets values for all fields found on the EntityForm from the specified entity.
     * 
     * @param ef
     * @param entity
     */
    public void populateEntityFormFields(EntityForm ef, Entity entity);

    /**
     * Sets values for the necessary adorned fields on the EntityForm from the specified entity.
     * 
     * @param ef
     * @param entity
     * @param adornedList
     */
    public void populateAdornedEntityFormFields(EntityForm ef, Entity entity, AdornedTargetList adornedList);

    /**
     * Sets values for the necessary map fields on the EntityForm from the specified entity.
     * 
     * @param ef
     * @param entity
     */
    public void populateMapEntityFormFields(EntityForm ef, Entity entity);

    /**
     * Copies all values for fields from the destinationForm into the sourceForm.
     * 
     * @param destinationForm
     * @param sourceForm
     */
    public void copyEntityFormValues(EntityForm destinationForm, EntityForm sourceForm);

    /**
     * Builds the EntityForm used in modal dialogs when adding items to adorned target collections.
     * 
     * @param adornedMd
     * @param adornedList
     * @param parentId
     * @return the EntityForm
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
    public EntityForm buildAdornedListForm(AdornedTargetCollectionMetadata adornedMd, AdornedTargetList adornedList,
            String parentId)
            throws ServiceException, ApplicationSecurityException;

    /**
     * Builds the EntityForm used in modal dialogs when adding items to map collections.
     * 
     * @param mapMd
     * @param mapStructure
     * @param cmd
     * @param parentId
     * @return the EntityForm
     * @throws ServiceException
     * @throws ApplicationSecurityException
     */
    public EntityForm buildMapForm(MapMetadata mapMd, MapStructure mapStructure, ClassMetadata cmd, String parentId)
            throws ServiceException, ApplicationSecurityException;

}
