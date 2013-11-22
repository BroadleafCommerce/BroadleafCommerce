/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.structure.message;

import org.broadleafcommerce.cms.structure.domain.StructuredContent;

/**
 * The ArchivedStructuredContentPublisher will be notified when a StructuredContent item has
 * been marked as archived.    This provides a convenient cache-eviction
 * point for items in production.
 *
 * Implementers of this service could send a JMS or AMQP message so
 * that other VMs can evict the item.
 *
 * Created by bpolster.
 */
public interface ArchivedStructuredContentPublisher {
    void processStructuredContentArchive(StructuredContent sc, String baseTypeKey, String baseNameKey);
}
