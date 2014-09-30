/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.openadmin.server.service.persistence.module.provider.extension;

import org.broadleafcommerce.common.extension.ExtensionManager;
import org.springframework.stereotype.Service;

/**
 * For internal usage. Allows extension of entity API calls without subclassing the entity.
 *
 * @author Jeff Fischer
 */
@Service("blMediaFieldPersistenceProviderExtensionManager")
public class MediaFieldPersistenceProviderExtensionManager extends ExtensionManager<MediaFieldPersistenceProviderExtensionHandler> {

    public MediaFieldPersistenceProviderExtensionManager() {
        super(MediaFieldPersistenceProviderExtensionHandler.class);
    }

}
