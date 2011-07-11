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
package org.broadleafcommerce.admin.client;

import java.util.ArrayList;
import java.util.List;

import org.broadleafcommerce.openadmin.client.AbstractModule;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.validation.ValidationFactoryManager;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;

/**
 * 
 * @author jfischer
 *
 */
public class CustomerCareModule extends AbstractModule {
	
	public static final CustomerCareMessages ADMINMESSAGES = GWT.create(CustomerCareMessages.class);
	
	public void onModuleLoad() {
		ValidationFactoryManager.getInstance().getConstants().add(CustomerCareModule.ADMINMESSAGES);
		
		setModuleTitle(CustomerCareModule.ADMINMESSAGES.adminModuleTitle());
		setModuleKey("BLCCustomerCare");
		
		List<String> orderRoles = new ArrayList<String>();
		orderRoles.add("ROLE_ADMIN");
		orderRoles.add("ROLE_CUSTOMER_SERVICE_REP");
		setSection(
			CustomerCareModule.ADMINMESSAGES.orderMainTitle(),
			"order",
			"org.broadleafcommerce.admin.client.view.order.OrderView",
			"orderPresenter",
			"org.broadleafcommerce.admin.client.presenter.order.OrderPresenter",
			orderRoles,
			null
		);
		
		List<String> customerRoles = new ArrayList<String>();
		customerRoles.add("ROLE_ADMIN");
		customerRoles.add("ROLE_CUSTOMER_SERVICE_REP");
		setSection(
			CustomerCareModule.ADMINMESSAGES.customerMainTitle(),
			"customer",
			"org.broadleafcommerce.admin.client.view.customer.CustomerView",
			"customerPresenter",
			"org.broadleafcommerce.admin.client.presenter.customer.CustomerPresenter",
			customerRoles,
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
