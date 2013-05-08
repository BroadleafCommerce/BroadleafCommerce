/*
 * Copyright 2008-2012 the original author or authors.
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

package org.broadleafcommerce.cms.admin.web.controller;

import org.broadleafcommerce.cms.file.domain.ImageStaticAssetImpl;
import org.broadleafcommerce.cms.file.domain.StaticAsset;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.cms.file.service.StaticAssetStorageService;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandler;
import org.broadleafcommerce.openadmin.web.controller.AdminAbstractController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @RequestMapping(value = "/{id}/chooseAsset", method = RequestMethod.GET)
    public String chooseMediaForMapKey(Model model, @PathVariable String sectionKey, @PathVariable String id) {
        model.addAttribute("viewType", "modal/assetSelection");
        model.addAttribute("modalHeaderType", "assetSelection");
        model.addAttribute("entityId", id);
        model.addAttribute("sectionKey", sectionKey);
        model.addAttribute("notFoundImage", "");
        return "modules/modalContainer";
    }
    
    @RequestMapping(value = "/{id}/uploadAsset", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public @ResponseBody Map<String, Object> upload(HttpServletRequest request,
            @RequestParam("file") MultipartFile file, 
            @PathVariable String sectionKey, @PathVariable String id) throws IOException {
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

        String assetUrl = request.getSession().getServletContext().getContextPath() + staticAssetUrlPrefix +
                staticAsset.getFullUrl();

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

}
