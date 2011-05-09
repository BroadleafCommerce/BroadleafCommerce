package org.broadleafcommerce.gwt.client.datasource.catalog;

import org.broadleafcommerce.gwt.client.datasource.CeilingEntities;
import org.broadleafcommerce.gwt.client.datasource.results.RemoveType;
import org.broadleafcommerce.gwt.client.service.AppServices;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;

public class CategoryListDataSourceFactory {

	public static void createDataSource(String name, RemoveType removeType, AsyncCallback<DataSource> cb) {
		CategoryListDataSource dataSource = new CategoryListDataSource(CeilingEntities.CATEGORY, name, removeType, AppServices.DYNAMIC_ENTITY);
		dataSource.buildFields(cb);
	}

}
