package org.broadleafcommerce.gwt.client;

import java.util.LinkedHashMap;

import org.broadleafcommerce.gwt.client.reflection.ModuleFactory;
import org.broadleafcommerce.gwt.client.view.MasterView;
import org.broadleafcommerce.gwt.client.view.ProgressWindow;
import org.broadleafcommerce.gwt.client.view.SimpleProgress;
import org.broadleafcommerce.gwt.client.view.dynamic.dialog.EntityEditDialog;

import com.google.gwt.core.client.EntryPoint;

public class BLCMain implements EntryPoint {
	
	public static ProgressWindow MODAL_PROGRESS = new ProgressWindow();
	public static SimpleProgress NON_MODAL_PROGRESS = new SimpleProgress(16, 150);
	public static EntityEditDialog ENTITY_ADD = new EntityEditDialog();
	
	public static final boolean DEBUG = true;
	//public static AdminUser USER;
	
	protected LinkedHashMap<String, String[]> pages = new LinkedHashMap<String, String[]>();
	
	public BLCMain() {
		//do nothing
	}
	
	public void clearViews() {
		pages.clear();
		ModuleFactory.getInstance().clear();
	}

	public void onModuleLoad() {	    	
		MasterView masterView = new MasterView(pages);
		masterView.draw();
		
//		AppServices.SECURITY.getAdminUser(new AbstractCallback<AdminUser>() {
//            @Override
//            public void onSuccess(AdminUser result) {
//                USER = result;
//                if (USER == null) {
//                	SC.say("This page cannot be accessed without first successfully logging in.");
//                } else {
                	AppController.getInstance().go(masterView.getContainer(), pages);
//                }
//            }
//        });    	
	}

}
