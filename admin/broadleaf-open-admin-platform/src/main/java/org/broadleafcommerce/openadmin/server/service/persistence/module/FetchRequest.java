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

import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping;

import java.util.List;
import java.util.Map;

/**
 * POJO for useful params dictating {@link FetchWrapper} behavior
 *
 * @author Jeff Fischer
 */
public class FetchRequest {

    protected PersistencePackage persistencePackage;
    protected CriteriaTransferObject cto;
    protected String ceilingEntity;
    protected List<FilterMapping> filterMappings;

    public FetchRequest(PersistencePackage persistencePackage,
                        CriteriaTransferObject cto,
                        String ceilingEntity,
                        List<FilterMapping> filterMappings) {
        this.persistencePackage = persistencePackage;
        this.cto = cto;
        this.ceilingEntity = ceilingEntity;
        this.filterMappings = filterMappings;
    }

    /**
     * Object describing the overall fetch request
     *
     * @return
     */
    public PersistencePackage getPersistencePackage() {
        return persistencePackage;
    }

    public void setPersistencePackage(PersistencePackage persistencePackage) {
        this.persistencePackage = persistencePackage;
    }

    /**
     * Object describing query filters and sorting. Generally passed from the client UI.
     *
     * @return
     */
    public CriteriaTransferObject getCto() {
        return cto;
    }

    public void setCto(CriteriaTransferObject cto) {
        this.cto = cto;
    }

    /**
     * The top entity in the inheritance hierarchy.
     *
     * @return
     */
    public String getCeilingEntity() {
        return ceilingEntity;
    }

    public void setCeilingEntity(String ceilingEntity) {
        this.ceilingEntity = ceilingEntity;
    }

    /**
     * The various fetch query filter restrictions
     *
     * @return
     */
    public List<FilterMapping> getFilterMappings() {
        return filterMappings;
    }

    public void setFilterMappings(List<FilterMapping> filterMappings) {
        this.filterMappings = filterMappings;
    }

}
