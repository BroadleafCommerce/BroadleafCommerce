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

package org.broadleafcommerce.cms.admin.client;

import com.google.gwt.core.client.GWT;
import org.broadleafcommerce.openadmin.client.AbstractModule;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.GeneratedMessagesEntityCommon;
import org.broadleafcommerce.openadmin.client.GeneratedMessagesEntityFramework;
import org.broadleafcommerce.openadmin.client.GeneratedMessagesEntityOpenAdmin;
import org.broadleafcommerce.openadmin.client.GeneratedMessagesEntityProfile;
import org.broadleafcommerce.openadmin.client.OpenAdminMessages;
import org.broadleafcommerce.openadmin.client.i18nConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public class SandBoxModule extends AbstractModule {
	
	public void onModuleLoad() {
        addConstants(GWT.<i18nConstants>create(ContentManagementMessages.class));
        addConstants(GWT.<i18nConstants>create(SandBoxMessages.class));
        addConstants(GWT.<i18nConstants>create(GeneratedMessagesEntityCommon.class));
        addConstants(GWT.<i18nConstants>create(GeneratedMessagesEntityFramework.class));
        addConstants(GWT.<i18nConstants>create(GeneratedMessagesEntityProfile.class));
        addConstants(GWT.<i18nConstants>create(GeneratedMessagesEntityOpenAdmin.class));
        addConstants(GWT.<i18nConstants>create(OpenAdminMessages.class));

        setModuleTitle(BLCMain.getMessageManager().getString("sandBoxModuleTitle"));
        setModuleKey("BLCSandBox");

        List<String> userSandBoxPermissions = new ArrayList<String>();
        userSandBoxPermissions.add("PERMISSION_ALL_USER_SANDBOX");
        setSection(
                BLCMain.getMessageManager().getString("userSandBoxTitle"),
                "userSandBox",
                "org.broadleafcommerce.cms.admin.client.view.sandbox.MySandBoxView",
                "userSandBoxPresenter",
                "org.broadleafcommerce.cms.admin.client.presenter.sandbox.MySandBoxPresenter",
                userSandBoxPermissions
        );

        List<String> approverSandBoxPermissions = new ArrayList<String>();
        approverSandBoxPermissions.add("PERMISSION_ALL_APPROVER_SANDBOX");
        setSection(
            BLCMain.getMessageManager().getString("approverSandBoxTitle"),
            "approverSandBox",
            "org.broadleafcommerce.cms.admin.client.view.sandbox.SandBoxView",
            "approverSandBoxPresenter",
            "org.broadleafcommerce.cms.admin.client.presenter.sandbox.SandBoxPresenter",
            approverSandBoxPermissions
        );

        setOrder(150);

        registerModule();
    }
}
