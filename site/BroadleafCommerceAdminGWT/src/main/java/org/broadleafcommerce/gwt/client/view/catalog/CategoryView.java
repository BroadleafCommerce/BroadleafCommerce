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
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.tree.TreeGrid;

public class CategoryView extends AbstractView implements Instantiable, CategoryDisplay {
	
	protected ToolStripButton addButton;
	protected ToolStripButton removeButton;
	protected TreeGrid categoryTreeGrid;
	protected DynamicForm dynamicForm;
	protected ToolStripButton saveFormButton;
	protected ToolStripButton refreshButton;
	protected SelectItem entityType = new SelectItem();
	protected ToolStrip formToolBar;
	protected ToolStrip allChildCategoryToolBar;
	protected ToolStripButton addChildCategoryButton;
	protected ToolStripButton removeChildCategoryButton;
	protected HStack additionalContainer;
	protected ListGrid allChildCategoryGrid;
	protected ToolStripButton removeOrphanedButton;
	protected ToolStripButton insertOrphanButton;
	protected ListGrid orphanedCategoryGrid;
	protected ToolStripButton addDefaultParentCategoryButton;
	protected TextItem defaultParentCategoryTextItem;
	protected ToolStrip featuredProductToolBar;
	protected ToolStripButton addFeaturedProductButton;
	protected ToolStripButton removeFeaturedProductButton;
	protected ListGrid featuredProductGrid;
	protected ToolStrip mediaToolBar;
	protected ToolStripButton addMediaButton;
	protected ToolStripButton removeMediaButton;
	protected ListGrid mediaGrid;
    
	public void build(final DataSource dataSource) {
		VLayout leftVerticalLayout = new VLayout();
		leftVerticalLayout.setHeight100();
		leftVerticalLayout.setWidth("30%");
		leftVerticalLayout.setShowResizeBar(true);
        
		VLayout categoryVerticalLayout = new VLayout();
        ToolStrip categoryTopBar = new ToolStrip();
        categoryTopBar.setHeight(20);
        categoryTopBar.setWidth100();
        categoryTopBar.addSpacer(6);
        addButton = new ToolStripButton();  
        addButton.setDisabled(true);
        addButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/plus.png");   
        categoryTopBar.addButton(addButton);
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
            categoryTopBar.addFormItem(entityType);
        }
        removeButton = new ToolStripButton(); 
        removeButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/minus.png"); 
        removeButton.setDisabled(true);
        categoryTopBar.addButton(removeButton);
        categoryVerticalLayout.addMember(categoryTopBar);
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
        categoryTreeGrid.setCanSort(false);
        categoryTreeGrid.setCanResizeFields(true);
        categoryVerticalLayout.setShowResizeBar(true);
        categoryVerticalLayout.addMember(categoryTreeGrid);
        
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
        abandonedCategoryTopBar.addFill();
        Label abandonedLabel = new Label();
        abandonedLabel.setContents("Orphaned Categories");
        abandonedLabel.setWrap(false);
        abandonedCategoryTopBar.addMember(abandonedLabel);
        abandonedCategoryTopBar.addSpacer(6);
        abandonedCategoryVerticalLayout.addMember(abandonedCategoryTopBar);
        orphanedCategoryGrid = new ListGrid();
        orphanedCategoryGrid.setAlternateRecordStyles(true);
        orphanedCategoryGrid.setSelectionType(SelectionStyle.SINGLE);
        orphanedCategoryGrid.setDrawAheadRatio(4);
        orphanedCategoryGrid.setCanSort(false);
        orphanedCategoryGrid.setCellPadding(5);
        abandonedCategoryVerticalLayout.addMember(orphanedCategoryGrid);
        
        leftVerticalLayout.addMember(categoryVerticalLayout);
        leftVerticalLayout.addMember(abandonedCategoryVerticalLayout);
        
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
        
        addMember(leftVerticalLayout);
        addMember(formlayout);
        
        additionalContainer = new HStack(10);
        additionalContainer.setHeight(180);
        additionalContainer.setWidth100();
        additionalContainer.setBackgroundColor("#eaeaea");
        additionalContainer.setAlign(Alignment.CENTER);
        
        VStack allPStack = new VStack();
        allPStack.setHeight(150);
        allPStack.setWidth100();
        allPStack.setLayoutMargin(12);
        
