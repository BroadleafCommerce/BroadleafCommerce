package org.broadleafcommerce.gwt.client;

import org.broadleafcommerce.gwt.client.view.DynamicEntityView;
import org.broadleafcommerce.gwt.client.view.ProgressWindow;
import org.broadleafcommerce.gwt.client.view.SimpleProgress;
import org.broadleafcommerce.security.domain.AdminUser;

import com.google.gwt.core.client.EntryPoint;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

public class Main implements EntryPoint {
	
	public static ProgressWindow MODAL_PROGRESS = new ProgressWindow();
	public static SimpleProgress NON_MODAL_PROGRESS = new SimpleProgress(16, 150);
	public static DynamicEntityView ENTITY_ADD = new DynamicEntityView();
	
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
        topBar.addMember(sgwtHomeButton);
        topBar.addSpacer(6);
        
        topBar.addFill();
        topBar.addMember(NON_MODAL_PROGRESS);
        topBar.addSpacer(20);
        vlayout.addMember(topBar);
        vlayout.setWidth100();
        vlayout.setHeight100();
        HLayout canvas = new HLayout();
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
		vlayout.reflow();
	}

}
