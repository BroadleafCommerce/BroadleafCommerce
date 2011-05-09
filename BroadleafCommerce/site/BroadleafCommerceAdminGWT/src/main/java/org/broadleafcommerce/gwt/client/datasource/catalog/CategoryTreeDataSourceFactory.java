package org.broadleafcommerce.gwt.client.datasource.catalog;

import org.broadleafcommerce.gwt.client.datasource.CeilingEntities;
import org.broadleafcommerce.gwt.client.datasource.results.RemoveType;
import org.broadleafcommerce.gwt.client.service.AppServices;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;

public class CategoryTreeDataSourceFactory {

	public static void createDataSource(String name, RemoveType removeType, AsyncCallback<DataSource> cb) {
		CategoryTreeDataSource dataSource = new CategoryTreeDataSource(CeilingEntities.CATEGORY, name, removeType, AppServices.DYNAMIC_ENTITY);
		dataSource.buildFields(cb);
	}

}
