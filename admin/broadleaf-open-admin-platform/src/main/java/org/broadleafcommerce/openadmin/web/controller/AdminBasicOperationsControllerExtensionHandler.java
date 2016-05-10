/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.controller;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicOperationsController;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;

import java.util.List;


/**
 * Extension handler for methods present in {@link AdminBasicOperationsController}.
 * 
 * @author ckittrell
 */
public interface AdminBasicOperationsControllerExtensionHandler extends ExtensionHandler {

    /**
     * Invoked every time {@link AdminBasicOperationsController#showSelectCollectionItem()} is invoked to allow the
     * ListGrid style to be built in a different style. For example, Tree ListGrids should be used for Categories.
     * 
     *
     * @param ppr
     * @param cmd
     * @param owningClass
     * @param sectionCrumbs
     * @param model
     * @param requestParams
     * @return ExtensionResultStatusType
     */
    public ExtensionResultStatusType buildLookupListGrid(PersistencePackageRequest ppr, ClassMetadata cmd, String owningClass,
            List<SectionCrumb> sectionCrumbs, Model model, MultiValueMap<String, String> requestParams);
}
