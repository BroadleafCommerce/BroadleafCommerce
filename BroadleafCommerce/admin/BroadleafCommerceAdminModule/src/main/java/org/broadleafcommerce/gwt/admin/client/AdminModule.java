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
package org.broadleafcommerce.gwt.admin.client;

import java.util.ArrayList;
import java.util.List;

import org.broadleafcommerce.gwt.client.AbstractModule;
import org.broadleafcommerce.gwt.client.BLCMain;
import org.broadleafcommerce.gwt.client.validation.ValidationFactoryManager;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;

/**
 * 
 * @author jfischer
 *
 */
public class AdminModule extends AbstractModule {
	
	public static final AdminMessages ADMINMESSAGES = GWT.create(AdminMessages.class);
	
	public void onModuleLoad() {
		ValidationFactoryManager.getInstance().getConstants().add(AdminModule.ADMINMESSAGES);
		
		setModuleTitle(AdminModule.ADMINMESSAGES.adminModuleTitle());
		setModuleKey("BLCAdmin");
		
		List<String> categoryRoles = new ArrayList<String>();
		categoryRoles.add("ROLE_ADMIN");
		categoryRoles.add("ROLE_MERCHANDISE_MANAGER");
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
		productRoles.add("ROLE_MERCHANDISE_MANAGER");
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
		orderRoles.add("ROLE_CUSTOMER_SERVICE_REP");
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
		customerRoles.add("ROLE_CUSTOMER_SERVICE_REP");
		setSection(
			AdminModule.ADMINMESSAGES.customerMainTitle(),
			"customer",
			"org.broadleafcommerce.gwt.admin.client.view.customer.CustomerView",
			"customerPresenter",
			"org.broadleafcommerce.gwt.admin.client.presenter.customer.CustomerPresenter",
			customerRoles,
			null
		);
		
		List<String> offerRoles = new ArrayList<String>();
		offerRoles.add("ROLE_ADMIN");
		offerRoles.add("ROLE_PROMOTION_MANAGER");
		setSection(
			AdminModule.ADMINMESSAGES.promotionMainTitle(),
			"offer",
			"org.broadleafcommerce.gwt.admin.client.view.promotion.OfferView",
			"offerPresenter",
			"org.broadleafcommerce.gwt.admin.client.presenter.promotion.OfferPresenter",
			offerRoles,
			null
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
