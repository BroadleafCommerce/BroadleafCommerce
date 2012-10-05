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
package org.broadleafcommerce.admin.client.presenter.catalog.inventory;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import org.broadleafcommerce.admin.client.datasource.catalog.inventory.FulfillmentLocationDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.inventory.InventoryDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.inventory.SkuListDataSourceFactory;
import org.broadleafcommerce.admin.client.view.catalog.inventory.FulfillmentLocationDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.CustomCriteriaListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.presenter.structure.CreateBasedListStructurePresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;

public class FulfillmentLocationPresenter extends DynamicEntityPresenter implements Instantiable {

    protected CreateBasedListStructurePresenter inventoryPresenter;

    @Override
    protected void changeSelection(final Record selectedRecord) {
        inventoryPresenter.load(selectedRecord, getPresenterSequenceSetupManager().getDataSource("inventoryDS"));

        String fulfillmentLocationId = selectedRecord.getAttribute("id");
        CustomCriteriaListGridDataSource skuDS = (CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("skuDS");
        skuDS.setCustomCriteria(new String[]{skuDS.getCustomCriteria()[0], fulfillmentLocationId});

    }

    @Override
    public void bind() {
        super.bind();
        inventoryPresenter.bind();
    }

    public void setup() {

        //setup FulfillmentLocation DataSource
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("fulfillmentLocationDS", new FulfillmentLocationDataSourceFactory(), new AsyncCallbackAdapter() {
            public void onSetupSuccess(DataSource top) {
                setupDisplayItems(top);
                ((ListGridDataSource) top).setupGridFields(new String[]{}, new Boolean[]{});
            }
        }));

        //setup Inventory DataSource
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("inventoryDS", new InventoryDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                inventoryPresenter = new CreateBasedListStructurePresenter(getDisplay().getInventoryDisplay(), BLCMain.getMessageManager().getString("newInventory"));
                inventoryPresenter.setDataSource((CustomCriteriaListGridDataSource) result, new String[]{}, new Boolean[]{});
            }
        }));

        //setup sku lookup
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("skuDS", new SkuListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                ListGridDataSource listGridDataSource = (ListGridDataSource) result;
                listGridDataSource.resetPermanentFieldVisibility("id", "name", "productOptionList");
                final EntitySearchDialog skuSearch = new EntitySearchDialog(listGridDataSource, true);
                DynamicEntityDataSource dataSource = getPresenterSequenceSetupManager().getDataSource("inventoryDS");
                dataSource.getFormItemCallbackHandlerManager().addSearchFormItemCallback("sku", skuSearch, "Sku", null, null, null, dataSource);
            }
        }));

    }

    @Override
    public FulfillmentLocationDisplay getDisplay() {
        return (FulfillmentLocationDisplay) display;
    }

    @Override
    protected void saveClicked() {
        super.saveClicked();
        display.getListDisplay().getGrid().invalidateCache();
    }
}
