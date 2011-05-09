package org.broadleafcommerce.gwt.admin.client.view.catalog.product;

import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEditDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.grid.GridStructureDisplay;

public interface OneToOneProductSkuDisplay extends DynamicEditDisplay {

	public GridStructureDisplay getCrossSaleDisplay();
	
	public GridStructureDisplay getUpSaleDisplay();
	
	public GridStructureDisplay getMediaDisplay();
	
	public GridStructureDisplay getAttributesDisplay();
	
	public GridStructureDisplay getAllCategoriesDisplay();
	
}
