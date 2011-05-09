package org.broadleafcommerce.gwt.client.view.catalog.category;

import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEditDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.grid.GridStructureDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.grid.GridStructureView;

import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public interface CategoryDisplay extends DynamicEditDisplay {
	
	public ToolStripButton getRemoveOrphanedButton();

	public ListGrid getOrphanedCategoryGrid();
	
	public ToolStripButton getInsertOrphanButton();
	
	public DynamicEntityListDisplay getListDisplay();
	
	public GridStructureDisplay getMediaDisplay();

	public DynamicFormDisplay getDynamicFormDisplay();
	
	public GridStructureDisplay getFeaturedDisplay();
	
	public GridStructureDisplay getAllCategoriesDisplay();
	
	public GridStructureView getAllProductsDisplay();
	
}
