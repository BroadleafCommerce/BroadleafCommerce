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

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.user.AdminPermissionListDataSourceFactory;
import org.broadleafcommerce.openadmin.client.datasource.user.AdminRoleListDataSourceFactory;
import org.broadleafcommerce.openadmin.client.datasource.user.AdminUserListDataSourceFactory;
import org.broadleafcommerce.openadmin.client.datasource.user.AdminUserPermissionListDataSourceFactory;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.presenter.entity.SubPresentable;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.openadmin.client.view.user.UserManagementDisplay;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;

/**
 * @author jfischer
 */
public class UserManagementPresenter extends DynamicEntityPresenter implements Instantiable {

    protected SubPresentable userPermissionPresenter;
    protected UserRolePresenter userRolePresenter;
    protected EntitySearchDialog roleSearchView;
    protected EntitySearchDialog permissionSearchView;

    @Override
    protected void changeSelection(final Record selectedRecord) {
        userRolePresenter.load(selectedRecord, (AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource(), null);
        userPermissionPresenter.load(selectedRecord, (AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource(), null);
    }

    @Override
    protected void addClicked() {
        addClicked(BLCMain.getMessageManager().getString("newAdminUserTitle"));
    }

    @Override
    public void bind() {
        super.bind();
    }

    @Override
    public void setup() {
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("adminUserDS", new AdminUserListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource top) {
                setupDisplayItems(top);
                ((ListGridDataSource) top).setupGridFields(new String[]{"name", "login", "email"}, new Boolean[]{true, true, true});
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("adminRoleDS", new AdminRoleListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                roleSearchView = new EntitySearchDialog((ListGridDataSource) result, true);
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("adminPermissionDS", new AdminPermissionListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                userRolePresenter = new UserRolePresenter(getDisplay().getUserRolesDisplay(), roleSearchView);
                userRolePresenter.setDataSource((ListGridDataSource) getPresenterSequenceSetupManager().getDataSource("adminRoleDS"), new String[]{"name", "description"}, new Boolean[]{false, false});
                userRolePresenter.setExpansionDataSource((ListGridDataSource) result, new String[]{"name", "description"}, new Boolean[]{false, false});
                userRolePresenter.bind();
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("adminUserPermissionDS", new AdminUserPermissionListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                permissionSearchView = new EntitySearchDialog((ListGridDataSource) result, true);
                userPermissionPresenter = new UserPermissionPresenter(getDisplay().getUserPermissionDisplay(), permissionSearchView);
                userPermissionPresenter.setDataSource((ListGridDataSource) result, new String[]{"name", "description"}, new Boolean[]{false, false});
                userPermissionPresenter.bind();
            }
        }));
    }
@Override
 public void postSetup(Canvas container) {
    
    gridHelper.addSubPresentableHandlers(display.getListDisplay().getGrid(),userRolePresenter,userPermissionPresenter );
    
    super.postSetup(container);
   }


@Override
public UserManagementDisplay getDisplay() {
    return (UserManagementDisplay) display;
}


}