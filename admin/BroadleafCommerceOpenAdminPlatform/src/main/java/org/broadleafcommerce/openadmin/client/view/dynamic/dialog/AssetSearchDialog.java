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
package org.broadleafcommerce.openadmin.client.view.dynamic.dialog;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.tile.TileGrid;
import com.smartgwt.client.widgets.tile.events.SelectionChangedEvent;
import com.smartgwt.client.widgets.tree.TreeGrid;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.TileGridDataSource;
import org.broadleafcommerce.openadmin.client.event.SearchItemSelectedEvent;
import org.broadleafcommerce.openadmin.client.event.SearchItemSelectedEventHandler;
import org.broadleafcommerce.openadmin.client.setup.AppController;

/**
 * 
 * @author krosenberg
 *
 */
public class AssetSearchDialog extends Window {
		
	protected TileGrid tileGrid;
	protected TreeGrid treeGrid;
	protected IButton saveButton;
	protected SearchItemSelectedEventHandler handler;
	
	public AssetSearchDialog(final TileGridDataSource staticAssetDataSource, final PresentationLayerAssociatedDataSource staticAssetFolderDataSource) {
		this.setIsModal(true);
		this.setShowModalMask(true);
		this.setShowMinimizeButton(false);
		this.setWidth(600);
		this.setHeight(300);
		this.setCanDragResize(true);
		this.setOverflow(Overflow.AUTO);
		this.setVisible(true);
		
		treeGrid = new TreeGrid();
		treeGrid.setDataSource(staticAssetFolderDataSource);
		treeGrid.setHeight100();
		treeGrid.setWidth("30%");
		treeGrid.setAlternateRecordStyles(true);
	    treeGrid.setSelectionType(SelectionStyle.SINGLE);
	    treeGrid.setCanEdit(false);
	    treeGrid.setAutoFetchData(true);
	    treeGrid.setDrawAheadRatio(4);
	    treeGrid.setCanSort(false);
	    treeGrid.setCanResizeFields(true);
	    treeGrid.setShowRoot(true);
		treeGrid.addSelectionChangedHandler(new com.smartgwt.client.widgets.grid.events.SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
			GWT.log("tree selection");
			String id = staticAssetFolderDataSource.getPrimaryKeyValue(event.getSelectedRecord());
			GWT.log("id: " + id);
			((PresentationLayerAssociatedDataSource) tileGrid.getDataSource()).loadAssociatedGridBasedOnRelationship(id, new DSCallback() {
				public void execute(DSResponse response, Object rawData, DSRequest request) {
					tileGrid.enable();
				}
			});
		}});
		
        
		tileGrid = new TileGrid();
        tileGrid.setTileWidth(80);
        tileGrid.setTileHeight(120);
        tileGrid.setAutoFetchData(false);
        tileGrid.setSelectionType(SelectionStyle.SINGLE);
        tileGrid.setShowAllRecords(false);
        tileGrid.setHeight(230);
        tileGrid.setWidth("70%");
        staticAssetDataSource.setAssociatedGrid(tileGrid);
        staticAssetDataSource.setupGridFields(new String[]{"pictureLarge", "name"});
        tileGrid.setDataSource(staticAssetDataSource);
        tileGrid.addSelectionChangedHandler(new com.smartgwt.client.widgets.tile.events.SelectionChangedHandler() {
        	@Override
        	public void onSelectionChanged(SelectionChangedEvent event) {
        		saveButton.enable();
        	}
        });
        
        
		HLayout dialogHLayout = new HLayout();
		dialogHLayout.addMember(treeGrid);
		dialogHLayout.addMember(tileGrid);
		
		addItem(dialogHLayout);
        
        saveButton = new IButton("Ok");
        saveButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	Record selectedRecord = tileGrid.getSelectedRecord();
            	AppController.getInstance().getEventBus().addHandler(SearchItemSelectedEvent.TYPE, AssetSearchDialog.this.handler);
            	AppController.getInstance().getEventBus().fireEvent(new SearchItemSelectedEvent((ListGridRecord) selectedRecord, tileGrid.getDataSource()));
            	AppController.getInstance().getEventBus().removeHandler(SearchItemSelectedEvent.TYPE, AssetSearchDialog.this.handler);
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
//		searchGrid.deselectAllRecords();
		show();
	}

    public SearchItemSelectedEventHandler getHandler() {
        return handler;
    }

    public void setHandler(SearchItemSelectedEventHandler handler) {
        this.handler = handler;
    }

    public IButton getSaveButton() {
        return saveButton;
    }

    public void setSaveButton(IButton saveButton) {
        this.saveButton = saveButton;
    }

}
