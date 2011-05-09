package org.broadleafcommerce.gwt.client.view.dynamic.form;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class DynamicFormView extends VLayout implements DynamicFormDisplay {

	protected ToolStrip toolbar;
	protected ToolStripButton saveButton;
	protected ToolStripButton refreshButton;
	protected FormOnlyView formOnlyView;
	
	public DynamicFormView(String title, DataSource dataSource) {
		super();
		
		//setHeight100();
        setWidth100();
        setLayoutMargin(0);
        toolbar = new ToolStrip();
        toolbar.setHeight(20);
        toolbar.setWidth100();
        toolbar.addSpacer(6);
        saveButton = new ToolStripButton();  
        saveButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/save.png");   
        toolbar.addButton(saveButton);
        saveButton.setDisabled(true);
        refreshButton = new ToolStripButton();  
        refreshButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/refresh.png");   
        toolbar.addButton(refreshButton);
        toolbar.addSpacer(6);
        Label productDetailsLabel = new Label();
        productDetailsLabel.setContents(title);
        productDetailsLabel.setWrap(false);
        toolbar.addMember(productDetailsLabel);
        addMember(toolbar);
        
        formOnlyView = new FormOnlyView(dataSource);
        addMember(formOnlyView);
        
        setOverflow(Overflow.HIDDEN);
	}

	public ToolStrip getToolbar() {
		return toolbar;
	}

	public ToolStripButton getSaveButton() {
		return saveButton;
	}

	public ToolStripButton getRefreshButton() {
		return refreshButton;
	}

	public FormOnlyDisplay getFormOnlyDisplay() {
		return formOnlyView;
	}

}
