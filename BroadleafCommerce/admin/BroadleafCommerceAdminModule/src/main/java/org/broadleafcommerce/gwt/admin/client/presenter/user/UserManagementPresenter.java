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
package org.broadleafcommerce.gwt.admin.client.presenter.user;

import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.admin.client.AdminModule;
import org.broadleafcommerce.gwt.admin.client.datasource.user.AdminPermissionListDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.datasource.user.AdminRoleListDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.datasource.user.AdminUserListDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.view.user.UserManagementDisplay;
import org.broadleafcommerce.gwt.admin.client.view.user.UserRoleDisplay;
import org.broadleafcommerce.gwt.client.BLCMain;
import org.broadleafcommerce.gwt.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.gwt.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.gwt.client.setup.PresenterSetupItem;
import org.broadleafcommerce.gwt.client.view.dynamic.dialog.EntitySearchDialog;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;

/**
 * 
 * @author jfischer
 *
 */
public class UserManagementPresenter extends DynamicEntityPresenter implements Instantiable {
	
	protected UserRolePresenter userRolePresenter;
	protected EntitySearchDialog roleSearchView;
	protected HashMap<String, Object> library = new HashMap<String, Object>();
	
	@Override
	protected void changeSelection(final Record selectedRecord) {
		userRolePresenter.load(selectedRecord, (AbstractDynamicDataSource) display.getListDisplay().getGrid().getDataSource(), null);
	}
	
	@Override
	protected void addClicked() {
		Map<String, Object> initialValues = new HashMap<String, Object>();
		initialValues.put("_type", new String[]{((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()});
		BLCMain.ENTITY_ADD.editNewRecord(AdminModule.ADMINMESSAGES.newAdminUserTitle(), (DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource(), initialValues, new NewItemCreatedEventHandler() {
			public void onNewItemCreated(NewItemCreatedEvent event) {
				Criteria myCriteria = new Criteria();
				myCriteria.addCriteria("login", event.getRecord().getAttribute("login"));
				display.getListDisplay().getGrid().fetchData(myCriteria);
			}
		}, "90%", null, null);
	}
	
	@Override
	public void bind() {
		super.bind();
	}

	public void setup() {
		getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("adminUserDS", new AdminUserListDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource top) {
				setupDisplayItems(top);
				((ListGridDataSource) top).setupGridFields(new String[]{"name", "login", "email"}, new Boolean[]{true, true, true});
			}
		}));
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
	}

	@Override
	public UserManagementDisplay getDisplay() {
		return (UserManagementDisplay) display;
	}
	
}
