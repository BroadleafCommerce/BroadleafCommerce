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

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import org.broadleafcommerce.openadmin.client.callback.SearchItemSelected;
import org.broadleafcommerce.openadmin.client.callback.SearchItemSelectedHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class PolymorphicTypeSelectionDialog extends Window {

    protected ListGrid searchGrid;
    protected IButton saveButton;
    protected SearchItemSelectedHandler handler;

    public PolymorphicTypeSelectionDialog() {
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
        searchGrid.setAlternateRecordStyles(true);
        searchGrid.setSelectionType(SelectionStyle.SINGLE);
        searchGrid.setShowAllColumns(true);
        searchGrid.setShowAllRecords(true);
        searchGrid.setShowFilterEditor(false);
        searchGrid.setHeight(230);
        searchGrid.setCanGroupBy(false);
        searchGrid.setFields(new ListGridField("name", "Name"));
        
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
                PolymorphicTypeSelectionDialog.this.handler.onSearchItemSelected(new SearchItemSelected((ListGridRecord) selectedRecord, searchGrid.getDataSource()));
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
    
    public void search(String title, LinkedHashMap<String, String> types, SearchItemSelectedHandler handler) {
        this.setTitle(title);
        this.handler = handler;
        centerInPage();
        saveButton.disable();
        searchGrid.deselectAllRecords();

        ListGridRecord[] records = new ListGridRecord[types.size()];
        int j = 0;
        for (Map.Entry<String, String> entry : types.entrySet()) {
            ListGridRecord record = new ListGridRecord();
            record.setAttribute("name", entry.getValue());
            record.setAttribute("fullyQualifiedType", entry.getKey());
            records[j] = record;
            j++;
        }
        searchGrid.setData(records);

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
