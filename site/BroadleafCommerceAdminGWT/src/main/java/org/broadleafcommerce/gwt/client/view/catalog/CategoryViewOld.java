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
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.events.RowContextClickEvent;
import com.smartgwt.client.widgets.grid.events.RowContextClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;

public class CategoryViewOld extends VLayout implements DynamicListDisplay<TreeGrid> {
	
	private static final TreeGridField name = new TreeGridField("name", "Name", 200);
	private static final TreeGridField activeStartDate = new TreeGridField("activeStartDate", "Start Date", 150);
	private static final TreeGridField activeEndDate = new TreeGridField("activeEndDate", "End Date", 150);
	private static final TreeGridField url = new TreeGridField("url", "Url", 150);
	private static final TreeGridField urlKey = new TreeGridField("urlKey", "Url Key", 150);
	
	private ToolStripButton addButton;
	private ToolStripButton removeButton;
	private TreeGrid categoryTreeGrid;
	private DynamicForm dynamicForm;
	private ToolStripButton saveFormButton;
	private ToolStripButton refreshButton;
	private SelectItem entityType = new SelectItem();
	private ToolStrip formToolBar;
    
	public void build(final DataSource dataSource) {
        setHeight100();
        setWidth("50%");
        setShowResizeBar(true);
        
        ToolStrip topBar = new ToolStrip();
        topBar.setHeight(20);
        topBar.setWidth100();
        topBar.addSpacer(6);
        addButton = new ToolStripButton();  
        addButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/plus.png");   
        topBar.addButton(addButton);
        HashMap<String, String> polymorphicEntities = ((DynamicEntityDataSource) dataSource).getPolymorphicEntities();
        if (polymorphicEntities.size() > 1) { 
            entityType.setShowTitle(false);  
            entityType.setWidth(120); 
            
            LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();  
            for (String name : polymorphicEntities.keySet()) {
            	valueMap.put(polymorphicEntities.get(name), name); 
            }
            entityType.setValueMap(valueMap);  
            entityType.setDefaultValue(((DynamicEntityDataSource) dataSource).getCeilingEntityFullyQualifiedClassname()); 
            topBar.addFormItem(entityType);
        }
        removeButton = new ToolStripButton(); 
        removeButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/minus.png"); 
        removeButton.setDisabled(true);
        topBar.addButton(removeButton);
        addMember(topBar);
        categoryTreeGrid = new TreeGrid();
        categoryTreeGrid.setAlternateRecordStyles(true);
        categoryTreeGrid.setSelectionType(SelectionStyle.SINGLE);
        categoryTreeGrid.setCanEdit(true);
        categoryTreeGrid.setEditEvent(ListGridEditEvent.DOUBLECLICK);
        categoryTreeGrid.setEditByCell(true);
        categoryTreeGrid.setAutoSaveEdits(true);
        categoryTreeGrid.setSaveByCell(true);
        categoryTreeGrid.setDataSource(dataSource);
        categoryTreeGrid.setAutoFetchData(true);
        categoryTreeGrid.setCanReparentNodes(true);
        categoryTreeGrid.setDrawAheadRatio(4);  
        name.setFrozen(true);
        categoryTreeGrid.setFields(name, activeStartDate, activeEndDate, url, urlKey);
        
        categoryTreeGrid.addRowContextClickHandler(new RowContextClickHandler() {
            public void onRowContextClick(RowContextClickEvent event) {
                SC.say("Secret context menu discovered", "Hello from DAO Fusion team :-)");
                
                // prevent default browser context menu
                event.cancel();
            }
        });
        
        categoryTreeGrid.setShowResizeBar(true);
        addMember(categoryTreeGrid);
        
        VLayout formlayout = new VLayout();
        
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
        
        formlayout.setHeight(215);
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
        formlayout.setOverflow(Overflow.AUTO);
        addMember(formlayout);
	}

	public Canvas asCanvas() {
		return this;
	}

	public ToolStripButton getAddButton() {
		return addButton;
	}
	
	public ToolStripButton getRemoveButton() {
		return removeButton;
	}

	public TreeGrid getGrid() {
		return categoryTreeGrid;
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
	
}
