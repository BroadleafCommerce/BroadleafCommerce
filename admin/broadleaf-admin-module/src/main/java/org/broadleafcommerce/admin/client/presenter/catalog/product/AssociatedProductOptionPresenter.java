/*
 * Copyright 2012 the original author or authors.
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

package org.broadleafcommerce.admin.client.presenter.catalog.product;

import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.presenter.structure.SimpleSearchJoinStructureAndListPresenter;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.ExpandableGridStructureDisplay;

/**
 * Represents the Product -> ProductOption presenter that has a nested grid for the ProductOptionValues
 * 
 * @author Phillip Verheyden
 *
 */
public class AssociatedProductOptionPresenter extends SimpleSearchJoinStructureAndListPresenter {
    
    public AssociatedProductOptionPresenter(ExpandableGridStructureDisplay display, EntitySearchDialog searchDialog, String searchDialogTitle) {
        super(display, searchDialog, searchDialogTitle);
    }
    
    public void setExpansionDataSource(ListGridDataSource dataSource, String[] gridFields, Boolean[] editable) {
        ExpandableGridStructureDisplay expandableDisplay = (ExpandableGridStructureDisplay)display;
        expandableDisplay.getExpansionGrid().setDataSource(dataSource);
        dataSource.setAssociatedGrid(expandableDisplay.getExpansionGrid());
        dataSource.setupGridFields(gridFields, editable);
    }
    
}