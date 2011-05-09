package org.broadleafcommerce.gwt.client;

import org.broadleafcommerce.security.domain.AdminUser;

import com.google.gwt.core.client.EntryPoint;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

public class Main implements EntryPoint {
	
	public static final boolean DEBUG = true;
	public static AdminUser USER;

	public void onModuleLoad() {
		VLayout vlayout = new VLayout();
		ToolStrip topBar = new ToolStrip();
        topBar.setHeight(62);
        topBar.setWidth100();

        topBar.addSpacer(6);
        
        ImgButton sgwtHomeButton = new ImgButton();
        sgwtHomeButton.setSrc("../resources/images/blc_logo.png");
        sgwtHomeButton.setWidth(101);
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
        topBar.addMember(sgwtHomeButton);
        topBar.addSpacer(6);
        
        topBar.addFill();
        vlayout.addMember(topBar);
        vlayout.setWidth100();
        vlayout.setHeight100();
        Canvas canvas = new Canvas();
        canvas.setWidth100();
        canvas.setHeight100();
        vlayout.addMember(canvas);
        vlayout.draw();
//		AppServices.SECURITY.getAdminUser(new AbstractCallback<AdminUser>() {
//            @Override
//            public void onSuccess(AdminUser result) {
//                USER = result;
//                if (USER == null) {
//                	SC.say("This page cannot be accessed without first successfully logging in.");
//                } else {
                	AppController.getInstance().go(canvas);
//                }
//            }
//        });
		
	}

}
