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

import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;

/**
 * For special {@link RowLevelSecurityProvider} instances, add special behavior that allows for modifying an {@link EntityForm}
 * that has been marked as read only. Presumably, the modifier would want to enable portions of the EntityForm for specialized
 * behavior.
 *
 * @author Jeff Fischer
 */
public interface ExceptionAwareRowLevelSecurityProvider {

    /**
     * Provide a modifier capable of manipulating an {@link EntityForm} that has been marked as readonly and modify its
     * state, presumably to set one or more aspects of the form as editable.
     *
     * @see EntityFormModifier
     * @return package containing the modifier implementations and any configurations for those modifiers
     */
    EntityFormModifierConfiguration getUpdateDenialExceptions();

}
