package org.broadleafcommerce.gwt.client.view.catalog;

import org.broadleafcommerce.gwt.client.datasource.CategoryDataSource;

import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.events.RowContextClickEvent;
import com.smartgwt.client.widgets.grid.events.RowContextClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;

public class CatalogView extends HLayout implements CatalogDisplay {
	
	private static final TreeGridField name = new TreeGridField(CategoryDataSource._NAME, "Name", 200);
	private static final TreeGridField activeStartDate = new TreeGridField(CategoryDataSource._ACTIVE_START_DATE, "Start Date", 150);
	private static final TreeGridField activeEndDate = new TreeGridField(CategoryDataSource._ACTIVE_END_DATE, "End Date", 150);
	private static final TreeGridField url = new TreeGridField(CategoryDataSource._URL, "Url", 150);
	private static final TreeGridField urlKey = new TreeGridField(CategoryDataSource._URL_KEY, "Url Key", 150);
    
	public CatalogView() {
		super();
        setWidth100();
        setHeight100();
        
        VLayout sideNavLayout = new VLayout();
        sideNavLayout.setHeight100();
        sideNavLayout.setWidth(500);
        sideNavLayout.setShowResizeBar(true);

        TreeGrid categoryTreeGrid = new TreeGrid();
        categoryTreeGrid.setAlternateRecordStyles(true);
        categoryTreeGrid.setSelectionType(SelectionStyle.SINGLE);
        
        categoryTreeGrid.setCanEdit(true);
        categoryTreeGrid.setEditEvent(ListGridEditEvent.DOUBLECLICK);
        categoryTreeGrid.setEditByCell(true);
        categoryTreeGrid.setAutoSaveEdits(false);
        categoryTreeGrid.setDataSource(new CategoryDataSource());
        categoryTreeGrid.setAutoFetchData(true);
        //categoryTreeGrid.setShowFilterEditor(true);
        categoryTreeGrid.setCanReparentNodes(true);
       
        name.setFrozen(true);
        
        categoryTreeGrid.setFields(name, activeStartDate, activeEndDate, url, urlKey);
        
        categoryTreeGrid.addRowContextClickHandler(new RowContextClickHandler() {
            public void onRowContextClick(RowContextClickEvent event) {
                SC.say("Secret context menu discovered", "Hello from DAO Fusion team :-)");
                
                // prevent default browser context menu
                event.cancel();
            }
        });

        sideNavLayout.addMember(categoryTreeGrid);
        addMember(sideNavLayout);
	}

	public Canvas asCanvas() {
		return this;
	}

}
