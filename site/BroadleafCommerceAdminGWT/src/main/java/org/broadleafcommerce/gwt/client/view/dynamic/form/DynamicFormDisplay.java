package org.broadleafcommerce.gwt.client.view.dynamic.form;

import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public interface DynamicFormDisplay {

	public ToolStrip getToolbar();

	public ToolStripButton getSaveButton();

	public ToolStripButton getRefreshButton();

	public FormOnlyDisplay getFormOnlyDisplay();
	
}
