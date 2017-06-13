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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * POJO for useful params dictating {@link FetchWrapper} behavior
 *
 * @author Jeff Fischer
 */
public class FetchExtractionRequest {

    protected CriteriaTransferObject cto;
    protected PersistencePackage persistencePackage;
    protected String ceilingEntity;
    protected Map<String, FieldMetadata> primaryUnfilteredMergedProperties;
    protected List<? extends Serializable> records;
    protected Map<String, FieldMetadata> alternateUnfilteredMergedProperties;
    protected String pathToTargetObject;

    public FetchExtractionRequest(PersistencePackage persistencePackage,
                                CriteriaTransferObject cto,
                                String ceilingEntity,
                                Map<String, FieldMetadata> primaryUnfilteredMergedProperties,
                                List<? extends Serializable> records) {
        this.persistencePackage = persistencePackage;
        this.cto = cto;
        this.ceilingEntity = ceilingEntity;
        this.primaryUnfilteredMergedProperties = primaryUnfilteredMergedProperties;
        this.records = records;
    }

    public FetchExtractionRequest withAlternateMergedProperties(Map<String, FieldMetadata> alternateUnfilteredMergedProperties) {
        this.setAlternateUnfilteredMergedProperties(alternateUnfilteredMergedProperties);
        return this;
    }

    public FetchExtractionRequest withPathToTargetObject(String pathToTargetObject) {
        this.setPathToTargetObject(pathToTargetObject);
        return this;
    }

    public CriteriaTransferObject getCto() {
        return cto;
    }

    public void setCto(CriteriaTransferObject cto) {
        this.cto = cto;
    }

    public Map<String, FieldMetadata> getPrimaryUnfilteredMergedProperties() {
        return primaryUnfilteredMergedProperties;
    }

    public void setPrimaryUnfilteredMergedProperties(Map<String, FieldMetadata> primaryUnfilteredMergedProperties) {
        this.primaryUnfilteredMergedProperties = primaryUnfilteredMergedProperties;
    }

    public List<? extends Serializable> getRecords() {
        return records;
    }

    public void setRecords(List<? extends Serializable> records) {
        this.records = records;
    }

    public Map<String, FieldMetadata> getAlternateUnfilteredMergedProperties() {
        return alternateUnfilteredMergedProperties;
    }

    public void setAlternateUnfilteredMergedProperties(Map<String, FieldMetadata> alternateUnfilteredMergedProperties) {
        this.alternateUnfilteredMergedProperties = alternateUnfilteredMergedProperties;
    }

    public String getPathToTargetObject() {
        return pathToTargetObject;
    }

    public void setPathToTargetObject(String pathToTargetObject) {
        this.pathToTargetObject = pathToTargetObject;
    }

    public PersistencePackage getPersistencePackage() {
        return persistencePackage;
    }

    public void setPersistencePackage(PersistencePackage persistencePackage) {
        this.persistencePackage = persistencePackage;
    }

    public String getCeilingEntity() {
        return ceilingEntity;
    }

    public void setCeilingEntity(String ceilingEntity) {
        this.ceilingEntity = ceilingEntity;
    }
}
