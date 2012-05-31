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

package org.broadleafcommerce.admin.client.view.catalog.product;

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormOnlyView;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureView;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * 
 * @author Phillip Verheyden
 *
 */
public class ProductOptionView extends HLayout implements Instantiable, ProductOptionDisplay {
    
    protected DynamicFormView dynamicFormDisplay;
    protected DynamicEntityListView listDisplay;
    protected GridStructureView productOptionValuesDisplay;
    
    public ProductOptionView() {
        setHeight100();
        setWidth100();
    }
    
    public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
        VLayout leftVerticalLayout = new VLayout();
        leftVerticalLayout.setID("productOptionLeftVerticalLayout");
        leftVerticalLayout.setHeight100();
        leftVerticalLayout.setWidth("50%");
        leftVerticalLayout.setShowResizeBar(true);
        
        listDisplay = new DynamicEntityListView(BLCMain.getMessageManager().getString("productOptionListTitle"), entityDataSource);
        leftVerticalLayout.addMember(listDisplay);

        VLayout rightVerticalLayout = new VLayout();
        rightVerticalLayout.setID("productOptionVerticalLayout");
        rightVerticalLayout.setHeight100();
        rightVerticalLayout.setWidth("50%");
        dynamicFormDisplay = new DynamicFormView(BLCMain.getMessageManager().getString("productOptionDetailsTitle"), entityDataSource);

        productOptionValuesDisplay = new GridStructureView(BLCMain.getMessageManager().getString("productOptionValuesTitle"), false, true);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(productOptionValuesDisplay);
        
        rightVerticalLayout.addMember(dynamicFormDisplay);
        
        addMember(leftVerticalLayout);
        addMember(rightVerticalLayout);
    }
    
    @Override
    public Canvas asCanvas() {
        return this;
    }
    
    @Override
    public DynamicFormDisplay getDynamicFormDisplay() {
        return dynamicFormDisplay;
    }
    
    @Override
    public DynamicEntityListDisplay getListDisplay() {
        return listDisplay;
    }
    
    @Override
    public GridStructureDisplay getProductOptionValuesDisplay() {
        return productOptionValuesDisplay;
    }
    
}
