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

package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import org.apache.commons.lang.ArrayUtils;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

/**
 * @author Jeff Fischer
 */
public abstract class AbstractPersistenceProvider implements PersistenceProvider {

    protected Class<?> getListFieldType(Serializable instance, FieldManager fieldManager, Property property, PersistenceManager persistenceManager) {
        Class<?> returnType = null;
        Field field = fieldManager.getField(instance.getClass(), property.getName());
        java.lang.reflect.Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Class<?> clazz = (Class<?>) pType.getActualTypeArguments()[0];
            Class<?>[] entities = persistenceManager.getDynamicEntityDao().getAllPolymorphicEntitiesFromCeiling(clazz);
            if (!ArrayUtils.isEmpty(entities)) {
                returnType = entities[entities.length-1];
            }
        }
        return returnType;
    }
}
