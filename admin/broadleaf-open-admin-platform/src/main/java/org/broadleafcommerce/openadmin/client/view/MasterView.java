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

package org.broadleafcommerce.openadmin.client.view;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import org.broadleafcommerce.openadmin.client.BLCLaunch;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.Module;
import org.broadleafcommerce.openadmin.client.callback.ItemEdited;
import org.broadleafcommerce.openadmin.client.callback.ItemEditedHandler;
import org.broadleafcommerce.openadmin.client.datasource.CeilingEntities;
import org.broadleafcommerce.openadmin.client.datasource.EntityImplementations;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.security.AdminUser;
import org.broadleafcommerce.openadmin.client.security.SecurityManager;
import org.broadleafcommerce.openadmin.client.setup.AppController;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntityEditDialog;

import java.util.LinkedHashMap;


/**
 * 
 * @author jfischer
 *
 */
public class MasterView extends VLayout implements ValueChangeHandler<String> {
	
	protected Canvas canvas;
    protected LinkedHashMap<String, Module> modules;

    public MasterView(LinkedHashMap<String, Module> modules) {

        this.modules = modules;

        setHeight("90%");
        setWidth("81.15%");
        setZIndex(1);

        canvas = new HLayout();
        addMember(canvas);

        bind();
    }

    private void bind() {
        History.addValueChangeHandler(this);
    }

    public void onValueChange(ValueChangeEvent<String> event) {
        String token = event.getValue();
        if (token != null) {
            String page = BLCLaunch.getSelectedPage(token);
            String moduleName = BLCLaunch.getSelectedModule(token);

            LinkedHashMap<String, String[]> pages = modules.get(moduleName).getPages();
            if (SecurityManager.getInstance().isUserAuthorizedToViewModule(moduleName) &&
                    SecurityManager.getInstance().isUserAuthorizedToViewSection(pages.get(page)[0])) {

                if (moduleName != null && ! moduleName.equals(BLCMain.currentModuleKey)) {
                    BLCMain.setCurrentModuleKey(moduleName);
                    AppController.getInstance().clearCurrentView();
                } else {
                    AppController.getInstance().clearCurrentView();
                }
            }
        }
    }
    
    private void buildHistoryNewItem(String pageKey, String moduleKey, String itemId) {
        String destinationPage = "moduleKey=" + moduleKey +"&pageKey="+pageKey;

        if (itemId != null) {
            destinationPage = destinationPage + "&itemId="+itemId;
        }
        History.newItem(destinationPage);
    }

	public Canvas getContainer() {
		return canvas;
	}

    public static void editUserInfoDialog() {
        final DynamicEntityDataSource userDS = new DynamicEntityDataSource(CeilingEntities.ADMIN_USER);
        userDS.buildFields(null, false, new AsyncCallbackAdapter() {
            public void onSetupSuccess(DataSource ds) {
                AdminUser currentUser = SecurityManager.USER;
                Record userRecord = new Record();
                userRecord.setAttribute("id", currentUser.getId());
                userRecord.setAttribute("name", currentUser.getName());
                userRecord.setAttribute("email", currentUser.getEmail());
                userRecord.setAttribute("phoneNumber", currentUser.getPhoneNumber());
                userRecord.setAttribute("login", currentUser.getUserName());
                userRecord.setAttribute("_type", new String[]{EntityImplementations.ADMIN_USER});

                EntityEditDialog ed = new EntityEditDialog();

                ed.editRecord("Edit User Information", userDS, userRecord, new ItemEditedHandler() {
                    public void onItemEdited(ItemEdited event) {
                        SecurityManager.USER.setPhoneNumber(event.getRecord().getAttribute("phoneNumber"));
                        SecurityManager.USER.setName(event.getRecord().getAttribute("name"));
                        SecurityManager.USER.setEmail(event.getRecord().getAttribute("email"));

                    }
                }, null, new String[]{"login", "activeStatusFlag", "password"}, false);
            }
        });
    }

    protected static native void exportEditUserInfo()/*-{
        $wnd.blShowEditUserInfo = $entry(@org.broadleafcommerce.openadmin.client.view.MasterView::editUserInfoDialog());
    }-*/;

    protected static native void redirect(String url)/*-{ 
        $wnd.location = url;
    }-*/; 

}
