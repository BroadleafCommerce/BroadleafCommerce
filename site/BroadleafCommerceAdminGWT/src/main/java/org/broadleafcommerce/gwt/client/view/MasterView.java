package org.broadleafcommerce.gwt.client.view;

import org.broadleafcommerce.gwt.client.Main;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

public class MasterView extends VLayout {
	
	private Canvas canvas;

	public MasterView() {
		ToolStrip topBar = new ToolStrip();
        topBar.setHeight(62);
        topBar.setWidth100();

        topBar.addSpacer(6);
        
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
        topBar.addMember(sgwtHomeButton);
        topBar.addSpacer(6);
        
        topBar.addFill();
        topBar.addMember(Main.NON_MODAL_PROGRESS);
        topBar.addSpacer(20);
        addMember(topBar);
        setWidth100();
        setHeight100();
        canvas = new HLayout();
        canvas.setWidth100();
        canvas.setHeight100();
        addMember(canvas);
	}
	
	public Canvas getContainer() {
		return canvas;
	}
}
