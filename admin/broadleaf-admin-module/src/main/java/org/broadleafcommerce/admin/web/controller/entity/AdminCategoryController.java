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

package org.broadleafcommerce.admin.web.controller.entity;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicEntityController;
import org.broadleafcommerce.openadmin.web.form.entity.EntityFormAction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles admin operations for the {@link Category} entity.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Controller("blAdminCategoryController")
@RequestMapping("/" + AdminCategoryController.SECTION_KEY)
public class AdminCategoryController extends AdminBasicEntityController {
    
    protected static final String SECTION_KEY = "category";
    
    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;
    
    @Override
    protected String getSectionKey(Map<String, String> pathVars) {
        //allow external links to work for ToOne items
        if (super.getSectionKey(pathVars) != null) {
            return super.getSectionKey(pathVars);
        }
        return SECTION_KEY;
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewEntityList(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @RequestParam  MultiValueMap<String, String> requestParams) throws Exception {
        super.viewEntityList(request, response, model, pathVars, requestParams);
        
        List<Category> parentCategories = catalogService.findAllParentCategories();
        model.addAttribute("parentCategories", parentCategories);
        
        List<EntityFormAction> mainActions = (List<EntityFormAction>) model.asMap().get("mainActions");
        
        mainActions.add(new EntityFormAction("CategoryTreeView")
            .withButtonClass("show-category-tree-view")
            .withDisplayText("Category_Tree_View"));
        
        mainActions.add(new EntityFormAction("CategoryListView")
            .withButtonClass("show-category-list-view active")
            .withDisplayText("Category_List_View"));
        
        model.addAttribute("viewType", "categoryTree");
        return "modules/defaultContainer";
    }
    
}
