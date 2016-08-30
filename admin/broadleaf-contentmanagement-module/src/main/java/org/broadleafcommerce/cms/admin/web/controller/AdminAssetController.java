/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.admin.web.controller;

import org.broadleafcommerce.cms.admin.web.service.AssetFormBuilderService;
import org.broadleafcommerce.cms.file.StaticAssetMultiTenantExtensionManager;
import org.broadleafcommerce.cms.file.domain.StaticAssetImpl;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.file.service.StaticAssetStorageService;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicEntityController;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.EntityFormAction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Handles admin operations for the {@link Asset} entity. This is mostly to support displaying image assets inline 
 * in listgrids.
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Controller("blAdminAssetController")
@RequestMapping("/" + AdminAssetController.SECTION_KEY)
public class AdminAssetController extends AdminBasicEntityController {
    
    public static final String SECTION_KEY = "assets";
    
    @Resource(name = "blAssetFormBuilderService")
    protected AssetFormBuilderService formService;
    
    @Resource(name = "blStaticAssetService")
    protected StaticAssetService staticAssetService;

    @Resource(name = "blStaticAssetStorageService")
    protected StaticAssetStorageService staticAssetStorageService;

    @Resource(name = "blStaticAssetMultiTenantExtensionManager")
    protected StaticAssetMultiTenantExtensionManager staticAssetExtensionManager;
    
    @Override
    protected String getSectionKey(Map<String, String> pathVars) {
        //allow external links to work for ToOne items
        if (super.getSectionKey(pathVars) != null) {
            return super.getSectionKey(pathVars);
        }
        return SECTION_KEY;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String viewEntityList(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @RequestParam  MultiValueMap<String, String> requestParams) throws Exception {
        String returnPath = super.viewEntityList(request, response, model, pathVars, requestParams);
        
        // Remove the default add button and replace it with an upload asset button
        List<EntityFormAction> mainActions = (List<EntityFormAction>) model.asMap().get("mainActions");
        Iterator<EntityFormAction> actions = mainActions.iterator();
        while (actions.hasNext()) {
            EntityFormAction action = actions.next();
            if (EntityFormAction.ADD.equals(action.getId())) {
                actions.remove();
                break;
            }
        }
        mainActions.add(0, new EntityFormAction("UPLOAD_ASSET")
                .withButtonClass("upload-asset")
                .withIconClass("icon-camera")
                .withDisplayText("Upload_Asset"));

        // Change the listGrid view to one that has a hidden form for uploading the image.
        model.addAttribute("viewType", "entityListWithUploadForm");
        
        ListGrid listGrid = (ListGrid) model.asMap().get("listGrid");
        formService.addImageThumbnailField(listGrid, "fullUrl");

        return returnPath;
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id) throws Exception {
        Site currentSite = BroadleafRequestContext.getBroadleafRequestContext().getNonPersistentSite();

        model.addAttribute("cmsUrlPrefix", staticAssetService.getStaticAssetUrlPrefix());
        String returnPath = super.viewEntityForm(request, response, model, pathVars, id);

        staticAssetExtensionManager.getProxy().removeShareOptionsForMTStandardSite(model, currentSite);

        return returnPath;
    }
    
    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String saveEntity(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable  Map<String, String> pathVars,
            @PathVariable(value="id") String id,
            @ModelAttribute(value="entityForm") EntityForm entityForm, BindingResult result,
            RedirectAttributes ra) throws Exception {
        String templatePath = super.saveEntity(request, response, model, pathVars, id, entityForm, result, ra);

        if (result.hasErrors()) {
            model.addAttribute("cmsUrlPrefix", staticAssetService.getStaticAssetUrlPrefix());
        }
        
        return templatePath;
    }

    @Override
    protected String getDefaultEntityType() {
        return StaticAssetImpl.class.getName();
    }

}
