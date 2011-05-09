package org.broadleafcommerce.gwt.client.view;

import java.util.LinkedHashMap;

import org.broadleafcommerce.gwt.client.BLCMain;
import org.broadleafcommerce.gwt.client.Module;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.menu.IMenuButton;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ItemClickEvent;
import com.smartgwt.client.widgets.menu.events.ItemClickHandler;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class MasterView extends VLayout implements ValueChangeHandler<String> {
	
	protected Canvas canvas;
	protected TabSet topTabSet;
	protected ToolStrip topBar;
	protected ToolStrip bottomBar;
	protected Label status;

	public MasterView(String moduleKey, LinkedHashMap<String, Module> modules) {
		History.addValueChangeHandler(this);
		
		topBar = new ToolStrip();
        topBar.setHeight(62);
        topBar.setWidth100();
        topBar.addSpacer(6);
        topBar.addSpacer(6);
        
        VStack temp = new VStack();
        temp.setAlign(VerticalAlignment.BOTTOM);
        temp.setWidth100();
        temp.setHeight100();
        
        HStack moduleStack = new HStack(10);
        moduleStack.setWidth100();
        moduleStack.setHeight(20);
        moduleStack.setAlign(Alignment.CENTER);
        moduleStack.setLayoutBottomMargin(10);
        temp.addMember(moduleStack);
        if (modules.size() > 1) {
	        Menu modulesMenu = new Menu();  
	        modulesMenu.setCanSelectParentItems(true); 
	        MenuItem[] menuItems = new MenuItem[modules.size()];
	        int j = 0;
	        for (Module module : modules.values()) {
	        	MenuItem tempMenuItem = new MenuItem(module.getModuleTitle());
	        	tempMenuItem.setAttribute("key", module.getModuleKey());
	        	menuItems[j] = tempMenuItem;
	        	j++;
	        }
	        modulesMenu.setData(menuItems);  
	        
	        modulesMenu.addItemClickHandler(new ItemClickHandler() {  
	            public void onItemClick(final ItemClickEvent event) {  
	            	BLCMain.MODAL_PROGRESS.startProgress(new Timer() {
						@Override
						public void run() {
							UrlBuilder builder = com.google.gwt.user.client.Window.Location.createUrlBuilder();
			            	builder.setParameter("defaultModule", event.getItem().getAttribute("key"));
			            	String url = builder.buildString();
			            	//remove any history tokens
			            	if (url.indexOf("#") >= 0) {
			            		url = url.substring(0, url.indexOf("#"));
			            	}
			            	com.google.gwt.user.client.Window.Location.assign(url);
						}
	            	});
	            }  
	        });
	        
	        IMenuButton moduleSelectionButton = new IMenuButton("", modulesMenu);
	        moduleSelectionButton.setWidth(22); 
	        moduleStack.addMember(moduleSelectionButton);
        }
        Label moduleTitle = new Label(modules.get(moduleKey).getModuleTitle());
        moduleTitle.setWrap(false);
        moduleTitle.setStyleName("label-bold");
        moduleStack.addMember(moduleTitle);
        
        topTabSet = new TabSet();  
        topTabSet.setTabBarPosition(Side.TOP);  
        topTabSet.setHeight(23);
        LinkedHashMap<String, String[]> pages = modules.get(moduleKey).getPages();
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
        addMember(topBar);
        setWidth100();
        setHeight100();
        canvas = new HLayout();
        canvas.setWidth100();
        canvas.setHeight100();
        
        addMember(canvas);
        
        bottomBar = new ToolStrip();
        bottomBar.setHeight(30);
        bottomBar.setWidth100();
        status = new Label();
        status.setWrap(false);
        bottomBar.addSpacer(6);
        
        ToolStripButton developerButton = new ToolStripButton();
        developerButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/settings.png");  
        developerButton.setShowTitle(false);
        developerButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				SC.showConsole();
			}
        });
        bottomBar.addMember(developerButton);
        
        bottomBar.addSpacer(6);
        
        bottomBar.addMember(status);
        bottomBar.addFill();
        bottomBar.addMember(BLCMain.NON_MODAL_PROGRESS);
        bottomBar.addSpacer(5);
        
        addMember(bottomBar);
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

	public ToolStrip getTopBar() {
		return topBar;
	}

	public ToolStrip getBottomBar() {
		return bottomBar;
	}

	public Label getStatus() {
		return status;
	}
	
	public void clearStatus() {
		status.setContents("");
	}
	
}
