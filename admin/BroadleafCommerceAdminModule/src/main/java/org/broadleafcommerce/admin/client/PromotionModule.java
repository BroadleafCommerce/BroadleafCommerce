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
public class PromotionModule extends AbstractModule {
	
	public static final PromotionMessages ADMINMESSAGES = GWT.create(PromotionMessages.class);
	
	public void onModuleLoad() {
		ValidationFactoryManager.getInstance().getConstants().add(PromotionModule.ADMINMESSAGES);
		
		setModuleTitle(PromotionModule.ADMINMESSAGES.adminModuleTitle());
		setModuleKey("BLCPromotion");
		
		List<String> offerRoles = new ArrayList<String>();
		offerRoles.add("ROLE_ADMIN");
		offerRoles.add("ROLE_PROMOTION_MANAGER");
		setSection(
			PromotionModule.ADMINMESSAGES.promotionMainTitle(),
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
