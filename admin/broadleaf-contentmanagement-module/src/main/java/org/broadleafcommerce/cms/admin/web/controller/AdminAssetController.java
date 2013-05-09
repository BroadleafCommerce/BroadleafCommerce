/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.admin.web.controller;

import org.broadleafcommerce.cms.admin.web.service.AssetFormBuilderService;
import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicEntityController;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles admin operations for the {@link Asset} entity. This is mostly to support displaying image assets inline 
 * in listgrids.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Controller("blAdminAssetController")
@RequestMapping("/" + AdminAssetController.SECTION_KEY)
public class AdminAssetController extends AdminBasicEntityController {
    
    protected static final String SECTION_KEY = "assets";
    
    @Resource(name = "blAssetFormBuilderService")
    protected AssetFormBuilderService formService;
    
    
    @Override
    protected String getSectionKey(Map<String, String> pathVars) {
        //allow external links to work for ToOne items
        if (super.getSectionKey(pathVars) != null) {
            return super.getSectionKey(pathVars);
        }
        return SECTION_KEY;
    }
    
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewEntityList(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable Map<String, String> pathVars,
            @RequestParam MultiValueMap<String, String> requestParams) throws Exception {
        String returnPath = super.viewEntityList(request, response, model, pathVars, requestParams);
        
        ListGrid listGrid = (ListGrid) model.asMap().get("listGrid");
        formService.addImageThumbnailField(listGrid, "fullUrl");
        
        return returnPath;
    }
    
}
