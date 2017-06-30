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
package org.broadleafcommerce.openadmin.server.service.persistence.module;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.EntityResult;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.service.ValidationException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.RestrictionFactory;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.EntityValidatorService;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * Helper interface for serializing/deserializing the generic {@link Entity} DTO to/from its actual domain object
 * representation. 
 * 
 * @author jfischer
 * @see {@link BasicPersistenceModule}
 * @see {@link MapStructurePersistenceModule}
 * @see {@link AdornedTargetListPersistenceModule}
 */
public interface RecordHelper extends DataFormatProvider {

    List<FilterMapping> getFilterMappings(PersistencePerspective persistencePerspective, CriteriaTransferObject cto,
                                                 String ceilingEntityFullyQualifiedClassname,
                                                 Map<String, FieldMetadata> mergedProperties);

    List<FilterMapping> getFilterMappings(PersistencePerspective persistencePerspective, CriteriaTransferObject cto,
                                                     String ceilingEntityFullyQualifiedClassname,
                                                     Map<String, FieldMetadata> mergedUnfilteredProperties,
                                                     RestrictionFactory customRestrictionFactory);

    /**
     * Based on retrieved persistent entities and entity metadata, construct data transfer object instances to represent these records
     * to the caller.
     *
     * @param fetchExtractionRequest
     * @return
     */
    Entity[] getRecords(FetchExtractionRequest fetchExtractionRequest);

    /**
     * @deprecated use {@link #getRecords(FetchExtractionRequest)} instead
     * @param primaryMergedProperties
     * @param records
     * @param alternateMergedProperties
     * @param pathToTargetObject
     * @return
     */
    Entity[] getRecords(Map<String, FieldMetadata> primaryMergedProperties, List<? extends Serializable> records, Map<String, FieldMetadata> alternateMergedProperties, String pathToTargetObject);

    /**
     * @deprecated use {@link #getRecords(FetchExtractionRequest)} instead.
     * @param primaryMergedProperties
     * @param records
     * @return
     */
    @Deprecated
    Entity[] getRecords(Map<String, FieldMetadata> primaryMergedProperties, List<? extends Serializable> records);
    
    Entity[] getRecords(Class<?> ceilingEntityClass, PersistencePerspective persistencePerspective, List<? extends Serializable> records);
    
    Entity getRecord(Map<String, FieldMetadata> primaryMergedProperties, Serializable record, Map<String, FieldMetadata> alternateMergedProperties, String pathToTargetObject);
    
    Entity getRecord(Class<?> ceilingEntityClass, PersistencePerspective persistencePerspective, Serializable record);

    /**
     * <p>Populates a Hibernate entity <b>instance</b> based on the values from <b>entity</b> (the DTO representation of
     * <b>instance</b>) and the metadata from <b>mergedProperties</b>.</p>
     * <p>While populating <b>instance</b>, validation is also performed using the {@link EntityValidatorService}. If this
     * validation fails, then the instance is left unchanged and a {@link ValidationException} is thrown. In the common
     * case, this exception bubbles up to the {@link DynamicRemoteService} which catches the exception and communicates
     * appropriately to the invoker</p>
     * 
     * @param instance
     * @param entity
     * @param mergedProperties
     * @param setId
     * @param validateUnsubmittedProperties if set to true, will ignore validation for properties that weren't submitted
     *                                      along with the entity
     * @throws ValidationException if after populating <b>instance</b> via the values in <b>entity</b> then
     * {@link EntityValidatorService#validate(Entity, Serializable, Map)} returns false
     * @return <b>instance</b> populated with the property values from <b>entity</b> according to the metadata specified
     * in <b>mergedProperties</b>
     * @see {@link EntityValidatorService}
     */
    Serializable createPopulatedInstance(Serializable instance, Entity entity,
            Map<String, FieldMetadata> mergedProperties, Boolean setId, Boolean validateUnsubmittedProperties) throws ValidationException;

    /**
     * Delegates to the overloaded method with validateUnsubmittedProperties set to true.
     * 
     * @see #createPopulatedInstance(Serializable, Entity, Map, Boolean, Boolean)
     */
    Serializable createPopulatedInstance(Serializable instance, Entity entity,
            Map<String, FieldMetadata> unfilteredProperties, Boolean setId) throws ValidationException;
    
    Object getPrimaryKey(Entity entity, Map<String, FieldMetadata> mergedProperties);

    /**
     * For the fully qualified entity class name, find the primary key property name.
     *
     * @param entityClass
     * @return
     */
    String getIdPropertyName(String entityClass);
    
    Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective);
    
    FieldManager getFieldManager();

    PersistenceModule getCompatibleModule(OperationType operationType);

    /**
     * Validates the {@link Entity} based on the validators associated with each property
     * @param entity the instance that is attempted to be saved from. Implementers should set {@link Entity#isValidationFailure()}
     * accordingly as a result of the validation
     * @param populatedInstance
     * @param mergedProperties TODO
     * @param validateUnsubmittedProperties if set to true, will ignore validation for properties that weren't submitted
     *                                      along with the entity
     * @return whether or not the entity passed validation. This yields the same result as calling !{@link Entity#isValidationFailure()}
     * after invoking this method
     */
    boolean validate(Entity entity, Serializable populatedInstance, Map<String, FieldMetadata> mergedProperties, boolean validateUnsubmittedProperties);

    /**
     * Delegates to the overloaded method with validateUnsubmittedProperties set to true.
     * 
     * @see #validate(Entity, Serializable, Map, boolean)
     */
    boolean validate(Entity entity, Serializable populatedInstance, Map<String, FieldMetadata> mergedProperties);

    /**
     * Retrieve a total count of persistent entities given some basic metadata and restrictions
     *
     * @deprecated use {@link #getTotalRecords(FetchRequest)} instead
     * @param ceilingEntity
     * @param filterMappings
     * @return
     */
    @Deprecated
    Integer getTotalRecords(String ceilingEntity, List<FilterMapping> filterMappings);

    /**
     * Retrieve a total count of persistent entities given some basic metadata and restrictions
     *
     * @param fetchRequest
     * @return
     */
    Integer getTotalRecords(FetchRequest fetchRequest);

    Serializable getMaxValue(String ceilingEntity, List<FilterMapping> filterMappings, String maxField);

    /**
     * Retrieve a paged list of persistent entity instances given some basic metadata and restrictions.
     *
     * @deprecated use {@link #getPersistentRecords(FetchRequest)} instead
     * @param ceilingEntity
     * @param filterMappings
     * @param firstResult
     * @param maxResults
     * @return
     */
    @Deprecated
    List<Serializable> getPersistentRecords(String ceilingEntity, List<FilterMapping> filterMappings, Integer firstResult, Integer maxResults);

    /**
     * Retrieve a paged list of persistent entity instances given some basic metadata and restrictions.
     *
     * @param fetchRequest
     * @return
     */
    List<Serializable> getPersistentRecords(FetchRequest fetchRequest);

    EntityResult update(PersistencePackage persistencePackage, boolean includeRealEntityObject) throws ServiceException;

    EntityResult add(PersistencePackage persistencePackage, boolean includeRealEntityObject) throws ServiceException;

    /**
     * Returns a string representation of the field on the given instance specified by the property name. The propertyName
     * should start from the root of the given instance
     * 
     * @param instance
     * @param propertyName
     * @return
     */
    String getStringValueFromGetter(Serializable instance, String propertyName)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException;

}
