/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.setup;

import org.broadleafcommerce.openadmin.client.BLCLaunch;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.Module;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.presenter.entity.EntityPresenter;
import org.broadleafcommerce.openadmin.client.presenter.entity.PresenterModifier;
import org.broadleafcommerce.openadmin.client.reflection.AsyncClient;
import org.broadleafcommerce.openadmin.client.reflection.ModuleFactory;
import org.broadleafcommerce.openadmin.client.security.AdminUser;
import org.broadleafcommerce.openadmin.client.security.SecurityManager;
import org.broadleafcommerce.openadmin.client.service.AbstractCallback;
import org.broadleafcommerce.openadmin.client.service.AppServices;
import org.broadleafcommerce.openadmin.client.view.Display;
import org.broadleafcommerce.openadmin.client.view.UIFactory;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.smartgwt.client.widgets.Canvas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public class AppController implements ValueChangeHandler<String> {

    private static AppController controller = null;

    public static AppController getInstance() {
        if (controller == null) {
            controller = new AppController();
        }
        return controller;
    }

    private final HandlerManager eventBus = new HandlerManager(null);

    private Canvas container;

    private final UIFactory uiFactory = new UIFactory();

    private HashMap<String, String[]> pages;

    private AppController() {
        bind();
    }

    private void bind() {
        History.addValueChangeHandler(this);
    }

    private void buildHistoryNewItem(String pageKey, String moduleKey) {
        String token = History.getToken();
        String destinationPage = "moduleKey=" + moduleKey + "&pageKey=" + pageKey;
        if (BLCLaunch.getDefaultItem(token) != null) {
            destinationPage = destinationPage + "&itemId=" + BLCLaunch.getDefaultItem(token);
        }
        History.newItem(destinationPage);
    }

    public void clearCurrentView() {
        uiFactory.clearCurrentView();
    }

    public void go(final Canvas container, HashMap<String, String[]> pages, String pageKey, String moduleKey, boolean firstTime) {
        this.pages = pages;
        this.container = container;

        if (firstTime) {
            String token = History.getToken();
            if (pageKey.equals(BLCLaunch.getSelectedPage(token)) && moduleKey.equals(BLCLaunch.getSelectedModule(token))) {
                String itemId = BLCLaunch.getDefaultItem(token);
                showView(pages.get(pageKey)[0], pages.get(pageKey)[1], itemId);
            } else {
                buildHistoryNewItem(pageKey, moduleKey);
            }
            return;
        }

        if (pageKey != null && pages.get(pageKey) != null) {
            if (SecurityManager.getInstance().isUserAuthorizedToViewSection(pages.get(pageKey)[0])) {
                buildHistoryNewItem(pageKey, moduleKey);
                return;
            }
        }

        for (String sectionTitle : pages.keySet()) {
            if (SecurityManager.getInstance().isUserAuthorizedToViewSection(pages.get(sectionTitle)[0])) {
                buildHistoryNewItem(sectionTitle, moduleKey);
                break;
            }
        }
    }

    public HandlerManager getEventBus() {
        return eventBus;
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        String token = event.getValue();

        if (token != null) {
            String page = BLCLaunch.getSelectedPage(token);
            String moduleName = BLCLaunch.getSelectedModule(token);
            String itemId = BLCLaunch.getDefaultItem(token);

            if (page != null && moduleName != null) {
                if (!uiFactory.equalsCurrentView(page) || itemId != null) {
                    Module module = BLCMain.getModule(moduleName);
                    String[] vals = module.getPages().get(page);
                    if (vals != null) {
                        showView(vals[0], vals[1], itemId);
                    }
                }
            }
        }
    }

    protected void showView(final String viewKey, final String presenterKey, final String itemId) {
        if (!BLCMain.ISNEW) {
            BLCMain.MODAL_PROGRESS.startProgress(new Timer() {

                @Override
                public void run() {
                    setupView(viewKey, presenterKey, itemId);
                }
            });
        } else {
            setupView(viewKey, presenterKey, itemId);
        }
    }

    protected void setupView(final String viewKey, final String presenterKey, final String itemId) {
        AppServices.SECURITY.getAdminUser(new AbstractCallback<AdminUser>() {

            @Override
            public void onSuccess(AdminUser result) {
                if (result == null) {
                    UrlBuilder builder = Window.Location.createUrlBuilder();
                    builder.setPath(BLCMain.webAppContext + "/admin/adminLogout.htm");
                    builder.setParameter("time", String.valueOf(System.currentTimeMillis()));
                    Window.open(builder.buildString(), "_self", null);
                } else {
                    if (SecurityManager.getInstance().isUserAuthorizedToViewSection(viewKey)) {
                        uiFactory.clearCurrentView();
                        uiFactory.getView(viewKey, false, false, new AsyncClient() {

                            @Override
                            public void onSuccess(Object instance) {
                                final Display view = (Display) instance;
                                uiFactory.getPresenter(presenterKey, new AsyncClient() {

                                    @Override
                                    public void onSuccess(Object instance) {
                                        EntityPresenter presenter = (EntityPresenter) instance;

                                        List<PresenterModifier> presenterModifierList = findPresenterModifiers(presenter.getClass());
                                        if(presenterModifierList!=null && presenter instanceof DynamicEntityPresenter) {
                                            for(PresenterModifier modifier:presenterModifierList) {
                                              modifier.setParentPresenter(((DynamicEntityPresenter ) presenter));
                                            }
                                            ((DynamicEntityPresenter ) presenter).getModifierList().addAll(presenterModifierList);
                                        }
                                        presenter.setDefaultItemId(itemId);
                                        presenter.setDisplay(view);
                                        presenter.setEventBus(eventBus);
                                        BLCMain.currentViewKey = viewKey;
                                        if (presenter.getPresenterSequenceSetupManager() != null) {
                                            presenter.getPresenterSequenceSetupManager().setCanvas(container);
                                         
                                            presenter.setup();
                                            for(PresenterModifier modifier:presenterModifierList) {
                                                modifier.setup();
                                            }
                                            presenter.getPresenterSequenceSetupManager().launch();
                                        } else {
                                            for(PresenterModifier modifier:presenterModifierList) {
                                                modifier.setup();
                                            }
                                            presenter.setup();
                                        }
                                    }

                                    @Override
                                    public void onUnavailable() {
                                        throw new RuntimeException("Unable to show item: " + presenterKey);
                                    }
                                });
                            }

                            @Override
                            public void onUnavailable() {
                                throw new RuntimeException("Unable to show item: " + viewKey);
                            }
                        });
                    }
                }
            }
        });
    }

    protected List<PresenterModifier> findPresenterModifiers(Class<?> presenter) {
      
        if(presenter.getName().equals(DynamicEntityPresenter.class.getName())) {
            return new ArrayList<PresenterModifier>();
        }
     
        if(ModuleFactory.getInstance().getModifiers(presenter.getName())==null) { 
            if(presenter.getClass().getName().equals(presenter.getClass().getSuperclass().getName())) {
                return new ArrayList<PresenterModifier>(); 
            }
            //TODO need to get superclass info, as it doesn't work currently
            return new ArrayList<PresenterModifier>();
  //          return findPresenterModifiers(presenter.getClass().getSuperclass());    
           
        } else {
           return ModuleFactory.getInstance().getModifiers(presenter.getName());
        }
    }
}
