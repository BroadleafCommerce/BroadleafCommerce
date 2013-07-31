/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.web.service;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;

/**
 * Abstract class to provide convenience for determining how to handle form 
 * extensions in the admin.
 * 
 * @author Kelly Tisdell
 *
 */
public abstract class AbstractFormBuilderExtensionHandler extends AbstractExtensionHandler implements FormBuilderExtensionHandler {

    @Override
    public ExtensionResultStatusType addFormExtensions(EntityForm ef) {
        if (canHandle(ef)) {
            handleFormExtensions(ef);
            return ExtensionResultStatusType.HANDLED;
        }
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    /**
     * Determine how to handle this.
     * @param ef
     * @return
     */
    protected abstract boolean canHandle(EntityForm ef);

    /**
     * Allows you to do things like add a button to a form.
     * @param ef
     */
    protected abstract void handleFormExtensions(EntityForm ef);

}
