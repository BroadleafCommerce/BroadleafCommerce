package org.broadleafcommerce.gwt.client.view.dynamic.grid;

import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public interface GridStructureWithTopLevelSearchDisplay extends GridStructureDisplay {

	public TextItem getTextItem();

	public ToolStripButton getTextItemButton();
	
}
