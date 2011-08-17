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

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import org.broadleafcommerce.openadmin.client.AbstractModule;
import org.broadleafcommerce.openadmin.client.BLCMain;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public class MerchandisingModule extends AbstractModule {
	
	public void onModuleLoad() {
        addConstants(GWT.<ConstantsWithLookup>create(MerchandisingMessages.class));
        addConstants(GWT.<ConstantsWithLookup>create(PromotionMessages.class));
		
		setModuleTitle(BLCMain.getMessageManager().getString("merchandisingModuleTitle"));
		setModuleKey("BLCMerchandising");
		
		List<String> categoryRoles = new ArrayList<String>();
		categoryRoles.add("ROLE_ADMIN");
		categoryRoles.add("ROLE_MERCHANDISE_MANAGER");
		setSection(
            BLCMain.getMessageManager().getString("categoryMainTitle"),
			"category",
			"org.broadleafcommerce.admin.client.view.catalog.category.CategoryView",
			"categoryPresenter",
			"org.broadleafcommerce.admin.client.presenter.catalog.category.CategoryPresenter",
			categoryRoles,
			null
		);
		List<String> productRoles = new ArrayList<String>();
		productRoles.add("ROLE_ADMIN");
		productRoles.add("ROLE_MERCHANDISE_MANAGER");
		setSection(
            BLCMain.getMessageManager().getString("productMainTitle"),
			"product",
			"org.broadleafcommerce.admin.client.view.catalog.product.OneToOneProductSkuView",
			"productPresenter",
			"org.broadleafcommerce.admin.client.presenter.catalog.product.OneToOneProductSkuPresenter",
			productRoles,
			null
		);

		List<String> offerRoles = new ArrayList<String>();
		offerRoles.add("ROLE_ADMIN");
		offerRoles.add("ROLE_PROMOTION_MANAGER");
		setSection(
            BLCMain.getMessageManager().getString("promotionMainTitle"),
			"offer",
			"org.broadleafcommerce.admin.client.view.promotion.OfferView",
			"offerPresenter",
			"org.broadleafcommerce.admin.client.presenter.promotion.OfferPresenter",
			offerRoles,
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
        sgwtHomeButton.setPrompt(BLCMain.getMessageManager().getString("blcProjectPage"));
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
