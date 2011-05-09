package org.broadleafcommerce.gwt.admin.client;

import java.util.ArrayList;
import java.util.List;

import org.broadleafcommerce.gwt.client.AbstractModule;
import org.broadleafcommerce.gwt.client.BLCMain;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;

public class AdminModule extends AbstractModule {
	
	public static final AdminMessages ADMINMESSAGES = GWT.create(AdminMessages.class);
	
	public void onModuleLoad() {
		setModuleTitle(AdminModule.ADMINMESSAGES.adminModuleTitle());
		setModuleKey("BLCAdmin");
		
		List<String> categoryRoles = new ArrayList<String>();
		categoryRoles.add("ROLE_ADMIN");
		setSection(
			AdminModule.ADMINMESSAGES.categoryMainTitle(),
			"category",
			"org.broadleafcommerce.gwt.admin.client.view.catalog.category.CategoryView",
			"categoryPresenter",
			"org.broadleafcommerce.gwt.admin.client.presenter.catalog.category.CategoryPresenter",
			categoryRoles,
			null
		);
		List<String> productRoles = new ArrayList<String>();
		productRoles.add("ROLE_ADMIN");
		setSection( 
			AdminModule.ADMINMESSAGES.productMainTitle(),
			"product",
			"org.broadleafcommerce.gwt.admin.client.view.catalog.product.OneToOneProductSkuView",
			"productPresenter",
			"org.broadleafcommerce.gwt.admin.client.presenter.catalog.product.OneToOneProductSkuPresenter",
			productRoles,
			null
		);
		
		List<String> orderRoles = new ArrayList<String>();
		orderRoles.add("ROLE_ADMIN");
		setSection(
			AdminModule.ADMINMESSAGES.orderMainTitle(),
			"order",
			"org.broadleafcommerce.gwt.admin.client.view.order.OrderView",
			"orderPresenter",
			"org.broadleafcommerce.gwt.admin.client.presenter.order.OrderPresenter",
			orderRoles,
			null
		);
		
		List<String> customerRoles = new ArrayList<String>();
		customerRoles.add("ROLE_ADMIN");
		setSection(
			AdminModule.ADMINMESSAGES.customerMainTitle(),
			"customer",
			"org.broadleafcommerce.gwt.admin.client.view.customer.CustomerView",
			"customerPresenter",
			"org.broadleafcommerce.gwt.admin.client.presenter.customer.CustomerPresenter",
			customerRoles,
			null
		);
		
		List<String> offerPermissions = new ArrayList<String>();
		offerPermissions.add("PERMISSION_VIEW_OFFER_SECTION");
		setSection(
			AdminModule.ADMINMESSAGES.promotionMainTitle(),
			"offer",
			"org.broadleafcommerce.gwt.admin.client.view.promotion.offer.OfferView",
			"offerPresenter",
			"org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.OfferPresenter",
			null,
			offerPermissions
		);
		
		List<String> userManagementRoles = new ArrayList<String>();
		userManagementRoles.add("ROLE_ADMIN");
		setSection(
			AdminModule.ADMINMESSAGES.userManagementMainTitle(),
			"user",
			"org.broadleafcommerce.gwt.admin.client.view.user.UserManagementView",
			"userPresenter",
			"org.broadleafcommerce.gwt.admin.client.presenter.user.UserManagementPresenter",
			userManagementRoles,
			null
		);
		
		registerModule();
	}

	@Override
	public void postDraw() {
		ImgButton sgwtHomeButton = new ImgButton();
        sgwtHomeButton.setSrc(GWT.getModuleBaseURL() + "admin/images/blc_logo.png");
        sgwtHomeButton.setWidth(98);
        sgwtHomeButton.setHeight(50);
        sgwtHomeButton.setPrompt(ADMINMESSAGES.blcProjectPage());
        sgwtHomeButton.setHoverStyle("interactImageHover");
        sgwtHomeButton.setShowRollOver(false);
        sgwtHomeButton.setShowDownIcon(false);
        sgwtHomeButton.setShowDown(false);
        sgwtHomeButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
            public void onClick(ClickEvent event) {
                com.google.gwt.user.client.Window.open("http://www.broadleafcommerce.org", "sgwt", null);
            }
        });
        BLCMain.MASTERVIEW.getTopBar().addMember(sgwtHomeButton, 1);  
	}

}
