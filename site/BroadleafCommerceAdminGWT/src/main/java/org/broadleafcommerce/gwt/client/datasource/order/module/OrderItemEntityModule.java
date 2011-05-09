package org.broadleafcommerce.gwt.client.datasource.order.module;

import java.util.Arrays;

import org.broadleafcommerce.gwt.client.datasource.EntityImplementations;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.BasicEntityModule;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class OrderItemEntityModule extends BasicEntityModule {

	/**
	 * @param ceilingEntityFullyQualifiedClassname
	 * @param persistencePerspective
	 * @param service
	 */
	public OrderItemEntityModule(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service) {
		super(ceilingEntityFullyQualifiedClassname, persistencePerspective, service);
	}

	@Override
	public Record updateRecord(Entity entity, Record record, Boolean updateId) {
		Record temp = super.updateRecord(entity, record, updateId);
		if (Arrays.binarySearch(entity.getType(), EntityImplementations.BUNDLE_ORDER_ITEM) >= 0) {
			((ListGridRecord) temp).setCanExpand(true);
		} else {
			((ListGridRecord) temp).setCanExpand(false);
		}
		return temp;
	}

}
