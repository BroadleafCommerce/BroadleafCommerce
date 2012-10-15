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
import org.broadleafcommerce.admin.client.datasource.catalog.inventory.InventorySkuDataSourceFactory;
import org.broadleafcommerce.admin.client.view.catalog.inventory.FulfillmentLocationDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.CustomCriteriaListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;

public class FulfillmentLocationPresenter extends DynamicEntityPresenter implements Instantiable {

    protected InventoryPresenter inventoryPresenter;

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
                inventoryPresenter = new InventoryPresenter(getDisplay().getInventoryDisplay(), BLCMain.getMessageManager().getString("newInventory"));
                inventoryPresenter.setDataSource((CustomCriteriaListGridDataSource) result, new String[]{"sku.id", "sku.name", "quantityAvailable", "quantityOnHand"}, new Boolean[]{false, false, false, false});
            }
        }));

        //setup sku lookup
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("skuDS", new InventorySkuDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                ListGridDataSource ds = (ListGridDataSource) result;
                ds.resetPermanentFieldVisibility("id", "name", "productOptionList");
                ds.getField("id").setAttribute("order", 1);
                DynamicEntityDataSource dataSource = getPresenterSequenceSetupManager().getDataSource("inventoryDS");
                dataSource.getFormItemCallbackHandlerManager().addSearchFormItemCallback("sku", new EntitySearchDialog(ds), "Sku", null, null, null, dataSource);
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
