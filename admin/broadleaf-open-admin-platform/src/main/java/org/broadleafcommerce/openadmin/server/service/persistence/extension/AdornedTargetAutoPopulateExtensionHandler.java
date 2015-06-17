/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.service.persistence.extension;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;

import java.io.Serializable;
import java.util.Map;

/**
 * Extension handler for methods present in {@link org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicEntityController}.
 *
 * @author Jeff Fischer
 */
public interface AdornedTargetAutoPopulateExtensionHandler extends ExtensionHandler {

    /**
     * Provides a hook for automatically setting the values for one or more adorned target collection managed fields. This can
     * allow, through code, partial or total completion of the form in the second tab on an adorned target add interaction.
     * Note, a special key/value pair can be included put into the managedField map to cause the second tab to be skipped
     * and the adorned target item add form to auto submit after completing the first tab.
     *
     * @param md the metadata describing the adorned target collection field
     * @param mainClassName the class name of the entity that contains this adorned target field
     * @param id the id of the containing entity
     * @param collectionField the name of the adorned target field
     * @param collectionItemId the id of the adorned target collection member
     * @param managedFields the map containing the adorned target field values that should be auto populated
     * @return the final status of the operation
     */
    public ExtensionResultStatusType autoSetAdornedTargetManagedFields(FieldMetadata md, String mainClassName,
                                   String id, String collectionField, String collectionItemId, Map<String, Object> managedFields);

    /**
     * Provide validation during persistence. The implementation is responsible for determining suitability based
     * on the property information passed in.
     *
     * @param entity all the values passed from the client for persistence
     * @param instance the entity instance that is being populated
     * @param entityFieldMetadata the {@link FieldMetadata} for all the fields
     * @param propertyMetadata the {@link FieldMetadata} for the property
     * @param propertyName the name of the field
     * @param value the value being assigned to the property
     * @param validationResult whether or not the property passes validation
     * @return the final status of the operation
     */
    public ExtensionResultStatusType validateSubmittedAdornedTargetManagedFields(Entity entity, Serializable instance,
                                Map<String, FieldMetadata> entityFieldMetadata, BasicFieldMetadata propertyMetadata,
                                String propertyName, String value, ExtensionResultHolder<Boolean> validationResult);
}
