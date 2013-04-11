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

package org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request;

import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;

import java.util.Map;

/**
 * Contains the <tt>Entity</tt> instance and unfiltered property list.
 *
 * @author Jeff Fischer
 */
public class AddFilterPropertiesRequest {

    private final Entity entity;
    private final Map<String, FieldMetadata> requestedProperties;

    public AddFilterPropertiesRequest(Entity entity, Map<String, FieldMetadata> requestedProperties) {
        this.entity = entity;
        this.requestedProperties = requestedProperties;
    }

    public Entity getEntity() {
        return entity;
    }

    public Map<String, FieldMetadata> getRequestedProperties() {
        return requestedProperties;
    }
}
