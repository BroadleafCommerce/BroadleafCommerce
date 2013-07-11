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

import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;

/**
 * Contains the requested field, metadata and support classes.
 *
 * @author Jeff Fischer
 */
public class LateStageAddMetadataRequest {

    private final String fieldName;
    private final Class<?> parentClass;
    private final Class<?> targetClass;
    private final DynamicEntityDao dynamicEntityDao;
    private final String prefix;

    public LateStageAddMetadataRequest(String fieldName, Class<?> parentClass, Class<?> targetClass, DynamicEntityDao dynamicEntityDao, String prefix) {
        this.fieldName = fieldName;
        this.parentClass = parentClass;
        this.targetClass = targetClass;
        this.dynamicEntityDao = dynamicEntityDao;
        this.prefix = prefix;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getParentClass() {
        return parentClass;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public DynamicEntityDao getDynamicEntityDao() {
        return dynamicEntityDao;
    }

    public String getPrefix() {
        return prefix;
    }
}
