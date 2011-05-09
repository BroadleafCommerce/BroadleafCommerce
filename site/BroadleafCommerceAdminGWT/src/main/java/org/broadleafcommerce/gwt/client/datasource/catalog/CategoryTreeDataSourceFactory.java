package org.broadleafcommerce.gwt.client.datasource.catalog;

import org.broadleafcommerce.gwt.client.datasource.CeilingEntities;
import org.broadleafcommerce.gwt.client.datasource.EntityImplementations;
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

public class CategoryTreeDataSourceFactory {

	public static final String hasChildrenProperty = "hasAllChildCategories";
	public static final String foreignKeyName = "allParentCategories";
	public static final String defaultParentCategoryForeignKey = "defaultParentCategory";
	
	public static void createDataSource(String name, String rootId, String rootName, AsyncCallback<DataSource> cb) {
		OperationTypes operationTypes = new OperationTypes(OperationType.JOINTABLE, OperationType.FOREIGNKEY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY);
		PersistencePerspective persistencePerspective = new PersistencePerspective(operationTypes, new String[] {hasChildrenProperty}, new ForeignKey[]{new ForeignKey(defaultParentCategoryForeignKey, EntityImplementations.CATEGORY, null)});
		persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, new ForeignKey(foreignKeyName, EntityImplementations.CATEGORY, null));
		persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.JOINTABLE, new JoinTable(foreignKeyName, "categoryXrefPK.category", "id", "categoryXrefPK.subCategory", "id", EntityImplementations.CATEGORY_XREF, "displayOrder", true));
		DataSourceModule[] modules = new DataSourceModule[]{
			new BasicEntityModule(CeilingEntities.CATEGORY, persistencePerspective, AppServices.DYNAMIC_ENTITY),
			new JoinTableModule(CeilingEntities.CATEGORY, persistencePerspective, AppServices.DYNAMIC_ENTITY)
		};
		CategoryTreeDataSource dataSource = new CategoryTreeDataSource(CeilingEntities.CATEGORY, name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules, rootId, rootName);
		dataSource.buildFields(cb);
	}

}
