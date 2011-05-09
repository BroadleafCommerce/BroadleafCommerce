package org.broadleafcommerce.gwt.client.view.order;

import org.broadleafcommerce.gwt.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.gwt.client.view.dynamic.form.FormOnlyDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.form.FormOnlyView;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Visibility;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class OrderItemGridStructureView extends VLayout implements OrderItemGridStructureDisplay {
	
	protected ToolStrip toolBar;
	protected ToolStripButton addButton;
	protected ToolStripButton removeButton;
	protected ListGrid grid;
	protected ListGrid expansionGrid;
	protected FormOnlyView orderItemFormDisplay;

	public OrderItemGridStructureView(String title, Boolean canReorder, Boolean canEdit) {
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
        toolBar.setDisabled(false);
        Label crossSaleLabel = new Label();
        crossSaleLabel.setContents(title);
        crossSaleLabel.setWrap(false);
        toolBar.addSpacer(6);
        toolBar.addMember(crossSaleLabel);
        toolBar.addFill();
        stack.addMember(toolBar);
        
        expansionGrid = new ListGrid();
        expansionGrid.setShowHeader(true);
        expansionGrid.setShowHeaderContextMenu(false);
        expansionGrid.setCanReorderRecords(canReorder);
        expansionGrid.setCanEdit(canEdit);
        expansionGrid.setEditEvent(ListGridEditEvent.DOUBLECLICK);
        expansionGrid.setEditByCell(true);
        expansionGrid.setAutoSaveEdits(true);
        expansionGrid.setSaveByCell(true);
        expansionGrid.setAlternateRecordStyles(true);
        if (!canEdit) {
        	expansionGrid.setAlternateBodyStyleName("editRowDisabled");
        }
        expansionGrid.setVisibility(Visibility.HIDDEN);
        expansionGrid.setHeight(100);
        expansionGrid.draw();
        
        grid = new ListGrid() {
        	@Override  
            protected Canvas getExpansionComponent(final ListGridRecord record) {
                VLayout layout = new VLayout(5);
                layout.setPadding(5);
                layout.addMember(expansionGrid);
                expansionGrid.setVisibility(Visibility.INHERIT);
                String id = record.getAttribute("id");
        		((PresentationLayerAssociatedDataSource) expansionGrid.getDataSource()).loadAssociatedGridBasedOnRelationship(id, null);
                return layout;
            }  
        };
        
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
        grid.setCanExpandMultipleRecords(false);
        grid.setCanExpandRecords(true);
        if (!canEdit) {
        	grid.setAlternateBodyStyleName("editRowDisabled");
        }
        stack.addMember(grid);
        
        hStack.addMember(stack);
        hStack.setOverflow(Overflow.AUTO);
        hStack.setShowResizeBar(true);
        
        addMember(hStack);
        orderItemFormDisplay = new FormOnlyView();
        addMember(orderItemFormDisplay);
	}

	public ToolStrip getToolBar() {
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

	public ListGrid getExpansionGrid() {
		return expansionGrid;
	}

	public FormOnlyDisplay getOrderItemFormDisplay() {
		return orderItemFormDisplay;
	}

}
