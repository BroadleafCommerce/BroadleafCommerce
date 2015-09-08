/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.web.controller;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.dto.SectionCrumb;
import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicOperationsController;
import org.springframework.ui.Model;

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
     * @param drs
     * @param cmd
     * @param owningClass
     * @param sectionCrumbs
     * @param model
     * @return ExtensionResultStatusType
     */
    public ExtensionResultStatusType buildLookupListGrid(DynamicResultSet drs, ClassMetadata cmd, String owningClass,
            List<SectionCrumb> sectionCrumbs, Model model);
}
