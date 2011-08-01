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
package org.broadleafcommerce.openadmin.client;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import org.broadleafcommerce.openadmin.client.validation.ValidationFactoryManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public class UserManagementModule extends AbstractModule {
	
	public static final OpenAdminMessages OPENADMINMESSAGES = GWT.create(OpenAdminMessages.class);
	
	public void onModuleLoad() {
		ValidationFactoryManager.getInstance().getConstants().add(UserManagementModule.OPENADMINMESSAGES);
		
		setModuleTitle(UserManagementModule.OPENADMINMESSAGES.adminModuleTitle());
		setModuleKey("BLCOpenAdmin");

		List<String> userManagementRoles = new ArrayList<String>();
		userManagementRoles.add("ROLE_ADMIN");
		setSection(
			UserManagementModule.OPENADMINMESSAGES.userManagementMainTitle(),
			"user",
			"org.broadleafcommerce.openadmin.client.view.user.UserManagementView",
			"userPresenter",
			"org.broadleafcommerce.openadmin.client.presenter.user.UserManagementPresenter",
			userManagementRoles,
			null
		);
		

		/*
                setSection(
			UserManagementModule.OPENADMINMESSAGES.roleManagementMainTitle(),
			"role",
			"org.broadleafcommerce.gwt.admin.client.view.user.RoleManagementView",
			"userPresenter",
			"org.broadleafcommerce.gwt.admin.client.presenter.user.RoleManagementPresenter",
			userManagementRoles,
			null
		);
        */
		
		registerModule();
	}

	@Override
	public void postDraw() {
		ImgButton sgwtHomeButton = new ImgButton();
        sgwtHomeButton.setSrc(GWT.getModuleBaseURL() + "admin/images/blc_logo.png");
        sgwtHomeButton.setWidth(98);
        sgwtHomeButton.setHeight(50);
        sgwtHomeButton.setPrompt(OPENADMINMESSAGES.blcProjectPage());
        sgwtHomeButton.setHoverStyle("interactImageHover");
        sgwtHomeButton.setShowRollOver(false);
        sgwtHomeButton.setShowDownIcon(false);
        sgwtHomeButton.setShowDown(false);
        sgwtHomeButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
            public void onClick(ClickEvent event) {
                com.google.gwt.user.client.Window.open("http://www.broadleafcommerce.org", "sgwt", null);
            }
        });
        BLCMain.MASTERVIEW.getTopBar().addMember(sgwtHomeButton, 1);
	}

}