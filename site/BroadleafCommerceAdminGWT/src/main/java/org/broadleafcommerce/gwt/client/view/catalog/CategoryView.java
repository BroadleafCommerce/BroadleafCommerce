package org.broadleafcommerce.gwt.client.view.catalog;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.client.view.dynamic.AbstractView;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.tree.TreeGrid;

public class CategoryView extends AbstractView implements Instantiable, CategoryDisplay {
	
	private ToolStripButton addButton;
	private ToolStripButton removeButton;
	private TreeGrid categoryTreeGrid;
	private DynamicForm dynamicForm;
	private ToolStripButton saveFormButton;
	private ToolStripButton refreshButton;
	private SelectItem entityType = new SelectItem();
	private ToolStrip formToolBar;
	private ToolStrip allParentCategoryToolBar;
	private ToolStripButton addParentCategoryButton;
	private ToolStripButton removeParentCategoryButton;
	private HStack additionalContainer;
	private ListGrid allParentCategoryGrid;
    
	public void build(final DataSource dataSource) {
		VLayout gridlayout = new VLayout();
		gridlayout.setHeight100();
		gridlayout.setWidth("30%");
		gridlayout.setShowResizeBar(true);
        
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
        gridlayout.addMember(topBar);
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
        categoryTreeGrid.setDrawAheadRatio(4);
        categoryTreeGrid.setShowRoot(false);
        categoryTreeGrid.setTreeRootValue("1");
        
        gridlayout.addMember(categoryTreeGrid);
        
        VLayout formlayout = new VLayout();
        formlayout.setHeight100();
        formlayout.setWidth("70%");
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
        
        VLayout formlayout2 = new VLayout();
        formlayout2.setHeight100();
        formlayout2.setWidth100();
        formlayout2.setBackgroundColor("#eaeaea");
        dynamicForm = new DynamicForm(); 
        dynamicForm.setHeight(175);
        dynamicForm.setWidth100();
        dynamicForm.setNumCols(4);
        dynamicForm.setPadding(10);
        dynamicForm.disable();
        dynamicForm.setBackgroundColor("#eaeaea");
        buildFields(dataSource, dynamicForm);
        formlayout2.addMember(dynamicForm);
        formlayout2.setOverflow(Overflow.AUTO);
        formlayout.addMember(formlayout2);
        
        addMember(gridlayout);
        addMember(formlayout);
        
        additionalContainer = new HStack(10);
        additionalContainer.setLayoutMargin(12);
        additionalContainer.setHeight(180);
        additionalContainer.setWidth100();
        additionalContainer.setBackgroundColor("#eaeaea");
        additionalContainer.setAlign(Alignment.CENTER);
        
        VStack allPStack = new VStack();
        allPStack.setHeight(150);
        allPStack.setWidth("40%");
        allParentCategoryToolBar = new ToolStrip();
        allParentCategoryToolBar.setHeight(20);
        allParentCategoryToolBar.setWidth100();
        allParentCategoryToolBar.addSpacer(6);
        addParentCategoryButton = new ToolStripButton();  
        addParentCategoryButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/plus.png");   
        allParentCategoryToolBar.addButton(addParentCategoryButton);
        removeParentCategoryButton = new ToolStripButton();
        removeParentCategoryButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/minus.png"); 
        removeParentCategoryButton.setDisabled(true);
        allParentCategoryToolBar.addButton(removeParentCategoryButton);
        allParentCategoryToolBar.addFill();
        allParentCategoryToolBar.setDisabled(true);
        Label allPLabel = new Label();
        allPLabel.setContents("All Child Categories");
        allPLabel.setWrap(false);
        allParentCategoryToolBar.addMember(allPLabel);
        allParentCategoryToolBar.addSpacer(6);

        allPStack.addMember(allParentCategoryToolBar);
        allParentCategoryGrid = new ListGrid();
        allParentCategoryGrid.setCanEdit(false);
        allParentCategoryGrid.setAutoFetchData(false);
        allParentCategoryGrid.setShowHeader(true);
        allParentCategoryGrid.setShowHeaderContextMenu(false);
        ListGridField idField = new ListGridField("id");
        allParentCategoryGrid.setFields(idField);  
        allParentCategoryGrid.setPreventDuplicates(true);
        allParentCategoryGrid.setCanReorderRecords(true);
        allParentCategoryGrid.setHeight100();
        allParentCategoryGrid.setDisabled(true);
        allPStack.addMember(allParentCategoryGrid);
        
        additionalContainer.addMember(allPStack);
        formlayout2.addMember(additionalContainer);
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

	public ListGrid getAllParentCategoryGrid() {
		return allParentCategoryGrid;
	}

	public ToolStrip getAllParentCategoryToolBar() {
		return allParentCategoryToolBar;
	}

	public ToolStripButton getAddParentCategoryButton() {
		return addParentCategoryButton;
	}

	public ToolStripButton getRemoveParentCategoryButton() {
		return removeParentCategoryButton;
	}
	
}