        allChildCategoryToolBar = new ToolStrip();
        allChildCategoryToolBar.setHeight(26);
        allChildCategoryToolBar.setWidth100();
        allChildCategoryToolBar.addSpacer(6);
        addChildCategoryButton = new ToolStripButton();  
        addChildCategoryButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/plus.png");   
        allChildCategoryToolBar.addButton(addChildCategoryButton);
        removeChildCategoryButton = new ToolStripButton();
        removeChildCategoryButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/minus.png"); 
        removeChildCategoryButton.setDisabled(true);
        allChildCategoryToolBar.addButton(removeChildCategoryButton);
        allChildCategoryToolBar.setDisabled(true);
        Label allPLabel = new Label();
        allPLabel.setContents("All Child Categories");
        allPLabel.setWrap(false);
        allChildCategoryToolBar.addSpacer(6);
        allChildCategoryToolBar.addMember(allPLabel);
        allChildCategoryToolBar.addFill();
        defaultParentCategoryTextItem = new TextItem();
        defaultParentCategoryTextItem.setShowTitle(false);
        defaultParentCategoryTextItem.setWrapTitle(false);
        defaultParentCategoryTextItem.setDisabled(true);
        defaultParentCategoryTextItem.setHeight(18);
        defaultParentCategoryTextItem.setValue("Default Parent Category");
        allChildCategoryToolBar.addFormItem(defaultParentCategoryTextItem);
        addDefaultParentCategoryButton = new ToolStripButton();  
        addDefaultParentCategoryButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/plus.png");
        addDefaultParentCategoryButton.setDisabled(true);
        allChildCategoryToolBar.addButton(addDefaultParentCategoryButton);
        allChildCategoryToolBar.addSpacer(6);

        allPStack.addMember(allChildCategoryToolBar);
        allChildCategoryGrid = new ListGrid();
        allChildCategoryGrid.setCanEdit(false);
        allChildCategoryGrid.setAutoFetchData(false);
        allChildCategoryGrid.setShowHeader(true);
        allChildCategoryGrid.setShowHeaderContextMenu(false);
        allChildCategoryGrid.setPreventDuplicates(true);
        allChildCategoryGrid.setCanReorderRecords(true);
        allChildCategoryGrid.setHeight100();
        allChildCategoryGrid.setDisabled(true);
        allChildCategoryGrid.setCanSort(false);
        allChildCategoryGrid.setCellPadding(5);
        allPStack.addMember(allChildCategoryGrid);
        
        additionalContainer.addMember(allPStack);
        
        HStack additionalContainer3 = new HStack(10);
        additionalContainer3.setHeight(180);
        additionalContainer3.setWidth100();
        additionalContainer3.setBackgroundColor("#eaeaea");
        additionalContainer3.setAlign(Alignment.CENTER);
        
        VStack mediaStack = new VStack();
        mediaStack.setHeight(150);
        mediaStack.setWidth100();
        mediaStack.setLayoutMargin(12);
        
        mediaToolBar = new ToolStrip();
        mediaToolBar.setHeight(26);
        mediaToolBar.setWidth100();
        mediaToolBar.addSpacer(6);
        addMediaButton = new ToolStripButton();  
        addMediaButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/plus.png");   
        addMediaButton.setDisabled(true);
        mediaToolBar.addButton(addMediaButton);
        removeMediaButton = new ToolStripButton();
        removeMediaButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/minus.png"); 
        removeMediaButton.setDisabled(true);
        mediaToolBar.addButton(removeMediaButton);
        Label mediaLabel = new Label();
        mediaLabel.setContents("Category Media");
        mediaLabel.setWrap(false);
        mediaToolBar.addSpacer(6);
        mediaToolBar.addMember(mediaLabel);

        mediaStack.addMember(mediaToolBar);
        mediaGrid = new ListGrid();
        mediaGrid.setAutoFetchData(false);
        mediaGrid.setShowHeader(true);
        mediaGrid.setShowHeaderContextMenu(false);
        mediaGrid.setPreventDuplicates(true);
        mediaGrid.setHeight100();
        mediaGrid.setDisabled(true);
        mediaGrid.setCanSort(true);
        mediaGrid.setCanEdit(true);
        mediaGrid.setEditEvent(ListGridEditEvent.DOUBLECLICK);
        mediaGrid.setEditByCell(true);
        mediaGrid.setAutoSaveEdits(true);
        mediaGrid.setSaveByCell(true);
        mediaGrid.setCellPadding(5);
        mediaStack.addMember(mediaGrid);
        
