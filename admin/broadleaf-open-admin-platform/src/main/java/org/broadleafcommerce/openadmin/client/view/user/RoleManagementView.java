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

package org.broadleafcommerce.openadmin.client.view.user;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormOnlyView;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureView;

/**
 * 
 * @author bpolster
 *
 */
public class RoleManagementView extends HLayout implements Instantiable, RoleManagementDisplay {

	protected DynamicFormView dynamicFormDisplay;
	protected DynamicEntityListView listDisplay;
    protected GridStructureView permissionsDisplay;

	public RoleManagementView() {
		setHeight100();
		setWidth100();
	}
	
	public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
		VLayout leftVerticalLayout = new VLayout();
		leftVerticalLayout.setID("roleLeftVerticalLayout");
		leftVerticalLayout.setHeight100();
		leftVerticalLayout.setWidth("50%");
		leftVerticalLayout.setShowResizeBar(true);
        
		listDisplay = new DynamicEntityListView(BLCMain.getMessageManager().getString("roleListTitle"), entityDataSource, false);
        leftVerticalLayout.addMember(listDisplay);

        dynamicFormDisplay = new DynamicFormView(BLCMain.getMessageManager().getString("roleDetailsTitle"), entityDataSource);
        permissionsDisplay = new GridStructureView(BLCMain.getMessageManager().getString("permissionListTitle"), false, false);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(permissionsDisplay);
        
        addMember(leftVerticalLayout);
        addMember(dynamicFormDisplay);
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

    public GridStructureView getPermissionsDisplay() {
        return permissionsDisplay;
    }
}
