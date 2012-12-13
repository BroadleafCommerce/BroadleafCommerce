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

/**
 * 
 */

package org.broadleafcommerce.admin.client.presenter.catalog.product;

import org.broadleafcommerce.admin.client.datasource.catalog.product.ProductOptionListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.catalog.product.ProductOptionValueDataSourceFactory;
import org.broadleafcommerce.admin.client.view.catalog.product.ProductOptionDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.presenter.entity.SubPresentable;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.MapStructureEntityEditDialog;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;

/**
 * @author Phillip Verheyden
 *
 */
public class ProductOptionPresenter extends DynamicEntityPresenter implements Instantiable {

    protected SubPresentable productOptionValuePresenter;
    protected MapStructureEntityEditDialog mapEntityAdd;

    @Override
    protected void changeSelection(final Record selectedRecord) {
        AbstractDynamicDataSource dataSource = (AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource();
        productOptionValuePresenter.load(selectedRecord, dataSource);
    }

    @Override
    public void bind() {
        super.bind();
        productOptionValuePresenter.bind();
    }

    @Override
    public void setup() {
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productOptionDS", new ProductOptionListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource top) {
                setupDisplayItems(top);
                ((ListGridDataSource) top).setupGridFields(new String[] {}, new Boolean[] {});
            }
        }));

        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productOptionLocaleDS", new LocaleDataSourceFactory(org.broadleafcommerce.openadmin.client.datasource.CeilingEntities.LOCALE), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource top) {

            }
        }));

        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("productOptionValueDS", new ProductOptionValueDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                productOptionValuePresenter = new ProductOptionValueSubPresenter(getDisplay().getProductOptionValueDisplay(), BLCMain.getMessageManager().getString("newProductOptionValue"), null, false, true, false);
                productOptionValuePresenter.setDataSource((ListGridDataSource) result, new String[] {}, new Boolean[] {});
            }
        }));
    }

    @Override
    public void postSetup(Canvas container) {
        gridHelper.addSubPresentableHandlers(display.getListDisplay().getGrid(), productOptionValuePresenter);
        super.postSetup(container);
    }

    @Override
    public ProductOptionDisplay getDisplay() {
        return (ProductOptionDisplay) display;
    }

}
