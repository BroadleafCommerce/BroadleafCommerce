/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.openadmin.server.service.persistence.validation;

import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.server.service.persistence.module.BasicPersistenceModule;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;


/**
 * Validates all of the populated properties for entities
 * 
 * @author Phillip Verheyden
 * @see {@link BasicPersistenceModule#createPopulatedInstance(Serializable, Entity, Map, Boolean)}
 */
public interface EntityValidatorService {

    /**
     * Validate the given entity. Implementers should set {@link Entity#setValidationFailure(boolean)} appropriately.
     * Validation is invoked after the entire instance has been populated according to
     * {@link BasicPersistenceModule#createPopulatedInstance(Serializable, Entity, Map, Boolean)}.
     * 
     * @param entity DTO representation of <b>instance</b>
     * @param instance actual domain representation of <b>entity</b>
     * @param propertiesMetadata all of the merged properties metadata for the given {@link Entity}
     * @param recordHelper
     * @param validateUnsubmittedProperties if set to true, will ignore validation for properties that weren't submitted
     *                                      along with the entity
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public void validate(Entity submittedEntity, @Nullable Serializable instance, Map<String, FieldMetadata> propertiesMetadata, 
            RecordHelper recordHelper, boolean validateUnsubmittedProperties);
    /**
     * @return the global validators that will be executed for every {@link Entity}
     */
    public List<GlobalPropertyValidator> getGlobalEntityValidators();

    
    /**
     * <p>Set the global validators that will be run on every entity that is attempted to be saved in the admin. Global
     * validators are useful to operate on things like field types and other scenarios that could occur with a number of
     * entities. Rather than being required to define a {@link ValidationConfiguration} on all of those properties, this
     * can more conveniently validate that set of properties.</p>
     * 
     * <p>An example of a global validator in Broadleaf is the {@link RequiredPropertyValidator} which will ensure that every
     * property that is marked as required will fail validation if a value is unset.</p>
     * @param globalEntityValidators the globalEntityValidators to set
     */
    public void setGlobalEntityValidators(List<GlobalPropertyValidator> globalEntityValidators);

}
