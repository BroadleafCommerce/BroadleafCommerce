/*
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.server.service.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.core.search.dao.SearchFacetDao;
import org.broadleafcommerce.core.search.domain.SearchFacet;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.springframework.stereotype.Component;



import javax.annotation.Resource;


/**
 * When deleting a {@link SearchFacet}, we want to make sure it is a soft delete.
 *
 * @author Nathan Moore (nathandmoore)
 */
@Component("blSearchFacetCustomPersistenceHandler")
public class SearchFacetCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private static final Log LOG = LogFactory.getLog(SearchFacetCustomPersistenceHandler.class);

    @Resource(name = "blSearchFacetDao")
    protected SearchFacetDao searchFacetDao;

    @Override
    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        return persistencePackage.getCeilingEntityFullyQualifiedClassname() != null
                && persistencePackage.getCeilingEntityFullyQualifiedClassname().equals(SearchFacet.class.getName());
    }

    @Override
    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper)
            throws ServiceException {
        Entity entity = persistencePackage.getEntity();

        try {
            Object adminInstance = Class.forName(entity.getType()[0]).newInstance();
            Property idProperty = entity.findProperty("id");
            if (idProperty != null ) {
                Long instanceId = Long.parseLong(idProperty.getValue());
                if (instanceId != null) {
                    adminInstance = dynamicEntityDao.find(adminInstance.getClass(),instanceId);
                }
            }
            if (Status.class.isAssignableFrom(adminInstance.getClass())) {
                ((Status) adminInstance).setArchived('Y');
                searchFacetDao.save((SearchFacet)adminInstance);
                return;
            }

        } catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
            throw new ServiceException("Unable to remove entity for " + entity.getType()[0], e);
        }
    }
}
