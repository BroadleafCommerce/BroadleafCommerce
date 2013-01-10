/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.admin.client.datasource.catalog.category.module;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.BasicClientEntityModule;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityOperationType;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityServiceAsyncCallback;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

/**
 * 
 * @author jfischer
 *
 */
public class CategoryTreeEntityModule extends BasicClientEntityModule {

    /**
     * @param ceilingEntityFullyQualifiedClassname
     * @param persistencePerspective
     * @param service
     */
    public CategoryTreeEntityModule(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service) {
        super(ceilingEntityFullyQualifiedClassname, persistencePerspective, service);
    }

    @Override
    public Record buildRecord(Entity entity, Boolean updateId) {
        return super.buildRecord(entity, true);
    }

    @Override
    public void executeUpdate(final String requestId, DSRequest request, final DSResponse response, String[] customCriteria, AsyncCallback<DataSource> cb) {
        BLCMain.NON_MODAL_PROGRESS.startProgress();
        JavaScriptObject data = request.getData();
        final TreeNode record = new TreeNode(data);
        Entity entity = buildEntity(record, request);
        service.update(new PersistencePackage(ceilingEntityFullyQualifiedClassname, entity, persistencePerspective, null, BLCMain.csrfToken), new EntityServiceAsyncCallback<Entity>(EntityOperationType.UPDATE, requestId, request, response, dataSource) {
            public void onSuccess(Entity result) {
                super.onSuccess(result);
                if (processResult(result, requestId, response, dataSource)) {
                    /*
                     * Since we've hacked the tree to be able to display duplicate entries, we must iterate
                     * through the currently loaded records to see if there are any other instances of our
                     * entity and update them as well.
                     */
                    String realStartingId = dataSource.stripDuplicateAllowSpecialCharacters(dataSource.getPrimaryKeyValue(record));
                    String embellishedStartingId = dataSource.getPrimaryKeyValue(record);
                    RecordList resultSet = ((PresentationLayerAssociatedDataSource) dataSource).getAssociatedGrid().getRecordList();
                    if (resultSet != null) {
                        Record[] myRecords = resultSet.toArray();
                        int count = 1;
                        for (Record myRecord : myRecords) {
                            String realMyId = dataSource.stripDuplicateAllowSpecialCharacters(dataSource.getPrimaryKeyValue(myRecord));
                            String embellishedMyId = dataSource.getPrimaryKeyValue(myRecord);
                            if (realStartingId.equals(realMyId) && !embellishedStartingId.equals(embellishedMyId)) {
                                updateRecord(result, (TreeNode) myRecord, false);
                                ((ListGrid) ((PresentationLayerAssociatedDataSource) dataSource).getAssociatedGrid()).refreshRow(count);
                            }
                            count++;
                        }
                    }
                    TreeNode[] recordList = new TreeNode[]{record};
                    response.setData(recordList);

                    dataSource.processResponse(requestId, response);
                }
            }
        });
    }

    protected void logAttributes(Record record) {
        for (String attr : record.getAttributes()) {
            GWT.log(attr + ", " + record.getAttribute(attr));
        }
    }
}
