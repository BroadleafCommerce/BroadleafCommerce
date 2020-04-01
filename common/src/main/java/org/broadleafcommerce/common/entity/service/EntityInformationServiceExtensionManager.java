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
import org.broadleafcommerce.common.extension.ExtensionManager;
import org.broadleafcommerce.common.extension.ExtensionManagerOperation;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Extension manager that holds the list of {@link EntityInformationServiceExtensionHandler}.
 * 
 */
@Service("blEntityInformationServiceExtensionManager")
public class EntityInformationServiceExtensionManager extends ExtensionManager<EntityInformationServiceExtensionHandler> implements EntityInformationServiceExtensionHandler {

    public static final ExtensionManagerOperation getDefaultCatalogIdForSite = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((EntityInformationServiceExtensionHandler) handler).getDefaultCatalogIdForSite((Site) params[0], (ExtensionResultHolder<Long>) params[1]);
        }
    };

    public static final ExtensionManagerOperation updateEntityInformationDto = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((EntityInformationServiceExtensionHandler) handler).updateEntityInformationDto((EntityInformationDto) params[0], params[1]);
        }
    };

    public static final ExtensionManagerOperation updateBasicEntityInformationDto = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((EntityInformationServiceExtensionHandler) handler).updateBasicEntityInformationDto((EntityInformationDto) params[0], params[1]);
        }
    };

    public static final ExtensionManagerOperation getBaseProfileIdForSite = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((EntityInformationServiceExtensionHandler) handler).getBaseProfileIdForSite((Site) params[0], (ExtensionResultHolder<Long>) params[1]);
        }
    };

    public static final ExtensionManagerOperation getTypeForSite = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((EntityInformationServiceExtensionHandler) handler).getTypeForSite((Site) params[0], (ExtensionResultHolder<String>) params[1]);
        }
    };

    public static final ExtensionManagerOperation getParentSiteForProfile = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((EntityInformationServiceExtensionHandler) handler).getParentSiteForProfile((Site) params[0], (ExtensionResultHolder<Site>) params[1]);
        }
    };

    public static final ExtensionManagerOperation getOkayToUseSiteDiscriminator = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((EntityInformationServiceExtensionHandler) handler).getOkayToUseSiteDiscriminator(params[0], (ExtensionResultHolder<Boolean>) params[1]);
        }
    };

    public static final ExtensionManagerOperation findAllCatalogs = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((EntityInformationServiceExtensionHandler) handler).findAllCatalogs((ExtensionResultHolder<List<Catalog>>) params[0]);
        }
    };
    
    public static final ExtensionManagerOperation getChildSiteIdsForProfile = new ExtensionManagerOperation() {
        @Override
        public ExtensionResultStatusType execute(ExtensionHandler handler, Object... params) {
            return ((EntityInformationServiceExtensionHandler) handler).getChildSiteIdsForProfile((Site) params[0], (ExtensionResultHolder<Set<Long>>) params[1]);
        }
    };

    public EntityInformationServiceExtensionManager() {
        super(EntityInformationServiceExtensionHandler.class);
    }

    @Override
    public ExtensionResultStatusType updateEntityInformationDto(EntityInformationDto dto, Object entityInstance) {
        return execute(updateEntityInformationDto, dto, entityInstance);
    }

    @Override
    public ExtensionResultStatusType updateBasicEntityInformationDto(EntityInformationDto dto, Object entityInstance) {
        return execute(updateBasicEntityInformationDto, dto, entityInstance);
    }

    @Override
    public boolean isEnabled() {
        //not used - fulfills interface contract
        return true;
    }

    @Override
    public ExtensionResultStatusType getBaseProfileIdForSite(Site site, ExtensionResultHolder<Long> erh) {
        return execute(getBaseProfileIdForSite, site, erh);
    }

    @Override
    public ExtensionResultStatusType getParentSiteForProfile(Site profile, ExtensionResultHolder<Site> erh) {
        return execute(getParentSiteForProfile, profile, erh);
    }

    @Override
    public ExtensionResultStatusType getOkayToUseSiteDiscriminator(Object o, ExtensionResultHolder<Boolean> erh) {
        return execute(getOkayToUseSiteDiscriminator, o, erh);
    }

    @Override
    public ExtensionResultStatusType findAllCatalogs(ExtensionResultHolder<List<Catalog>> erh) {
        return execute(findAllCatalogs, erh);
    }

    @Override
    public ExtensionResultStatusType getDefaultCatalogIdForSite(Site site, ExtensionResultHolder<Long> erh) {
        return execute(getDefaultCatalogIdForSite, site, erh);
    }

    @Override
    public ExtensionResultStatusType getChildSiteIdsForProfile(Site profile, ExtensionResultHolder<Set<Long>> erh) {
        return execute(getChildSiteIdsForProfile, profile, erh);
    }

    @Override
    public ExtensionResultStatusType getTypeForSite(Site site, ExtensionResultHolder<String> erh) {
        return execute(getTypeForSite, site, erh);
    }
}
