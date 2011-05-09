package org.broadleafcommerce.gwt.client.datasource.dynamic;

import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.AsyncCallbackAdapter;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;

public class AutoRefreshListGridDataSource extends ListGridDataSource {

	/**
	 * @param ceilingEntityFullyQualifiedClassname
	 * @param name
	 * @param persistencePerspective
	 * @param service
	 * @param modules
	 */
	public AutoRefreshListGridDataSource(String ceilingEntityFullyQualifiedClassname, String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
		super(ceilingEntityFullyQualifiedClassname, name, persistencePerspective, service, modules);
	}

	@Override
	protected void executeAdd(String requestId, DSRequest request, final DSResponse response, String[] customCriteria, final AsyncCallback<DataSource> cb) {
		super.executeAdd(requestId, request, response, customCriteria, new AsyncCallbackAdapter() {
			public void onSuccess(DataSource arg0) {
				response.setInvalidateCache(true);
				if (cb != null) {
					cb.onSuccess(arg0);
				}
			}

			@Override
			public void onFailure(Throwable arg0) {
				if (cb != null) {
					cb.onFailure(arg0);
				}
			}
			
		});
	}

	@Override
	protected void executeUpdate(String requestId, DSRequest request, final DSResponse response, String[] customCriteria, final AsyncCallback<DataSource> cb) {
		super.executeUpdate(requestId, request, response, customCriteria, new AsyncCallbackAdapter() {
			public void onSuccess(DataSource arg0) {
				response.setInvalidateCache(true);
				if (cb != null) {
					cb.onSuccess(arg0);
				}
			}

			@Override
			public void onFailure(Throwable arg0) {
				if (cb != null) {
					cb.onFailure(arg0);
				}
			}
			
		});
	}
	
	
}
