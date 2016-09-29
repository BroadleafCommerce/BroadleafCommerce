/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.security.service;

import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;

/**
 * Qualified instances are expected to manipulate the {@link EntityForm} instance passed in the request.
 *
 * @author Jeff Fischer
 */
public interface EntityFormModifier {

    /**
     * Modify the {@link EntityForm} in the request. The request contains other relevant information that should be
     * useful during the modification.
     *
     * @see EntityFormModifierRequest
     * @param request the EntityForm, and other supporting objects
     */
    void modifyEntityForm(EntityFormModifierRequest request);

    /**
     * Modify the {@link ListGrid} in the request. The request contains other relevant information that should be
     * useful during the modification.
     *
     * @param request The ListGrid, and other supporting objects
     */
    void modifyListGrid(EntityFormModifierRequest request);

    /**
     * Whether or not this EntityFormModifier is qualified to modify based on the modifierType.
     *
     * @param modifierType and identifier for the class of modification being requested
     * @return Whether or not qualified
     */
    boolean isQualified(String modifierType);

}
