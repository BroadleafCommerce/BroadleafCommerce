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

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.TabSet;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.openadmin.client.view.dynamic.SubItemDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.SubItemView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormOnlyView;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.ExpandableGridStructureDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.ExpandableGridStructureView;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureView;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * 
 * @author jfischer
 *
 */
public class OneToOneProductSkuView extends VLayout implements Instantiable, OneToOneProductSkuDisplay {
	
	protected DynamicFormView dynamicFormDisplay;
	protected GridStructureView crossSaleDisplay;
	protected GridStructureView upSaleDisplay;
	protected GridStructureView mediaDisplay;
	protected DynamicEntityListView listDisplay;
	protected GridStructureView attributesDisplay;
	protected GridStructureView allCategoriesDisplay;
	protected ExpandableGridStructureView productOptionsDisplay;
	protected ToolStripButton generateSkusButton;
	protected SubItemView skusDisplay;
	protected GridStructureView bundleItemsDisplay;
	protected ToolStripButton cloneProductButton;
	protected ToolStripButton exportProductsButton;
    
	public OneToOneProductSkuView() {
		setHeight100();
		setWidth100();
	}
	
	@Override
    public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
		VLayout leftVerticalLayout = new VLayout();
		leftVerticalLayout.setID("productSkuLeftVerticalLayout");
		leftVerticalLayout.setHeight("25%");
        leftVerticalLayout.setWidth100();
		//leftVerticalLayout.setHeight100();
		//leftVerticalLayout.setWidth("50%");
		leftVerticalLayout.setShowResizeBar(true);
        
		listDisplay = new DynamicEntityListView(BLCMain.getMessageManager().getString("productsListTitle"), entityDataSource);
		cloneProductButton = new ToolStripButton(BLCMain.getMessageManager().getString("cloneButtonTitle"));
        cloneProductButton.disable();
		exportProductsButton = new ToolStripButton(BLCMain.getMessageManager().getString("exportProductsButtonTitle"));
		listDisplay.getToolBar().addButton(cloneProductButton);
		listDisplay.getToolBar().addButton(exportProductsButton);
		leftVerticalLayout.addMember(listDisplay);
        
        TabSet topTabSet = new TabSet(); 
        topTabSet.setID("productSkuTopTabSet");
        topTabSet.setTabBarPosition(Side.TOP);  
        topTabSet.setPaneContainerOverflow(Overflow.HIDDEN);
        topTabSet.setHeight("75%");
        topTabSet.setWidth100();
        //topTabSet.setWidth("50%");
        //topTabSet.setHeight100();
        topTabSet.setPaneMargin(0);
        
        Tab detailsTab = new Tab(BLCMain.getMessageManager().getString("detailsTabTitle"));
        detailsTab.setID("productSkuDetailsTab");
        
        dynamicFormDisplay = new DynamicFormView(BLCMain.getMessageManager().getString("productDetailsTitle"), entityDataSource);
        attributesDisplay = new GridStructureView(BLCMain.getMessageManager().getString("productAttributesTitle"), false, true);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(attributesDisplay);
        bundleItemsDisplay = new GridStructureView(BLCMain.getMessageManager().getString("productBundleItemsTitle"), false, true);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(bundleItemsDisplay);
        detailsTab.setPane(dynamicFormDisplay);
        
        Tab crossSaleTab = new Tab(BLCMain.getMessageManager().getString("featuredTabTitle")); 
        crossSaleTab.setID("productSkuCrossSaleTab");
        
        VLayout crossLayout = new VLayout();
        crossLayout.setID("productSkuCrossLayout");
        crossLayout.setHeight100();
        crossLayout.setWidth100();
        crossLayout.setStyleName("blcFormBg");
        crossLayout.setOverflow(Overflow.AUTO);
        
        crossSaleDisplay = new GridStructureView(BLCMain.getMessageManager().getString("crossSaleProductsTitle"), true, true);
        crossLayout.addMember(crossSaleDisplay);
        
        upSaleDisplay = new GridStructureView(BLCMain.getMessageManager().getString("upsaleProductsTitle"), true, true);
        crossLayout.addMember(upSaleDisplay);
        
        crossSaleTab.setPane(crossLayout);
        
        Tab mediaTab = new Tab(BLCMain.getMessageManager().getString("mediaTabTitle"));
        mediaTab.setID("productSkuMediaTab");
        
