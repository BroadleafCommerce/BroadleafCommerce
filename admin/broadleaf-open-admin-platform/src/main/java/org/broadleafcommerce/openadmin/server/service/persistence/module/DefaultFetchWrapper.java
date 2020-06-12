/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.service.persistence.module;

import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManagerFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * Default pass-through implementation of {@link FetchWrapper}
 *
 * @author Jeff Fischer
 */
@Component("blFetchWrapper")
public class DefaultFetchWrapper implements FetchWrapper {

    @Override
    public List<Serializable> getPersistentRecords(FetchRequest fetchRequest) {
        return getBasicPersistenceModule().getPersistentRecords(fetchRequest.getCeilingEntity(),
                            fetchRequest.getFilterMappings(), fetchRequest.getCto().getFirstResult(), fetchRequest.getCto().getMaxResults());
    }

    @Override
    public Integer getTotalRecords(FetchRequest fetchRequest) {
        return getBasicPersistenceModule().getTotalRecords(fetchRequest.getCeilingEntity(), fetchRequest.getFilterMappings());
    }

    protected BasicPersistenceModule getBasicPersistenceModule() {
        PersistenceManager persistenceManager = PersistenceManagerFactory.getPersistenceManager();
        BasicPersistenceModule basicPersistenceModule = (BasicPersistenceModule) ((InspectHelper) persistenceManager).getCompatibleModule(OperationType.BASIC);
        return basicPersistenceModule;
    }

    @Override
    public Entity[] getRecords(FetchExtractionRequest fetchExtractionRequest) {
        return getBasicPersistenceModule().getRecords(fetchExtractionRequest.getPrimaryUnfilteredMergedProperties(),
                fetchExtractionRequest.getRecords(), fetchExtractionRequest.getAlternateUnfilteredMergedProperties(),
                fetchExtractionRequest.getPathToTargetObject(), fetchExtractionRequest.getPersistencePackage().getCustomCriteria());
    }
}
