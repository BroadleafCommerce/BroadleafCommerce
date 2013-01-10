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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.BkgndRepeat;
import com.smartgwt.client.types.Cursor;
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
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import org.broadleafcommerce.openadmin.client.BLCLaunch;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.Module;
import org.broadleafcommerce.openadmin.client.callback.ItemEdited;
import org.broadleafcommerce.openadmin.client.callback.ItemEditedHandler;
import org.broadleafcommerce.openadmin.client.datasource.CeilingEntities;
import org.broadleafcommerce.openadmin.client.datasource.EntityImplementations;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.security.AdminUser;
import org.broadleafcommerce.openadmin.client.security.SecurityManager;
import org.broadleafcommerce.openadmin.client.setup.AppController;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntityEditDialog;


/**
 * 
 * @author jfischer
 *
 */
public class MasterView extends VLayout implements ValueChangeHandler<String> {
    
    protected Canvas canvas;
    protected ToolStrip bottomBar;
    protected Label status;
    
    protected Map<String,Label> moduleLabelMap = new HashMap<String, Label>();

    protected HLayout secondaryMenu = new HLayout();
    protected Label selectedSecondaryMenuOption;

    protected LinkedHashMap<String, Module> modules;


    public MasterView(String moduleKey, String pageKey, LinkedHashMap<String, Module> modules) {

        this.modules = modules;

        setWidth100();
        setHeight100();

        addMember(buildHeader());
        addMember(buildPrimaryMenu(moduleKey));
        addMember(buildSecondaryMenu(pageKey, moduleKey));


        canvas = new HLayout();
        canvas.setWidth100();
        canvas.setHeight100();

        addMember(canvas);

        buildFooter();
        bind();
    }

    private void bind() {
        History.addValueChangeHandler(this);
    }

    public void onValueChange(ValueChangeEvent<String> event) {
            String token = event.getValue();
            if (token != null) {
                String page = BLCLaunch.getSelectedPage(token);
                String moduleName = BLCLaunch.getSelectedModule(token);

                LinkedHashMap<String, String[]> pages = modules.get(moduleName).getPages();
                if (SecurityManager.getInstance().isUserAuthorizedToViewModule(moduleName) &&
                        SecurityManager.getInstance().isUserAuthorizedToViewSection(pages.get(page)[0])) {

                    if (moduleName != null && ! moduleName.equals(BLCMain.currentModuleKey)) {
                        BLCMain.setCurrentModuleKey(moduleName);            
                        selectPrimaryMenu(moduleName);
                        buildSecondaryMenu(page, moduleName);
                        AppController.getInstance().clearCurrentView();
                    } else {
                        AppController.getInstance().clearCurrentView();
                        buildSecondaryMenu(page, moduleName);
                    }
                }
            }
        }
    
    private void selectPrimaryMenu(String selectedModule) {
        // Set selected primary menu option.
        for (String moduleKey : moduleLabelMap.keySet()) {
            Label primaryMenuLabel = moduleLabelMap.get(moduleKey);
            if (moduleKey.equals(selectedModule)) {
                primaryMenuLabel.setBaseStyle("primaryMenuText-selected");
            } else {
                primaryMenuLabel.setBaseStyle("primaryMenuText");
            }
        }
    }

    private Layout buildHeader() {
        HLayout header = new HLayout();
        header.setWidth100();
        header.setLayoutMargin(10);
        header.setBackgroundImage(GWT.getModuleBaseURL() + "admin/images/header_bg.png");

        LayoutSpacer sp = new LayoutSpacer();
        sp.setWidth(20);
        header.addMember(sp);
        header.addMember(buildLogo());
        header.addMember(new LayoutSpacer());

        VLayout userAndLocale = new VLayout();
        userAndLocale.setHeight100();
        userAndLocale.addMember(buildUserInfo());
        //userAndLocale.addMember(buildLocaleSelection());

        header.addMember(userAndLocale);
        return header;
    }

