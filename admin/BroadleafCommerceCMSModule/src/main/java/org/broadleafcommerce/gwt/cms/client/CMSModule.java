package org.broadleafcommerce.gwt.cms.client;

import org.broadleafcommerce.gwt.client.AbstractModule;
import org.broadleafcommerce.gwt.client.BLCMain;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;

public class CMSModule extends AbstractModule {
	
	public void onModuleLoad() {
		setModuleTitle("Broadleaf Commerce Content Management System");
		setModuleKey("BLCCMS");
		setSection(
			"CMS",
			"cms",
			"org.broadleafcommerce.gwt.cms.client.view.cms.CmsView",
			"cmsPresenter",
			"org.broadleafcommerce.gwt.cms.client.presenter.cms.CmsPresenter"
		);
		registerModule();
	}

	@Override
	public void postDraw() {
		ImgButton sgwtHomeButton = new ImgButton();
        sgwtHomeButton.setSrc(GWT.getModuleBaseURL() + "admin/images/blc_logo.png");
        sgwtHomeButton.setWidth(98);
        sgwtHomeButton.setHeight(50);
        sgwtHomeButton.setPrompt("Broadleaf Commerce Project Page");
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
