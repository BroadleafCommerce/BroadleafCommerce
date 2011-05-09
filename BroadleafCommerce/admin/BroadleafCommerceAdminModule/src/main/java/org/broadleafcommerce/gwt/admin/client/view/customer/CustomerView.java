package org.broadleafcommerce.gwt.admin.client.view.customer;

import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormView;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class CustomerView extends HLayout implements Instantiable, CustomerDisplay {
	
	protected DynamicFormView dynamicFormDisplay;
	protected DynamicEntityListView listDisplay;
	protected ToolStripButton updateLoginButton;
    
	public CustomerView() {
		setHeight100();
		setWidth100();
	}
	
	public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
		VLayout leftVerticalLayout = new VLayout();
		leftVerticalLayout.setHeight100();
		leftVerticalLayout.setWidth("50%");
		leftVerticalLayout.setShowResizeBar(true);
        
		listDisplay = new DynamicEntityListView("Customers", entityDataSource, false, false);
        leftVerticalLayout.addMember(listDisplay);
        
        dynamicFormDisplay = new DynamicFormView("Customer Details", entityDataSource);
        dynamicFormDisplay.setWidth("50%");
        ToolStrip toolbar = dynamicFormDisplay.getToolbar();
        toolbar.addFill();
        Label label = new Label();
        label.setContents("Update Password");
        label.setWrap(false);
        toolbar.addMember(label);
        
        updateLoginButton = new ToolStripButton();  
        updateLoginButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/settings.png");   
        updateLoginButton.setDisabled(true);
        toolbar.addButton(updateLoginButton);
        toolbar.addSpacer(6);
        
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

	public ToolStripButton getUpdateLoginButton() {
		return updateLoginButton;
	}
	
}
