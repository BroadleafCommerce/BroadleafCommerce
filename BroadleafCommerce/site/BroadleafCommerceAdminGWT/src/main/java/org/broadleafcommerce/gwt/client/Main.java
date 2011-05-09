package org.broadleafcommerce.gwt.client;

import java.util.HashMap;

import org.broadleafcommerce.gwt.client.reflection.ModuleFactory;
import org.broadleafcommerce.gwt.client.view.MasterView;
import org.broadleafcommerce.gwt.client.view.ProgressWindow;
import org.broadleafcommerce.gwt.client.view.SimpleProgress;
import org.broadleafcommerce.gwt.client.view.dynamic.dialog.EntityEditDialog;

import com.google.gwt.core.client.EntryPoint;

public class Main implements EntryPoint {
	
	public static ProgressWindow MODAL_PROGRESS = new ProgressWindow();
	public static SimpleProgress NON_MODAL_PROGRESS = new SimpleProgress(16, 150);
	public static EntityEditDialog ENTITY_ADD = new EntityEditDialog();
	
	public static final boolean DEBUG = true;
	//public static AdminUser USER;
	
	protected HashMap<String, String[]> pages = new HashMap<String, String[]>();
	
	public Main() {
		pages.put("Category", new String[]{"category", "categoryPresenter"});
		pages.put("Product", new String[]{"product", "productPresenter"});
		pages.put("Order", new String[]{"order", "orderPresenter"});
		
		ModuleFactory moduleFactory = ModuleFactory.getInstance();
		moduleFactory.put("category", "org.broadleafcommerce.gwt.client.view.catalog.CategoryView");
		moduleFactory.put("categoryPresenter", "org.broadleafcommerce.gwt.client.presenter.catalog.CategoryPresenter");
		moduleFactory.put("product", "org.broadleafcommerce.gwt.client.view.catalog.OneToOneProductSkuView");
		moduleFactory.put("productPresenter", "org.broadleafcommerce.gwt.client.presenter.catalog.OneToOneProductSkuPresenter");
		moduleFactory.put("order", "org.broadleafcommerce.gwt.client.view.order.OrderView");
		moduleFactory.put("orderPresenter", "org.broadleafcommerce.gwt.client.presenter.order.OrderPresenter");
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
