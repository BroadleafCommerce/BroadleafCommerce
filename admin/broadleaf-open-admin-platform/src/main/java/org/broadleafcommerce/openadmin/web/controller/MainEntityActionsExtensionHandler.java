/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.web.form.entity.DefaultMainActions;
import org.broadleafcommerce.openadmin.web.form.entity.EntityFormAction;

import java.util.List;


/**
 * Deprecated - Use {@link org.broadleafcommerce.openadmin.web.controller.AdminAbstractControllerExtensionHandler}
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@Deprecated
public interface MainEntityActionsExtensionHandler extends ExtensionHandler {

    /**
     * Extension point to override the actions that are added by default when viewing a ceiling entity for a particular
     * section (for instance, a list of Products in the 'Product' section). Assuming that the user has proper permissions,
     * the mainActions list would have {@link DefaultMainActions#ADD}
     * 
     * @param cmd the metadata for the ceiling entity that is being displayed
     * @param mainActions the actions that are added to the main form by default. Use this list to add more actions
     */
    public void modifyMainActions(ClassMetadata cmd, List<EntityFormAction> mainActions);
    
}
