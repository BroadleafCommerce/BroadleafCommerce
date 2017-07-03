/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
                fetchExtractionRequest.getPathToTargetObject());
    }
}
