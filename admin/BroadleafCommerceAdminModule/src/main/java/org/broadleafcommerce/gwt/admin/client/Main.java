package org.broadleafcommerce.gwt.admin.client;

import org.broadleafcommerce.gwt.client.BLCMain;
import org.broadleafcommerce.gwt.client.reflection.ModuleFactory;

public class Main extends BLCMain {
	
	public Main() {
		super();
		pages.put("Category", new String[]{"category", "categoryPresenter"});
		pages.put("Product", new String[]{"product", "productPresenter"});
		pages.put("Order", new String[]{"order", "orderPresenter"});
		pages.put("Customer", new String[]{"customer", "customerPresenter"});
		pages.put("Promotion", new String[]{"offer", "offerPresenter"});
		pages.put("Promotional Code", new String[]{"offerCode", "offerCodePresenter"});
		
		ModuleFactory moduleFactory = ModuleFactory.getInstance();
		moduleFactory.put("category", "org.broadleafcommerce.gwt.admin.client.view.catalog.category.CategoryView");
		moduleFactory.put("categoryPresenter", "org.broadleafcommerce.gwt.admin.client.presenter.catalog.category.CategoryPresenter");
		moduleFactory.put("product", "org.broadleafcommerce.gwt.admin.client.view.catalog.product.OneToOneProductSkuView");
		moduleFactory.put("productPresenter", "org.broadleafcommerce.gwt.admin.client.presenter.catalog.product.OneToOneProductSkuPresenter");
		moduleFactory.put("order", "org.broadleafcommerce.gwt.admin.client.view.order.OrderView");
		moduleFactory.put("orderPresenter", "org.broadleafcommerce.gwt.admin.client.presenter.order.OrderPresenter");
		moduleFactory.put("customer", "org.broadleafcommerce.gwt.admin.client.view.customer.CustomerView");
		moduleFactory.put("customerPresenter", "org.broadleafcommerce.gwt.admin.client.presenter.customer.CustomerPresenter");
		moduleFactory.put("offer", "org.broadleafcommerce.gwt.admin.client.view.promotion.offer.OfferView");
		moduleFactory.put("offerPresenter", "org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.OfferPresenter");
		moduleFactory.put("offerCode", "org.broadleafcommerce.gwt.admin.client.view.promotion.offercode.OfferCodeView");
		moduleFactory.put("offerCodePresenter", "org.broadleafcommerce.gwt.admin.client.presenter.promotion.offercode.OfferCodePresenter");
	}

}
