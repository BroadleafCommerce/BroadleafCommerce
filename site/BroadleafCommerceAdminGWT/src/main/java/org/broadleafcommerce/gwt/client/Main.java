package org.broadleafcommerce.gwt.client;

import org.broadleafcommerce.gwt.client.view.MasterView;
import org.broadleafcommerce.gwt.client.view.ProgressWindow;
import org.broadleafcommerce.gwt.client.view.SimpleProgress;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntitySearchView;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityView;

import com.google.gwt.core.client.EntryPoint;

public class Main implements EntryPoint {
	
	public static ProgressWindow MODAL_PROGRESS = new ProgressWindow();
	public static SimpleProgress NON_MODAL_PROGRESS = new SimpleProgress(16, 150);
	public static DynamicEntityView ENTITY_ADD = new DynamicEntityView();
	public static DynamicEntitySearchView SEARCH_VIEW = new DynamicEntitySearchView();
	
	public static final boolean DEBUG = true;
	//public static AdminUser USER;

	public void onModuleLoad() {	    	
		MasterView masterView = new MasterView();
		masterView.draw();
		
//		AppServices.SECURITY.getAdminUser(new AbstractCallback<AdminUser>() {
//            @Override
//            public void onSuccess(AdminUser result) {
//                USER = result;
//                if (USER == null) {
//                	SC.say("This page cannot be accessed without first successfully logging in.");
//                } else {
                	AppController.getInstance().go(masterView.getContainer());
//                }
//            }
//        });    	
	}

}
