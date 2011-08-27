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
package org.broadleafcommerce.cms.admin.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;
import org.broadleafcommerce.openadmin.client.AbstractModule;
import org.broadleafcommerce.openadmin.client.BLCMain;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public class ContentManagementModule extends AbstractModule {

	public void onModuleLoad() {
        addConstants(GWT.<ConstantsWithLookup>create(ContentManagementMessages.class));
		
		setModuleTitle(BLCMain.getMessageManager().getString("cmsModuleTitle"));
		setModuleKey("BLCContentManagement");
		
		List<String> cmsRoles = new ArrayList<String>();
		cmsRoles.add("ROLE_ADMIN");
		cmsRoles.add("ROLE_CONTENT_MANAGER");

        setSection(
            BLCMain.getMessageManager().getString("pagesTitle"),
			"pages",
			"org.broadleafcommerce.cms.admin.client.view.pages.PagesView",
			"pagesPresenter",
			"org.broadleafcommerce.cms.admin.client.presenter.pages.PagesPresenter",
			cmsRoles,
			null
		);

        registerModule();
    }

}
