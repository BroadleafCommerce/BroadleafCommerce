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
