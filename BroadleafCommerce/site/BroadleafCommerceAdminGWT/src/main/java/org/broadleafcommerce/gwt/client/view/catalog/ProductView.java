package org.broadleafcommerce.gwt.client.view.catalog;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicListDisplay;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.tree.TreeGridField;

public class ProductView extends VLayout implements DynamicListDisplay<ListGrid> {
	
	private static final ListGridField name = new ListGridField("name", "Name", 200);
	private static final TreeGridField activeStartDate = new TreeGridField("activeStartDate", "Start Date", 150);
	private static final TreeGridField activeEndDate = new TreeGridField("activeEndDate", "End Date", 150);
	private static final TreeGridField model = new TreeGridField("model", "Model", 150);
	private static final TreeGridField manufacturer = new TreeGridField("manufacturer", "Manufacturer", 150);
	private static final TreeGridField isFeaturedProduct = new TreeGridField("isFeaturedProduct", "Is Featured", 150);
	
	private ToolStripButton addButton;
	private ToolStripButton removeButton;
	private ListGrid listGrid;
	private SelectItem entityType = new SelectItem();
	private ToolStrip formToolBar;
	private ToolStripButton saveFormButton;
	private ToolStripButton refreshButton;
	private DynamicForm dynamicForm;
	private TabSet topTabSet;
    
	public void build(final DataSource dataSource) {
        setHeight100();
        setWidth("50%");
        ToolStrip topBar = new ToolStrip();
        topBar.setHeight(20);
        topBar.setWidth100();
        topBar.addSpacer(6);
        addButton = new ToolStripButton();  
        addButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/plus.png");   
        addButton.setDisabled(true);
        topBar.addButton(addButton);
        HashMap<String, String> polymorphicEntities = ((DynamicEntityDataSource) dataSource).getPolymorphicEntities();
        if (polymorphicEntities.size() > 1) { 
            entityType.setShowTitle(false);  
            entityType.setWidth(120); 
            
            LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();  
            for (String name : polymorphicEntities.keySet()) {
            	valueMap.put(name, polymorphicEntities.get(name)); 
            }
            entityType.setValueMap(valueMap);  
            entityType.setDefaultValue(valueMap.keySet().iterator().next()); 
            entityType.setDisabled(true);
            topBar.addFormItem(entityType);
        }
        removeButton = new ToolStripButton(); 
        removeButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/minus.png"); 
        removeButton.setDisabled(true);
        topBar.addButton(removeButton);
        addMember(topBar);

        listGrid = new ListGrid();
        listGrid.setCellPadding(5);
        listGrid.setSelectionType(SelectionStyle.SINGLE);
        listGrid.setCanEdit(true);
        listGrid.setEditEvent(ListGridEditEvent.DOUBLECLICK);
        listGrid.setEditByCell(true);
        listGrid.setAutoSaveEdits(true);
        listGrid.setSaveByCell(true);
        listGrid.setDataSource(dataSource);
        listGrid.setAutoFetchData(false);
        listGrid.setShowFilterEditor(true);
        name.setFrozen(true);
        listGrid.setFields(name, activeStartDate, activeEndDate, model,manufacturer,isFeaturedProduct);
        listGrid.setShowResizeBar(true);
        addMember(listGrid);
        
        topTabSet = new TabSet();  
        topTabSet.setTabBarPosition(Side.TOP);  
        topTabSet.setWidth100();
        topTabSet.setHeight(215); 
        topTabSet.setOverflow(Overflow.AUTO);
        
        Tab productTab = new Tab("Product");
        topTabSet.addTab(productTab);
        
        VLayout formlayout = new VLayout();
        formlayout.setLayoutMargin(0);
        formlayout.setMargin(0);
        formlayout.setPadding(0);
        formToolBar = new ToolStrip();
        formToolBar.setHeight(20);
        formToolBar.setWidth100();
        formToolBar.addSpacer(6);
        saveFormButton = new ToolStripButton();  
        saveFormButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/save.png");   
        formToolBar.addButton(saveFormButton);
        saveFormButton.setDisabled(true);
        refreshButton = new ToolStripButton();  
        refreshButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/refresh.png");   
        formToolBar.addButton(refreshButton);
        formToolBar.setDisabled(true);
        formlayout.addMember(formToolBar);
        formlayout.setBackgroundColor("#eaeaea");
        dynamicForm = new DynamicForm(); 
        dynamicForm.setHeight(175);
        dynamicForm.setWidth(700);
        dynamicForm.setNumCols(4);  
        dynamicForm.setDataSource(dataSource); 
        dynamicForm.setPadding(10);
        dynamicForm.disable();
        dynamicForm.setBackgroundColor("#eaeaea");
        
        formlayout.addMember(dynamicForm);
        productTab.setPane(formlayout);
        
        //Tab skuTab = new Tab("Sku");
        //topTabSet.addTab(skuTab);
        //skuTab.setPane(skuView);
        
        topTabSet.setShowPaneContainerEdges(false);
        addMember(topTabSet);
	}

	public Canvas asCanvas() {
		return this;
	}

	public ListGrid getGrid() {
		return listGrid;
	}
	
	public ToolStripButton getAddButton() {
		return addButton;
	}
	
	public ToolStripButton getRemoveButton() {
		return removeButton;
	}

	public DynamicForm getDynamicForm() {
		return dynamicForm;
	}
	
	public ToolStrip getFormToolBar() {
		return formToolBar;
	}
	
	public ToolStripButton getSaveFormButton() {
		return saveFormButton;
	}
	
	public ToolStripButton getRefreshButton() {
		return refreshButton;
	}
	
	public SelectItem getEntityType() {
		return entityType;
	}

	public TabSet getTopTabSet() {
		return topTabSet;
	}
}
