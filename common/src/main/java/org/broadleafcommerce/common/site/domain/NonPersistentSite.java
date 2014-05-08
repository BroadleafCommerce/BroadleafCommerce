/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.site.domain;

import org.broadleafcommerce.common.site.service.type.SiteResolutionType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mimics a normal {@link SiteImpl} but is not attached to any Hibernate session
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class NonPersistentSite extends SiteImpl {
    
    protected Map<String, Object> additionalProperties = new HashMap<String, Object>();
    
    public NonPersistentSite(Site site) {
        if (site == null) {
            throw new IllegalArgumentException("Site cannot be null when instantiating a non-persistent site");
        }
        this.id = site.getId();
        this.name = site.getName();
        this.siteIdentifierType = site.getSiteIdentifierType();
        this.siteIdentifierValue = site.getSiteIdentifierValue();
        
        // Explicitly traverse the catalogs using an iterator. Attempting to use addAll() here requires a session
        // and will be illegal if there is no entity manager bound to the transaction
        for (Catalog catalog : getCatalogs()) {
            Catalog cloneCatalog = new CatalogImpl();
            cloneCatalog.setId(catalog.getId());
            cloneCatalog.setName(catalog.getName());
            this.catalogs.add(cloneCatalog);
        }
        
        this.deactivated = site.isDeactivated();
        this.archiveStatus = site.getArchiveStatus();
    }

    @Override
    public void setId(Long id) {
        throw new IllegalStateException("Cannot modify properties of the non-persistent DTO");
    }

    @Override
    public void setName(String name) {
        throw new IllegalStateException("Cannot modify properties of the non-persistent DTO");
    }

    @Override
    public void setSiteIdentifierType(String siteIdentifierType) {
        throw new IllegalStateException("Cannot modify properties of the non-persistent DTO");
    }

    @Override
    public void setSiteIdentifierValue(String siteIdentifierValue) {
        throw new IllegalStateException("Cannot modify properties of the non-persistent DTO");
    }

    @Override
    public void setSiteResolutionType(SiteResolutionType siteResolutionType) {
        throw new IllegalStateException("Cannot modify properties of the non-persistent DTO");
    }

    @Override
    public List<Catalog> getCatalogs() {
        return Collections.unmodifiableList(catalogs);
    }

    @Override
    public void setCatalogs(List<Catalog> catalogs) {
        throw new IllegalStateException("Cannot modify properties of the non-persistent DTO");
    }

    @Override
    public Site clone() {
        return this;
    }

    @Override
    public void setDeactivated(boolean deactivated) {
        throw new IllegalStateException("Cannot modify properties of the non-persistent DTO");
    }
    
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    
    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
    
    public Object getAdditionalProperty(String propertyName) {
        return additionalProperties.get(propertyName);
    }
    
    public void setAdditionalProperty(String propertyName, Object value) {
        additionalProperties.put(propertyName, value);
    }

}
