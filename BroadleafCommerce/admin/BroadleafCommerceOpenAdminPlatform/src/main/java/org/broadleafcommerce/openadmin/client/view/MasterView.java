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
package org.broadleafcommerce.openadmin.client.view;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.History;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.IMenuButton;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.Module;
import org.broadleafcommerce.openadmin.client.security.SecurityManager;
import org.broadleafcommerce.openadmin.client.setup.AppController;


/**
 * 
 * @author jfischer
 *
 */
public class MasterView extends VLayout {
	
	protected Canvas canvas;
	protected ToolStrip bottomBar;
	protected Label status;

    protected Label selectedPrimaryMenuOption;
    protected Label selectedSecondaryMenuOption;

    protected HLayout secondaryMenu = new HLayout();

    protected String moduleKey;
    protected String selectedPage;
    protected LinkedHashMap<String, Module> modules;


    public MasterView(String moduleKey, LinkedHashMap<String, Module> modules) {
        this.moduleKey = moduleKey;
        this.modules = modules;

        setWidth100();
        setHeight100();

        addMember(buildHeader());
        addMember(buildPrimaryMenu());
        addMember(buildSecondaryMenu(null));


        canvas = new HLayout();
        canvas.setWidth100();
        canvas.setHeight100();

        addMember(canvas);

        buildFooter();
    }



    private Layout buildHeader() {
        HLayout header = new HLayout();
        header.setWidth100();
        header.setLayoutMargin(10);
        header.setBackgroundImage(GWT.getModuleBaseURL() + "admin/images/header_bg.png");

        header.addMember(buildLogo());
        header.addMember(new LayoutSpacer());

        VLayout userAndLocale = new VLayout();
        userAndLocale.setHeight100();
        userAndLocale.addMember(buildUserInfo());
        //userAndLocale.addMember(buildLocaleSelection());

        header.addMember(userAndLocale);
        return header;
    }

    private void addAuthorizedModulesToMenu(Layout menuHolder) {
        Collection<Module> allowedModules = modules.values();
        for (Iterator<Module> iterator = allowedModules.iterator(); iterator.hasNext(); ) {
            Module testModule =  iterator.next();
            if (! SecurityManager.getInstance().isUserAuthorizedToViewModule(testModule.getModuleKey())) {
                iterator.remove();
                if (moduleKey != null && moduleKey.equals(testModule.getModuleKey())) {
                    moduleKey = null;
                }
            }
        }

       // The module being requested is not visible to the current user.  Set the default to
       // the first module (if one is available).
       if (moduleKey == null && allowedModules.size() > 0) {
           moduleKey = allowedModules.iterator().next().getModuleKey();
       }

       for (Module module : allowedModules) {
           boolean selected = module.getModuleKey().equals(moduleKey);
           menuHolder.addMember(buildPrimaryMenuOption(module, selected));
       }
    }

    private Layout buildPrimaryMenu() {

        VLayout moduleLayout = new VLayout();
        moduleLayout.setAlign(VerticalAlignment.TOP);
        moduleLayout.setWidth100();
        moduleLayout.setAlign(Alignment.LEFT);
        moduleLayout.setHeight(40);
        moduleLayout.setBackgroundImage(GWT.getModuleBaseURL() + "admin/images/nav_bg.png");
        moduleLayout.addMember(new LayoutSpacer());

        HLayout primaryMenuOptionsHolder = new HLayout();
        primaryMenuOptionsHolder.setMembersMargin(5);
        primaryMenuOptionsHolder.setWidth100();
        primaryMenuOptionsHolder.setHeight(30);

        LayoutSpacer sp = new LayoutSpacer();
        sp.setWidth(20);
        primaryMenuOptionsHolder.addMember(sp);
        addAuthorizedModulesToMenu(primaryMenuOptionsHolder);
        moduleLayout.addMember(primaryMenuOptionsHolder);
        return moduleLayout;
    }

    private Layout buildSecondaryMenu(String selectedPage) {
        Module currentModule = modules.get(moduleKey);
        secondaryMenu.removeMembers(secondaryMenu.getMembers());

        LayoutSpacer sp2 = new LayoutSpacer();
        sp2.setWidth(10);
        secondaryMenu.addMember(sp2);
        secondaryMenu.setHeight(35);
        secondaryMenu.setBackgroundColor("#78a22F");
        secondaryMenu.addMember(sp2);

        LinkedHashMap<String, String[]> pages = modules.get(moduleKey).getPages();

        Collection<String> allowedPages  = pages.keySet();

        for (Iterator<String> iterator = allowedPages.iterator(); iterator.hasNext(); ) {
            String testPage =  (String) iterator.next();
            if (! SecurityManager.getInstance().isUserAuthorizedToViewSection(pages.get(testPage)[0])) {
                iterator.remove();
                if (selectedPage != null && selectedPage.equals(testPage)) {
                    selectedPage = null;
                }
            }
        }

        if (selectedPage == null && allowedPages.size() > 0) {
            selectedPage = (String) allowedPages.iterator().next();
        }

        for (String page : allowedPages) {
            boolean selected = (page.equals(selectedPage));
            secondaryMenu.addMember(buildSecondaryMenuOption(page, selected));
            if (selectedPage == null) {
                selectedPage = page;
            }
            selected = false;
        }

        return secondaryMenu;
    }

