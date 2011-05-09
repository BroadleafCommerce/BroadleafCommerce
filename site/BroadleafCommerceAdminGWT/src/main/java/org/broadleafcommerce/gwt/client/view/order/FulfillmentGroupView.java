package org.broadleafcommerce.gwt.client.view.order;

import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.form.FormOnlyDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.form.FormOnlyView;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class FulfillmentGroupView extends VLayout implements FulfillmentGroupDisplay, DynamicFormDisplay {

	protected ToolStrip toolBar;
	protected ToolStripButton addButton;
	protected ToolStripButton removeButton;
	protected ToolStripButton saveButton;
	protected ToolStripButton refreshButton;
	protected ListGrid grid;
	protected FormOnlyView formOnlyView;

	public FulfillmentGroupView(String title, Boolean canReorder, Boolean canEdit) {
        setHeight100();
        setWidth100();
        setBackgroundColor("#eaeaea");
        setOverflow(Overflow.AUTO);
		
		HStack hStack = new HStack(10);
		
		hStack.setHeight("45%");
		hStack.setWidth100();
		hStack.setBackgroundColor("#eaeaea");
		hStack.setAlign(Alignment.CENTER);
        
        VLayout stack = new VLayout();
        stack.setHeight100();
        stack.setWidth100();
        //stack.setLayoutMargin(12);
        
        toolBar = new ToolStrip();
        toolBar.setHeight(26);
        toolBar.setWidth100();
        toolBar.setMinWidth(300);
        toolBar.addSpacer(6);
        addButton = new ToolStripButton();  
        addButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/plus.png"); 
        addButton.setDisabled(true);
        toolBar.addButton(addButton);
        removeButton = new ToolStripButton();
        removeButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/minus.png"); 
        removeButton.setDisabled(true);
        toolBar.addButton(removeButton);
        toolBar.addSpacer(6);
        saveButton = new ToolStripButton();  
        saveButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/save.png"); 
        saveButton.setDisabled(true);
        toolBar.addButton(saveButton);
        saveButton.setDisabled(true);
        refreshButton = new ToolStripButton();  
        refreshButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/refresh.png"); 
        refreshButton.setDisabled(true);
        toolBar.addButton(refreshButton);
        Label crossSaleLabel = new Label();
        crossSaleLabel.setContents(title);
        crossSaleLabel.setWrap(false);
        toolBar.addSpacer(6);
        toolBar.addMember(crossSaleLabel);
        toolBar.addFill();
        stack.addMember(toolBar);
        
        grid = new ListGrid();
        grid.setAutoFetchData(false);
        grid.setShowHeader(true);
        grid.setShowHeaderContextMenu(false); 
        grid.setPreventDuplicates(true);
        grid.setCanReorderRecords(canReorder);
        grid.setDisabled(true);
        grid.setCanSort(false);
        grid.setCellPadding(5);
        grid.setCanEdit(canEdit);
        grid.setEditEvent(ListGridEditEvent.DOUBLECLICK);
        grid.setEditByCell(true);
        grid.setAutoSaveEdits(true);
        grid.setSaveByCell(true);
        grid.setAlternateRecordStyles(true);
        if (!canEdit) {
        	grid.setAlternateBodyStyleName("editRowDisabled");
        }
        stack.addMember(grid);
        
        hStack.addMember(stack);
        hStack.setOverflow(Overflow.AUTO);
        hStack.setShowResizeBar(true);
        
        addMember(hStack);
        formOnlyView = new FormOnlyView();
        addMember(formOnlyView);
	}

	public ToolStrip getToolbar() {
		return toolBar;
	}

	public ToolStripButton getAddButton() {
		return addButton;
	}

	public ToolStripButton getRemoveButton() {
		return removeButton;
	}

	public ListGrid getGrid() {
		return grid;
	}

	public ToolStripButton getSaveButton() {
		return saveButton;
	}

	public ToolStripButton getRefreshButton() {
		return refreshButton;
	}

	public FormOnlyDisplay getFormOnlyDisplay() {
		return formOnlyView;
	}
	
	
}
