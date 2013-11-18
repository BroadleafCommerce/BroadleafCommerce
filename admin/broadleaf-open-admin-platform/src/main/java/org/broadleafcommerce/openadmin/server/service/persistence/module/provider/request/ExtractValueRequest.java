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
package org.broadleafcommerce.openadmin.server.service.persistence.module.provider.request;

import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.dto.Property;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.DataFormatProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import java.io.Serializable;
import java.util.List;

/**
 * Contains the requested value, property and support classes.
 *
 * @author Jeff Fischer
 */
public class ExtractValueRequest {

    protected final List<Property> props;
    protected final FieldManager fieldManager;
    protected final BasicFieldMetadata metadata;
    protected final Object requestedValue;
    protected String displayVal;
    protected final PersistenceManager persistenceManager;
    protected final RecordHelper recordHelper;
    protected final Serializable entity;

    public ExtractValueRequest(List<Property> props, FieldManager fieldManager, BasicFieldMetadata metadata, 
            Object requestedValue, String displayVal, PersistenceManager persistenceManager, 
            RecordHelper recordHelper, Serializable entity) {
        this.props = props;
        this.fieldManager = fieldManager;
        this.metadata = metadata;
        this.requestedValue = requestedValue;
        this.displayVal = displayVal;
        this.persistenceManager = persistenceManager;
        this.recordHelper = recordHelper;
        this.entity = entity;
    }

    public List<Property> getProps() {
        return props;
    }

    public FieldManager getFieldManager() {
        return fieldManager;
    }

    public BasicFieldMetadata getMetadata() {
        return metadata;
    }

    public Object getRequestedValue() {
        return requestedValue;
    }

    public String getDisplayVal() {
        return displayVal;
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public DataFormatProvider getDataFormatProvider() {
        return recordHelper;
    }
    
    public RecordHelper getRecordHelper() {
        return recordHelper;
    }

    public void setDisplayVal(String displayVal) {
        this.displayVal = displayVal;
    }
    
    public Serializable getEntity() {
        return entity;
    }
    
}
