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

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.TabSet;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityTreeView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormOnlyView;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureView;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * 
 * @author jfischer
 *
 */
public class CategoryView extends HLayout implements Instantiable, CategoryDisplay {
    
    protected DynamicEntityTreeView listDisplay;
    protected DynamicFormView dynamicFormDisplay;
    protected GridStructureView mediaDisplay;
    protected GridStructureView featuredDisplay;
    protected GridStructureView allCategoriesDisplay;
    protected GridStructureView allProductsDisplay;
    protected GridStructureView crossSaleDisplay;
    protected GridStructureView upSaleDisplay;
    protected ToolStripButton removeOrphanedButton;
    protected ToolStripButton insertOrphanButton;
    protected ListGrid orphanedCategoryGrid;
    protected TabSet topTabSet;
    public CategoryView() {
        setHeight100();
        setWidth100();
    }
    
    @Override
    public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
        VLayout leftVerticalLayout = new VLayout();
        leftVerticalLayout.setID("categoryLeftVerticalLayout");
        leftVerticalLayout.setHeight100();
        leftVerticalLayout.setWidth("50%");
        leftVerticalLayout.setShowResizeBar(true);
        
        listDisplay = new DynamicEntityTreeView(BLCMain.getMessageManager().getString("categoryListTitle"), entityDataSource);
        listDisplay.setShowResizeBar(true);
        leftVerticalLayout.addMember(listDisplay);
        
        VLayout abandonedCategoryVerticalLayout = new VLayout();
        abandonedCategoryVerticalLayout.setID("abandonedCategoryVerticalLayout");
        abandonedCategoryVerticalLayout.setHeight("30%");
        ToolStrip abandonedCategoryTopBar = new ToolStrip();
        abandonedCategoryTopBar.setID("abandonedCategoryTopBar");
        abandonedCategoryTopBar.setHeight(20);
        abandonedCategoryTopBar.setWidth100();
        abandonedCategoryTopBar.addSpacer(6);
        insertOrphanButton = new ToolStripButton();  
        insertOrphanButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/double_arrow_up.png");  
        insertOrphanButton.setDisabled(true);
        abandonedCategoryTopBar.addButton(insertOrphanButton);
        removeOrphanedButton = new ToolStripButton(); 
        removeOrphanedButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/minus.png"); 
        removeOrphanedButton.setDisabled(true);
        abandonedCategoryTopBar.addButton(removeOrphanedButton);
        abandonedCategoryTopBar.addSpacer(6);
        Label abandonedLabel = new Label();
        abandonedLabel.setContents(BLCMain.getMessageManager().getString("orphanCategoryListTitle"));
        abandonedLabel.setWrap(false);
        abandonedCategoryTopBar.addMember(abandonedLabel);
        abandonedCategoryVerticalLayout.addMember(abandonedCategoryTopBar);
        orphanedCategoryGrid = new ListGrid();
        orphanedCategoryGrid.setAlternateRecordStyles(true);
        orphanedCategoryGrid.setSelectionType(SelectionStyle.SINGLE);
        orphanedCategoryGrid.setDrawAheadRatio(4);
        orphanedCategoryGrid.setCanSort(false);
        orphanedCategoryGrid.setCellPadding(5);
        orphanedCategoryGrid.setCanGroupBy(false);
        abandonedCategoryVerticalLayout.addMember(orphanedCategoryGrid);
        
        leftVerticalLayout.addMember(abandonedCategoryVerticalLayout);
        
         topTabSet = new TabSet(); 
        topTabSet.setID("categoryTopTabSet");
        topTabSet.setTabBarPosition(Side.TOP);  
        topTabSet.setPaneContainerOverflow(Overflow.HIDDEN);
        topTabSet.setWidth("50%");  
        topTabSet.setHeight100();
        topTabSet.setPaneMargin(0);
        
        Tab detailsTab = new Tab(BLCMain.getMessageManager().getString("detailsTabTitle"));
        detailsTab.setID("categoryDetailsTab");
        
        dynamicFormDisplay = new DynamicFormView(BLCMain.getMessageManager().getString("categoryDetailsTitle"), entityDataSource);
        
