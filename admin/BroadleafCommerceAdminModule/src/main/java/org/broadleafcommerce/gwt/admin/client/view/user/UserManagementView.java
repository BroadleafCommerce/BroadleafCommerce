package org.broadleafcommerce.gwt.admin.client.view.user;

import org.broadleafcommerce.gwt.admin.client.AdminModule;
import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.client.view.TabSet;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormView;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

public class UserManagementView extends HLayout implements Instantiable, UserManagementDisplay {
	
	protected DynamicFormView dynamicFormDisplay;
	protected DynamicEntityListView listDisplay;
	protected UserRoleView userRolesDisplay;
    
	public UserManagementView() {
		setHeight100();
		setWidth100();
	}
	
	public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
		VLayout leftVerticalLayout = new VLayout();
		leftVerticalLayout.setHeight100();
		leftVerticalLayout.setWidth("50%");
		leftVerticalLayout.setShowResizeBar(true);
        
		listDisplay = new DynamicEntityListView(AdminModule.ADMINMESSAGES.userListTitle(), entityDataSource, false, false);
        leftVerticalLayout.addMember(listDisplay);

        TabSet topTabSet = new TabSet();  
        topTabSet.setTabBarPosition(Side.TOP);  
        topTabSet.setPaneContainerOverflow(Overflow.HIDDEN);
        topTabSet.setWidth("50%");  
        topTabSet.setHeight100();
        topTabSet.setPaneMargin(0);
        
        Tab detailsTab = new Tab(AdminModule.ADMINMESSAGES.userDetailsTitle());
        dynamicFormDisplay = new DynamicFormView(AdminModule.ADMINMESSAGES.userDetailsTitle(), entityDataSource);
        
        detailsTab.setPane(dynamicFormDisplay);
        topTabSet.addTab(detailsTab);
        
        Tab userRolesTab = new Tab(AdminModule.ADMINMESSAGES.userRolesTitle()); 
        userRolesDisplay = new UserRoleView(AdminModule.ADMINMESSAGES.userRolesTitle(), false, false);
        userRolesTab.setPane(userRolesDisplay);
        topTabSet.addTab(userRolesTab);
        
        addMember(leftVerticalLayout);
        addMember(topTabSet);
	}

	public Canvas asCanvas() {
		return this;
	}

	public DynamicFormDisplay getDynamicFormDisplay() {
		return dynamicFormDisplay;
	}
	
	public DynamicEntityListDisplay getListDisplay() {
		return listDisplay;
	}

	public UserRoleView getUserRolesDisplay() {
		return userRolesDisplay;
	}
	
}
