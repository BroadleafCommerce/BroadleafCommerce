package org.broadleafcommerce.gwt.admin.client.datasource.promotion.offer;

import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;

public class OfferListDataSource extends ListGridDataSource {

	/**
	 * @param name
	 * @param persistencePerspective
	 * @param service
	 * @param modules
	 */
	public OfferListDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
		super(name, persistencePerspective, service, modules);
	}

	@Override
	public void buildFields(String[] customCriteria, Boolean overrideFieldSort, AsyncCallback<DataSource> cb) {
		super.buildFields(new String[]{"Offer"}, overrideFieldSort, cb);
	}

	@Override
	protected void executeFetch(String requestId, DSRequest request, DSResponse response, String[] customCriteria, AsyncCallback<DataSource> cb) {
		super.executeFetch(requestId, request, response, new String[]{"Offer"}, cb);
	}

	@Override
	protected void executeAdd(String requestId, DSRequest request, DSResponse response, String[] customCriteria, AsyncCallback<DataSource> cb) {
		super.executeAdd(requestId, request, response, new String[]{"Offer"}, cb);
	}

	@Override
	protected void executeUpdate(String requestId, DSRequest request, DSResponse response, String[] customCriteria, AsyncCallback<DataSource> cb) {
		super.executeUpdate(requestId, request, response, new String[]{"Offer"}, cb);
	}

	@Override
	protected void executeRemove(String requestId, DSRequest request, DSResponse response, String[] customCriteria, AsyncCallback<DataSource> cb) {
		super.executeRemove(requestId, request, response, new String[]{"Offer"}, cb);
	}

	
}
