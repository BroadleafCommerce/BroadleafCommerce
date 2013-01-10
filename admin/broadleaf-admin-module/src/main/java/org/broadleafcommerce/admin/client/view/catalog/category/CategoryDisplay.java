/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.admin.client.view.catalog.category;

import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEditDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureView;

import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * 
 * @author jfischer
 *
 */
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
    
    public GridStructureDisplay getCrossSaleDisplay();
    
    public GridStructureDisplay getUpSaleDisplay();
}
