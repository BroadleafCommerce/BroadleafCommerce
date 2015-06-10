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
package org.broadleafcommerce.openadmin.web.service;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.openadmin.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.MapMetadata;
import org.broadleafcommerce.openadmin.dto.MapStructure;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.springframework.validation.BindingResult;

import java.util.List;
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
     * @param drs
     * @param cmd
     * @param sectionKey
     * @return the ListGrid
     * @throws ServiceException
     */
    public ListGrid buildMainListGrid(DynamicResultSet drs, ClassMetadata cmd, String sectionKey, List<SectionCrumb> sectionCrumbs)
            throws ServiceException;

    /**
     * Builds a list grid that is used to render a collection inline in an entity form.
     * 
     * Note that it can also be used in other places that require the same grid provided the type on the returned
     * ListGrid is set appropriately. 
     * 
     * @param containingEntityId
     * @param drs
     * @param field
     * @param sectionKey
     * @return the ListGrid
     * @throws ServiceException
     */
    public ListGrid buildCollectionListGrid(String containingEntityId, DynamicResultSet drs, Property field, String sectionKey, List<SectionCrumb> sectionCrumbs)
            throws ServiceException;

    /**
     *
     * @param containingEntityId
     * @param drs
     * @param field
     * @param sectionKey
     * @return the ListGrid
     * @throws ServiceException
     */
    public List<Map<String, String>> buildSelectizeCollectionOptions(String containingEntityId, DynamicResultSet drs, Property field, String sectionKey, List<SectionCrumb> sectionCrumbs)
            throws ServiceException;

    /**
     * Extracts the DefaultValue from the FieldMetaData and parses it based on the
     * {@link org.broadleafcommerce.common.presentation.client.SupportedFieldType} that the field uses.
     *
     * Logs a warning in the event of failure to parse the value and returns null.
     *
     * @param fieldType
     * @param fmd
     * @return The value to be used on the form field.
     */
    public String extractDefaultValueFromFieldData(String fieldType, BasicFieldMetadata fmd);

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
     * Creates a new EntityForm with the a default 'Save' action. This will then delegate to
     * {@link #populateEntityForm(ClassMetadata, EntityForm)} to ensure that the newly created {@link EntityForm}
     * has all of the appropriate fields set up without any values based on <b>cmd</b>
     * 
     * @param cmd
     * @return the EntityForm
     * @throws ServiceException
     * @see {@link #populateEntityForm(ClassMetadata, EntityForm)}
     */
    public EntityForm createEntityForm(ClassMetadata cmd, List<SectionCrumb> sectionCrumbs) throws ServiceException;

    /**
     * Populates the given <b>ef</b> with all of the fields based on the properties from <b>cmd</b>. For all the fields that
     * are created, no values are set (as <b>cmd</b> usually does not have any). In order to fill out values in the given
     * <b>ef</b>, consider instead calling {@link #populateEntityForm(ClassMetadata, Entity, EntityForm, boolean)}
     * 
     * @param cmd
     * @param ef
     * @throws ServiceException
     */
    public void populateEntityForm(ClassMetadata cmd, EntityForm ef, List<SectionCrumb> sectionCrumbs) throws ServiceException;
    
    /**
     * Creates a new EntityForm that has all of the appropriate fields set up along with the values for those fields
     * from the given Entity. Delegates to {@link #createEntityForm(ClassMetadata)} for further population
     * 
     * @param cmd metadata that the created {@link EntityForm} should use to initialize its fields
     * @param entity
     * @return the EntityForm
     * @throws ServiceException
     * @see {@link #createEntityForm(ClassMetadata)}
     */
    public EntityForm createEntityForm(ClassMetadata cmd, Entity entity, List<SectionCrumb> sectionCrumbs)
            throws ServiceException;
    
    /**
     * Populates a given <b>ef</b> based on the given <b>cmd</b> to initially create fields with the necessary metadata
     * and then fills those fields out based on the property values from <b>entity</b>.
     * 
     * @param cmd
     * @param entity
     * @param ef
     * @throws ServiceException
     * @see {@link #populateEntityForm(ClassMetadata, EntityForm)}
     */
    public void populateEntityForm(ClassMetadata cmd, Entity entity, EntityForm ef, List<SectionCrumb> sectionCrumbs)
            throws ServiceException;

    /**
     * Populates the given {@link EntityForm} with values based on the {@link Entity} that has been passed in. The 
     * {@link ClassMetadata} is used to determine which properties should be attempted to be populated
     * 
     * @param cmd 'inspect' metadata for the class being populated
     * @param entity the {@link Entity} that should be used to fill out the field values in the given {@link EntityForm}
     * @param ef the {@link EntityForm} to populate field values from the given {@link Entity}
     */
    public void populateEntityFormFieldValues(ClassMetadata cmd, Entity entity, EntityForm ef);
    
    /**
     * Builds an EntityForm that has all of the appropriate fields set up along with the values for those fields
     * from the given Entity as well as all sub-collections of the given Entity that appear in the collectionRecords map.
     * This method simply delegates to create a standard {@link EntityForm} (that has a save action) and then populates
     * that {@link EntityForm} using {@link #populateEntityForm(ClassMetadata, Entity, Map, EntityForm)}.
     * 
     * NOTE: if you are submitting a validation result, you must not call this method and instead invoke the one that has
     * an {@link EntityForm} as a parameter. You cannot re-assign the entityForm to the model after it has already been
     * bound to a BindingResult, else the binding result will be removed.
     * 
     * @param cmd
     * @param entity
     * @param collectionRecords
     * @return the EntityForm
     * @throws ServiceException
     * @see {@link #populateEntityForm(ClassMetadata, Entity, Map, EntityForm)}
     */
    public EntityForm createEntityForm(ClassMetadata cmd, Entity entity, Map<String, DynamicResultSet> collectionRecords, List<SectionCrumb> sectionCrumbs)
            throws ServiceException;

    /**
     * Builds an EntityForm that has all of the appropriate fields set up along with the values for thsoe fields
     * from the given Entity as well as all sub-collections of the given Entity that appear in the collectionRecords map.
     * 
     * NOTE: This method is mainly used when coming back from validation. In the case of validation, you cannot re-add a new
     * {@link EntityForm} to the model or else you lose the whole {@link BindingResult} and errors will not properly be 
     * displayed. In that scenario, you must use this method rather than the one that does not take in an entityForm as it
     * will attempt to instantiate a new object.
     * 
     * @param cmd
     * @param entity
     * @param collectionRecords
     * @param entityForm rather than instantiate a new EntityForm, this will use this parameter to fill out
     * @return the EntityForm
     * @throws ServiceException
     */
    public void populateEntityForm(ClassMetadata cmd, Entity entity, Map<String, DynamicResultSet> collectionRecords, EntityForm entityForm, List<SectionCrumb> sectionCrumbs)
            throws ServiceException;
    

    /**
     * Delegates to {@link #populateEntityFormFields(EntityForm, Entity, boolean, boolean)} with true for populating both
     * the id and type.
     * 
     * @see {@link #populateEntityFormFields(EntityForm, Entity, boolean, boolean)}
     * @param ef
     * @param entity
     */
    public void populateEntityFormFields(EntityForm ef, Entity entity);
    
    /**
     * Sets values for all fields found on the EntityForm from the specified entity.
     * 
     * @param ef
     * @param entity
     * @param populateType whether or not to use the type from the given {@link Entity} or keep the current value on
     * the {@link EntityForm}
     * @param populateId whether or not to use the id from the given {@link Entity} or keep the current value on
     * the {@link EntityForm}
     */
    public void populateEntityFormFields(EntityForm ef, Entity entity, boolean populateType, boolean populateId);

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
     * Builds the EntityForm used in modal dialogs when adding items to adorned target collections.
     * 
     * @param adornedMd
     * @param adornedList
     * @param parentId
     * @return the EntityForm
     * @throws ServiceException
     */
    public EntityForm buildAdornedListForm(AdornedTargetCollectionMetadata adornedMd, AdornedTargetList adornedList,
            String parentId)
            throws ServiceException;

    /**
     * Equivalent to {@link #buildAdornedListForm(AdornedTargetCollectionMetadata, AdornedTargetList, String)} except rather than creating a
     * new {@link EntityForm} this simply uses the {@link EntityForm} that was passed in as <b>ef</b>. Used mainly when
     * rebuilding an {@link EntityForm} after it has already been bound by Spring.
     * 
     * Before invoking this method, you should invoke {@link EntityForm#clearFieldsMap()} to ensure that you have a clean
     * set of field groups and tabs for this method to work with
     * 
     * @param mapMd
     * @param mapStructure
     * @param cmd
     * @param parentId
     * @param ef the form DTO to populate
     * @return the original {@link EntityForm} passed in but fully populated
     * @throws ServiceException
     */
    public EntityForm buildAdornedListForm(AdornedTargetCollectionMetadata adornedMd, AdornedTargetList adornedList,
            String parentId, EntityForm ef)
            throws ServiceException;

    /**
     * Builds the EntityForm used in modal dialogs when adding items to map collections.
     * 
     * @param mapMd
     * @param mapStructure
     * @param cmd
     * @param parentId
     * @return the EntityForm
     * @throws ServiceException
     */
    public EntityForm buildMapForm(MapMetadata mapMd, MapStructure mapStructure, ClassMetadata cmd, String parentId)
            throws ServiceException;

    /**
     * Equivalent to {@link #buildMapForm(MapMetadata, MapStructure, ClassMetadata, String)} except rather than creating a
     * new {@link EntityForm} this simply uses the {@link EntityForm} that was passed in as <b>ef</b>. Used mainly when
     * rebuilding an {@link EntityForm} after it has already been bound by Spring.
     * 
     * Before invoking this method, you should invoke {@link EntityForm#clearFieldsMap()} to ensure that you have a clean
     * set of field groups and tabs for this method to work with
     * 
     * @param mapMd
     * @param mapStructure
     * @param cmd
     * @param parentId
     * @param ef the form DTO to populate
     * @return the original {@link EntityForm} passed in but fully populated
     * @throws ServiceException
     */
    public EntityForm buildMapForm(MapMetadata mapMd, final MapStructure mapStructure, ClassMetadata cmd, String parentId, EntityForm ef)
            throws ServiceException;
}
