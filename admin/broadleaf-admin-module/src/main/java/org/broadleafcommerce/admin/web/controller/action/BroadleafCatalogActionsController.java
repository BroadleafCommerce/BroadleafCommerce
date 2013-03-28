package org.broadleafcommerce.admin.web.controller.action;

import org.broadleafcommerce.admin.client.service.AdminCatalogService;
import org.broadleafcommerce.admin.web.controller.entity.BroadleafAdminProductController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * Copyright 2008-2013 the original author or authors.
 *
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
 */

/**
 * Controller that responds to custom catalog actions. These would normally be hooked up in customized controllers like 
 * {@link BroadleafAdminProductController}
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link BroadleafAdminProductController}
 */
@Controller("blCatalogActionsController")
public class BroadleafCatalogActionsController {
    
    @Resource(name = "blAdminCatalogService")
    protected AdminCatalogService adminCatalogService;

    /**
     * Invokes a separate service to generate a list of Skus for a particular {@link Product} and that {@link Product}'s
     * Product Options
     * @return
     */
    @RequestMapping(value = "product/{productId}/{skusFieldName}/generate-skus",
                    method = RequestMethod.GET,
                    produces = "application/json")
    public @ResponseBody Map<String, Object> generateSkus(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable Long productId,
            @PathVariable String skusFieldName) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        Integer skusGenerated = adminCatalogService.generateSkusFromProduct(productId);
        
        result.put("skusGenerated", skusGenerated);
        result.put("error", (skusGenerated == -1));
        return result;
    }
}
