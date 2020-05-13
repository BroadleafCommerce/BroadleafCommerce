/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.entity.service;

import org.broadleafcommerce.common.entity.dto.EntityInformationDto;
import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * ExtensionHandler for methods within {@link EntityInformationService}
 * 
 * <p>
 * Rather than implementing this interface directly you should extend your implementation from
 * {@link AbstractEntityInformationServiceExtensionHandler}.
 * 
 * <p>
 * Intended to be used by enterprise and multi-tenant modules to populate a dto so that other 
 * modules can have easy access to this information w/out a formal dependency on the multi-tenant or
 * enterprise modules.
 * 
 * @author bpolster
 * @see {@link AbstractEntityInformationServiceExtensionHandler}
 */
public interface EntityInformationServiceExtensionHandler extends ExtensionHandler {

    /**
     * Handler implementations will override or populate the values on the passed in dto.
     * 
     * @param dto {@link EntityInformationDto} the dto to add values to
     * @param entityInstance the object to be examined
     * @see {@link EntityInformationServiceImpl#buildEntityInformationForObject(Object)}
     */
    ExtensionResultStatusType updateEntityInformationDto(EntityInformationDto dto, Object entityInstance);

    ExtensionResultStatusType updateBasicEntityInformationDto(EntityInformationDto dto, Object entityInstance);

    /**
     * Handler implementations will populate the {@link ExtensionResultHolder} with a valid 
     * base profile id if one exists for the site
     * 
     * @param site {@link Site} the Site to check for a base profile
     * @param erh {@link ExtensionResultHolder} a container for the result     
     */
    ExtensionResultStatusType getBaseProfileIdForSite(Site site, ExtensionResultHolder<Long> erh);

    ExtensionResultStatusType getTypeForSite(Site site, ExtensionResultHolder<String> erh);

    /**
     * Handler implementations will populate the {@link ExtensionResultHolder} with a valid
     * child site ids whose base profile is the given profile
     *
     * @param profile {@link Site} the profile to check for the child sites
     * @param erh a container for the result
     */
    ExtensionResultStatusType getChildSiteIdsForProfile(Site profile, ExtensionResultHolder<Set<Long>> erh);

    /**
     * Handler implementations will popoulate the {@link ExtensionResultHolder} with a valid
     * parent site if one exists for the profile
     *
     * @param profile {@link Site} the profile to check for the parent site
     * @param erh {@link ExtensionResultHolder} a container for the result
     */
    ExtensionResultStatusType getParentSiteForProfile(Site profile, ExtensionResultHolder<Site> erh);

    /**
     * Handler implementations will set the value of {@link ExtensionResultHolder} to true if the
     * passed in object supports site discriminator usage.   For example, when running in a Multi-Tenant
     * Broadleaf implementation.
     * 
     * @param o
     * @return
     */
    ExtensionResultStatusType getOkayToUseSiteDiscriminator(Object o, ExtensionResultHolder<Boolean> erh);

    /**
     * Handler implementations will set the value of {@link ExtensionResultHolder} to a list of all catalogs,
     * specifically when running in a Multi-Tenant Broadleaf implementation.
     *
     * @param erh
     * @return
     */
    ExtensionResultStatusType findAllCatalogs(ExtensionResultHolder<List<Catalog>> erh);

    /**
     * Handler implementations will populate the {@link ExtensionResultHolder} with a valid default catalog id for the site
     *
     * @param site {@link Site} the Site to get the catalog id from
     * @param erh {@link ExtensionResultHolder} a container for the result
     */
    ExtensionResultStatusType getDefaultCatalogIdForSite(Site site, ExtensionResultHolder<Long> erh);
}
