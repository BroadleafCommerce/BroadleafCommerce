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
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tile.TileGrid;
import com.smartgwt.client.widgets.tile.events.SelectionChangedEvent;
import com.smartgwt.client.widgets.tile.events.SelectionChangedHandler;
import org.broadleafcommerce.openadmin.client.callback.TileGridItemSelected;
import org.broadleafcommerce.openadmin.client.callback.TileGridItemSelectedHandler;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.TileGridDataSource;

/**
 * 
 * @author krosenberg
 *
 */
public class AssetSearchDialog extends Window {
        
    protected final TileGrid tileGrid;
    protected IButton saveButton;
    protected TileGridItemSelectedHandler handler;
    
    public AssetSearchDialog(TileGridDataSource staticAssetDataSource) {
        setIsModal(true);
        setShowModalMask(true);
        setShowMinimizeButton(false);
        setWidth(600);
        setHeight(500);
        setCanDragResize(true);
        setOverflow(Overflow.AUTO);
        setVisible(true);

        tileGrid = new TileGrid();
        tileGrid.setTileWidth(80);
        tileGrid.setTileHeight(120);
        tileGrid.setAutoFetchData(true);
        tileGrid.setSelectionType(SelectionStyle.SINGLE);
        tileGrid.setShowAllRecords(false);
        tileGrid.setHeight100();
        tileGrid.setWidth100();
        staticAssetDataSource.setAssociatedGrid(tileGrid);
        staticAssetDataSource.setupGridFields(new String[]{"pictureLarge", "name"});
        tileGrid.setDataSource(staticAssetDataSource);
        tileGrid.addSelectionChangedHandler(new SelectionChangedHandler() {
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
        buttonsLayout.setWidth100();
        
        HLayout filterLayout = new HLayout(15);
        filterLayout.setAlign(Alignment.CENTER);
        filterLayout.setLayoutTopMargin(10);
        filterLayout.setLayoutRightMargin(5);
        filterLayout.setLayoutBottomMargin(10);
        filterLayout.setWidth100();
        
        final DynamicForm filterForm = new DynamicForm();  
        filterForm.setDataSource(staticAssetDataSource);  
        filterForm.setAutoFocus(false);
        
        TextItem nameFilterItem = new TextItem("name", "Name");
        nameFilterItem.setWrapTitle(false);

        TextItem urlFilterItem = new TextItem("fullUrl", "Url");
        nameFilterItem.setWrapTitle(false);
        
        filterForm.setFields(nameFilterItem, urlFilterItem);

        IButton searchButton = new IButton("Search");
        searchButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Criteria valuesAsCriteria = filterForm.getValuesAsCriteria();
                tileGrid.fetchData(valuesAsCriteria);
            }
        });
        
        filterLayout.addMember(filterForm);
        filterLayout.addMember(searchButton);
        VLayout mainLayout = new VLayout();
        
        mainLayout.addMember(filterLayout);
        mainLayout.addMember(tileGrid);
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
