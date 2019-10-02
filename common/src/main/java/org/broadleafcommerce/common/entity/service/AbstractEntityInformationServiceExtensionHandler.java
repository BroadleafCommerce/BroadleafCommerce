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
import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;

import java.util.List;
import java.util.Set;

public class AbstractEntityInformationServiceExtensionHandler extends AbstractExtensionHandler
        implements EntityInformationServiceExtensionHandler {

    @Override
    public ExtensionResultStatusType updateEntityInformationDto(EntityInformationDto dto, Object entityInstance) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType updateBasicEntityInformationDto(EntityInformationDto dto, Object entityInstance) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType getBaseProfileIdForSite(Site site, ExtensionResultHolder<Long> erh) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType getTypeForSite(Site site, ExtensionResultHolder<String> erh) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType getChildSiteIdsForProfile(Site profile, ExtensionResultHolder<Set<Long>> erh) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType getParentSiteForProfile(Site profile, ExtensionResultHolder<Site> erh) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType getOkayToUseSiteDiscriminator(Object o, ExtensionResultHolder<Boolean> erh) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override public ExtensionResultStatusType getDefaultCatalogIdForSite(Site site, ExtensionResultHolder<Long> erh) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType findAllCatalogs(ExtensionResultHolder<List<Catalog>> erh) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
}
