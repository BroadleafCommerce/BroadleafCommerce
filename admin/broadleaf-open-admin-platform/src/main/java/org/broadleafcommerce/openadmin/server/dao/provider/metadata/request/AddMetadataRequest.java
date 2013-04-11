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

package org.broadleafcommerce.openadmin.server.dao.provider.metadata.request;

import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Contains the requested field, metadata and support classes.
 *
 * @author Jeff Fischer
 */
public class AddMetadataRequest {

    private final Field requestedField;
    private final Class<?> parentClass;
    private final Class<?> targetClass;
    private final Map<String, FieldMetadata> requestedMetadata;
    private final DynamicEntityDao dynamicEntityDao;
    private final String prefix;

    public AddMetadataRequest(Field requestedField, Class<?> parentClass, Class<?> targetClass, Map<String, FieldMetadata> requestedMetadata, DynamicEntityDao dynamicEntityDao, String prefix) {
        this.requestedField = requestedField;
        this.parentClass = parentClass;
        this.targetClass = targetClass;
        this.requestedMetadata = requestedMetadata;
        this.dynamicEntityDao = dynamicEntityDao;
        this.prefix = prefix;
    }

    public Field getRequestedField() {
        return requestedField;
    }

    public Class<?> getParentClass() {
        return parentClass;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Map<String, FieldMetadata> getRequestedMetadata() {
        return requestedMetadata;
    }

    public DynamicEntityDao getDynamicEntityDao() {
        return dynamicEntityDao;
    }

    public String getPrefix() {
        return prefix;
    }
}
