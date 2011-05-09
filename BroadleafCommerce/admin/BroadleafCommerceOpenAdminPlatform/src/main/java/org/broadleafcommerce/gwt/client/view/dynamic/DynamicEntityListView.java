package org.broadleafcommerce.gwt.client.view.dynamic;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class DynamicEntityListView extends VLayout implements DynamicEntityListDisplay {
	
	protected ToolStripButton addButton;
	protected ToolStripButton removeButton;
	protected SelectItem entityType = new SelectItem();
	protected ListGrid grid;
	protected ToolStrip toolBar;
	
	public DynamicEntityListView(String title, DataSource dataSource) {
		this(title, dataSource, true, true);
	}
	
	public DynamicEntityListView(String title, DataSource dataSource, Boolean canReorder, Boolean canEdit) {
		super();
		toolBar = new ToolStrip();
		toolBar.setHeight(20);
		toolBar.setWidth100();
		toolBar.addSpacer(6);
        addButton = new ToolStripButton();  
        //addButton.setDisabled(true);
        addButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/plus.png");   
        toolBar.addButton(addButton);
        removeButton = new ToolStripButton(); 
        removeButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/minus.png"); 
        removeButton.setDisabled(true);
        toolBar.addButton(removeButton);
        toolBar.addSpacer(6);
        Label productLabel = new Label();
        productLabel.setContents(title);
        productLabel.setWrap(false);
        toolBar.addMember(productLabel);
        toolBar.addFill();
        HashMap<String, String> polymorphicEntities = ((DynamicEntityDataSource) dataSource).getPolymorphicEntities();
        if (polymorphicEntities.size() > 1) { 
            entityType.setShowTitle(false);  
            entityType.setWidth(180); 
            
            LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();  
            for (String name : polymorphicEntities.keySet()) {
            	valueMap.put(name, polymorphicEntities.get(name)); 
            }
            entityType.setValueMap(valueMap);  
            entityType.setDefaultValue(((DynamicEntityDataSource) dataSource).getDefaultNewEntityFullyQualifiedClassname()); 
            toolBar.addFormItem(entityType);
        }
        addMember(toolBar);
        grid = new ListGrid();
        grid.setCanReorderRecords(canReorder);
        grid.setAlternateRecordStyles(true);
        grid.setSelectionType(SelectionStyle.SINGLE);
        grid.setCanEdit(true);
        grid.setEditEvent(ListGridEditEvent.DOUBLECLICK);
        grid.setEditByCell(true);
        grid.setAutoSaveEdits(true);
        grid.setSaveByCell(true);
        grid.setDataSource(dataSource);
        grid.setAutoFetchData(false);
        grid.setDrawAllMaxCells(10);
        grid.setCanSort(true);
        grid.setCanResizeFields(true);
        grid.setShowFilterEditor(true);
        grid.setDataPageSize(10);
        if (!canEdit) {
        	grid.setAlternateBodyStyleName("editRowDisabled");
        }
        addMember(grid);
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListDisplay#getAddButton()
	 */
	public ToolStripButton getAddButton() {
		return addButton;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListDisplay#getRemoveButton()
	 */
	public ToolStripButton getRemoveButton() {
		return removeButton;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListDisplay#getEntityType()
	 */
	public SelectItem getEntityType() {
		return entityType;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListDisplay#getGrid()
	 */
	public ListGrid getGrid() {
		return grid;
	}

	public ToolStrip getToolBar() {
		return toolBar;
	}

}
