/*
 * Copyright 2008-2013 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.web.controller;

import org.broadleafcommerce.openadmin.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.web.form.entity.DefaultMainActions;
import org.broadleafcommerce.openadmin.web.form.entity.EntityFormAction;

import java.util.List;


/**
 * 
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface MainEntityActionsExtensionListener {

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
