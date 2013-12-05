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
package org.broadleafcommerce.openadmin.web.service;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.web.form.component.ListGridRecord;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;

/**
 * Abstract class to provide convenience for determining how to handle form 
 * extensions in the admin
 * 
 * @author Kelly Tisdell
 * @author Phillip Verheyden (phillipuniverse)
 */
public abstract class AbstractFormBuilderExtensionHandler extends AbstractExtensionHandler implements FormBuilderExtensionHandler {

    /**
     * Determine if e to handle this.
     * @param ef
     * @return
     */
    protected abstract boolean canHandle(EntityForm ef);

    /**
     * Convenience method for {@link #modifyUnpopulatedEntityForm(EntityForm)}. See that method for usage documentation
     */
    protected abstract void handleModifyUnpopulatedEntityForm(EntityForm ef);
    
    /**
     * Convenience method for {@link #modifyPopulatedEntityForm(EntityForm, Entity)}. See that method for usage documentation
     */
    protected abstract void handleModifyPopulatedEntityForm(EntityForm ef, Entity entity);
    
    /**
     * Convenience method for {@link #modifyDetailEntityForm(EntityForm)}. See that method for usage documentation
     */
    protected abstract void handleModifyDetailEntityForm(EntityForm ef);

    @Override
    public ExtensionResultStatusType modifyUnpopulatedEntityForm(EntityForm ef) {
        if (canHandle(ef)) {
            handleModifyUnpopulatedEntityForm(ef);
            return ExtensionResultStatusType.HANDLED;
        }
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType modifyPopulatedEntityForm(EntityForm ef, Entity entity) {
        if (canHandle(ef)) {
            handleModifyPopulatedEntityForm(ef, entity);
            return ExtensionResultStatusType.HANDLED;
        }
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType modifyDetailEntityForm(EntityForm ef) {
        if (canHandle(ef)) {
            handleModifyDetailEntityForm(ef);
            return ExtensionResultStatusType.HANDLED;
        }
        return ExtensionResultStatusType.NOT_HANDLED;
    }
    
    @Override
    public ExtensionResultStatusType modifyListGridRecord(String className, ListGridRecord record, Entity entity) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
    
    @Override
    public ExtensionResultStatusType addAdditionalFormActions(EntityForm entityForm) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
    
}