    private void addAuthorizedModulesToMenu(Layout menuHolder, String moduleKey) {
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
           Label primaryMenuLabel = buildPrimaryMenuOption(module, selected);
           menuHolder.addMember(primaryMenuLabel);
           menuHolder.addMember(buildMenuSpacer());
           moduleLabelMap.put(module.getModuleKey(), primaryMenuLabel);
       }
    }

    private Layout buildPrimaryMenu(String currentModule) {

        HLayout moduleLayout = new HLayout();
        moduleLayout.setWidth100();
        moduleLayout.setHeight(38);
        moduleLayout.setBackgroundImage(GWT.getModuleBaseURL() + "admin/images/nav_bg.png");
        moduleLayout.setBackgroundRepeat(BkgndRepeat.REPEAT_X);

        moduleLayout.addMember(new LayoutSpacer());


        HLayout primaryMenuOptionsHolder = new HLayout();
        primaryMenuOptionsHolder.setLayoutAlign(VerticalAlignment.BOTTOM);
        //primaryMenuOptionsHolder.setLayoutAlign(Alignment.LEFT);
        primaryMenuOptionsHolder.setMembersMargin(5);
        primaryMenuOptionsHolder.setWidth100();
        primaryMenuOptionsHolder.setHeight(30);
        primaryMenuOptionsHolder.setAlign(Alignment.LEFT);



        LayoutSpacer sp = new LayoutSpacer();
        sp.setWidth(20);
        primaryMenuOptionsHolder.addMember(sp);
        addAuthorizedModulesToMenu(primaryMenuOptionsHolder, currentModule);
        moduleLayout.addMember(primaryMenuOptionsHolder);
        return moduleLayout;
    }

    private Layout buildSecondaryMenu(String selectedPage, String moduleKey) {
        secondaryMenu.removeMembers(secondaryMenu.getMembers());

        LayoutSpacer sp2 = new LayoutSpacer();
        sp2.setWidth(10);
        secondaryMenu.addMember(sp2);
        secondaryMenu.setHeight(40);
        //secondaryMenu.setBackgroundColor("#78a22F");
        secondaryMenu.setBackgroundImage(GWT.getModuleBaseURL() + "admin/images/nav_sec_bg.png");
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
        logo.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    BLCMain.SPLASH_PROGRESS.explicitShow();
                }
            }
        });

        return logo;
    }

    private Canvas buildMenuSpacer(){
        ImgButton spacer = new ImgButton();
        spacer.setSrc(GWT.getModuleBaseURL() + "admin/images/nav_spacer_36.png");

        spacer.setWidth(2);
        spacer.setHeight(30);
        spacer.setShowRollOver(false);
        spacer.setShowDownIcon(false);
        spacer.setShowDown(false);
        return spacer;
    }

    private Label buildPrimaryMenuOption(final Module module, boolean selected) {
        Label tmp = new Label(module.getModuleTitle());
        tmp.setValign(VerticalAlignment.CENTER);
        tmp.setHeight(30);
        tmp.setAlign(Alignment.CENTER);
        tmp.setWrap(false);
        tmp.setPadding(0);
        tmp.setShowRollOver(true);
        tmp.setCursor(Cursor.POINTER);

        final String style;
        if (selected) {
            style = "primaryMenuText-selected";            
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
                    if (! "primaryMenuText-selected".equals(lbl.getBaseStyle())) {
                        selectPrimaryMenu(module.getModuleKey());
                        lbl.setBaseStyle("primaryMenuText-selected");
                        BLCMain.setCurrentModuleKey(module.getModuleKey());                     
                        buildSecondaryMenu(null, module.getModuleKey());
                        AppController.getInstance().go(canvas, module.getPages(), null, module.getModuleKey(), false);
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
        tmp.setShowRollOver(true);
        tmp.setCursor(Cursor.POINTER);

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
                        BLCMain.setCurrentPageKey(lbl.getTitle());
                        buildHistoryNewItem(lbl.getTitle(), BLCLaunch.getSelectedModule(History.getToken()), null);
                    }
                }
            }
        });

        return tmp;
    }
    
    private void buildHistoryNewItem(String pageKey, String moduleKey, String itemId) {
        String destinationPage = "moduleKey=" + moduleKey +"&pageKey="+pageKey;

        if (itemId != null) {
            destinationPage = destinationPage + "&itemId="+itemId;
        }
        History.newItem(destinationPage);
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

    private Canvas buildLogoutImage() {
        ImgButton logo = new ImgButton();
        logo.setSrc(GWT.getModuleBaseURL() + "admin/images/logout_arrow.png");
        logo.setSize(11);
        logo.setValign(VerticalAlignment.CENTER);
        logo.setShowRollOver(false);
        logo.setShowDownIcon(false);
        logo.setShowDown(false);
        return logo;
    }

    private Canvas buildUserInfo() {
        HLayout userFields = new HLayout();
        userFields.setAlign(Alignment.RIGHT);
        userFields.addMember(buildUserImage());
        LayoutSpacer sp1 = new LayoutSpacer();
        sp1.setWidth(8);
        userFields.addMember(sp1);

        //Label userLabel = new Label(SecurityManager.USER.getUserName());
      //  userLabel.setBaseStyle("userText");
      //  userLabel.setWidth(1);
     //   userLabel.setOverflow(Overflow.VISIBLE);
     //   userFields.addMember(userLabel);

       //  userFields.addMember(buildLogoutImage());
        
        
        Menu menu = new Menu();  
        menu.setShowShadow(true);  
        menu.setShadowDepth(10);  
        menu.setShowIcons(false);
        
        MenuItem logout = new MenuItem("Logout");
        MenuItem edit = new MenuItem("Edit ...");        
        MenuItem changePassword = new MenuItem("Change Password ...");
        
        menu.setItems(edit, changePassword, logout);

        
        changePassword.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
                    @Override
                    public void onClick(MenuItemClickEvent event) {
                        final DynamicEntityDataSource userDS = new DynamicEntityDataSource(CeilingEntities.ADMIN_USER);
                        userDS.buildFields(null, false, new AsyncCallbackAdapter() {
                            public void onSetupSuccess(DataSource ds) {
                                AdminUser currentUser = SecurityManager.USER;
                                Record userRecord = new Record();
                                userRecord.setAttribute("id", currentUser.getId());                                
                                userRecord.setAttribute("login", currentUser.getUserName());
                                userRecord.setAttribute("_type", new String[]{EntityImplementations.ADMIN_USER});
        
                                EntityEditDialog ed = new EntityEditDialog();
        
                                ed.editRecord("Change Password", userDS, userRecord, new ItemEditedHandler() {
                                    public void onItemEdited(ItemEdited event) {
                                        String currentPage = BLCLaunch.getSelectedPage(History.getToken());
                                        if ("User Management".equals(currentPage)) {
                                            buildHistoryNewItem(currentPage, BLCLaunch.getSelectedModule(History.getToken()), event.getRecord().getAttribute("id"));
                                        }
                                    }
                                }, new String[]{"password"}, new String[]{}, false);
                            }
                        });
        
                    }
                });

        edit.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
            @Override
            public void onClick(MenuItemClickEvent event) {
                final DynamicEntityDataSource userDS = new DynamicEntityDataSource(CeilingEntities.ADMIN_USER);
                userDS.buildFields(null, false, new AsyncCallbackAdapter() {
                    public void onSetupSuccess(DataSource ds) {
                        AdminUser currentUser = SecurityManager.USER;
                        Record userRecord = new Record();
                        userRecord.setAttribute("id", currentUser.getId());
                        userRecord.setAttribute("name", currentUser.getName());
                        userRecord.setAttribute("email", currentUser.getEmail());
                        userRecord.setAttribute("phoneNumber", currentUser.getPhoneNumber());
                        userRecord.setAttribute("login", currentUser.getUserName());
                        userRecord.setAttribute("_type", new String[]{EntityImplementations.ADMIN_USER});

                        EntityEditDialog ed = new EntityEditDialog();

                        ed.editRecord("Edit User Information", userDS, userRecord, new ItemEditedHandler() {
                            public void onItemEdited(ItemEdited event) {
                                SecurityManager.USER.setPhoneNumber(event.getRecord().getAttribute("phoneNumber"));
                                SecurityManager.USER.setName(event.getRecord().getAttribute("name"));
                                SecurityManager.USER.setEmail(event.getRecord().getAttribute("email"));
                                String currentPage = BLCLaunch.getSelectedPage(History.getToken());
                                // If we are on the user module, reload the page with the specifically edited item.
                                if ("User Management".equals(currentPage)) {
                                    buildHistoryNewItem(currentPage, BLCLaunch.getSelectedModule(History.getToken()), event.getRecord().getAttribute("id"));
                                }
                            }
                        }, null, new String[]{"login", "activeStatusFlag", "password"}, false);
                    }
                });

            }
        });

        logout.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
            @Override
            public void onClick(MenuItemClickEvent event) {
                UrlBuilder builder = Window.Location.createUrlBuilder();
                builder.setPath(BLCMain.webAppContext + "/adminLogout.htm");
                builder.setHash(null);
                builder.setParameter("time", String.valueOf(System.currentTimeMillis()));
                Window.open(builder.buildString(), "_self", null);
            }
        } );

        
        IMenuButton menuButton = new IMenuButton(SecurityManager.USER.getUserName(), menu);          
        menuButton.setPadding(5);
        menuButton.setChildrenSnapResizeToGrid(true);
        menuButton.setOverflow(Overflow.VISIBLE);
        userFields.addMember(menuButton);
        
        LayoutSpacer sp2 = new LayoutSpacer();
        sp2.setWidth(200);
        userFields.addMember(sp2);

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
    
    public Module lookupModule(String key) {
        return modules.get(key);
    }
}
