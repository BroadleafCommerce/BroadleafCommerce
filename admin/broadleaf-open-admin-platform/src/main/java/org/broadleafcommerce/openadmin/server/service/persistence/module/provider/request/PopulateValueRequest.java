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

import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.DataFormatProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;

/**
 * Contains the requested value, instance and support classes.
 *
 * @author Jeff Fischer
 */
public class PopulateValueRequest {

    private final Boolean setId;
    private final FieldManager fieldManager;
    private final Property property;
    private final BasicFieldMetadata metadata;
    private final Class<?> returnType;
    private final String requestedValue;
    private final PersistenceManager persistenceManager;
    private final DataFormatProvider dataFormatProvider;

    public PopulateValueRequest(Boolean setId, FieldManager fieldManager, Property property, BasicFieldMetadata metadata, Class<?> returnType, String requestedValue, PersistenceManager persistenceManager, DataFormatProvider dataFormatProvider) {
        this.setId = setId;
        this.fieldManager = fieldManager;
        this.property = property;
        this.metadata = metadata;
        this.returnType = returnType;
        this.requestedValue = requestedValue;
        this.persistenceManager = persistenceManager;
        this.dataFormatProvider = dataFormatProvider;
    }

    public Boolean getSetId() {
        return setId;
    }

    public FieldManager getFieldManager() {
        return fieldManager;
    }

    public Property getProperty() {
        return property;
    }

    public BasicFieldMetadata getMetadata() {
        return metadata;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public String getRequestedValue() {
        return requestedValue;
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public DataFormatProvider getDataFormatProvider() {
        return dataFormatProvider;
    }
}
