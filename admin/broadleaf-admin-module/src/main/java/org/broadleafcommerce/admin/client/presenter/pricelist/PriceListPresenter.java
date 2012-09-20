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

package org.broadleafcommerce.admin.client.presenter.pricelist;

import org.broadleafcommerce.admin.client.datasource.pricelist.PriceListDataSourceFactory;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;

import com.smartgwt.client.data.DataSource;

/**
 * 
 * @author ppatel
 *
 */
public class PriceListPresenter extends DynamicEntityPresenter implements Instantiable {

    public PriceListPresenter() {
        setGridFields(new String[]{"id","friendlyName","priceKey","currency"});
    }

    @Override
    protected void addClicked() {
        addClicked(BLCMain.getMessageManager().getString("PriceListImpl_Details"));

    }

    @Override
    public void setup() {
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("priceListDS", new PriceListDataSourceFactory(), new AsyncCallbackAdapter() {
			@Override
            public void onSetupSuccess(DataSource top) {
				setupDisplayItems(top);
				((ListGridDataSource) top).setupGridFields(new String[]{"id","friendlyName","priceKey","currency"}, new Boolean[]{true, true,true,true});
			}
		}));
	}
}