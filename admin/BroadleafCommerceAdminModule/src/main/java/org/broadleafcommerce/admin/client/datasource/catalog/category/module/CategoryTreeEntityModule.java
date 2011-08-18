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
package org.broadleafcommerce.admin.client.datasource.catalog.category.module;

import java.util.Map;

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.BasicClientEntityModule;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityOperationType;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityServiceAsyncCallback;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.widgets.tree.TreeNode;

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
	 * @param metadataOverrides
	 */
	public CategoryTreeEntityModule(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, Map<String, FieldMetadata> metadataOverrides) {
		super(ceilingEntityFullyQualifiedClassname, persistencePerspective, service, metadataOverrides);
	}

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
		service.update(new PersistencePackage(null, entity, persistencePerspective, dataSource.createSandBoxInfo(), null), new EntityServiceAsyncCallback<Entity>(EntityOperationType.UPDATE, requestId, request, response, dataSource) {
			public void onSuccess(Entity result) {
				super.onSuccess(result);
				/*
				 * Since we've hacked the tree to be able to display duplicate entries, we must iterate
				 * through the currently loaded records to see if there are any other instances of our
				 * entity and update them as well.
				 */
				String startingId = dataSource.stripDuplicateAllowSpecialCharacters(dataSource.getPrimaryKeyValue(record));
				RecordList resultSet = ((PresentationLayerAssociatedDataSource) dataSource).getAssociatedGrid().getRecordList();
				if (resultSet != null) {
					Record[] myRecords = resultSet.toArray();
					int count = 1;
					for (Record myRecord : myRecords) {
						String myId = dataSource.stripDuplicateAllowSpecialCharacters(dataSource.getPrimaryKeyValue(myRecord));
						if (startingId.equals(myId) && !dataSource.getPrimaryKeyValue(record).equals(myId)) {
							updateRecord(result, (TreeNode) myRecord, false);
							((PresentationLayerAssociatedDataSource) dataSource).getAssociatedGrid().refreshRow(count);
						}
						count++;
					}
				}
				TreeNode myRecord = (TreeNode) updateRecord(result, (Record) record, false);
				TreeNode[] recordList = new TreeNode[]{myRecord};
				response.setData(recordList);
				dataSource.processResponse(requestId, response);
			}
		});
	}

}
