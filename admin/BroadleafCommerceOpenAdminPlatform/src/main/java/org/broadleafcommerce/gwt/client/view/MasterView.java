package org.broadleafcommerce.gwt.client.view;

import java.util.HashMap;

import org.broadleafcommerce.gwt.client.BLCMain;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

public class MasterView extends VLayout implements ValueChangeHandler<String> {
	
	protected Canvas canvas;
	protected TabSet topTabSet;

	public MasterView(HashMap<String, String[]> pages) {
		History.addValueChangeHandler(this);
		
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
        
        VStack temp = new VStack();
        temp.setAlign(VerticalAlignment.BOTTOM);
        temp.setWidth100();
        temp.setHeight100();
        
        topTabSet = new TabSet();  
        topTabSet.setTabBarPosition(Side.TOP);  
        topTabSet.setHeight(23);
        for (String page : pages.keySet()) {
	        Tab tab = new Tab(page); 
	        tab.setAttribute("token", page);
	        tab.setID(page);
	        topTabSet.setShowPaneContainerEdges(false);
	        topTabSet.addTab(tab);
        }
        topTabSet.addTabSelectedHandler(new TabSelectedHandler() {
			public void onTabSelected(TabSelectedEvent event) {
				if (event.isLeftButtonDown()) {
					History.newItem(event.getTab().getAttribute("token"));
				}
			}
        });
        
        temp.addMember(topTabSet);
        topBar.addMember(temp);
        
        topBar.addFill();
        topBar.addMember(BLCMain.NON_MODAL_PROGRESS);
        topBar.addSpacer(20);
        addMember(topBar);
        setWidth100();
        setHeight100();
        canvas = new HLayout();
        canvas.setWidth100();
        canvas.setHeight100();
        addMember(canvas);
	}
	
	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();
		if (token != null) {
			topTabSet.selectTab(token);
		}
	}

	public Canvas getContainer() {
		return canvas;
	}
}
