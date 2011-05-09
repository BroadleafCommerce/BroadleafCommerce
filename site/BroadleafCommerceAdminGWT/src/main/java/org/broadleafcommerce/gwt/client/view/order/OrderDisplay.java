package org.broadleafcommerce.gwt.client.view.order;

import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEditDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;

import com.smartgwt.client.data.DataSource;

public interface OrderDisplay extends DynamicEditDisplay {

	public void build(final DataSource dataSource);
	
	public DynamicFormDisplay getDynamicFormDisplay();

	public DynamicEntityListDisplay getListDisplay();

	public OrderItemGridStructureDisplay getOrderItemsDisplay();
	
	public FulfillmentGroupDisplay getFulfillmentGroupDisplay();
	
}