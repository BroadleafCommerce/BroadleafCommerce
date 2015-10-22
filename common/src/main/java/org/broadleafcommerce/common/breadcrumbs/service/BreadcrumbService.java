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
