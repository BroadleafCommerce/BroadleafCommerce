package org.broadleafcommerce.gwt.client.view.promotion.offer;

import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEditDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;

import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public interface OfferDisplay extends DynamicEditDisplay {

	public DynamicFormDisplay getDynamicFormDisplay();

	public DynamicEntityListDisplay getListDisplay();
	
	public ImgButton getOrderButton();

	public FilterBuilder getOrderFilterBuilder();

	public ImgButton getOrderItemButton();

	public FilterBuilder getOrderItemFilterBuilder();

	public ImgButton getFulfillmentGroupButton();

	public FilterBuilder getFulfillmentGroupFilterBuilder();

	public ImgButton getCustomerButton();

	public FilterBuilder getCustomerFilterBuilder();
	
	public ToolStrip getRulesToolbar();

	public ToolStripButton getRulesSaveButton();

	public ToolStripButton getRulesRefreshButton();

	public ToolStripButton getRulesBuilderButton();
	
	public void showFilterBuilder();
	
	public void showRawFields();
	
}