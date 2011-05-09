package org.broadleafcommerce.gwt.admin.client.datasource.catalog.category;

import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.EntityOperationType;
import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.EntityServiceAsyncCallback;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.tree.TreeNode;

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
		service.fetch(fetchModule.getCeilingEntityFullyQualifiedClassname(), fetchModule.getCto(request), persistencePerspective, null, new EntityServiceAsyncCallback<DynamicResultSet>(EntityOperationType.FETCH, requestId, request, response, this) {
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
