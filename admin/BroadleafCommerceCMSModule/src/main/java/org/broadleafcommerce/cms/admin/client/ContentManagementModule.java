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
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
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
		cmsRoles.add("ROLE_CONTENT_EDITOR");

        List<String> approverRoles = new ArrayList<String>();
		approverRoles.add("ROLE_ADMIN");
		approverRoles.add("ROLE_CONTENT_APPROVER");

        setSection(
            BLCMain.getMessageManager().getString("pagesTitle"),
			"pages",
			"org.broadleafcommerce.cms.admin.client.view.pages.PagesView",
			"pagesPresenter",
			"org.broadleafcommerce.cms.admin.client.presenter.pages.PagesPresenter",
			cmsRoles,
			null
		);

        setSection(
            BLCMain.getMessageManager().getString("staticAssetsTitle"),
			"staticAssets",
			"org.broadleafcommerce.cms.admin.client.view.file.StaticAssetsView",
			"staticAssetsPresenter",
			"org.broadleafcommerce.cms.admin.client.presenter.file.StaticAssetsPresenter",
			cmsRoles,
			null
		);

        setSection(
            BLCMain.getMessageManager().getString("structuredContentTitle"),
			"structuredContent",
			"org.broadleafcommerce.cms.admin.client.view.structure.StructuredContentView",
			"structuredContentPresenter",
			"org.broadleafcommerce.cms.admin.client.presenter.structure.StructuredContentPresenter",
			cmsRoles,
			null
		);

   /*     setSection(
            BLCMain.getMessageManager().getString("userSandboxTitle"),
			"userSandBox",
			"org.broadleafcommerce.cms.admin.client.view.sandbox.UserSandBoxView",
			"userSandBoxPresenter",
			"org.broadleafcommerce.cms.admin.client.presenter.sandbox.UserSandBoxPresenter",
			cmsRoles,
			null
		);



        setSection(
            BLCMain.getMessageManager().getString("approvalSandboxTitle"),
			"approvalSandBox",
			"org.broadleafcommerce.cms.admin.client.view.sandbox.ApprovalSandBoxView",
			"userSandBoxPresenter",
			"org.broadleafcommerce.cms.admin.client.presenter.sandbox.ApprovalSandBoxPresenter",
			approverRoles,
			null
		);

		*/

        setHtmlEditorIFramePath("/broadleafdemo/richTextFullFeatured.html");
        setBasicHtmlEditorIFramePath("/broadleafdemo/richTextBasic.html");
        registerModule();
    }

    @Override
	public void postDraw() {
		ImgButton sgwtHomeButton = new ImgButton();
        sgwtHomeButton.setSrc(GWT.getModuleBaseURL() + "admin/images/blc_logo.png");
        sgwtHomeButton.setWidth(98);
        sgwtHomeButton.setHeight(50);
        sgwtHomeButton.setPrompt(BLCMain.getMessageManager().getString("blcProjectPage"));
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
