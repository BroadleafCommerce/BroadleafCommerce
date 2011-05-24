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
package org.broadleafcommerce.gwt.admin.client.datasource.promotion.module;

import java.util.Map;

import org.broadleafcommerce.gwt.client.BLCMain;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.BasicClientEntityModule;
import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.EntityOperationType;
import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.EntityServiceAsyncCallback;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.FieldMetadata;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtincubator.security.exception.ApplicationSecurityException;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * 
 * @author jfischer
 *
 */
public class OfferItemCriteriaListModule extends BasicClientEntityModule {

	public OfferItemCriteriaListModule(
			String ceilingEntityFullyQualifiedClassname,
			PersistencePerspective persistencePerspective,
			DynamicEntityServiceAsync service,
			Map<String, FieldMetadata> metadataOverrides) {
		super(ceilingEntityFullyQualifiedClassname, persistencePerspective, service,
				metadataOverrides);
	}

	public OfferItemCriteriaListModule(
			String ceilingEntityFullyQualifiedClassname,
			PersistencePerspective persistencePerspective,
			DynamicEntityServiceAsync service) {
		super(ceilingEntityFullyQualifiedClassname, persistencePerspective, service);
	}

	@Override
	public void executeRemove(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
    	BLCMain.NON_MODAL_PROGRESS.startProgress();
		JavaScriptObject data = request.getData();
        TreeNode record = new TreeNode(data);
        record.setAttribute("_type", new String[]{((DynamicEntityDataSource) dataSource).getDefaultNewEntityFullyQualifiedClassname()});
        Entity entity = buildEntity(record);
        service.remove(entity, persistencePerspective, customCriteria, new EntityServiceAsyncCallback<Void>(EntityOperationType.REMOVE, requestId, request, response, dataSource) {
			public void onSuccess(Void item) {
				super.onSuccess(null);
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
