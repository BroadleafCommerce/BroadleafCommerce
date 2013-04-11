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

package org.broadleafcommerce.openadmin.client.view.user;

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.TabSet;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

/**
 * 
 * @author jfischer
 *
 */
public class UserManagementView extends HLayout implements Instantiable, UserManagementDisplay {
    
    protected DynamicFormView dynamicFormDisplay;
    protected DynamicEntityListView listDisplay;
    protected UserRoleView userRolesDisplay;
    protected UserPermissionView userPermissionDisplay;
    
    public UserManagementView() {
        setHeight100();
        setWidth100();
    }
    
    @Override
    public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
        VLayout leftVerticalLayout = new VLayout();
        leftVerticalLayout.setID("userLeftVerticalLayout");
        leftVerticalLayout.setHeight100();
        leftVerticalLayout.setWidth("50%");
        leftVerticalLayout.setShowResizeBar(true);
        
        listDisplay = new DynamicEntityListView(BLCMain.getMessageManager().getString("userListTitle"), entityDataSource, false);
        leftVerticalLayout.addMember(listDisplay);

        TabSet topTabSet = new TabSet();  
        topTabSet.setID("userTopTabSet");
        topTabSet.setTabBarPosition(Side.TOP);  
        topTabSet.setPaneContainerOverflow(Overflow.HIDDEN);
        topTabSet.setWidth("50%");  
        topTabSet.setHeight100();
        topTabSet.setPaneMargin(0);
        
        Tab detailsTab = new Tab(BLCMain.getMessageManager().getString("userDetailsTitle"));
        detailsTab.setID("userDetailsTab");
        dynamicFormDisplay = new DynamicFormView(BLCMain.getMessageManager().getString("userDetailsTitle"), entityDataSource);
        
        detailsTab.setPane(dynamicFormDisplay);
        topTabSet.addTab(detailsTab);
        
        Tab userRolesTab = new Tab(BLCMain.getMessageManager().getString("userRolesTitle"));
        userRolesTab.setID("userUserRolesTab");
        userRolesDisplay = new UserRoleView(false, false);
        userRolesTab.setPane(userRolesDisplay);
        topTabSet.addTab(userRolesTab);

        Tab userPermissionsTab = new Tab(BLCMain.getMessageManager().getString("userPermissionsTitle"));
        userPermissionsTab.setID("userUserPermissionsTab");
        userPermissionDisplay = new UserPermissionView(false, false);
        userPermissionsTab.setPane(userPermissionDisplay);
        topTabSet.addTab(userPermissionsTab);
        leftVerticalLayout.setParentElement(this);
        addMember(leftVerticalLayout);
        addMember(topTabSet);
    }

    @Override
    public Canvas asCanvas() {
        return this;
    }

    @Override
    public DynamicFormDisplay getDynamicFormDisplay() {
        return dynamicFormDisplay;
    }
    
    @Override
    public DynamicEntityListDisplay getListDisplay() {
        return listDisplay;
    }

    @Override
    public UserRoleView getUserRolesDisplay() {
        return userRolesDisplay;
    }

    @Override
    public UserPermissionView getUserPermissionDisplay() {
        return userPermissionDisplay;
    }

}
