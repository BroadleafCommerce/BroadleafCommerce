package org.broadleafcommerce.gwt.client.datasource.catalog;

import org.broadleafcommerce.gwt.client.datasource.CeilingEntities;
import org.broadleafcommerce.gwt.client.datasource.EntityImplementations;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.BasicEntityModule;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspectiveItemType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationTypes;
import org.broadleafcommerce.gwt.client.service.AppServices;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;

public class OrphanedCategoryListDataSourceFactory {

	public static final String foreignKeyName = "allParentCategories";
	
	public static void createDataSource(String name, String root, AsyncCallback<DataSource> cb) {
		OperationTypes operationTypes = new OperationTypes(OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY);
		PersistencePerspective persistencePerspective = new PersistencePerspective(operationTypes, new String[]{}, new ForeignKey[]{});
		persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, new ForeignKey(foreignKeyName, EntityImplementations.CATEGORY, null));
		DataSourceModule[] modules = new DataSourceModule[]{
			new BasicEntityModule(CeilingEntities.CATEGORY, persistencePerspective, AppServices.DYNAMIC_ENTITY)
		};
		OrphanedCategoryListDataSource dataSource = new OrphanedCategoryListDataSource(CeilingEntities.CATEGORY, root, name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules);
		dataSource.buildFields(cb);
	}

}
