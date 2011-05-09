package org.broadleafcommerce.gwt.client.datasource.catalog;

import org.broadleafcommerce.gwt.client.datasource.CeilingEntities;
import org.broadleafcommerce.gwt.client.datasource.EntityImplementations;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.BasicEntityModule;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.JoinTableModule;
import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.relations.JoinTable;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspectiveItemType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationTypes;
import org.broadleafcommerce.gwt.client.service.AppServices;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;

public class CategoryListDataSourceFactory {

	public static final String symbolName = "allParentCategories";
	public static final String linkedObjectPath = "categoryXrefPK.category";
	public static final String linkedIdProperty = "id";
	public static final String targetObjectPath = "categoryXrefPK.subCategory";
	public static final String targetIdProperty = "id";
	public static final String sortField = "displayOrder";
	
	public static void createDataSource(String name, AsyncCallback<DataSource> cb) {
		OperationTypes operationTypes = new OperationTypes(OperationType.JOINTABLE, OperationType.JOINTABLE, OperationType.JOINTABLE, OperationType.JOINTABLE, OperationType.ENTITY);
		createDataSource(name, operationTypes, cb);
	}
	
	public static void createDataSource(String name, OperationTypes operationTypes, AsyncCallback<DataSource> cb) {
		PersistencePerspective persistencePerspective = new PersistencePerspective(operationTypes, new String[]{}, new ForeignKey[]{new ForeignKey(CategoryTreeDataSourceFactory.defaultParentCategoryForeignKey, EntityImplementations.CATEGORY, null)});
		persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.JOINTABLE, new JoinTable(symbolName, linkedObjectPath, linkedIdProperty, targetObjectPath, targetIdProperty, EntityImplementations.CATEGORY_XREF, sortField, true));
		DataSourceModule[] modules = new DataSourceModule[]{
			new BasicEntityModule(CeilingEntities.CATEGORY, persistencePerspective, AppServices.DYNAMIC_ENTITY),
			new JoinTableModule(CeilingEntities.CATEGORY, persistencePerspective, AppServices.DYNAMIC_ENTITY)
		};
		ListGridDataSource dataSource = new ListGridDataSource(CeilingEntities.CATEGORY, name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules);
		dataSource.buildFields(cb);
	}

}