        VLayout mediaLayout = new VLayout();
        mediaLayout.setID("productSkuMediaLayout");
        mediaLayout.setHeight100();
        mediaLayout.setWidth100();
        mediaLayout.setStyleName("blcFormBg");
        mediaLayout.setOverflow(Overflow.AUTO);
        
        Tab productOptionsTab = new Tab(BLCMain.getMessageManager().getString("productOptionsTabTitle"));
        productOptionsTab.setID("productSkuProductOptionsTab");
        
        VLayout productOptionsLayout = new VLayout();
        productOptionsLayout.setID("productSkuProductOptionsLayout");
        productOptionsLayout.setHeight100();
        productOptionsLayout.setWidth100();
        productOptionsLayout.setStyleName("blcFormBg");
        productOptionsLayout.setOverflow(Overflow.AUTO);
        
        productOptionsDisplay = new ExpandableGridStructureView(BLCMain.getMessageManager().getString("productOptionsListTitle"), false, true);
        generateSkusButton = new ToolStripButton(BLCMain.getMessageManager().getString("generateSkusButtonTitle"));
        productOptionsDisplay.getToolBar().addButton(generateSkusButton);
        productOptionsLayout.addMember(productOptionsDisplay);
        productOptionsTab.setPane(productOptionsLayout);

        mediaDisplay = new GridStructureView(BLCMain.getMessageManager().getString("mediaListTitle"), false, true);
        mediaLayout.addMember(mediaDisplay);
        
        mediaTab.setPane(mediaLayout);
        
        Tab categoriesTab = new Tab(BLCMain.getMessageManager().getString("categoriesTabTitle"));
        categoriesTab.setID("productSkuCategoriesTab");
        
        VLayout categoriesLayout = new VLayout();
        categoriesLayout.setID("productSkuCategoriesLayout");
        categoriesLayout.setHeight100();
        categoriesLayout.setWidth100();
        categoriesLayout.setStyleName("blcFormBg");
        categoriesLayout.setOverflow(Overflow.AUTO);
        
        allCategoriesDisplay = new GridStructureView(BLCMain.getMessageManager().getString("allParentCategoriesListTitle"), false, false);
        categoriesLayout.addMember(allCategoriesDisplay);
        
        categoriesTab.setPane(categoriesLayout);
        
        Tab skusTab = new Tab(BLCMain.getMessageManager().getString("skusTabTitle"));
        skusTab.setID("skusTab");
        skusDisplay = new SubItemView(BLCMain.getMessageManager().getString("skusListTitle"), false, true);
        skusTab.setPane(skusDisplay);
        
        topTabSet.addTab(detailsTab);
        topTabSet.addTab(crossSaleTab);
        topTabSet.addTab(mediaTab);
        topTabSet.addTab(categoriesTab);
        topTabSet.addTab(productOptionsTab);
        topTabSet.addTab(skusTab);
        
        addMember(leftVerticalLayout);
        addMember(topTabSet);
	}

	@Override
    public Canvas asCanvas() {
		return this;
	}

	@Override
    public GridStructureDisplay getCrossSaleDisplay() {
		return crossSaleDisplay;
	}

	@Override
    public GridStructureDisplay getUpSaleDisplay() {
		return upSaleDisplay;
	}

	@Override
    public GridStructureDisplay getMediaDisplay() {
		return mediaDisplay;
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
    public GridStructureDisplay getAttributesDisplay() {
		return attributesDisplay;
	}

	@Override
    public GridStructureDisplay getAllCategoriesDisplay() {
		return allCategoriesDisplay;
	}

	@Override
    public ExpandableGridStructureDisplay getProductOptionsDisplay() {
	    return productOptionsDisplay;
	}
	
	@Override
    public ToolStripButton getGenerateSkusButton() {
	    return generateSkusButton;
	}
	
	@Override
    public SubItemDisplay getSkusDisplay() {
        return skusDisplay;
    }
	
	@Override
    public GridStructureDisplay getBundleItemsDisplay() {
	    return bundleItemsDisplay;
	}
	
	@Override
	public ToolStripButton getCloneProductButton() {
	    return cloneProductButton;
	}
	
	@Override
	public ToolStripButton getExportProductsButton() {
	    return exportProductsButton;
	}

}
