/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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

package org.broadleafcommerce.openadmin.web.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.service.AdminEntityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @see SearchFieldResolver
 * @author Andre Azzolini (apazzolini)
 */
@Service("blSearchFieldResolver")
public class SearchFieldResolverImpl implements SearchFieldResolver {
    protected static final Log LOG = LogFactory.getLog(SearchFieldResolverImpl.class);

    @Resource(name = "blAdminEntityService")
    protected AdminEntityService service;
    
    @Override
    public String resolveField(String className) throws ServiceException {
        PersistencePackageRequest ppr = PersistencePackageRequest.standard()
                .withCeilingEntityClassname(className);
        ClassMetadata md = service.getClassMetadata(ppr).getDynamicResultSet().getClassMetaData();
        
        if (md.getPMap().containsKey("name")) {
            return "name";
        }

        if (md.getPMap().containsKey("friendlyName")) {
            return "friendlyName";
        }

        if (md.getPMap().containsKey("templateName")) {
            return "templateName";
        }
        
        return "id";
    }


}
