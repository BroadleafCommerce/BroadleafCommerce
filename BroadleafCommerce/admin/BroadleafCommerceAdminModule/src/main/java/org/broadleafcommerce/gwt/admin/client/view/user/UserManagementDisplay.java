package org.broadleafcommerce.gwt.admin.client.view.user;

import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEditDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;

import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public interface UserManagementDisplay extends DynamicEditDisplay {

	public DynamicFormDisplay getDynamicFormDisplay();

	public DynamicEntityListDisplay getListDisplay();
	
	public ToolStripButton getUpdateLoginButton();
	
	public UserRoleDisplay getUserRolesDisplay();
	
}