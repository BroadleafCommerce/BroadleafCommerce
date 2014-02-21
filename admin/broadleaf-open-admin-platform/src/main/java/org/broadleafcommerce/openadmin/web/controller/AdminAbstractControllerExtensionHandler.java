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
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.springframework.ui.Model;


/**
 * Extension handler for methods present in {@link AdminAbstractController}.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface AdminAbstractControllerExtensionHandler extends ExtensionHandler {
    
    public static final String NEW_CLASS_NAME = "newClassName";

    /**
     * Invoked every time {@link AdminAbstractController#setModelAttributes(Model, String)} is invoked.
     * 
     * @param model
     * @param sectionKey
     * @return the extension result status
     */
    public ExtensionResultStatusType setAdditionalModelAttributes(Model model, String sectionKey);

    /**
     * Invoked whenever {@link AdminAbstractController#getClassNameForSection(String)} is invoked. If an extension
     * handler sets the {@link #NEW_CLASS_NAME} variable in the ExtensionResultHolder, the overriden value will be used.
     * 
     * @param erh
     * @param sectionKey
     * @param section
     * @return
     */
    public ExtensionResultStatusType overrideClassNameForSection(ExtensionResultHolder erh, String sectionKey, 
            AdminSection section);
}
