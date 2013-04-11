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

package org.broadleafcommerce.openadmin.client.datasource.dynamic;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;

/**
 * @author Jeff Fischer
 */
public class BatchPackage {

    private static int counter = 0;

    public BatchPackage() {
        batchPackageId = counter++;
    }

    private final Integer batchPackageId;
    protected PersistencePackage persistencePackage;
    protected AsyncCallback<DynamicResultSet> asyncCallback;
    protected BatchOperationType batchOperationType;
    protected String dataSourceUrl;

    public AsyncCallback<DynamicResultSet> getAsyncCallback() {
        return asyncCallback;
    }

    public void setAsyncCallback(AsyncCallback<DynamicResultSet> asyncCallback) {
        this.asyncCallback = asyncCallback;
    }

    public PersistencePackage getPersistencePackage() {
        return persistencePackage;
    }

    public void setPersistencePackage(PersistencePackage persistencePackage) {
        this.persistencePackage = persistencePackage;
    }

    public BatchOperationType getBatchOperationType() {
        return batchOperationType;
    }

    public void setBatchOperationType(BatchOperationType batchOperationType) {
        this.batchOperationType = batchOperationType;
    }

    public Integer getBatchPackageId() {
        return batchPackageId;
    }

    public String getDataSourceUrl() {
        return dataSourceUrl;
    }

    public void setDataSourceUrl(String dataSourceUrl) {
        this.dataSourceUrl = dataSourceUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BatchPackage)) return false;

        BatchPackage that = (BatchPackage) o;

        if (batchPackageId != that.batchPackageId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return batchPackageId;
    }
}
