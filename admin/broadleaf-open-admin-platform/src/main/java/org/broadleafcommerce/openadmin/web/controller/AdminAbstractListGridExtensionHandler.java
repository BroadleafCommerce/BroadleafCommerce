/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.controller;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;

/**
 * Extension handler for {@link org.broadleafcommerce.openadmin.web.form.component.ListGrid}.
 *
 * Created by Reginald Cole
 */
public interface AdminAbstractListGridExtensionHandler extends ExtensionHandler {

    /**
     * This can be used to add {@link org.broadleafcommerce.openadmin.web.form.component.ListGridAction}s to the {@link ListGrid}
     * @param listGrid
     * @return
     */
    public ExtensionResultStatusType addAdditionalRowAction(ListGrid listGrid);
}
