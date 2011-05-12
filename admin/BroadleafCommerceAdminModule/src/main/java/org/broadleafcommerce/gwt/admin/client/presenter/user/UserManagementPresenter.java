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
import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.AsyncCallbackAdapter;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.gwt.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.client.view.dynamic.dialog.EntitySearchDialog;

import com.google.gwt.user.client.Timer;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;

public class UserManagementPresenter extends DynamicEntityPresenter implements Instantiable {
	
	protected PasswordUpdateDialog passwordUpdateDialog = new PasswordUpdateDialog();
	protected UserRolePresenter userRolePresenter;
	protected EntitySearchDialog roleSearchView;
	
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

	@Override
	public void go(final Canvas container) {
		BLCMain.MODAL_PROGRESS.startProgress(new Timer() {
			public void run() {
				if (loaded) {
					UserManagementPresenter.super.go(container);
					return;
				}
				
				AdminUserListDataSourceFactory.createDataSource("adminUserDS", new AsyncCallbackAdapter() {
					public void onSuccess(final DataSource top) {
						setupDisplayItems(top);
						((ListGridDataSource) top).setupGridFields(new String[]{"name", "login", "email"}, new Boolean[]{true, true, true});
						
						AdminRoleListDataSourceFactory.createDataSource("adminRoleDS", new AsyncCallbackAdapter() {
							public void onSuccess(final DataSource adminRoleDS) {
								roleSearchView = new EntitySearchDialog((ListGridDataSource) adminRoleDS);
								
								AdminPermissionListDataSourceFactory.createDataSource("adminPermissionDS", new AsyncCallbackAdapter() {
									public void onSuccess(DataSource adminPermissionDS) {
										userRolePresenter = new UserRolePresenter((UserRoleDisplay) getDisplay().getUserRolesDisplay(), roleSearchView);
										userRolePresenter.setDataSource((ListGridDataSource) adminRoleDS, new String[]{"name", "description"}, new Boolean[]{false, false});
										userRolePresenter.setExpansionDataSource((ListGridDataSource) adminPermissionDS , new String[]{"name", "description"}, new Boolean[]{false, false});
										userRolePresenter.setReadOnly(false);
										userRolePresenter.bind();
										UserManagementPresenter.super.go(container);	
									}
								});
							}
						});
						
					}
				});
			}
		});
	}

	@Override
	public UserManagementDisplay getDisplay() {
		return (UserManagementDisplay) display;
	}
	
}
