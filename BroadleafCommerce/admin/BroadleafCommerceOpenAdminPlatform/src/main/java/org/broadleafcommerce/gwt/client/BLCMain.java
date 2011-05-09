package org.broadleafcommerce.gwt.client;

import java.util.LinkedHashMap;

import org.broadleafcommerce.gwt.client.view.MasterView;
import org.broadleafcommerce.gwt.client.view.ProgressWindow;
import org.broadleafcommerce.gwt.client.view.SimpleProgress;
import org.broadleafcommerce.gwt.client.view.dynamic.dialog.EntityEditDialog;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class BLCMain implements EntryPoint {
	
	public static final OpenAdminMessages OPENADMINMESSAGES = GWT.create(OpenAdminMessages.class);
	private static LinkedHashMap<String, Module> modules = new LinkedHashMap<String, Module>();
	
	public static ProgressWindow MODAL_PROGRESS = new ProgressWindow();
	public static SimpleProgress NON_MODAL_PROGRESS = new SimpleProgress(16, 150);
	public static EntityEditDialog ENTITY_ADD = new EntityEditDialog();
	public static MasterView MASTERVIEW;
	
	public static final boolean DEBUG = true;
	//public static AdminUser USER;
	
	public static void addModule(Module module) {
		modules.put(module.getModuleKey(), module);
	}
	
	public static void drawCurrentState(String moduleKey) {
		if (moduleKey == null) {
			moduleKey = modules.keySet().iterator().next();
		}
		modules.get(moduleKey).preDraw();
		
		MASTERVIEW = new MasterView(moduleKey, modules);
		MASTERVIEW.draw();
		
//		AppServices.SECURITY.getAdminUser(new AbstractCallback<AdminUser>() {
//            @Override
//            public void onSuccess(AdminUser result) {
//                USER = result;
//                if (USER == null) {
//                	SC.say("This page cannot be accessed without first successfully logging in.");
//                } else {
                	AppController.getInstance().go(MASTERVIEW.getContainer(), modules.get(moduleKey).getPages());
//                }
//            }
//        }); 
		modules.get(moduleKey).postDraw();
	}

	public void onModuleLoad() {	    	
		//do nothing
	}

}
