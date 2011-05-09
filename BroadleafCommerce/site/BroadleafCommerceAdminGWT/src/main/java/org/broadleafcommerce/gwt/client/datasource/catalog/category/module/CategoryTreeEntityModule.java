package org.broadleafcommerce.gwt.client.datasource.catalog.category.module;

import java.util.Map;

import org.broadleafcommerce.gwt.client.Main;
import org.broadleafcommerce.gwt.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.BasicEntityModule;
import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.EntityOperationType;
import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.EntityServiceAsyncCallback;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.FieldMetadata;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.widgets.tree.TreeNode;

public class CategoryTreeEntityModule extends BasicEntityModule {

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
		Main.NON_MODAL_PROGRESS.startProgress();
		JavaScriptObject data = request.getData();
        final TreeNode record = new TreeNode(data);
        Entity entity = buildEntity(record);
		service.update(entity, persistencePerspective, null, new EntityServiceAsyncCallback<Entity>(EntityOperationType.UPDATE, requestId, request, response, dataSource) {
			public void onSuccess(Entity result) {
				super.onSuccess(result);
				/*
				 * Since we've hacked the tree to be able to display duplicate entries, we must iterate
				 * through the currently loaded records to see if there are any other instances of our
				 * entity and update them as well.
				 */
				String startingId = dataSource.stripDuplicateAllowSpecialCharacters(record.getAttribute("id"));
				RecordList resultSet = ((PresentationLayerAssociatedDataSource) dataSource).getAssociatedGrid().getRecordList();
				if (resultSet != null) {
					Record[] myRecords = resultSet.toArray();
					int count = 1;
					for (Record myRecord : myRecords) {
						String myId = dataSource.stripDuplicateAllowSpecialCharacters(myRecord.getAttribute("id"));
						if (startingId.equals(myId) && !record.getAttribute("id").equals(myId)) {
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
