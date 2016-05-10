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

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;

import java.util.List;


/**
 * Abstract implementation of {@link AdminBasicOperationsControllerExtensionHandler}.
 * 
 * Individual implementations of this extension handler should subclass this class as it will allow them to 
 * only override the methods that they need for their particular scenarios.
 * 
 * @author ckittrell
 */
public class AbstractAdminBasicOperationsControllerExtensionHandler extends AbstractExtensionHandler implements AdminBasicOperationsControllerExtensionHandler {

    @Override
    public ExtensionResultStatusType buildLookupListGrid(PersistencePackageRequest ppr, ClassMetadata cmd, String owningClass,
            List<SectionCrumb> sectionCrumbs, Model model, MultiValueMap<String, String> requestParams) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

}
