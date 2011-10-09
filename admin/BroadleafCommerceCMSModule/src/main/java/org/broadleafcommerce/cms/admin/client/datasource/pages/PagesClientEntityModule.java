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

package org.broadleafcommerce.cms.admin.client.datasource.pages;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtincubator.security.exception.ApplicationSecurityException;
import com.smartgwt.client.data.Criteria;
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
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/24/11
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class PagesClientEntityModule extends BasicClientEntityModule {

    public PagesClientEntityModule(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service) {
        super(ceilingEntityFullyQualifiedClassname, persistencePerspective, service);
    }

    @Override
    public void executeFetch(final String requestId, DSRequest request, final DSResponse response, String[] customCriteria, final AsyncCallback<DataSource> cb) {
        BLCMain.NON_MODAL_PROGRESS.startProgress();
        Criteria criteria = request.getCriteria();
        criteria.addCriteria(((PagesTreeDataSource) dataSource).permanentCriteria);
		CriteriaTransferObject cto = getCto(request);
		service.fetch(new PersistencePackage(ceilingEntityFullyQualifiedClassname, null, persistencePerspective, customCriteria), cto, new EntityServiceAsyncCallback<DynamicResultSet>(EntityOperationType.FETCH, requestId, request, response, dataSource) {
            public void onSuccess(DynamicResultSet result) {
                super.onSuccess(result);
                TreeNode[] recordList = buildRecords(result, null);

                //mark page items as leaf nodes
                for (TreeNode node : recordList) {
                    if ("org.broadleafcommerce.cms.page.domain.PageImpl".equals(node.getAttribute("_type"))) {
                        node.setIsFolder(false);
                        if (node.getAttributeAsBoolean("lockedFlag")) {
                            node.setIcon(GWT.getModuleBaseURL()+"admin/images/lock_page.png");
                        }
                    }
                }

                response.setData(recordList);
                response.setTotalRows(result.getTotalRecords());
                if (cb != null) {
                    cb.onSuccess(dataSource);
                }
                dataSource.processResponse(requestId, response);
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

    public void executeUpdate(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
    	BLCMain.NON_MODAL_PROGRESS.startProgress();
		JavaScriptObject data = request.getData();
        final TreeNode record = new TreeNode(data);
        Entity entity = buildEntity(record, request);
		String componentId = request.getComponentId();
        if (componentId != null) {
            if (entity.getType() == null) {
            	String[] type = ((ListGrid) Canvas.getById(componentId)).getSelectedRecord().getAttributeAsStringArray("_type");
            	entity.setType(type);
            }
        }
        service.update(new PersistencePackage(ceilingEntityFullyQualifiedClassname, entity, persistencePerspective, customCriteria), new EntityServiceAsyncCallback<Entity>(EntityOperationType.UPDATE, requestId, request, response, dataSource) {
			public void onSuccess(Entity result) {
				super.onSuccess(null);

                TreeNode record = (TreeNode) buildRecord(result, false);
				TreeNode[] recordList = new TreeNode[]{record};

                //mark page items as leaf nodes
                for (TreeNode node : recordList) {
                    if ("org.broadleafcommerce.cms.page.domain.PageImpl".equals(node.getAttribute("_type"))) {
                        node.setIsFolder(false);
                        if (node.getAttributeAsBoolean("lockedFlag")) {
                            node.setIcon(GWT.getModuleBaseURL()+"admin/images/lock_page.png");
                        }
                    }
                }
                
				response.setData(recordList);

				if (cb != null) {
					cb.onSuccess(dataSource);
				}
				dataSource.processResponse(requestId, response);
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
