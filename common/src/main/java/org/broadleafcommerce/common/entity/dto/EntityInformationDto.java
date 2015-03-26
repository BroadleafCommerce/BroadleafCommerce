/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.common.entity.dto;

import org.broadleafcommerce.common.entity.service.EntityInformationService;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.extensibility.jpa.copy.ProfileEntity;

/**
 * This class holds information about an entity.
 * 
 * It is populated by a call to {@link EntityInformationService}.    The out-of-box implementation 
 * is a placeholder service.
 * 
 * The enterprise-workflow and multi-tenant modules add functionality to properly populate the dto.
 *    
 * @author bpolster
 *
 */
public class EntityInformationDto {

    private Long profileId;
    private Long catalogId;
    private Long owningSiteId;

    /**
     * For entities that implement {@link ProfileEntity}, returns the value of the profile with which 
     * the entity is associated.    Otherwise, returns null.
     * 
     * @return the profileId
     * 
     */
    public Long getProfileId() {
        return profileId;
    }

    /**
     * Sets the profileId.  Typically called by {@link EntityInformationService} when creating this dto.
     *
     * @param profileId the profileId to set
     * @see EntityInformationDto#getProfileId()
     */
    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    /**
     * For entities that implement {@link DirectCopyTransformTypes.MULTITENANT_CATALOG}, returns the id of the catalog 
     * with which the entity is associated.    Otherwise, returns null.
     * 
     * @return the catalogId
     */
    public Long getCatalogId() {
        return catalogId;
    }

    /**
     * Sets the catalogId.  Typically called by {@link EntityInformationService} when creating this dto.
     *
     * @param catalogId the catalogId to set
     * @see EntityInformationDto#getCatalogId()()
     */
    public void setCatalogId(Long catalogId) {
        this.catalogId = catalogId;
    }

    /**
     * Return the site that owns this record.     If not in a MultiTenant scenario or if the entity is not
     * setup for MultiTenant, this method will return null.
     * @return the siteId
     */
    public Long getOwningSiteId() {
        return owningSiteId;
    }

    /**
     * Sets the siteId that owns this record.
     * @param siteId the siteId to set
     */
    public void setOwningSiteId(Long owningSiteId) {
        this.owningSiteId = owningSiteId;
    }

    /**
     * Returns true if this dto represents a profile entity
     * @return
     */
    public boolean isProfileEntity() {
        return getProfileId() != null;
    }

    /**
     * Returns true if this dto represents a catalog entity
     * @return
     */
    public boolean isCatalogEntity() {
        return getCatalogId() != null;
    }
}
