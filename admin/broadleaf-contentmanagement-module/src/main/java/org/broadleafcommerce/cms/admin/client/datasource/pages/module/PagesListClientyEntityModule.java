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

package org.broadleafcommerce.cms.admin.client.datasource.pages.module;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtincubator.security.exception.ApplicationSecurityException;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.BasicClientEntityModule;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityOperationType;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityServiceAsyncCallback;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

/**
 * @author jfischer
 */
public class PagesListClientyEntityModule extends BasicClientEntityModule {

    public PagesListClientyEntityModule(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service) {
        super(ceilingEntityFullyQualifiedClassname, persistencePerspective, service);
    }

    @Override
    public void executeUpdate(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
        BLCMain.NON_MODAL_PROGRESS.startProgress();
        JavaScriptObject data = request.getData();
        final TreeNode originalRecord = new TreeNode(data);
        Entity entity = buildEntity(originalRecord, request);
        String componentId = request.getComponentId();
        if (componentId != null) {
            if (entity.getType() == null) {
                String[] type = ((ListGrid) Canvas.getById(componentId)).getSelectedRecord().getAttributeAsStringArray("_type");
                entity.setType(type);
            }
        }
        service.update(new PersistencePackage(ceilingEntityFullyQualifiedClassname, entity, persistencePerspective, customCriteria, BLCMain.csrfToken), new EntityServiceAsyncCallback<Entity>(EntityOperationType.UPDATE, requestId, request, response, dataSource) {
            public void onSuccess(Entity result) {
                super.onSuccess(null);
                if (processResult(result, requestId, response, dataSource)) {
                    TreeNode record = (TreeNode) buildRecord(result, false);
                    response.setAttribute("newId", record.getAttribute("id"));
                    record.setAttribute("id", originalRecord.getAttribute("id"));
                    TreeNode[] recordList = new TreeNode[]{record};
                    response.setData(recordList);

                    if (cb != null) {
                        cb.onSuccess(dataSource);
                    }
                    dataSource.processResponse(requestId, response);
                }
            }

            @Override
            protected void onSecurityException(ApplicationSecurityException exception) {
                super.onSecurityException(exception);
                if (cb != null) {
                    cb.onFailure(exception);
                }
            }

            @Override
            protected void onOtherException(Throwable exception) {
                super.onOtherException(exception);
                if (cb != null) {
                    cb.onFailure(exception);
                }
            }

            @Override
            protected void onError(EntityOperationType opType, String requestId, DSRequest request, DSResponse response, Throwable caught) {
                super.onError(opType, requestId, request, response, caught);
                if (cb != null) {
                    cb.onFailure(caught);
                }
            }
        });
    }
}
