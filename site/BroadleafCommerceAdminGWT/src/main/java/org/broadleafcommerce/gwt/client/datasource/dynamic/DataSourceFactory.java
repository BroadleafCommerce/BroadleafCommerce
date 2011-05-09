package org.broadleafcommerce.gwt.client.datasource.dynamic;

import org.broadleafcommerce.gwt.client.datasource.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.JoinTable;
import org.broadleafcommerce.gwt.client.datasource.results.RemoveType;
import org.broadleafcommerce.gwt.client.service.AppServices;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;

public class DataSourceFactory {

	public static void createDataSource(String ceilingEntityFullyQualifiedClassname, ForeignKey[] foreignFields, String name, DynamicEntityServiceAsync service, RemoveType removeType, String[] additionalNonPersistentProperties, AsyncCallback<DataSource> cb) {
		DynamicEntityDataSource dataSource = new DynamicEntityDataSource(ceilingEntityFullyQualifiedClassname, foreignFields, name, service, removeType, additionalNonPersistentProperties);
		dataSource.buildFields(cb);
	}
	
	public static void createDataSource(String ceilingEntityFullyQualifiedClassname, ForeignKey[] foreignFields, String name, RemoveType removeType, String[] additionalNonPersistentProperties, AsyncCallback<DataSource> cb) {
		DynamicEntityDataSource dataSource = new DynamicEntityDataSource(ceilingEntityFullyQualifiedClassname, foreignFields, name, AppServices.DYNAMIC_ENTITY, removeType, additionalNonPersistentProperties);
		dataSource.buildFields(cb);
	}
	
	public static void createDataSource(String ceilingEntityFullyQualifiedClassname, String name, RemoveType removeType, AsyncCallback<DataSource> cb) {
		DynamicEntityDataSource dataSource = new DynamicEntityDataSource(ceilingEntityFullyQualifiedClassname, new ForeignKey[]{}, name, AppServices.DYNAMIC_ENTITY, removeType, new String[]{});
		dataSource.buildFields(cb);
	}
	
	public static void createDataSource(String ceilingEntityFullyQualifiedClassname, String name, AsyncCallback<DataSource> cb) {
		DynamicEntityDataSource dataSource = new DynamicEntityDataSource(ceilingEntityFullyQualifiedClassname, new ForeignKey[]{}, name, AppServices.DYNAMIC_ENTITY, RemoveType.REGULAR, new String[]{});
		dataSource.buildFields(cb);
	}
	
	public static void createDataSource(String ceilingEntityFullyQualifiedClassname, JoinTable joinTable, String name, DynamicEntityServiceAsync service, RemoveType removeType, String[] additionalNonPersistentProperties, AsyncCallback<DataSource> cb) {
		DynamicEntityDataSource dataSource = new DynamicEntityDataSource(ceilingEntityFullyQualifiedClassname, joinTable, name, service, removeType, additionalNonPersistentProperties);
		dataSource.buildFields(cb);
	}
}
