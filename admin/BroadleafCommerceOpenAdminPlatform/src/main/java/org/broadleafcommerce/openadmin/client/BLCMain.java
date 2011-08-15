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
package org.broadleafcommerce.openadmin.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.smartgwt.client.core.KeyIdentifier;
import com.smartgwt.client.util.KeyCallback;
import com.smartgwt.client.util.Page;
import com.smartgwt.client.util.SC;
import org.broadleafcommerce.openadmin.client.security.AdminUser;
import org.broadleafcommerce.openadmin.client.security.SecurityManager;
import org.broadleafcommerce.openadmin.client.service.AbstractCallback;
import org.broadleafcommerce.openadmin.client.service.AppServices;
import org.broadleafcommerce.openadmin.client.setup.AppController;
import org.broadleafcommerce.openadmin.client.view.*;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntityEditDialog;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * 
 * @author jfischer
 *
 */
public class BLCMain implements EntryPoint {
	
	public static final MessageManager MESSAGE_MANAGER = new MessageManager();
    static {
        MESSAGE_MANAGER.addConstants(GWT.<ConstantsWithLookup>create(OpenAdminMessages.class));
    }
	private static LinkedHashMap<String, Module> modules = new LinkedHashMap<String, Module>();
	
	public static ProgressWindow MODAL_PROGRESS = new ProgressWindow();
	//TODO set the version as part of the build
	public static SplashView SPLASH_PROGRESS = new SplashWindow(GWT.getModuleBaseURL()+"admin/images/splash_screen.jpg", "1.5.0-M2-SNAPSHOT");
	public static SimpleProgress NON_MODAL_PROGRESS = new SimpleProgress(16, 150);
	public static EntityEditDialog ENTITY_ADD = new EntityEditDialog();
	public static MasterView MASTERVIEW;
	public static boolean ISNEW = true;
	public static String currentModuleKey;
	
	public static final boolean DEBUG = true;
	
	public static void addModule(Module module) {
		modules.put(module.getModuleKey(), module);
	}
	
	public static Module getModule(String moduleKey) {
		return modules.get(moduleKey);
	}
	
	public static void setSplashWindow(SplashView splashWindow) {
		SPLASH_PROGRESS = splashWindow;
	}
	
	public static void removeSplashWindow() {
		SPLASH_PROGRESS = null;
	}
	
	public static void drawCurrentState(final String requestedModuleKey) {
		AppServices.SECURITY.getAdminUser(new AbstractCallback<AdminUser>() {
            @Override
            public void onSuccess(AdminUser result) {
            	SecurityManager.USER  = result;

                if (result == null) {
                	SC.say("This page cannot be accessed without first successfully logging in.");
                } else {
                    String moduleKey = requestedModuleKey;

                    for (Iterator<Module> iterator = modules.values().iterator(); iterator.hasNext(); ) {
                        Module currentModule = iterator.next();
                        if (! SecurityManager.getInstance().isUserAuthorizedToViewModule(currentModule.getModuleKey())) {
                              modules.remove(currentModule.getModuleKey());
                        }

                    }

                    if (moduleKey == null) {
                        moduleKey = modules.keySet().iterator().next();
                    }

                    currentModuleKey = moduleKey;
                    modules.get(currentModuleKey).preDraw();
                    MASTERVIEW = new MasterView(moduleKey, modules);
                    MASTERVIEW.draw();

                    AppController.getInstance().go(MASTERVIEW.getContainer(), modules.get(moduleKey).getPages());
                    modules.get(moduleKey).postDraw();
                }
            }
        }); 
	}

	public void onModuleLoad() {
		if (!GWT.isScript()) { 
		    KeyIdentifier debugKey = new KeyIdentifier(); 
		    debugKey.setCtrlKey(true); 
		    debugKey.setKeyName("D"); 
		    Page.registerKey(debugKey, new KeyCallback() { 
		        public void execute(String keyName) { 
		            SC.showConsole(); 
		        }
		    });
		}
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
