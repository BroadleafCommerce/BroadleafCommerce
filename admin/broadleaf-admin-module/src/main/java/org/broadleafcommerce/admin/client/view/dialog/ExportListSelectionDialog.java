/*
 * Copyright 2012 the original author or authors.
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

package org.broadleafcommerce.admin.client.view.dialog;

import org.broadleafcommerce.admin.client.dto.AdminExporterDTO;
import org.broadleafcommerce.openadmin.client.BLCMain;

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

import java.util.List;

/**
 * 
 * @author Phillip Verheyden
 */
public class ExportListSelectionDialog  extends Window {

    protected ListGrid searchGrid;
    protected IButton okButton;
    protected List<AdminExporterDTO> exporters;

    public ExportListSelectionDialog() {
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
        searchGrid.setFields(new ListGridField("friendlyName", "Name"));
        
        searchGrid.addSelectionChangedHandler(new SelectionChangedHandler() {
            @Override
            public void onSelectionChanged(SelectionEvent event) {
                okButton.enable();
            }
        });
        
        addItem(searchGrid);
        
        okButton = new IButton(BLCMain.getMessageManager().getString("ok"));
        okButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Record selectedRecord = searchGrid.getSelectedRecord();
                //Show additional criteria if available
                AdminExporterDTO exporter = (AdminExporterDTO)selectedRecord.getAttributeAsObject("exporter");
                //show form with criteria fields
                ExportCriteriaDialog dialog = new ExportCriteriaDialog(exporter);
                hide();
                dialog.launch();
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
        hLayout.addMember(okButton);
        hLayout.addMember(cancelButton);
        hLayout.setLayoutTopMargin(10);
        hLayout.setLayoutBottomMargin(10);
        
        addItem(hLayout);
    }
    
    public void search(String title, List<AdminExporterDTO> exporters) {
        this.setTitle(title);
        centerInPage();
        okButton.disable();
        searchGrid.deselectAllRecords();

        this.exporters = exporters;
        ListGridRecord[] records = new ListGridRecord[exporters.size()];
        int j = 0;
        for (AdminExporterDTO exporter : exporters) {
            ListGridRecord record = new ListGridRecord();
            record.setAttribute("friendlyName", exporter.getFriendlyName());
            record.setAttribute("exporter", exporter);
            records[j] = record;
            j++;
        }
        searchGrid.setData(records);

        show();
    }

    public IButton getOkButton() {
        return okButton;
    }

    public void setOkButton(IButton okButton) {
        this.okButton = okButton;
    }

    public ListGrid getSearchGrid() {
        return searchGrid;
    }

    public void setSearchGrid(ListGrid searchGrid) {
        this.searchGrid = searchGrid;
    }

}
