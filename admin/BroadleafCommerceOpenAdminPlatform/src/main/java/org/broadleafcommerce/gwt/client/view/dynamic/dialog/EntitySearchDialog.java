/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.gwt.client.view.dynamic.dialog;

import org.broadleafcommerce.gwt.client.BLCMain;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.event.SearchItemSelectedEvent;
import org.broadleafcommerce.gwt.client.event.SearchItemSelectedEventHandler;
import org.broadleafcommerce.gwt.client.setup.AppController;

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

/**
 * 
 * @author jfischer
 *
 */
public class EntitySearchDialog extends Window {
		
	private ListGrid searchGrid;
	private IButton saveButton;
	private SearchItemSelectedEventHandler handler;
	
	public EntitySearchDialog(ListGridDataSource dataSource) {
		super();
		this.setIsModal(true);
		this.setShowModalMask(true);
		this.setShowMinimizeButton(false);
		this.setWidth(600);
		this.setHeight(300);
		this.setCanDragResize(true);
		this.setOverflow(Overflow.AUTO);
		this.setVisible(false);
        
		searchGrid = new ListGrid();
		dataSource.setAssociatedGrid(searchGrid);
		dataSource.setupGridFields(new String[]{}, new Boolean[]{});
        searchGrid.setAlternateRecordStyles(true);
        searchGrid.setSelectionType(SelectionStyle.SINGLE);
        searchGrid.setAutoFetchData(false);
        searchGrid.setDataSource(dataSource);
        searchGrid.setShowAllColumns(false);
        searchGrid.setShowAllRecords(false);
        searchGrid.setDrawAllMaxCells(20);
        searchGrid.setShowFilterEditor(true);
        searchGrid.setHeight(230);
        searchGrid.setEmptyMessage(BLCMain.OPENADMINMESSAGES.emptyMessage());
        searchGrid.setCanGroupBy(false);
        
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
            	AppController.getInstance().getEventBus().addHandler(SearchItemSelectedEvent.TYPE, EntitySearchDialog.this.handler);
            	AppController.getInstance().getEventBus().fireEvent(new SearchItemSelectedEvent((ListGridRecord) selectedRecord, searchGrid.getDataSource()));
            	AppController.getInstance().getEventBus().removeHandler(SearchItemSelectedEvent.TYPE, EntitySearchDialog.this.handler);
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
	
	public void search(String title, SearchItemSelectedEventHandler handler) {
		this.setTitle(title);
		this.handler = handler;
		centerInPage();
		saveButton.disable();
		searchGrid.deselectAllRecords();
		show();
	}

}
