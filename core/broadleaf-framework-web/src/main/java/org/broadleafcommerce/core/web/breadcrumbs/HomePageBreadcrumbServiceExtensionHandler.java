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
package org.broadleafcommerce.core.web.breadcrumbs;

import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTO;
import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTOType;
import org.broadleafcommerce.common.breadcrumbs.service.BreadcrumbHandlerDefaultPriorities;
import org.broadleafcommerce.common.breadcrumbs.service.BreadcrumbServiceExtensionManager;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


/**
 * Contributes a breadcrumb (typically the first breadcrumb).    Simply the word home
 * as defined by the property "breadcrumb.homepageText" and the url "/".
 *  
 * @author bpolster
 *
 */
@Service("blHomePageBreadcrumbServiceExtensionHandler")
public class HomePageBreadcrumbServiceExtensionHandler extends AbstractBreadcrumbServiceExtensionHandler {

    @Value("${breadcrumb.homepageText:Home}")
    protected String homePageText;

    @Resource(name = "blBreadcrumbServiceExtensionManager")
    protected BreadcrumbServiceExtensionManager extensionManager;

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }

    @Override
    public ExtensionResultStatusType modifyBreadcrumbList(String url, Map<String, String[]> params,
            ExtensionResultHolder<List<BreadcrumbDTO>> holder) {
        BreadcrumbDTO homePageDto = new BreadcrumbDTO();
        homePageDto.setText(homePageText);
        homePageDto.setLink("/");
        homePageDto.setType(BreadcrumbDTOType.HOME);
        holder.getResult().add(0, homePageDto);
        return ExtensionResultStatusType.HANDLED_CONTINUE;
    }

    @Override
    public int getDefaultPriority() {
        return BreadcrumbHandlerDefaultPriorities.HOME_CRUMB;
    }

}
