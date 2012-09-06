/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.cms.admin.client.modules;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;
import org.broadleafcommerce.cms.admin.client.ContentManagementMessages;
import org.broadleafcommerce.cms.admin.client.GeneratedMessagesEntityCMS;
import org.broadleafcommerce.openadmin.client.AbstractHtmlEditingModule;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.GeneratedMessagesEntityCommon;
import org.broadleafcommerce.openadmin.client.GeneratedMessagesEntityOpenAdmin;

import java.util.ArrayList;
import java.util.List;

public class RedirectUrlModule extends AbstractHtmlEditingModule {

    public void onModuleLoad() {
        addConstants(GWT.<ConstantsWithLookup>create(ContentManagementMessages.class));
        addConstants(GWT.<ConstantsWithLookup>create(GeneratedMessagesEntityCMS.class));
        addConstants(GWT.<ConstantsWithLookup>create(GeneratedMessagesEntityCommon.class));
        addConstants(GWT.<ConstantsWithLookup>create(GeneratedMessagesEntityOpenAdmin.class));

        setModuleTitle(BLCMain.getMessageManager().getString("cmsModuleTitle"));
        setModuleKey("BLCRedirectUrlModule");

        List<String> redirectUrlPermissions = new ArrayList<String>();
        redirectUrlPermissions.add("PERMISSION_CREATE_URLHANDLER");
        redirectUrlPermissions.add("PERMISSION_UPDATE_URLHANDLER");
        redirectUrlPermissions.add("PERMISSION_DELETE_URLHANDLER");
        redirectUrlPermissions.add("PERMISSION_READ_URLHANDLER");
        setSection(
                BLCMain.getMessageManager().getString("redirectUrlPermissions"),
                "urlRedirect",
                "org.broadleafcommerce.cms.admin.client.view.urlRedirect.UrlRedirectView",
                "urlRedirectPresenter",
                "org.broadleafcommerce.cms.admin.client.presenter.urlRedirect.UrlRedirectPresenter",
                redirectUrlPermissions
        );

        setOrder(100);

        registerModule();
    }
}
