package org.broadleafcommerce.gwt.client.view.dynamic;

import org.broadleafcommerce.gwt.client.AppController;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.event.SearchItemSelectedEvent;
import org.broadleafcommerce.gwt.client.event.SearchItemSelectedEventHandler;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;

public class DynamicEntitySearchView extends Window {
		
	private ListGrid searchGrid;
	private IButton saveButton;
	private SearchItemSelectedEventHandler handler;
	
	public DynamicEntitySearchView() {
		this.setIsModal(true);
		this.setShowModalMask(true);
		this.setShowMinimizeButton(false);
		this.setWidth(600);
		this.setHeight(300);
		this.setCanDragResize(true);
		this.setOverflow(Overflow.AUTO);
		
		searchGrid = new ListGrid();
        searchGrid.setAlternateRecordStyles(true);
        searchGrid.setSelectionType(SelectionStyle.SINGLE);
        searchGrid.setAutoFetchData(false);
        searchGrid.setDrawAheadRatio(4);
        searchGrid.setShowFilterEditor(true);
        searchGrid.setHeight100();
        
        searchGrid.addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				saveButton.enable();
			}
        });
        
        addItem(searchGrid);
        
        saveButton = new IButton("Ok");
        saveButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	Record selectedRecord = searchGrid.getSelectedRecord();
            	AppController.getInstance().getEventBus().addHandler(SearchItemSelectedEvent.TYPE, DynamicEntitySearchView.this.handler);
            	AppController.getInstance().getEventBus().fireEvent(new SearchItemSelectedEvent((ListGridRecord) selectedRecord, searchGrid.getDataSource()));
            	AppController.getInstance().getEventBus().removeHandler(SearchItemSelectedEvent.TYPE, DynamicEntitySearchView.this.handler);
            	//destinationGrid.addData(selectedRecord);
            	hide();
            }
        });

        IButton cancelButton = new IButton("Cancel");  
        cancelButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
            	hide();
            }  
        });
        
        HLayout hLayout = new HLayout(10);
        hLayout.setAlign(Alignment.CENTER);
        hLayout.addMember(saveButton);
        hLayout.addMember(cancelButton);
        hLayout.setLayoutTopMargin(10);
        hLayout.setLayoutBottomMargin(10);
        addItem(hLayout);
	}
	
	public void search(ListGridDataSource dataSource, String title, SearchItemSelectedEventHandler handler) {
		this.setTitle(title);
		this.handler = handler;
		dataSource.setAssociatedGrid(searchGrid);
		dataSource.setupFields();
		searchGrid.setDataSource(dataSource);
		searchGrid.fetchData();
		centerInPage();
		saveButton.disable();
		show();
	}

}
