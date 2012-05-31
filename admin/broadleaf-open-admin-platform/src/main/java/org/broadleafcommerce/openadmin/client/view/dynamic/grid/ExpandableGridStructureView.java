package org.broadleafcommerce.openadmin.client.view.dynamic.grid;

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.PresentationLayerAssociatedDataSource;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Visibility;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * This is essentially the same as a GridStructureView except that it also includes an expansion ListGrid that
 * you can associate with a DataSource. This essentially builds a SmartGWT nested grid
 * 
 * @author Phillip Verheyden
 *
 */
public class ExpandableGridStructureView extends HLayout implements ExpandableGridStructureDisplay {

    protected ToolStrip toolBar;
    protected ToolStripButton addButton;
    protected ToolStripButton removeButton;
    protected ListGrid grid;
    protected ListGrid expansionGrid;
    protected Boolean canEdit;

    public ExpandableGridStructureView(String title, Boolean canReorder, Boolean canEdit) {
        this(title, canReorder, canEdit, false);
    }

    public ExpandableGridStructureView(String title, Boolean canReorder, Boolean canEdit, Boolean autoFetchData) {
        super(10);
        
        this.canEdit = canEdit;
        setHeight(300);
        setWidth100();
        setBackgroundColor("#eaeaea");
        setAlign(Alignment.CENTER);
        
        VStack stack = new VStack();
        stack.setHeight(250);
        stack.setWidth100();
        stack.setLayoutMargin(12);

        Label header = new Label(title);
        header.setBaseStyle("blcHeader");
        header.setHeight(15);

        stack.addMember(header);
        
        toolBar = new ToolStrip();
        toolBar.setHeight(30);
        toolBar.setWidth100();
        toolBar.setMinWidth(300);
        toolBar.addSpacer(6);
        addButton = new ToolStripButton();
        addButton.setTitle(BLCMain.getMessageManager().getString("addTitle"));
        addButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/actions/add.png");
        addButton.setDisabled(true);
        toolBar.addButton(addButton);
        toolBar.addSpacer(6);
        removeButton = new ToolStripButton();
        removeButton.setTitle(BLCMain.getMessageManager().getString("removeTitle"));
        removeButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Enterprise/images/actions/remove.png");
        removeButton.setDisabled(true);
        toolBar.addButton(removeButton);
        toolBar.setDisabled(false);
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
        expansionGrid.setCanGroupBy(false);
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
                String id = ((AbstractDynamicDataSource) grid.getDataSource()).getPrimaryKeyValue(record);
                ((PresentationLayerAssociatedDataSource) expansionGrid.getDataSource()).loadAssociatedGridBasedOnRelationship(id, null);
                return layout;
            }  
        };
        grid.setAutoFetchData(autoFetchData);
        grid.setShowHeader(true);
        grid.setShowHeaderContextMenu(false); 
        grid.setPreventDuplicates(true);
        grid.setCanReorderRecords(canReorder);
        grid.setHeight100();
        grid.setDisabled(true);
        grid.setCanSort(false);
        grid.setCellPadding(5);
        grid.setCanEdit(false);
        //grid.setEditEvent(ListGridEditEvent.DOUBLECLICK);
        //grid.setEditByCell(true);
        //grid.setAutoSaveEdits(true);
        //grid.setSaveByCell(true);
        grid.setAlternateRecordStyles(true);
        grid.setCanExpandMultipleRecords(false);
        grid.setCanExpandRecords(true);
        grid.setCanGroupBy(false);
        
        if (!canEdit) {
            grid.setAlternateBodyStyleName("editRowDisabled");
        }
        stack.addMember(grid);
        
        addMember(stack);
        setOverflow(Overflow.VISIBLE);

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

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }
    
    public ListGrid getExpansionGrid() {
        return expansionGrid;
    }
    
}
