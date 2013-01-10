/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.admin.client.view.customer;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;

/**
 * 
 * @author jfischer
 *
 */
public class CustomerView extends HLayout implements Instantiable, CustomerDisplay {
    
    protected DynamicFormView dynamicFormDisplay;
    protected DynamicEntityListView listDisplay;
    protected ToolStripButton updateLoginButton;

    public CustomerView() {
        setHeight100();
        setWidth100();
    }
    
    @Override
    public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
        VLayout leftVerticalLayout = new VLayout();
        leftVerticalLayout.setID("customerLeftVerticalLayout");
        leftVerticalLayout.setHeight100();
        leftVerticalLayout.setWidth("50%");
        leftVerticalLayout.setShowResizeBar(true);
        
        listDisplay = new DynamicEntityListView(BLCMain.getMessageManager().getString("customerListTitle"), entityDataSource, false);
        leftVerticalLayout.addMember(listDisplay);
       
        dynamicFormDisplay = new DynamicFormView(BLCMain.getMessageManager().getString("customerDetailsTitle"), entityDataSource);
        dynamicFormDisplay.setWidth("50%");
        ToolStrip toolbar = dynamicFormDisplay.getToolbar();
        toolbar.addFill();

        updateLoginButton = new ToolStripButton();
        updateLoginButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Broadleaf/images/headerIcons/settings.png");
        updateLoginButton.setDisabled(true);
        updateLoginButton.setTitle(BLCMain.getMessageManager().getString("resetPasswordPrompt"));
        updateLoginButton.setOverflow(Overflow.VISIBLE);

        toolbar.addButton(updateLoginButton);
        toolbar.addSpacer(6);
        leftVerticalLayout.setParentElement(this);
        addMember(leftVerticalLayout);
        addMember(dynamicFormDisplay);
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
    public ToolStripButton getUpdateLoginButton() {
        return updateLoginButton;
    }

}
