package org.broadleafcommerce.gwt.client.view.dynamic;

import org.broadleafcommerce.gwt.client.view.Display;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public interface DynamicListDisplay<GRID extends ListGrid> extends Display {

	public ToolStripButton getAddButton();

	public GRID getGrid();

	public DynamicForm getDynamicForm();

	public ToolStripButton getSaveFormButton();

	public SelectItem getEntityType();

	public ToolStrip getFormToolBar();

	public ToolStripButton getRefreshButton();

	public ToolStripButton getRemoveButton();

	public void build(DataSource dataSource);
	
}
