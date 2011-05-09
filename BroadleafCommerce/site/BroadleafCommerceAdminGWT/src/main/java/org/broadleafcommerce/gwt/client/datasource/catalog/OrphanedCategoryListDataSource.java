package org.broadleafcommerce.gwt.client.datasource.catalog;

import org.broadleafcommerce.gwt.client.datasource.dynamic.EntityOperationType;
import org.broadleafcommerce.gwt.client.datasource.dynamic.EntityServiceAsyncCallback;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.widgets.tree.TreeNode;

public class OrphanedCategoryListDataSource extends ListGridDataSource {
	
	private String root;

	/**
	 * @param ceilingEntityFullyQualifiedClassname
	 * @param name
	 * @param persistencePerspective
	 * @param service
	 * @param modules
	 */
	public OrphanedCategoryListDataSource(String ceilingEntityFullyQualifiedClassname, String root, String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
		super(ceilingEntityFullyQualifiedClassname, name, persistencePerspective, service, modules);
		this.root = root;
	}

	@Override
	protected void executeFetch(final String requestId, DSRequest request, final DSResponse response) {
		service.fetch(ceilingEntityFullyQualifiedClassname, getCto(request), persistencePerspective, null, new EntityServiceAsyncCallback<DynamicResultSet>(EntityOperationType.FETCH, requestId, request, response, this) {
			public void onSuccess(DynamicResultSet result) {
				super.onSuccess(result);
				TreeNode[] recordList = buildRecords(result, new String[]{root});
				response.setData(recordList);
				response.setTotalRows(result.getTotalRecords());
				
				processResponse(requestId, response);
			}
		});
	}	

}
