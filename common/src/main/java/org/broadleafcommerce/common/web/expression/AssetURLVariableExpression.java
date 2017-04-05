/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.web.expression;

import org.broadleafcommerce.common.file.service.StaticAssetPathService;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * For HTML fields maintained in the admin, redactor allows the user to select images. These images need to be able to be served from a CDN.
 * Goal is to be able to use this syntax in html pages.
 * Example of trying to  resolve images in longDescription:
 *
 * <div id="description" th:with="input=*{longDescription}">
 *     <span th:utext="${#cms.fixUrl(input)}"></span>
 * </div>
 *
 * @author by reginaldccole
 */
@Component("blAssetURLVariableExpression")
@ConditionalOnTemplating
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
