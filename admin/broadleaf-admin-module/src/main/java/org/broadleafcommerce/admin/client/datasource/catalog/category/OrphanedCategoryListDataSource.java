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

package org.broadleafcommerce.admin.client.datasource.catalog.category;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityOperationType;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityServiceAsyncCallback;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

/**
 * 
 * @author jfischer
 *
 */
public class OrphanedCategoryListDataSource extends ListGridDataSource {
    
    private String root;

    /**
     * @param name
     * @param persistencePerspective
     * @param service
     * @param modules
     */
    public OrphanedCategoryListDataSource(String root, String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
        super(name, persistencePerspective, service, modules);
        this.root = root;
    }

    @Override
    protected void executeFetch(final String requestId, DSRequest request, final DSResponse response) {
        final DataSourceModule fetchModule = getCompatibleModule(persistencePerspective.getOperationTypes().getFetchType());
        service.fetch(new PersistencePackage(fetchModule.getCeilingEntityFullyQualifiedClassname(), null, persistencePerspective, null, BLCMain.csrfToken), fetchModule.getCto(request), new EntityServiceAsyncCallback<DynamicResultSet>(EntityOperationType.FETCH, requestId, request, response, this) {
            public void onSuccess(DynamicResultSet result) {
                super.onSuccess(result);
                TreeNode[] recordList = fetchModule.buildRecords(result, new String[]{root});
                response.setData(recordList);
                response.setTotalRows(result.getTotalRecords());
                
                processResponse(requestId, response);
            }
        });
    }   

    @Override
    protected void executeRemove(String requestId, DSRequest request, DSResponse response, String[] customCriteria, AsyncCallback<DataSource> cb) {
        super.executeRemove(requestId, request, response, new String[]{"OrphanedCategoryListDataSource"}, cb);
    }
}
