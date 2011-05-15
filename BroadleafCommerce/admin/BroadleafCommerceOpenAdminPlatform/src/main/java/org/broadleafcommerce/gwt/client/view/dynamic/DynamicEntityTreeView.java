package org.broadleafcommerce.gwt.client.view.dynamic;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.events.FocusEvent;
import com.smartgwt.client.widgets.form.fields.events.FocusHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.tree.TreeGrid;

public class DynamicEntityTreeView extends VLayout implements DynamicEntityListDisplay {
	
	protected ToolStripButton addButton;
	protected ToolStripButton removeButton;
	protected ComboBoxItem entityType = new ComboBoxItem();
	protected TreeGrid grid;
	protected ToolStrip toolBar;
	
	public DynamicEntityTreeView(String title, DataSource dataSource) {
		super();
		toolBar = new ToolStrip();
		toolBar.setHeight(20);
		toolBar.setWidth100();
		toolBar.addSpacer(6);
        addButton = new ToolStripButton();  
        addButton.setDisabled(true);
        addButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/plus.png");   
        toolBar.addButton(addButton);
        removeButton = new ToolStripButton(); 
        removeButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/minus.png"); 
        removeButton.setDisabled(true);
        toolBar.addButton(removeButton);
        toolBar.addSpacer(6);
        Label categoryLabel = new Label();
        categoryLabel.setContents(title);
        categoryLabel.setWrap(false);
        toolBar.addMember(categoryLabel);
        toolBar.addFill();
        HashMap<String, String> polymorphicEntities = ((DynamicEntityDataSource) dataSource).getPolymorphicEntities();
        if (polymorphicEntities.size() > 1) { 
            entityType.setShowTitle(false);  
            entityType.setWidth(120); 
            
            LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();  
            for (String name : polymorphicEntities.keySet()) {
            	valueMap.put(name, polymorphicEntities.get(name)); 
            }
            entityType.setValueMap(valueMap);  
            entityType.setDefaultValue(((DynamicEntityDataSource) dataSource).getDefaultNewEntityFullyQualifiedClassname()); 
            entityType.addFocusHandler(new FocusHandler() {
				public void onFocus(FocusEvent event) {
					((ComboBoxItem) event.getItem()).selectValue();
				}
            });
            toolBar.addFormItem(entityType);
        }
        addMember(toolBar);
        grid = new TreeGrid();
        grid.setAlternateRecordStyles(true);
        grid.setSelectionType(SelectionStyle.SINGLE);
        grid.setCanEdit(false);
        grid.setDataSource(dataSource);
        grid.setAutoFetchData(true);
        grid.setDrawAheadRatio(4);
        grid.setCanSort(false);
        grid.setCanResizeFields(true);
        addMember(grid);
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityTreeDisplay#getAddButton()
	 */
	public ToolStripButton getAddButton() {
		return addButton;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityTreeDisplay#getRemoveButton()
	 */
	public ToolStripButton getRemoveButton() {
		return removeButton;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityTreeDisplay#getEntityType()
	 */
	public ComboBoxItem getEntityType() {
		return entityType;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityTreeDisplay#getGrid()
	 */
	public ListGrid getGrid() {
		return grid;
	}

	public ToolStrip getToolBar() {
		return toolBar;
	}

}
