package org.broadleafcommerce.gwt.client.view.dynamic;

import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public interface DynamicEntityListDisplay {

	public abstract ToolStripButton getAddButton();

	public abstract ToolStripButton getRemoveButton();

	public abstract SelectItem getEntityType();

	public abstract ListGrid getGrid();
	
	public abstract ToolStrip getToolBar();

}