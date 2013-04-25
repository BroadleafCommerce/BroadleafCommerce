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

package org.broadleafcommerce.openadmin.server.service.persistence.module;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.cto.FilterCriterionProviders;

import java.io.Serializable;
import java.text.DecimalFormat;
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
 *
 */
public interface RecordHelper {

    public BaseCtoConverter getCtoConverter(PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String ceilingEntityFullyQualifiedClassname, Map<String, FieldMetadata> mergedProperties);

    public BaseCtoConverter getCtoConverter(PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String ceilingEntityFullyQualifiedClassname, Map<String, FieldMetadata> mergedProperties, FilterCriterionProviders criterionProviders);

    public Entity[] getRecords(Map<String, FieldMetadata> primaryMergedProperties, List<? extends Serializable> records, Map<String, FieldMetadata> alternateMergedProperties, String pathToTargetObject);

    public Entity[] getRecords(Map<String, FieldMetadata> primaryMergedProperties, List<? extends Serializable> records);
    
    public Entity[] getRecords(Class<?> ceilingEntityClass, PersistencePerspective persistencePerspective, List<? extends Serializable> records);
    
    public Entity getRecord(Map<String, FieldMetadata> primaryMergedProperties, Serializable record, Map<String, FieldMetadata> alternateMergedProperties, String pathToTargetObject);
    
    public Entity getRecord(Class<?> ceilingEntityClass, PersistencePerspective persistencePerspective, Serializable record);

    public int getTotalRecords(PersistencePackage persistencePackage, CriteriaTransferObject cto, BaseCtoConverter ctoConverter);

    /**
     * Returns the count criteria representation that should be used to count the result set. This is an advanced use case
     * and should only be used when you need to have explicit control over the Hibernate criteria that can be created from
     * the result of this method (like if you are using table aliases in the {@link BaseCtoConverter#getFilterCriterionProviders()}).
     * @param persistencePackage
     * @param cto
     * @param ctoConverter
     * @return
     * @throws ClassNotFoundException
     */
    public PersistentEntityCriteria getCountCriteria(PersistencePackage persistencePackage, CriteriaTransferObject cto, BaseCtoConverter ctoConverter);

    public Serializable createPopulatedInstance(Serializable instance, Entity entity, Map<String, FieldMetadata> mergedProperties, Boolean setId);
    
    public Object getPrimaryKey(Entity entity, Map<String, FieldMetadata> mergedProperties);
    
    public Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective);
    
    public FieldManager getFieldManager();

    public PersistenceModule getCompatibleModule(OperationType operationType);

    public DecimalFormat getDecimalFormatter();
    
    /**
     * Validates the {@link Entity} based on the validators associated with each property
     * @param entity the instance that is attempted to be saved from. Implementers should set {@link Entity#isValidationFailure()}
     * accordingly as a result of the validation
     * @param populatedInstance
     * @param mergedProperties TODO
     * @return whether or not the entity passed validation. This yields the same result as calling !{@link Entity#isValidationFailure()}
     * after invoking this method
     */
    public boolean validate(Entity entity, Serializable populatedInstance, Map<String, FieldMetadata> mergedProperties);
    
}
