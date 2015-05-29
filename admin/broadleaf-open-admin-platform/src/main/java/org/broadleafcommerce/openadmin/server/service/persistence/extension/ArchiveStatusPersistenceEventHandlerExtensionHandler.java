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
package org.broadleafcommerce.openadmin.server.service.persistence.extension;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Allows for custom conditions to avoid ArchiveStatusPersistenceEventHandler's preFetch functionality
 *
 * @author Chris Kittrell
 */
public interface ArchiveStatusPersistenceEventHandlerExtensionHandler extends ExtensionHandler {

    /**
     * @param entity
     * @return
     */
    ExtensionResultStatusType isArchivable(Class<?> entity, AtomicBoolean isArchivable);

    public static final int DEFAULT_PRIORITY = Integer.MAX_VALUE;
}
