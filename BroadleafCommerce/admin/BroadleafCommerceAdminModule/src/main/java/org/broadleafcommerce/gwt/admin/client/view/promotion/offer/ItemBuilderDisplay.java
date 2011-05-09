package org.broadleafcommerce.gwt.admin.client.view.promotion.offer;

import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.form.fields.IntegerItem;

public interface ItemBuilderDisplay {

	public IntegerItem getItemQuantity();

	public FilterBuilder getItemFilterBuilder();

	public ImgButton getRemoveButton();
	
	public void enable();
	
	public void disable();
	
}