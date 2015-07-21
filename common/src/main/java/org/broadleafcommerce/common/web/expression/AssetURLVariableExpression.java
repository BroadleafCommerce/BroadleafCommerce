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
package org.broadleafcommerce.common.web.expression;

import org.broadleafcommerce.common.file.service.StaticAssetPathService;
import org.broadleafcommerce.common.web.BroadleafRequestContext;

import javax.annotation.Resource;

/**
 * For HTML fields maintained in the admin, redactor allows the user to select images. These images need to be able to be served from a CDN.
 * Goal is to be able to use this syntax in html pages.
 * Example of trying to  resolve images in longDescription:
 *
 * <div th:utext="${#cms.fixUrl('__*{longDescription}__')}" id="description"></div>
 *
 * @author by reginaldccole
 */
public class AssetURLVariableExpression implements BroadleafVariableExpression {

    @Resource(name="blStaticAssetPathService")
    protected StaticAssetPathService staticAssetPathService;


    @Override
    public String getName() {
        return "cms";
    }


    /**
     * This method will resolve image urls located in HTML.
     * @see StaticAssetPathService#convertAllAssetPathsInContent(String, boolean)
     * @param content
     * @return
     */
    public String fixUrl(String content){
        boolean isSecure = false;
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc != null) {
             isSecure  = brc.getRequest().isSecure();
        }
        return staticAssetPathService.convertAllAssetPathsInContent(content,isSecure);
    }

}
