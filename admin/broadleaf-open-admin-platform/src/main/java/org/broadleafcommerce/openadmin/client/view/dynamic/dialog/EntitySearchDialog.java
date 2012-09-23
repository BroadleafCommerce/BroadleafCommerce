/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.view.dynamic.dialog;

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.callback.SearchItemSelected;
import org.broadleafcommerce.openadmin.client.callback.SearchItemSelectedHandler;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;

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
		
	protected ListGrid searchGrid;
	protected IButton saveButton;
	protected SearchItemSelectedHandler handler;

    public EntitySearchDialog(ListGridDataSource dataSource) {
        this(dataSource, true);
    }
	
	public EntitySearchDialog(ListGridDataSource dataSource, boolean autoFetch) {
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
        searchGrid.setAutoFetchData(autoFetch);
        searchGrid.setAlternateRecordStyles(true);
        searchGrid.setSelectionType(SelectionStyle.SINGLE);
        searchGrid.setShowAllColumns(false);
        searchGrid.setShowAllRecords(false);
        searchGrid.setDrawAllMaxCells(20);
        searchGrid.setShowFilterEditor(true);
        searchGrid.setHeight(230);
        searchGrid.setEmptyMessage(BLCMain.getMessageManager().getString("emptyMessage"));
        searchGrid.setCanGroupBy(false);
       
        dataSource.setAssociatedGrid(searchGrid);
        searchGrid.setDataSource(dataSource);
		dataSource.setupGridFields(new String[]{}, new Boolean[]{});
        
        searchGrid.setCanEdit(false);
        searchGrid.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
            public void onSelectionChanged(SelectionEvent event) {
                if (searchGrid.anySelected()) {
				    saveButton.enable();
                }
			}
        });
        
        addItem(searchGrid);
        
        saveButton = new IButton(BLCMain.getMessageManager().getString("ok"));
        saveButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            	Record selectedRecord = searchGrid.getSelectedRecord();
                EntitySearchDialog.this.handler.onSearchItemSelected(new SearchItemSelected((ListGridRecord) selectedRecord, searchGrid.getDataSource()));
            	hide();
            }
        });

        IButton cancelButton = new IButton(BLCMain.getMessageManager().getString("cancel"));
        cancelButton.addClickHandler(new ClickHandler() {  
            @Override
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
	
	public void search(String title, SearchItemSelectedHandler handler) {
		this.setTitle(title);
		this.handler = handler;
		centerInPage();
		saveButton.disable();
		searchGrid.deselectAllRecords();
		show();
	}

    public SearchItemSelectedHandler getHandler() {
        return handler;
    }

    public void setHandler(SearchItemSelectedHandler handler) {
        this.handler = handler;
    }

    public IButton getSaveButton() {
        return saveButton;
    }

    public void setSaveButton(IButton saveButton) {
        this.saveButton = saveButton;
    }

    public ListGrid getSearchGrid() {
        return searchGrid;
    }

    public void setSearchGrid(ListGrid searchGrid) {
        this.searchGrid = searchGrid;
    }
}
