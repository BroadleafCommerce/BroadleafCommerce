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
import org.broadleafcommerce.openadmin.client.view.TabSet;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.openadmin.client.view.dynamic.SubItemView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormOnlyView;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureView;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

/**
 * 
 * @author Phillip Verheyden
 *
 */
public class ProductOptionView extends HLayout implements Instantiable, ProductOptionDisplay {
    
    protected DynamicFormView dynamicFormDisplay;
    protected DynamicEntityListView listDisplay;
    protected SubItemView productOptionValueDisplay;
    //protected GridStructureView priceAdjustmentDisplay;
    protected GridStructureView translationsDisplay;
    
    public ProductOptionView() {
        setHeight100();
        setWidth100();
    }
    
    @Override
    public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
        VLayout leftVerticalLayout = new VLayout();
        leftVerticalLayout.setID("productOptionLeftVerticalLayout");
        leftVerticalLayout.setHeight100();
        leftVerticalLayout.setWidth("50%");
        leftVerticalLayout.setShowResizeBar(true);
        
        listDisplay = new DynamicEntityListView(BLCMain.getMessageManager().getString("productOptionListTitle"), entityDataSource);
        leftVerticalLayout.addMember(listDisplay);

       
        
        TabSet topTabSet = new TabSet(); 
        topTabSet.setID("productOptionTopTabSet");
        topTabSet.setTabBarPosition(Side.TOP);  
        topTabSet.setPaneContainerOverflow(Overflow.HIDDEN);
        topTabSet.setWidth("50%");  
        topTabSet.setHeight100();
        topTabSet.setPaneMargin(0); 
        
        Tab detailsTab = new Tab(BLCMain.getMessageManager().getString("detailsTabTitle"));
        detailsTab.setID("productOptionDetailsTab");
        
        dynamicFormDisplay = new DynamicFormView(BLCMain.getMessageManager().getString("productOptionDetailsTitle"), entityDataSource);
        detailsTab.setPane(dynamicFormDisplay);
        
        Tab productOptionValueTab = new Tab(BLCMain.getMessageManager().getString("productOptionDetailsTitle")); 
        productOptionValueTab.setID("productOptionValueTab");
        productOptionValueDisplay = new SubItemView(BLCMain.getMessageManager().getString("productOptionValuesTitle"), true, true);
        productOptionValueDisplay.setID("productOptionValueTabSubView");
        translationsDisplay = new GridStructureView(BLCMain.getMessageManager().getString("productOptionImpl_Translations"), false, true);
        
           ((FormOnlyView) productOptionValueDisplay.getFormOnlyDisplay()).addMember(translationsDisplay);
        productOptionValueTab.setPane(productOptionValueDisplay);
        
        topTabSet.addTab(detailsTab);
        topTabSet.addTab(productOptionValueTab);
        
        
        
        leftVerticalLayout.setParentElement(this);
        addMember(leftVerticalLayout);
        addMember(topTabSet);
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
    public SubItemView getProductOptionValueDisplay() {
       return productOptionValueDisplay;
    }

    @Override
    public GridStructureView getTranslationsDisplay() {
        return translationsDisplay;
    }

    public void setTranslationsDisplay(GridStructureView translationsDisplay) {
        this.translationsDisplay = translationsDisplay;
    }

    
}
