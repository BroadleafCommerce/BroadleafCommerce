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
package org.broadleafcommerce.openadmin.client.view.dynamic.form;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * 
 * @author jfischer
 *
 */
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
