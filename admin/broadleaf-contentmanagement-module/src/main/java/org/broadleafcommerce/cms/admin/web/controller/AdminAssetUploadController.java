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

import org.broadleafcommerce.cms.file.domain.ImageStaticAssetImpl;
import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.file.service.StaticAssetStorageService;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandler;
import org.broadleafcommerce.openadmin.web.controller.AdminAbstractController;
import org.broadleafcommerce.openadmin.web.controller.modal.ModalHeaderType;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid.Type;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AdminAssetUploadController handles uploading or selecting assets.
 *     
 * Used with entities like {@link SkuImpl} and {@link CategoryImpl} that have {@link CustomPersistenceHandler} 
 * configurations that provide support for adding maps of Media objects.
 * 
 * @author Brian Polster (bpolster)
 */
@Controller("blAdminAssetUploadController")
@RequestMapping("/{sectionKey}")
public class AdminAssetUploadController extends AdminAbstractController {

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;
    
    @Resource(name = "blStaticAssetStorageService")
    protected StaticAssetStorageService staticAssetStorageService;
    
    @Resource(name = "blStaticAssetService")
    protected StaticAssetService staticAssetService;
    
    @Resource(name = "blAdminAssetController")
    protected AdminAssetController assetController;

    @RequestMapping(value = "/{id}/chooseAsset", method = RequestMethod.GET)
    public String chooseMediaForMapKey(HttpServletRequest request, HttpServletResponse response, Model model, 
            @PathVariable(value = "sectionKey") String sectionKey, 
            @PathVariable(value = "id") String id,
            @RequestParam MultiValueMap<String, String> requestParams) throws Exception {
        Map<String, String> pathVars = new HashMap<String, String>();
        pathVars.put("sectionKey", AdminAssetController.SECTION_KEY);
        assetController.viewEntityList(request, response, model, pathVars, requestParams);
        
        ListGrid listGrid = (ListGrid) model.asMap().get("listGrid");
        listGrid.setPathOverride("/" + sectionKey + "/" + id + "/chooseAsset");
        listGrid.setListGridType(Type.ASSET);
        listGrid.setSelectType(ListGrid.SelectType.SINGLE_SELECT);

        String userAgent = request.getHeader("User-Agent");
        model.addAttribute("isIE", userAgent.contains("MSIE"));
        
        model.addAttribute("viewType", "modal/selectAsset");
        model.addAttribute("currentUrl", request.getRequestURL().toString());
        model.addAttribute("modalHeaderType", ModalHeaderType.SELECT_ASSET.getType());

        model.addAttribute("currentParams", new ObjectMapper().writeValueAsString(requestParams));
        
        // We need these attributes to be set appropriately here
        model.addAttribute("entityId", id);
        model.addAttribute("sectionKey", sectionKey);
        return "modules/modalContainer";
    }
    
    @RequestMapping(value = "/{id}/uploadAsset", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> upload(HttpServletRequest request,
            @RequestParam("file") MultipartFile file, 
            @PathVariable(value="sectionKey") String sectionKey, @PathVariable(value="id") String id) throws IOException {
        Map<String, Object> responseMap = new HashMap<String, Object>();
        
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("entityType", sectionKey);
        properties.put("entityId", id);
        
        StaticAsset staticAsset = staticAssetService.createStaticAssetFromFile(file, properties);
        staticAssetStorageService.createStaticAssetStorageFromFile(file, staticAsset);

        String staticAssetUrlPrefix = staticAssetService.getStaticAssetUrlPrefix();
        if (staticAssetUrlPrefix != null && !staticAssetUrlPrefix.startsWith("/")) {
            staticAssetUrlPrefix = "/" + staticAssetUrlPrefix;
        }

        String assetUrl =  staticAssetUrlPrefix + staticAsset.getFullUrl();

        responseMap.put("adminDisplayAssetUrl", request.getContextPath() + assetUrl);
        responseMap.put("assetUrl", assetUrl);
        
        if (staticAsset instanceof ImageStaticAssetImpl) {
            responseMap.put("image", Boolean.TRUE);
            responseMap.put("assetThumbnail", assetUrl + "?smallAdminThumbnail");
            responseMap.put("assetLarge", assetUrl + "?largeAdminThumbnail");
        } else {
            responseMap.put("image", Boolean.FALSE);
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "text/html; charset=utf-8");
        return new ResponseEntity<Map<String, Object>>(responseMap, responseHeaders, HttpStatus.OK);
    }

    /**
     * Used by the Asset list view to upload an asset and then immediately show the
     * edit form for that record.
     *
     * @param request
     * @param file
     * @param sectionKey
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/uploadAsset", method = RequestMethod.POST)
    public String upload(HttpServletRequest request, HttpServletResponse response, Model model,
                         @PathVariable Map<String, String> pathVars,
                         @RequestParam("file") MultipartFile file,
                         @RequestParam MultiValueMap<String, String> requestParams) throws Exception {

        StaticAsset staticAsset = staticAssetService.createStaticAssetFromFile(file, null);
        staticAssetStorageService.createStaticAssetStorageFromFile(file, staticAsset);

        String staticAssetUrlPrefix = staticAssetService.getStaticAssetUrlPrefix();
        if (staticAssetUrlPrefix != null && !staticAssetUrlPrefix.startsWith("/")) {
            staticAssetUrlPrefix = "/" + staticAssetUrlPrefix;
        }

        return "redirect:/assets/" + staticAsset.getId();
    }

    @RequestMapping(value = "/{addlSectionKey}/{id}/chooseAsset", method = RequestMethod.GET)
    public String chooseMediaForMapKey(HttpServletRequest request, HttpServletResponse response, Model model,
            @PathVariable(value = "sectionKey") String sectionKey,
            @PathVariable(value = "addlSectionKey") String addlSectionKey,
            @PathVariable(value = "id") String id,
            @RequestParam MultiValueMap<String, String> requestParams) throws Exception {
        return chooseMediaForMapKey(request, response, model, sectionKey, id, requestParams);
    }

    @RequestMapping(value = "/{addlSectionKey}/{id}/uploadAsset",
            method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> upload(HttpServletRequest request,
            @RequestParam("file") MultipartFile file,            
            @PathVariable(value = "sectionKey") String sectionKey,
            @PathVariable(value = "addlSectionKey") String addlSectionKey,
            @PathVariable(value = "id") String id) throws IOException {
        return upload(request, file, sectionKey, id);
    }
}
