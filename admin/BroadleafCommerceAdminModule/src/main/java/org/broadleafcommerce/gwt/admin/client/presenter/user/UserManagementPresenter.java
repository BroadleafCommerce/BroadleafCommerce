package org.broadleafcommerce.gwt.admin.client.presenter.user;

import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.admin.client.AdminModule;
import org.broadleafcommerce.gwt.admin.client.datasource.user.AdminPermissionListDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.datasource.user.AdminRoleListDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.datasource.user.AdminUserListDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.view.customer.PasswordUpdateDialog;
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

public class UserManagementPresenter extends DynamicEntityPresenter implements Instantiable {
	
	protected PasswordUpdateDialog passwordUpdateDialog = new PasswordUpdateDialog();
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
		getPresenterSetupManager().addOrReplaceItem(new PresenterSetupItem("adminUserDS", new AdminUserListDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource top) {
				setupDisplayItems(top);
				((ListGridDataSource) top).setupGridFields(new String[]{"name", "login", "email"}, new Boolean[]{true, true, true});
			}
		}));
		getPresenterSetupManager().addOrReplaceItem(new PresenterSetupItem("adminRoleDS", new AdminRoleListDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource result) {
				roleSearchView = new EntitySearchDialog((ListGridDataSource) result);
				library.put("adminRoleDS", result);
			}
		}));
		getPresenterSetupManager().addOrReplaceItem(new PresenterSetupItem("adminPermissionDS", new AdminPermissionListDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
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
