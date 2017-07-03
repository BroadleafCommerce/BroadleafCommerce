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
