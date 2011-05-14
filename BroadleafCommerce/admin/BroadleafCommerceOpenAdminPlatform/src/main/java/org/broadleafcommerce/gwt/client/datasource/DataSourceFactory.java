package org.broadleafcommerce.gwt.client.datasource;

import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationTypes;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;

public interface DataSourceFactory {

	public void createDataSource(String name, OperationTypes operationTypes, Object[] additionalItems, AsyncCallback<DataSource> cb);
	
}
