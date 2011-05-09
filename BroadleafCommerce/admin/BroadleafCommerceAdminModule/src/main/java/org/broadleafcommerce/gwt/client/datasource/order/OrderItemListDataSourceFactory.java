package org.broadleafcommerce.gwt.client.datasource.order;

import org.broadleafcommerce.gwt.client.datasource.CeilingEntities;
import org.broadleafcommerce.gwt.client.datasource.EntityImplementations;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.order.module.OrderItemEntityModule;
import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspectiveItemType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationTypes;
import org.broadleafcommerce.gwt.client.service.AppServices;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;

public class OrderItemListDataSourceFactory {

	public static final String foreignKeyName = "order";
	public static ListGridDataSource dataSource = null;
	
	public static void createDataSource(String name, AsyncCallback<DataSource> cb) {
		if (dataSource == null) {
			OperationTypes operationTypes = new OperationTypes(OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY, OperationType.ENTITY);
			PersistencePerspective persistencePerspective = new PersistencePerspective(operationTypes, new String[]{}, new ForeignKey[]{});
			persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, new ForeignKey(foreignKeyName, EntityImplementations.ORDER, null));
			persistencePerspective.setPopulateToOneFields(true);
			persistencePerspective.setExcludeFields(
				new String[]{
					"giftWrapOrderItem", 
					"bundleOrderItem", 
					"category", 
					"product.defaultCategory",
					"product.name",
					"product.description",
					"product.longDescription",
					"product.activeStartDate",
					"product.activeEndDate",
					"sku.name",
					"sku.salePrice",
					"sku.retailPrice",
					"product.sku"
				}
			);
			DataSourceModule[] modules = new DataSourceModule[]{
				new OrderItemEntityModule(CeilingEntities.ORDER_ITEM, persistencePerspective, AppServices.DYNAMIC_ENTITY)
			};
			dataSource = new ListGridDataSource(name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules);
			dataSource.buildFields(null, false, cb);
		} else {
			if (cb != null) {
				cb.onSuccess(dataSource);
			}
		}
	}

}
