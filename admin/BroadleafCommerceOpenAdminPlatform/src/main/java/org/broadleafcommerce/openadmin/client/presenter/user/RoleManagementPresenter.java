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
package org.broadleafcommerce.openadmin.client.presenter.user;

import com.smartgwt.client.data.DataSource;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.user.AdminRoleListDataSourceFactory;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.user.RoleManagementDisplay;

/**
 * 
 * @author jfischer
 *
 */
public class RoleManagementPresenter extends DynamicEntityPresenter implements Instantiable {

    public RoleManagementPresenter() {
        setAddNewItemTitle(BLCMain.getMessageManager().getString("newRoleTitle"));
    }

	public void setup() {
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("adminRoleDS", new AdminRoleListDataSourceFactory(), new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource top) {
				setupDisplayItems(top);
				((ListGridDataSource) top).setupGridFields(new String[]{"description"}, new Boolean[]{true, true, true});
			}
		}));

		/*
		TODO:  handle permissions
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("adminRoleDS", new AdminRoleListDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {

			public void onSetupSuccess(DataSource result) {
				roleSearchView = new EntitySearchDialog((ListGridDataSource) result);
				library.put("adminRoleDS", result);
			}
		}));
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("adminPermissionDS", new AdminPermissionListDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				userRolePresenter = new UserRolePresenter((UserRoleDisplay) getDisplay().getUserRolesDisplay(), roleSearchView);
				userRolePresenter.setDataSource((ListGridDataSource) library.get("adminRoleDS"), new String[]{"name", "description"}, new Boolean[]{false, false});
				userRolePresenter.setExpansionDataSource((ListGridDataSource) result , new String[]{"name", "description"}, new Boolean[]{false, false});
				userRolePresenter.setReadOnly(false);
				userRolePresenter.bind();
			}
		}));
		*/
	}

	@Override
	public RoleManagementDisplay getDisplay() {
		return (RoleManagementDisplay) display;

	}
	
}
