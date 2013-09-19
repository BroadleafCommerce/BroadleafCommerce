/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.admin.web.controller;

import org.broadleafcommerce.cms.file.domain.ImageStaticAssetImpl;
import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.file.service.StaticAssetStorageService;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandler;
import org.broadleafcommerce.openadmin.web.controller.AdminAbstractController;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid.Type;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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
        
        model.addAttribute("viewType", "modal/selectAsset");
        model.addAttribute("currentUrl", request.getRequestURL().toString());
        model.addAttribute("modalHeaderType", "selectAsset");

        model.addAttribute("currentParams", new ObjectMapper().writeValueAsString(requestParams));
        
        // We need these attributes to be set appropriately here
        model.addAttribute("entityId", id);
        model.addAttribute("sectionKey", sectionKey);
        return "modules/modalContainer";
    }
    
    @RequestMapping(value = "/{id}/uploadAsset", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public @ResponseBody Map<String, Object> upload(HttpServletRequest request,
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

        return responseMap;
    }
    
    @RequestMapping(value = "/uploadAsset", method = RequestMethod.POST)
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
    public String upload(HttpServletRequest request,
            @RequestParam("file") MultipartFile file,
            @PathVariable(value="sectionKey") String sectionKey) throws IOException {
        
        StaticAsset staticAsset = staticAssetService.createStaticAssetFromFile(file, null);
        staticAssetStorageService.createStaticAssetStorageFromFile(file, staticAsset);

        String staticAssetUrlPrefix = staticAssetService.getStaticAssetUrlPrefix();
        if (staticAssetUrlPrefix != null && !staticAssetUrlPrefix.startsWith("/")) {
            staticAssetUrlPrefix = "/" + staticAssetUrlPrefix;
        }
        
        return "redirect:/assets/" + staticAsset.getId();
    }    

}
