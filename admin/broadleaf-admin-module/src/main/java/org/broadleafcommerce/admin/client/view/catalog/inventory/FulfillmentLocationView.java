/**
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
package org.broadleafcommerce.admin.client.view.catalog.inventory;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.TabSet;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureView;

public class FulfillmentLocationView extends HLayout implements Instantiable, FulfillmentLocationDisplay {

    protected DynamicEntityListView listDisplay;
    protected DynamicFormView dynamicFormDisplay;
    protected GridStructureView inventoryDisplay;

    public FulfillmentLocationView() {
        setHeight100();
        setWidth100();
    }

    @Override
    public void build(DataSource entityDataSource, DataSource... additionalDataSources) {

        //setup the layout for fulfillment locations
        VLayout leftVerticalLayout = new VLayout();
        leftVerticalLayout.setID("fulfillmentLocationListLayout");
        leftVerticalLayout.setHeight100();
        leftVerticalLayout.setWidth("33%");
        leftVerticalLayout.setShowResizeBar(true);
        addMember(leftVerticalLayout);

        //create the DynamicEntityListView and add to the View
        listDisplay = new DynamicEntityListView(BLCMain.getMessageManager().getString("fulfillmentLocationListTitle"), entityDataSource);
        listDisplay.setShowResizeBar(true);
        leftVerticalLayout.addMember(listDisplay);

        //setup TabSet widget to hold tabs and add to the View
        TabSet topTabSet = new TabSet();
        topTabSet.setID("inventoryTopTabSet");
        topTabSet.setTabBarPosition(Side.TOP);
        topTabSet.setPaneContainerOverflow(Overflow.HIDDEN);
        topTabSet.setWidth("67%");
        topTabSet.setHeight100();
        topTabSet.setPaneMargin(0);
        addMember(topTabSet);

        //create Tab for FulfillmentLocation details
        Tab detailsTab = new Tab(BLCMain.getMessageManager().getString("fulfillmentLocationDetailsTitle"));
        detailsTab.setID("fulfillmentLocationDetailsTab");
        topTabSet.addTab(detailsTab);

        //create DynamicFormView for FulfillmentLocation and set to Tab pane
        dynamicFormDisplay = new DynamicFormView(BLCMain.getMessageManager().getString("fulfillmentLocationDetailsTitle"), entityDataSource);
        detailsTab.setPane(dynamicFormDisplay);

        //create Tab for Inventory grid
        Tab inventoryTab = new Tab(BLCMain.getMessageManager().getString("inventoryListTitle"));
        inventoryTab.setID("inventoryList");
        topTabSet.addTab(inventoryTab);

        VLayout inventoryLayout = new VLayout();
        inventoryLayout.setID("inventoryLayout");
        inventoryLayout.setHeight100();
        inventoryLayout.setWidth100();
        inventoryLayout.setBackgroundColor("#eaeaea");
        inventoryLayout.setOverflow(Overflow.AUTO);

        // create SubItemView for Inventory and set the Tab pane
        inventoryDisplay = new GridStructureView(BLCMain.getMessageManager().getString("inventoryListTitle"), false, true);
        inventoryLayout.addMember(inventoryDisplay);
        inventoryTab.setPane(inventoryLayout);

    }

    @Override
    public DynamicEntityListDisplay getListDisplay() {
        return listDisplay;
    }

    @Override
    public DynamicFormDisplay getDynamicFormDisplay() {
        return dynamicFormDisplay;
    }

    @Override
    public GridStructureView getInventoryDisplay() {
        return inventoryDisplay;
    }

    @Override
    public Canvas asCanvas() {
        return this;
    }
}
