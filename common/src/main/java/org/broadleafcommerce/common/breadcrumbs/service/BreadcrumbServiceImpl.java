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
package org.broadleafcommerce.common.breadcrumbs.service;

import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTO;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

@Service("blBreadcrumbService")
public class BreadcrumbServiceImpl implements BreadcrumbService {

    @Resource(name = "blBreadcrumbServiceExtensionManager")
    protected BreadcrumbServiceExtensionManager extensionManager;
    
    public List<BreadcrumbDTO> buildBreadcrumbDTOs(String baseUrl, Map<String, String[]> params) {
        List<BreadcrumbDTO> dtos = new ArrayList<BreadcrumbDTO>();

        if (extensionManager != null) {
            ExtensionResultHolder<List<BreadcrumbDTO>> holder = new ExtensionResultHolder<List<BreadcrumbDTO>>();
            holder.setResult(dtos);
            extensionManager.getProxy().modifyBreadcrumbList(baseUrl, params, holder);
            dtos = holder.getResult();
        }
        return dtos;
    }
}