    private Canvas buildLogo() {
        ImgButton logo = new ImgButton();
        logo.setSrc(GWT.getModuleBaseURL() + "admin/images/blc_logo_white.png");

        logo.setWidth(149);
        logo.setHeight(71);
        logo.setShowRollOver(false);
        logo.setShowDownIcon(false);
        logo.setShowDown(false);
        return logo;
    }

    private Label buildPrimaryMenuOption(final Module module, boolean selected) {
        Label tmp = new Label(module.getModuleTitle());
        tmp.setValign(VerticalAlignment.BOTTOM);
        tmp.setAlign(Alignment.CENTER);
        tmp.setWrap(false);
        tmp.setPadding(10);

        final String style;
        if (selected) {
            style = "primaryMenuText-selected";
            selectedPrimaryMenuOption = tmp;
        } else {
            style = "primaryMenuText";
        }

        tmp.setBaseStyle(style);

        tmp.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                Object o = event.getSource();
                if (o instanceof Label) {
                    final Label lbl = (Label) o;
                    if (! lbl.getTitle().equals(selectedPrimaryMenuOption.getTitle())) {
                        selectedPrimaryMenuOption.setBaseStyle("primaryMenuText");
                        lbl.setBaseStyle("primaryMenuText-selected");
                        selectedPrimaryMenuOption = lbl;
                        moduleKey = module.getModuleKey();
                        buildSecondaryMenu(null);

                        //History.newItem(lbl.getTitle());

                        AppController.getInstance().go(canvas, module.getPages(), true);
	                 }
                }
	        }
        });

        return tmp;
    }

    private Label buildSecondaryMenuOption(final String title, boolean selected) {
        Label tmp = new Label(title);
        tmp.setTitle(title);
        tmp.setWrap(false);
        tmp.setValign(VerticalAlignment.BOTTOM);
        tmp.setAlign(Alignment.CENTER);
        tmp.setPadding(10);
        String style;

        if (selected) {
            style = "secondaryMenuText-selected";
            selectedSecondaryMenuOption = tmp;
        } else {
            style = "secondaryMenuText";
        }
        tmp.setBaseStyle(style);

        tmp.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Object o = event.getSource();
                if (o instanceof Label) {
                    Label lbl = (Label) o;
                    if (! lbl.getTitle().equals(selectedSecondaryMenuOption.getTitle())) {
                        selectedSecondaryMenuOption.setBaseStyle("secondaryMenuText");
                        lbl.setBaseStyle("secondaryMenuText-selected");
                        selectedSecondaryMenuOption = lbl;
					    History.newItem(lbl.getTitle());
                    }
                }
            }
        });


        return tmp;
    }


    private Canvas buildLocaleSelection() {
        String[] languages = {"English", "Spanish"};
        String[] languageCodes = {"en", "sp"};
        Menu localeMenu = new Menu();
        MenuItem[] menuItems = new MenuItem[languages.length];
        for (int i=0; i < languages.length; i++) {
            MenuItem tempMenuItem = new MenuItem(languages[i]);
            menuItems[i] = tempMenuItem;
        }
	    localeMenu.setData(menuItems);
        localeMenu.setShowIcons(false);

        String currentLanguage = languages[0];

        IMenuButton moduleSelectionButton = new IMenuButton(currentLanguage, localeMenu);
        moduleSelectionButton.setOverflow(Overflow.VISIBLE);
        return moduleSelectionButton;
    }

    private Canvas buildUserImage() {
        ImgButton logo = new ImgButton();
        logo.setSrc(GWT.getModuleBaseURL() + "admin/images/user.png");
        logo.setSize(16);
        logo.setShowRollOver(false);
        logo.setShowDownIcon(false);
        logo.setShowDown(false);
        return logo;
    }

    private Canvas buildUserInfo() {
        HLayout userFields = new HLayout();
        userFields.setAlign(Alignment.RIGHT);
        userFields.addMember(buildUserImage());

        Label userLabel = new Label(SecurityManager.USER.getUserName());
        userLabel.setBaseStyle("userText");
        userLabel.setWidth(1);
        userLabel.setOverflow(Overflow.VISIBLE);

        userFields.addMember(userLabel);

        Label logoutLink = new Label("(logout)");
        logoutLink.setBaseStyle("userLogout");


	    logoutLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                UrlBuilder builder = com.google.gwt.user.client.Window.Location.createUrlBuilder();
                builder.setPath(BLCMain.webAppContext + "/adminLogout.htm");
                com.google.gwt.user.client.Window.open(builder.buildString(), "_self", null);
            }
        } );

        userFields.addMember(logoutLink);

        return userFields;
    }




    private void buildFooter() {
        bottomBar = new ToolStrip();
        bottomBar.setBackgroundImage(GWT.getModuleBaseURL() + "admin/images/header_bg.png");
        bottomBar.setHeight(30);
        bottomBar.setWidth100();
        status = new Label();
        status.setWrap(false);
        bottomBar.addSpacer(6);

        bottomBar.addMember(status);
        bottomBar.addFill();
        bottomBar.addMember(BLCMain.NON_MODAL_PROGRESS);
        bottomBar.addSpacer(5);

        addMember(bottomBar);

    }





         /*

		
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
        moduleStack.setAlign(Alignment.RIGHT);
        moduleStack.setLayoutBottomMargin(10);
        temp.addMember(moduleStack);
        if (modules.size() > 1) {
	        Menu modulesMenu = new Menu();  
	        modulesMenu.setCanSelectParentItems(true);

            // Only show menu items for modules the user has access to
            Collection<Module> allowedModules = modules.values();

            for (Iterator<Module> iterator = allowedModules.iterator(); iterator.hasNext(); ) {
                Module testModule =  iterator.next();
                if (! SecurityManager.getInstance().isUserAuthorizedToViewModule(testModule.getModuleKey())) {
                    iterator.remove();
                    if (moduleKey != null && moduleKey.equals(testModule.getModuleKey())) {
                        moduleKey = null;
                    }
                }
            }

            if (moduleKey == null && allowedModules.size() > 0) {
                moduleKey = allowedModules.iterator().next().getModuleKey();
            }

	        MenuItem[] menuItems = new MenuItem[allowedModules.size()];
	        int j = 0;
	        for (Module module : allowedModules) {
	        	MenuItem tempMenuItem = new MenuItem(module.getModuleTitle());
	        	tempMenuItem.setAttribute("key", module.getModuleKey());
	        	menuItems[j] = tempMenuItem;
	        	j++;
	        }
	        modulesMenu.setData(menuItems);
            modulesMenu.setShowIcons(false);
	        
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
	        
	        IMenuButton moduleSelectionButton = new IMenuButton(modules.get(moduleKey).getModuleTitle(), modulesMenu);
            moduleSelectionButton.setOverflow(Overflow.VISIBLE);

	        //moduleSelectionButton.setWidth(22);

            moduleSelectionButton.setTitle(modules.get(moduleKey).getModuleTitle());
            moduleSelectionButton.setShowMenuBelow(true);


	        moduleStack.addMember(moduleSelectionButton);
        }

        HStack spacer = new HStack();
        spacer.setWidth(30);
        moduleStack.addMember(spacer);
        Label userName = new Label(BLCMain.getMessageManager().getString("currentUser") + ": <B>" + SecurityManager.USER.getUserName() + "</B>");
        userName.setWrap(false);
        userName.setStyleName("label-bold");
        moduleStack.addMember(userName);
        IButton logout = new IButton();
        logout.setTitle(BLCMain.getMessageManager().getString("logout"));
        logout.setWidth(60);
        logout.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
            public void onClick(ClickEvent event) {
                UrlBuilder builder = com.google.gwt.user.client.Window.Location.createUrlBuilder();
                builder.setPath(BLCMain.webAppContext + "/adminLogout.htm");
                com.google.gwt.user.client.Window.open(builder.buildString(), "_self", null);
            }
        });
        moduleStack.addMember(logout);
        HStack spacer2 = new HStack();
        spacer2.setWidth(30);
        moduleStack.addMember(spacer2);
        
        topTabSet = new TabSet();
        topTabSet.setTabBarPosition(Side.TOP);  
        topTabSet.setHeight(23);
        LinkedHashMap<String, String[]> pages = modules.get(moduleKey).getPages();
        for (String page : pages.keySet()) {
	        Tab tab = new Tab(page); 
	        tab.setAttribute("token", page);
	        tab.setID(page.replace(' ', '_'));
	        topTabSet.setShowPaneContainerEdges(false);
	        if (SecurityManager.getInstance().isUserAuthorizedToViewSection(pages.get(page)[0])){
	        	topTabSet.addTab(tab);
	        }
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

        addMember(topBar);    */
	

	public Canvas getContainer() {
		return canvas;
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
