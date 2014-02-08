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
import org.springframework.ui.Model;


/**
 * Extension handler for methods present in {@link AdminAbstractController}.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public interface AdminAbstractControllerExtensionHandler extends ExtensionHandler {

    /**
     * Invoked every time {@link AdminAbstractController#setModelAttributes(Model, String)} is invoked.
     * 
     * @param model
     * @param sectionKey
     * @return the extension result status
     */
    public ExtensionResultStatusType setAdditionalModelAttributes(Model model, String sectionKey);
}