        allCategoriesDisplay = new GridStructureView(BLCMain.getMessageManager().getString("allChildCategoriesListTitle"), true, false);
        
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(allCategoriesDisplay);
        detailsTab.setPane(dynamicFormDisplay);
        
        Tab crossSaleTab = new Tab(BLCMain.getMessageManager().getString("featuredTabTitle")); 
        crossSaleTab.setID("productSkuCrossSaleTab");
        
        VLayout crossLayout = new VLayout();
        crossLayout.setID("productSkuCrossLayout");
        crossLayout.setHeight100();
        crossLayout.setWidth100();
        crossLayout.setBackgroundColor("#eaeaea");
        crossLayout.setOverflow(Overflow.AUTO);
        
        crossSaleDisplay = new GridStructureView(BLCMain.getMessageManager().getString("crossSaleProductsTitle"), true, true);
        crossLayout.addMember(crossSaleDisplay);
        
        upSaleDisplay = new GridStructureView(BLCMain.getMessageManager().getString("upsaleProductsTitle"), true, true);
        crossLayout.addMember(upSaleDisplay);
        featuredDisplay = new GridStructureView(BLCMain.getMessageManager().getString("featuredProductsListTitle"), true, true);
        crossLayout.addMember(featuredDisplay);
        crossSaleTab.setPane(crossLayout); 
        
        Tab featuredTab = new Tab(BLCMain.getMessageManager().getString("productsTabTitle"));
        featuredTab.setID("categoryFeaturedTab");
        
        VLayout featuredLayout = new VLayout();
        featuredLayout.setID("categoryFeaturedLayout");
        featuredLayout.setHeight100();
        featuredLayout.setWidth100();
        featuredLayout.setBackgroundColor("#eaeaea");
        featuredLayout.setOverflow(Overflow.AUTO);
        
        
        allProductsDisplay = new GridStructureView(BLCMain.getMessageManager().getString("allProductsListTitle"), true, false);
        featuredLayout.addMember(allProductsDisplay);
        
        featuredTab.setPane(featuredLayout);
        
        Tab mediaTab = new Tab(BLCMain.getMessageManager().getString("mediaTabTitle"));
        mediaTab.setID("categoryMediaTab");
        
        VLayout mediaLayout = new VLayout();
        mediaLayout.setID("categoryMediaLayout");
        mediaLayout.setHeight100();
        mediaLayout.setWidth100();
        mediaLayout.setBackgroundColor("#eaeaea");
        mediaLayout.setOverflow(Overflow.AUTO);
        
        mediaDisplay = new GridStructureView(BLCMain.getMessageManager().getString("mediaListTitle"), false, true);
        mediaLayout.addMember(mediaDisplay);
        
        mediaTab.setPane(mediaLayout);
        
        topTabSet.addTab(detailsTab);
        topTabSet.addTab(crossSaleTab);
        topTabSet.addTab(featuredTab);
        topTabSet.addTab(mediaTab);
        
        addMember(leftVerticalLayout);
        addMember(topTabSet);
    }

    @Override
    public Canvas asCanvas() {
        return this;
    }
    
    @Override
    public DynamicEntityListDisplay getListDisplay() {
        return listDisplay;
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
    public GridStructureDisplay getFeaturedDisplay() {
        return featuredDisplay;
    }

    @Override
    public ToolStripButton getRemoveOrphanedButton() {
        return removeOrphanedButton;
    }

    @Override
    public ListGrid getOrphanedCategoryGrid() {
        return orphanedCategoryGrid;
    }

    @Override
    public ToolStripButton getInsertOrphanButton() {
        return insertOrphanButton;
    }

    @Override
    public GridStructureDisplay getAllCategoriesDisplay() {
        return allCategoriesDisplay;
    }

    @Override
    public GridStructureView getAllProductsDisplay() {
        return allProductsDisplay;
    }
    @Override
    public GridStructureDisplay getCrossSaleDisplay() {
        return crossSaleDisplay;
    }

    @Override
    public GridStructureDisplay getUpSaleDisplay() {
        return upSaleDisplay;
    }
}
