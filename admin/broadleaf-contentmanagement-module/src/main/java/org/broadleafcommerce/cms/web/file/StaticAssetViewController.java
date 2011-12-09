/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.cms.web.file;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.file.service.StaticAssetStorageService;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.service.persistence.SandBoxService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jfischer
 */
@Controller("blStaticAssetViewController")
public class StaticAssetViewController {

    private static final Log LOG = LogFactory.getLog(StaticAssetViewController.class);
    private static final String SANDBOX_ADMIN_ID_VAR = "blAdminCurrentSandboxId";
    private static final String SANDBOX_ID_VAR = "blSandboxId";

    @Resource(name="blStaticAssetStorageService")
    protected StaticAssetStorageService staticAssetStorageService;

    @Resource(name="blSandBoxService")
    protected SandBoxService sandBoxService;

    @RequestMapping(value = "/**/{fileName}", method = {RequestMethod.GET})
    public ModelAndView viewItem(@PathVariable String fileName, HttpServletRequest request) {
        try {
            String fullUrl = "/" + fileName;
            Long sandBoxId = (Long) request.getSession().getAttribute(SANDBOX_ID_VAR);
            if (sandBoxId == null) {
                sandBoxId = (Long) request.getSession().getAttribute(SANDBOX_ADMIN_ID_VAR);
            }
            SandBox sandBox = null;
            if (sandBoxId != null) {
                sandBox = sandBoxService.retrieveSandboxById(sandBoxId);
            }
            Map<String, String> model = staticAssetStorageService.getCacheFileModel(fullUrl, sandBox, convertParameterMap(request.getParameterMap()));

            return new ModelAndView("blStaticAssetView", model);
        } catch (Exception e) {
            LOG.error("Unable to retrieve static asset", e);
            throw new RuntimeException(e);
        }
    }

    protected Map<String, String> convertParameterMap(Map<String, String[]> parameterMap) {
        Map<String, String> convertedMap = new HashMap<String, String>(parameterMap.size());
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            convertedMap.put(entry.getKey(), StringUtils.join(entry.getValue(), ','));
        }

        return convertedMap;
    }

}
