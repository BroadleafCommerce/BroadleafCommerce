package org.broadleafcommerce.gwt.client.view.catalog;

import org.broadleafcommerce.gwt.client.view.dynamic.DynamicListDisplay;

import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.tree.TreeGrid;

public interface CategoryDisplay extends DynamicListDisplay<TreeGrid> {

	public ListGrid getAllParentCategoryGrid();

	public ToolStrip getAllParentCategoryToolBar();
	
	public ToolStripButton getAddParentCategoryButton();
	
	public ToolStripButton getRemoveParentCategoryButton();
	
}
