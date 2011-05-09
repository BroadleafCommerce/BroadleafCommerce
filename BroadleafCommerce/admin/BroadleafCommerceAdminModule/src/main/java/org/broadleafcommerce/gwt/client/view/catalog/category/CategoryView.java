package org.broadleafcommerce.gwt.client.view.catalog.category;

import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.client.view.TabSet;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityTreeView;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormView;
import org.broadleafcommerce.gwt.client.view.dynamic.form.FormOnlyView;
import org.broadleafcommerce.gwt.client.view.dynamic.grid.GridStructureDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.grid.GridStructureView;

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

public class CategoryView extends HLayout implements Instantiable, CategoryDisplay {
	
	protected DynamicEntityTreeView listDisplay;
	protected DynamicFormView dynamicFormDisplay;
	protected GridStructureView mediaDisplay;
	protected GridStructureView featuredDisplay;
	protected GridStructureView allCategoriesDisplay;
	protected GridStructureView allProductsDisplay;
	
	protected ToolStripButton removeOrphanedButton;
	protected ToolStripButton insertOrphanButton;
	protected ListGrid orphanedCategoryGrid;
	
	public CategoryView() {
		setHeight100();
		setWidth100();
	}
    
	public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
		VLayout leftVerticalLayout = new VLayout();
		leftVerticalLayout.setHeight100();
		leftVerticalLayout.setWidth("50%");
		leftVerticalLayout.setShowResizeBar(true);
		
		listDisplay = new DynamicEntityTreeView("Categories", entityDataSource);
		listDisplay.setShowResizeBar(true);
        leftVerticalLayout.addMember(listDisplay);
        
        VLayout abandonedCategoryVerticalLayout = new VLayout();
        abandonedCategoryVerticalLayout.setHeight("30%");
        ToolStrip abandonedCategoryTopBar = new ToolStrip();
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
        abandonedLabel.setContents("Orphaned Categories");
        abandonedLabel.setWrap(false);
        abandonedCategoryTopBar.addMember(abandonedLabel);
        abandonedCategoryVerticalLayout.addMember(abandonedCategoryTopBar);
        orphanedCategoryGrid = new ListGrid();
        orphanedCategoryGrid.setAlternateRecordStyles(true);
        orphanedCategoryGrid.setSelectionType(SelectionStyle.SINGLE);
        orphanedCategoryGrid.setDrawAheadRatio(4);
        orphanedCategoryGrid.setCanSort(false);
        orphanedCategoryGrid.setCellPadding(5);
        abandonedCategoryVerticalLayout.addMember(orphanedCategoryGrid);
        
        leftVerticalLayout.addMember(abandonedCategoryVerticalLayout);
        
        TabSet topTabSet = new TabSet();  
        topTabSet.setTabBarPosition(Side.TOP);  
        topTabSet.setPaneContainerOverflow(Overflow.HIDDEN);
        topTabSet.setWidth("50%");  
        topTabSet.setHeight100();
        topTabSet.setPaneMargin(0);
        
        Tab detailsTab = new Tab("Details");
        
        dynamicFormDisplay = new DynamicFormView("Category Details", entityDataSource);
        
        allCategoriesDisplay = new GridStructureView("All Child Categories", true, false);
        
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(allCategoriesDisplay);
        detailsTab.setPane(dynamicFormDisplay);
        
        Tab featuredTab = new Tab("Products"); 
        
        VLayout featuredLayout = new VLayout();
        featuredLayout.setHeight100();
        featuredLayout.setWidth100();
        featuredLayout.setBackgroundColor("#eaeaea");
        featuredLayout.setOverflow(Overflow.AUTO);
        
        featuredDisplay = new GridStructureView("Featured Products", true, true);
        featuredLayout.addMember(featuredDisplay);
        
        allProductsDisplay = new GridStructureView("All Products", true, false);
        featuredLayout.addMember(allProductsDisplay);
        
        featuredTab.setPane(featuredLayout);
        
        Tab mediaTab = new Tab("Media"); 
        
        VLayout mediaLayout = new VLayout();
        mediaLayout.setHeight100();
        mediaLayout.setWidth100();
        mediaLayout.setBackgroundColor("#eaeaea");
        mediaLayout.setOverflow(Overflow.AUTO);
        
        mediaDisplay = new GridStructureView("Media", false, true);
        mediaLayout.addMember(mediaDisplay);
        
        mediaTab.setPane(mediaLayout);
        
        topTabSet.addTab(detailsTab);
        topTabSet.addTab(featuredTab);
        topTabSet.addTab(mediaTab);
        
        addMember(leftVerticalLayout);
        addMember(topTabSet);
	}

	public Canvas asCanvas() {
		return this;
	}
	
	public DynamicEntityListDisplay getListDisplay() {
		return listDisplay;
	}
	
	public GridStructureDisplay getMediaDisplay() {
		return mediaDisplay;
	}

	public DynamicFormDisplay getDynamicFormDisplay() {
		return dynamicFormDisplay;
	}
	
	public GridStructureDisplay getFeaturedDisplay() {
		return featuredDisplay;
	}

	public ToolStripButton getRemoveOrphanedButton() {
		return removeOrphanedButton;
	}

	public ListGrid getOrphanedCategoryGrid() {
		return orphanedCategoryGrid;
	}

	public ToolStripButton getInsertOrphanButton() {
		return insertOrphanButton;
	}

	public GridStructureDisplay getAllCategoriesDisplay() {
		return allCategoriesDisplay;
	}

	public GridStructureView getAllProductsDisplay() {
		return allProductsDisplay;
	}
	
}
