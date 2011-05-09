package org.broadleafcommerce.gwt.client.datasource.dynamic;

import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;

public class DataSourceFactory {

	public static void createDataSource(String ceilingEntityFullyQualifiedClassname, String name, DynamicEntityServiceAsync service, PersistencePerspective persistencePerspective, AsyncCallback<DataSource> cb, DataSourceModule[] modules) {
		DynamicEntityDataSource dataSource = new DynamicEntityDataSource(ceilingEntityFullyQualifiedClassname, name, persistencePerspective, service, modules);
		dataSource.buildFields(null, cb);
	}
	
}
