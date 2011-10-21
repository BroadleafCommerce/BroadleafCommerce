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

import com.smartgwt.client.data.Criteria;
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
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tile.TileGrid;
import com.smartgwt.client.widgets.tile.events.SelectionChangedEvent;
import com.smartgwt.client.widgets.tree.TreeGrid;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.TileGridDataSource;
import org.broadleafcommerce.openadmin.client.callback.TileGridItemSelected;
import org.broadleafcommerce.openadmin.client.callback.TileGridItemSelectedHandler;

/**
 * 
 * @author krosenberg
 *
 */
public class AssetSearchDialog extends Window {
		
	protected final TileGrid tileGrid;
	protected final TreeGrid treeGrid;
	protected IButton saveButton;
	protected TileGridItemSelectedHandler handler;
	
	public AssetSearchDialog(final TileGridDataSource staticAssetDataSource, final PresentationLayerAssociatedDataSource staticAssetFolderDataSource) {
		this.setIsModal(true);
		this.setShowModalMask(true);
		this.setShowMinimizeButton(false);
		this.setWidth(600);
		this.setHeight(340);
		this.setCanDragResize(true);
		this.setOverflow(Overflow.AUTO);
		this.setVisible(true);
		
		treeGrid = new TreeGrid();
		treeGrid.setDataSource(staticAssetFolderDataSource);
		treeGrid.setHeight(230);
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
			if (event.getSelectedRecord() != null) {
				String id = staticAssetFolderDataSource.getPrimaryKeyValue(event.getSelectedRecord());
				((PresentationLayerAssociatedDataSource) tileGrid.getDataSource()).loadAssociatedGridBasedOnRelationship(id, new DSCallback() {
					public void execute(DSResponse response, Object rawData, DSRequest request) {
						tileGrid.enable();
					}
				});
			} 
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
        tileGrid.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    if (tileGrid.anySelected()) {
                        saveButton.enable();
                    }
                }
            }
        });
        

        
		HLayout browseHLayout = new HLayout();
		browseHLayout.addMember(treeGrid);
		browseHLayout.addMember(tileGrid);
		
        saveButton = new IButton("Ok");
        saveButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	//getSelectedRecord() throws a ClassCastException from SmartGWT, maybe a bug.  this seems to work instead:
            	Record selectedRecord = tileGrid.getSelection()[0];
                handler.onSearchItemSelected(new TileGridItemSelected(selectedRecord, tileGrid.getDataSource()));
            	hide();
            }
        });

        IButton cancelButton = new IButton("Cancel");  
        cancelButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
            	hide();
            }  
        });
        
        HLayout buttonsLayout = new HLayout(10);
        buttonsLayout.setAlign(Alignment.CENTER);
        buttonsLayout.addMember(saveButton);
        buttonsLayout.addMember(cancelButton);
        buttonsLayout.setLayoutTopMargin(10);
        buttonsLayout.setLayoutBottomMargin(10);
        
        HLayout filterLayout = new HLayout();
        filterLayout.setAlign(Alignment.RIGHT);
        filterLayout.setLayoutTopMargin(10);
        filterLayout.setLayoutRightMargin(5);
        
        final DynamicForm filterForm = new DynamicForm();  
        filterForm.setDataSource(staticAssetDataSource);  
        filterForm.setAutoFocus(false);
        
        TextItem nameFilterItem = new TextItem("name", "Search");
        nameFilterItem.setWrapTitle(false);
        
        filterForm.setFields(nameFilterItem);  
        filterForm.addItemChangedHandler(new ItemChangedHandler() {  
            public void onItemChanged(ItemChangedEvent event) {  
            	String id = staticAssetFolderDataSource.getPrimaryKeyValue(treeGrid.getSelectedRecord());
            	Criteria valuesAsCriteria = filterForm.getValuesAsCriteria();
            	valuesAsCriteria.addCriteria(new Criteria("parentFolder", id));
                tileGrid.fetchData(valuesAsCriteria); 
            }  
        });  
        
        filterLayout.addMember(filterForm);
        VLayout mainLayout = new VLayout();
        
        mainLayout.addMember(filterLayout);
        mainLayout.addMember(browseHLayout);
        mainLayout.addMember(buttonsLayout);

        addItem(mainLayout); 
	}
	
	public void search(String title, TileGridItemSelectedHandler handler) {
		this.setTitle(title);
		this.handler = handler;
		centerInPage();
		saveButton.disable();
		show();
	}

    public TileGridItemSelectedHandler getHandler() {
        return handler;
    }

    public void setHandler(TileGridItemSelectedHandler handler) {
        this.handler = handler;
    }

    public IButton getSaveButton() {
        return saveButton;
    }

    public void setSaveButton(IButton saveButton) {
        this.saveButton = saveButton;
    }

}
