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

package org.broadleafcommerce.openadmin.client;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.callback.PostLaunch;
import org.broadleafcommerce.openadmin.client.security.SecurityManager;
import org.broadleafcommerce.openadmin.client.setup.AppController;
import org.broadleafcommerce.openadmin.client.setup.EJB3ConfigurationPreProcessor;
import org.broadleafcommerce.openadmin.client.setup.PreProcessStatus;
import org.broadleafcommerce.openadmin.client.setup.PreProcessor;
import org.broadleafcommerce.openadmin.client.setup.UrlStructurePreProcessor;
import org.broadleafcommerce.openadmin.client.setup.UserSecurityPreProcessor;
import org.broadleafcommerce.openadmin.client.setup.WorkflowEnabledPreProcessor;
import org.broadleafcommerce.openadmin.client.view.MasterView;
import org.broadleafcommerce.openadmin.client.view.ProgressWindow;
import org.broadleafcommerce.openadmin.client.view.SimpleProgress;
import org.broadleafcommerce.openadmin.client.view.SplashView;
import org.broadleafcommerce.openadmin.client.view.SplashWindow;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntityEditDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.PolymorphicTypeSelectionDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.NumericTypeFactory;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.ServerProcessProgressWindow;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.i18n.client.NumberFormat;
import com.smartgwt.client.util.SC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class BLCMain implements EntryPoint {
	
	public static final MessageManager MESSAGE_MANAGER = new MessageManager();
    static {
        MESSAGE_MANAGER.addConstants(GWT.<i18nConstants>create(OpenAdminMessages.class));
    }
	private static HashMap<String, Module> modules = new HashMap<String, Module>(10);
    private static List<PreProcessor> preProcessors = new ArrayList<PreProcessor>(10);
    public static final ServerProcessProgressWindow progressWindow = new ServerProcessProgressWindow();

    public static String csrfToken;
    public static String webAppContext;
    public static String storeFrontWebAppPrefix;
    public static String assetServerUrlPrefix;
    public static String adminContext;
	public static ProgressWindow MODAL_PROGRESS = new ProgressWindow();
	public static SplashView SPLASH_PROGRESS = new SplashWindow(GWT.getModuleBaseURL()+"admin/images/splash_screen.jpg", "");
	public static SimpleProgress NON_MODAL_PROGRESS = new SimpleProgress(16, 150);
	public static EntityEditDialog ENTITY_ADD = new EntityEditDialog();
    public static PolymorphicTypeSelectionDialog POLYMORPHIC_ADD = new PolymorphicTypeSelectionDialog();
	public static MasterView MASTERVIEW;
	public static boolean ISNEW = true;
	public static String currentModuleKey;
    public static String currentPageKey;
    public static String currentViewKey;
    public static PostLaunch postLaunch = null;
    public static boolean workflowEnabled = false;
	
	public static final boolean DEBUG = true;
	
	public static void addModule(Module module) {
	   Module existing=modules.get(module.getModuleKey());
	    if(existing ==null || existing==module) {
	        modules.put(module.getModuleKey(), module);
	        return;
	    } 
	    //Merge the two modules named same.
	   for(ModuleSectionPojo section : new ArrayList<ModuleSectionPojo>(((AbstractModule)module).getSections())) {
	       //remove section from the new module that is already in our list
	       ((AbstractModule)module).removeSection(section.getSectionTitle());
	       //add the removed section back into the existing module that is already in the map.
	       ((AbstractModule)existing).setSection(section.getSectionTitle(),section.sectionViewKey,section.sectionViewClass,section.sectionPresenterKey,section.sectionPresenterClass,
	               section.sectionPermissions);
	      
	   }
	  
	}

    public static void removeModule(String moduleKey) {
        modules.remove(moduleKey);
    }
	
	public static Module getModule(String moduleKey) {
		return modules.get(moduleKey);
	}

    public static void addPreProcessor(PreProcessor preProcessor) {
        preProcessors.add(preProcessor);
    }
	
	public static void setSplashWindow(SplashView splashWindow) {
		SPLASH_PROGRESS = splashWindow;
	}
	
	public static void removeSplashWindow() {
		SPLASH_PROGRESS = null;
	}

    public static void setCurrentModuleKey(String requestedModuleKey) {
        if (requestedModuleKey != null && modules.get(requestedModuleKey) != null) {
            if (SecurityManager.getInstance().isUserAuthorizedToViewModule(requestedModuleKey)) {
                currentModuleKey = requestedModuleKey;
                return;
            }
        }

        currentModuleKey = null;

        for (Map.Entry<String, Module> entry : modules.entrySet()) {
            Module currentModule = entry.getValue();
            if (SecurityManager.getInstance().isUserAuthorizedToViewModule(currentModule.getModuleKey())) {
                currentModuleKey = currentModule.getModuleKey();
                return;
            }
        }
    }

    public static void setCurrentPageKey(String requestedPageKey) {
        Map<String,String[]> pagesMap = modules.get(currentModuleKey).getPages();

        if (pagesMap.get(requestedPageKey) != null) {
            String pageView = pagesMap.get(requestedPageKey)[0];
            if (SecurityManager.getInstance().isUserAuthorizedToViewSection(pageView)) {
                currentPageKey = requestedPageKey;
                return;
            }
        }

        currentPageKey = null;
        for(String pageKey : pagesMap.keySet()) {
            String view = pagesMap.get(pageKey)[0];
            if (SecurityManager.getInstance().isUserAuthorizedToViewSection(view)) {
                currentPageKey = pageKey;
                return;
            }
        }
    }

    public static String buildStoreFrontBaseUrl() {
        return buildStoreFrontBaseUrl(null);
    }

    public static String buildStoreFrontBaseUrl(String path) {
        String prefix = storeFrontWebAppPrefix;
        if (prefix.startsWith("/")) {
            UrlBuilder urlBuilder = new UrlBuilder();
            urlBuilder.setHost(com.google.gwt.user.client.Window.Location.getHost());
            String port = com.google.gwt.user.client.Window.Location.getPort();
            if (port != null && port.length() > 0) {
                urlBuilder.setPort(Integer.valueOf(port));
            }
            urlBuilder.setProtocol(com.google.gwt.user.client.Window.Location.getProtocol());
            urlBuilder.setPath(prefix + (path==null?"":path));

            return urlBuilder.buildString();
        }

        return prefix + (path==null?"":path);
    }
	
	public static void drawCurrentState(final String requestedModuleKey, final String requestedPageKey) {
        adminContext = GWT.getModuleBaseURL();
        if (!preProcessors.isEmpty()) {
            Map<String, String> pipelineSeed = new HashMap<String, String>();
            pipelineSeed.put("requestedModuleKey", requestedModuleKey);
            pipelineSeed.put("requestedPageKey", requestedPageKey);
            executePreProcessors(0, pipelineSeed);
        } else {
            if (postLaunch != null) {
                postLaunch.onLaunched();
            }
            finalizeCurrentState(requestedModuleKey, requestedPageKey);
        }
	}

    private static void executePreProcessors(final int count, final Map<String, String> pipelineSeed) {
        PreProcessor preProcessor = preProcessors.get(count);
        preProcessor.preProcess(progressWindow, pipelineSeed, new PreProcessStatus() {
            @Override
            public void complete() {
                int temp = count + 1;
                if (temp < preProcessors.size()) {
                    executePreProcessors(temp, pipelineSeed);
                } else {
                    if (postLaunch != null) {
                        postLaunch.onLaunched();
                    }
                    finalizeCurrentState(pipelineSeed.get("requestedModuleKey"), pipelineSeed.get("requestedPageKey"));
                }
            }
        });
    }

    private static void finalizeCurrentState(final String requestedModuleKey, final String requestedPageKey) {
        java.util.logging.Logger.getLogger(BLCMain.class.getName()).info("Admin user found. Loading interface...");
        setCurrentModuleKey(requestedModuleKey);

        if (currentModuleKey == null) {
            SC.say(getMessageManager().getString("noModulesAuthorized"));
            return;
        }

        setCurrentPageKey(requestedPageKey);

        if (currentPageKey == null) {
            SC.say(getMessageManager().getString("noAuthorizedPages"));
            return;
        }

        //sort modules by their declared order property
        LinkedHashMap<String, Module> orderedModules = new LinkedHashMap<String, Module>(modules.size());
        List<Module> moduleList = new ArrayList<Module>(modules.values());
        Collections.sort(moduleList, new Comparator<Module>() {
            @Override
            public int compare(Module module, Module module1) {
                return module.getOrder().compareTo(module1.getOrder());
            }
        });
        for (Module module : moduleList) {
            orderedModules.put(module.getModuleKey(), module);
        }
        modules = orderedModules;

        modules.get(currentModuleKey).preDraw();
        MASTERVIEW = new MasterView(currentModuleKey, currentPageKey, orderedModules);
        MASTERVIEW.draw();
        AppController.getInstance().go(MASTERVIEW.getContainer(), modules.get(currentModuleKey).getPages(), currentPageKey, currentModuleKey, true);
        modules.get(currentModuleKey).postDraw();
    }

	@Override
    public void onModuleLoad() {
	
        NumericTypeFactory.registerNumericSimpleType("localDecimal", NumberFormat.getDecimalFormat(), SupportedFieldType.DECIMAL);
        NumericTypeFactory.registerNumericSimpleType("localMoneyDecimal", NumberFormat.getDecimalFormat(), SupportedFieldType.MONEY);
        NumericTypeFactory.registerNumericSimpleType("localCurrency", NumberFormat.getCurrencyFormat(), SupportedFieldType.MONEY);

        addPreProcessor(new UrlStructurePreProcessor());
        addPreProcessor(new UserSecurityPreProcessor());
        addPreProcessor(new EJB3ConfigurationPreProcessor());
        addPreProcessor(new WorkflowEnabledPreProcessor());
	}

    public static MessageManager getMessageManager() {
        return MESSAGE_MANAGER;
    }

	/**
     * Log a debug.
     *
     * @param message  the message to log
     * @param category category to log in, defaults to "Log"
     */
    public static native void logDebug(String message, String category) /*-{
    	if ($wnd.isc.Log.logIsDebugEnabled(category)) {
    		$wnd.isc.Log.logDebug(message, category);
    	}
	}-*/;
    
    public static native boolean isLogDebugEnabled(String category) /*-{
		return $wnd.isc.Log.logIsDebugEnabled(category)
	}-*/;

}
