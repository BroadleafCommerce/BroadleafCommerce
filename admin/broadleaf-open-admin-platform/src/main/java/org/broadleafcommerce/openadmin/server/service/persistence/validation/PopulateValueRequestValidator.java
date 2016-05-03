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

import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.server.service.persistence.module.BasicPersistenceModule;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.FieldPersistenceProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request.PopulateValueRequest;
import org.springframework.core.Ordered;

import java.io.Serializable;


/**
 * <p>
 * This is injected into the {@link BasicPersistenceModule} and invoked prior to any attempts to actually populate values
 * from the {@link Entity} DTO representation into the Hibernate entity using the {@link FieldPersistenceProvider} paradigm.
 * </p>
 * <p>
 * An example validator would ensure that Booleans are actually booleans, integers are actually integers, etc. since all
 * values come in as Strings by default
 * </p>
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link FieldPersistenceProvider}
 * @see {@link PopulateValueRequest}
 * @see {@link BasicPersistenceModule#createPopulatedInstance(Serializable, Entity, java.util.Map, Boolean)}
 */
public interface PopulateValueRequestValidator extends Ordered {
    
    /**
     * Validates a population request prior to invoking any {@link FieldPersistenceProvider}s. If no validation could be
     * performed for the given {@link PopulateValueRequest} then return <b>true</b> to let it pass on to a different
     * {@link PopulateValueRequestValidator} or on to a {@link FieldPersistenceProvider}.
     * 
     * @param populateValueRequest the {@link PopulateValueRequest} that should be validated
     * @param instance the Hibernate entity that will attempt to be populated
     * @return false if the {@link PopulateValueRequest} failed validation. In this case, the request should not be passed
     * to any {@link FieldPersistenceProvider}s.
     */
    public PropertyValidationResult validate(PopulateValueRequest populateValueRequest, Serializable instance);
    
}
