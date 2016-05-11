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

import java.util.List;
import java.util.Map;

public interface BreadcrumbService {

    /**
     * Builds a breadcrumb from the passed in url and parameter list.   By default, this method simply delegates its logic
     * to the {@link BreadcrumbServiceExtensionManager} which will invoke {@link BreadcrumbServiceExtensionHandler}'s 
     * in priority order to build the Breadcrumbs
     * 
     * @param baseUrl
     * @param params
     * @return
     */
    List<BreadcrumbDTO> buildBreadcrumbDTOs(String baseUrl, Map<String, String[]> params);
}