        additionalContainer3.addMember(mediaStack);
        
        HStack additionalContainer2 = new HStack(10);
        additionalContainer2.setHeight(180);
        additionalContainer2.setWidth100();
        additionalContainer2.setBackgroundColor("#eaeaea");
        additionalContainer2.setAlign(Alignment.CENTER);
        
        VStack featuredPStack = new VStack();
        featuredPStack.setHeight(150);
        featuredPStack.setWidth100();
        featuredPStack.setLayoutMargin(12);
        
        featuredProductToolBar = new ToolStrip();
        featuredProductToolBar.setHeight(26);
        featuredProductToolBar.setWidth100();
        featuredProductToolBar.setMinWidth(300);
        featuredProductToolBar.addSpacer(6);
        addFeaturedProductButton = new ToolStripButton();  
        addFeaturedProductButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/plus.png"); 
        addFeaturedProductButton.setDisabled(true);
        featuredProductToolBar.addButton(addFeaturedProductButton);
        removeFeaturedProductButton = new ToolStripButton();
        removeFeaturedProductButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/minus.png"); 
        removeFeaturedProductButton.setDisabled(true);
        featuredProductToolBar.addButton(removeFeaturedProductButton);
        featuredProductToolBar.setDisabled(false);
        Label featuredLabel = new Label();
        featuredLabel.setContents("Featured Products");
        featuredLabel.setWrap(false);
        featuredProductToolBar.addSpacer(6);
        featuredProductToolBar.addMember(featuredLabel);
        featuredProductToolBar.addFill();
        featuredPStack.addMember(featuredProductToolBar);
        featuredProductGrid = new ListGrid();
        featuredProductGrid.setAutoFetchData(false);
        featuredProductGrid.setShowHeader(true);
        featuredProductGrid.setShowHeaderContextMenu(false); 
        featuredProductGrid.setPreventDuplicates(true);
        featuredProductGrid.setCanReorderRecords(true);
        featuredProductGrid.setHeight100();
        featuredProductGrid.setDisabled(true);
        featuredProductGrid.setCanSort(false);
        featuredProductGrid.setCellPadding(5);
        featuredProductGrid.setCanEdit(true);
        featuredProductGrid.setEditEvent(ListGridEditEvent.DOUBLECLICK);
        featuredProductGrid.setEditByCell(true);
        featuredProductGrid.setAutoSaveEdits(true);
        featuredProductGrid.setSaveByCell(true);
        featuredPStack.addMember(featuredProductGrid);
        
        additionalContainer2.addMember(featuredPStack);
        formlayout2.addMember(additionalContainer);
        formlayout2.addMember(additionalContainer3);
        formlayout2.addMember(additionalContainer2);
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

	public ListGrid getAllChildCategoryGrid() {
		return allChildCategoryGrid;
	}

	public ToolStrip getAllChildCategoryToolBar() {
		return allChildCategoryToolBar;
	}

	public ToolStripButton getAddChildCategoryButton() {
		return addChildCategoryButton;
	}

	public ToolStripButton getRemoveChildCategoryButton() {
		return removeChildCategoryButton;
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

	public ToolStripButton getAddDefaultParentCategoryButton() {
		return addDefaultParentCategoryButton;
	}

	public TextItem getDefaultParentCategoryTextItem() {
		return defaultParentCategoryTextItem;
	}

	public ToolStrip getFeaturedProductToolBar() {
		return featuredProductToolBar;
	}

	public ToolStripButton getAddFeaturedProductButton() {
		return addFeaturedProductButton;
	}

	public ToolStripButton getRemoveFeaturedProductButton() {
		return removeFeaturedProductButton;
	}

	public ListGrid getFeaturedProductGrid() {
		return featuredProductGrid;
	}

	public ToolStrip getMediaToolBar() {
		return mediaToolBar;
	}

	public ToolStripButton getAddMediaButton() {
		return addMediaButton;
	}

	public ToolStripButton getRemoveMediaButton() {
		return removeMediaButton;
	}

	public ListGrid getMediaGrid() {
		return mediaGrid;
	}
	
}
