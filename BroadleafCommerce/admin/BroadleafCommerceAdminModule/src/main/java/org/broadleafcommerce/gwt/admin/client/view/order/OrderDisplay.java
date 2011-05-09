package org.broadleafcommerce.gwt.admin.client.view.order;

import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEditDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.SubItemDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.grid.GridStructureDisplay;

public interface OrderDisplay extends DynamicEditDisplay {
	
	public DynamicFormDisplay getDynamicFormDisplay();

	public DynamicEntityListDisplay getListDisplay();

	public OrderItemDisplay getOrderItemsDisplay();
	
	public SubItemDisplay getFulfillmentGroupDisplay();
	
	public SubItemDisplay getPaymentInfoDisplay();
	
	public GridStructureDisplay getAdditionalAttributesDisplay();
	
	public SubItemDisplay getOfferCodeDisplay();
	
	public GridStructureDisplay getOrderAdjustmentDisplay();
	
	public GridStructureDisplay getOrderItemAdjustmentDisplay();
	
	public GridStructureDisplay getFulfillmentGroupAdjustmentDisplay();
	
}