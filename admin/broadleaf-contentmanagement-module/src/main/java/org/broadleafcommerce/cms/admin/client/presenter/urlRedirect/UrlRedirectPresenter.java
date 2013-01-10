/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.admin.client.presenter.urlRedirect;

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
public class UrlRedirectPresenter extends DynamicEntityPresenter implements Instantiable {

    public UrlRedirectPresenter() {
        setGridFields(new String[]{"id","incomingUrl","newURL","urlRedirectType"});
    }

    @Override
    protected void addClicked() {
        addClicked(BLCMain.getMessageManager().getString("newURLRedirectTitle"));
    }

    public void setup() {
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("urlRedirectDS", new org.broadleafcommerce.cms.admin.client.datasource.url.UrlRedirectDataSourceFactory(), new AsyncCallbackAdapter() {
            public void onSetupSuccess(DataSource top) {
                setupDisplayItems(top);
                ((ListGridDataSource) top).setupGridFields(new String[]{"id","incomingUrl","newURL","urlRedirectType"}, new Boolean[]{true, true,true,true});
            }
        }));
    }
}