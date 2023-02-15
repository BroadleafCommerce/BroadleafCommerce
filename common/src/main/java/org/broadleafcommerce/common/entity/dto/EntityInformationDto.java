/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
    private Long catalogOwner;
    private Boolean isOwnerToCatalogRelationshipActive;

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

    /**
     * Returns the site id of the site that manages the catalog id ({@link #getCatalogId()}), if applicable
     *
     * @return
     */
    public Long getCatalogOwner() {
        return catalogOwner;
    }

    /**
     * Set the site id of the site that manages the catalog id, if applicable
     *
     * @param catalogOwner
     */
    public void setCatalogOwner(Long catalogOwner) {
        this.catalogOwner = catalogOwner;
    }

    /**
     * Retrieve whether or not the relationship to the owned catalog is active for this site
     *
     * @return
     */
    public Boolean getOwnerToCatalogRelationshipActive() {
        return isOwnerToCatalogRelationshipActive;
    }

    /**
     * Set whether or not the relationship to the owned catalog is active for this site
     *
     * @param ownerToCatalogRelationshipActive
     */
    public void setOwnerToCatalogRelationshipActive(Boolean ownerToCatalogRelationshipActive) {
        isOwnerToCatalogRelationshipActive = ownerToCatalogRelationshipActive;
    }
}
