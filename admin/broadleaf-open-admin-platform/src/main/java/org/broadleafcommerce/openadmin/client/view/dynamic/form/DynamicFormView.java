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
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.broadleafcommerce.openadmin.client.BLCMain;

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

    public DynamicFormView(DataSource dataSource) {
        this("", dataSource);
    }
    
    public DynamicFormView(String title, DataSource dataSource) {
        super();
        
        setLayoutMargin(0);
        toolbar = new ToolStrip();
        toolbar.setHeight(30);
        toolbar.setWidth100();
        toolbar.addSpacer(6);
        saveButton = new ToolStripButton();  
        saveButton.setIcon(GWT.getModuleBaseURL()+"admin/images/button/save.png");
        saveButton.setTitle(BLCMain.getMessageManager().getString("saveTitle"));
        toolbar.addButton(saveButton);
        saveButton.setDisabled(true);
        toolbar.addSpacer(3);
        refreshButton = new ToolStripButton();  
        refreshButton.setIcon(GWT.getModuleBaseURL()+"admin/images/button/refresh.png");
        refreshButton.setTitle(BLCMain.getMessageManager().getString("restoreTitle"));
        refreshButton.setTooltip(BLCMain.getMessageManager().getString("restoreTooltip"));
        refreshButton.setDisabled(true);
        toolbar.addButton(refreshButton);
        toolbar.addSpacer(6);

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
