/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.admin.client.view.catalog.product;

import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEditDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.SubItemDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.ExpandableGridStructureDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureDisplay;

import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * 
 * @author jfischer
 *
 */
public interface OneToOneProductSkuDisplay extends DynamicEditDisplay {

	public GridStructureDisplay getCrossSaleDisplay();
	
	public GridStructureDisplay getUpSaleDisplay();
	
	public GridStructureDisplay getMediaDisplay();
	
	public GridStructureDisplay getAttributesDisplay();
	
	public GridStructureDisplay getAllCategoriesDisplay();
	
	public ExpandableGridStructureDisplay getProductOptionsDisplay();
	
	public ToolStripButton getGenerateSkusButton();
	
	public SubItemDisplay getSkusDisplay();
	
	public GridStructureDisplay getBundleItemsDisplay();
	
	public ToolStripButton getCloneProductButton();

	public ToolStripButton getExportProductsButton();
}
