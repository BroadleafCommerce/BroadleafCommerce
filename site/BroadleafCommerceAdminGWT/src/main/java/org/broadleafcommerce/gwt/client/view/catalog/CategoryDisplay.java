package org.broadleafcommerce.gwt.client.view.catalog;

import org.broadleafcommerce.gwt.client.view.dynamic.DynamicListDisplay;

import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.tree.TreeGrid;

public interface CategoryDisplay extends DynamicListDisplay<TreeGrid> {

	public ListGrid getAllChildCategoryGrid();

	public ToolStrip getAllChildCategoryToolBar();
	
	public ToolStripButton getAddChildCategoryButton();
	
	public ToolStripButton getRemoveChildCategoryButton();
	
	public ToolStripButton getRemoveOrphanedButton();

	public ListGrid getOrphanedCategoryGrid();
	
	public ToolStripButton getInsertOrphanButton();
	
	public ToolStripButton getAddDefaultParentCategoryButton();

	public TextItem getDefaultParentCategoryTextItem();
	
	public ToolStrip getFeaturedProductToolBar();

	public ToolStripButton getAddFeaturedProductButton();

	public ToolStripButton getRemoveFeaturedProductButton();

	public ListGrid getFeaturedProductGrid();
	
	public ToolStrip getMediaToolBar();

	public ToolStripButton getAddMediaButton();

	public ToolStripButton getRemoveMediaButton();

	public ListGrid getMediaGrid();
	
}
