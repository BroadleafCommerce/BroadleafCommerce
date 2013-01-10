/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.setup;

import org.broadleafcommerce.openadmin.client.datasource.DataSourceFactory;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;

/**
 * 
 * @author jfischer
 *
 */
public class PresenterSetupItem {
    
    private String name;
    private OperationTypes operationTypes;
    private Object[] additionalItems;
    private AsyncCallbackAdapter adapter = null;
    private DataSourceFactory factory;

    public PresenterSetupItem(String name, DataSourceFactory factory, OperationTypes operationTypes, Object[] additionalItems, AsyncCallbackAdapter adapter) {
        this.name = name;
        this.factory = factory;
        this.operationTypes = operationTypes;
        this.additionalItems = additionalItems;
        this.adapter = adapter;
    }

    public PresenterSetupItem(String name, DataSourceFactory factory, AsyncCallbackAdapter adapter) {
        this(name, factory, null, new Object[]{}, adapter);
    }
    
    public AsyncCallbackAdapter getAdapter() {
        return adapter;
    }
    
    public void setAdapter(AsyncCallbackAdapter adapter) {
        this.adapter = adapter;
    }
    
    public DataSourceFactory getFactory() {
        return factory;
    }
    
    public void setFactory(DataSourceFactory factory) {
        this.factory = factory;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OperationTypes getOperationTypes() {
        return operationTypes;
    }

    public void setOperationTypes(OperationTypes operationTypes) {
        this.operationTypes = operationTypes;
    }

    public Object[] getAdditionalItems() {
        return additionalItems;
    }

    public void setAdditionalItems(Object[] additionalItems) {
        this.additionalItems = additionalItems;
    }

    protected void invoke() {
        if (factory != null) {
            factory.createDataSource(name, operationTypes, additionalItems, adapter);
        } else {
            adapter.onSuccess(null);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PresenterSetupItem other = (PresenterSetupItem) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    
}
