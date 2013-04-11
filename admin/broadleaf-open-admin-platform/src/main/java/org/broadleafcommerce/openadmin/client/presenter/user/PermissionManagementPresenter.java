/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.presenter.user;

import com.smartgwt.client.data.DataSource;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.user.AdminCreatePermissionListDataSourceFactory;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;

/**
 * 
 * @author jfischer
 *
 */
public class PermissionManagementPresenter extends DynamicEntityPresenter implements Instantiable {

    public PermissionManagementPresenter() {
        setGridFields(new String[]{"name","description"});
    }

    @Override
    protected void addClicked() {
        addClicked(BLCMain.getMessageManager().getString("newPermissionTitle"));
    }

    public void setup() {
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("adminPermissionDS", new AdminCreatePermissionListDataSourceFactory(), new AsyncCallbackAdapter() {
            public void onSetupSuccess(DataSource top) {
                setupDisplayItems(top);
                ((ListGridDataSource) top).setupGridFields(new String[]{"name","description"}, new Boolean[]{true, true});
            }
        }));
    }
}
