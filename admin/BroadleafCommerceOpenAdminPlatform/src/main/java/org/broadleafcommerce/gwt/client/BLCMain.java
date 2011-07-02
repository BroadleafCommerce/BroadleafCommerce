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
package org.broadleafcommerce.gwt.client;

import java.util.LinkedHashMap;

import org.broadleafcommerce.gwt.client.security.AdminUser;
import org.broadleafcommerce.gwt.client.security.SecurityManager;
import org.broadleafcommerce.gwt.client.service.AbstractCallback;
import org.broadleafcommerce.gwt.client.service.AppServices;
import org.broadleafcommerce.gwt.client.setup.AppController;
import org.broadleafcommerce.gwt.client.view.MasterView;
import org.broadleafcommerce.gwt.client.view.SplashView;
import org.broadleafcommerce.gwt.client.view.ProgressWindow;
import org.broadleafcommerce.gwt.client.view.SimpleProgress;
import org.broadleafcommerce.gwt.client.view.SplashWindow;
import org.broadleafcommerce.gwt.client.view.dynamic.dialog.EntityEditDialog;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.smartgwt.client.core.KeyIdentifier;
import com.smartgwt.client.util.KeyCallback;
import com.smartgwt.client.util.Page;
import com.smartgwt.client.util.SC;

/**
 * 
 * @author jfischer
 *
 */
public class BLCMain implements EntryPoint {
	
	public static final OpenAdminMessages OPENADMINMESSAGES = GWT.create(OpenAdminMessages.class);
	private static LinkedHashMap<String, Module> modules = new LinkedHashMap<String, Module>();
	
	public static ProgressWindow MODAL_PROGRESS = new ProgressWindow();
	//TODO set the version as part of the build
	public static SplashView SPLASH_PROGRESS = new SplashWindow(GWT.getModuleBaseURL()+"admin/images/splash_screen.jpg", "1.5.0-M2-SNAPSHOT");
	public static SimpleProgress NON_MODAL_PROGRESS = new SimpleProgress(16, 150);
	public static EntityEditDialog ENTITY_ADD = new EntityEditDialog();
	public static MasterView MASTERVIEW;
	public static boolean ISNEW = true;
	
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
	
	public static void drawCurrentState(String moduleKey) {
		if (moduleKey == null) {
			moduleKey = modules.keySet().iterator().next();
		}
		final String finalKey = moduleKey;
		modules.get(finalKey).preDraw();
		
		AppServices.SECURITY.getAdminUser(new AbstractCallback<AdminUser>() {
            @Override
            public void onSuccess(AdminUser result) {
            	SecurityManager.USER  = result;
            	
                if (result == null) {
                	SC.say("This page cannot be accessed without first successfully logging in.");
                } else {
            		MASTERVIEW = new MasterView(finalKey, modules);
            		MASTERVIEW.draw();
            		
                	AppController.getInstance().go(MASTERVIEW.getContainer(), modules.get(finalKey).getPages());
                	modules.get(finalKey).postDraw();
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
