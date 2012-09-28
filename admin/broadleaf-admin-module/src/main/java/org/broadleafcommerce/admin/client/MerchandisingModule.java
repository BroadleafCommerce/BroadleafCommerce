/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import org.broadleafcommerce.openadmin.client.GeneratedMessagesEntityCommon;
import org.broadleafcommerce.openadmin.client.GeneratedMessagesEntityFramework;
import org.broadleafcommerce.openadmin.client.GeneratedMessagesEntityOpenAdmin;
import org.broadleafcommerce.openadmin.client.GeneratedMessagesEntityProfile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;

/**
 * 
 * @author jfischer
 *
 */
public class MerchandisingModule extends AbstractModule {
	
	@Override
    public void onModuleLoad() {
        addConstants(GWT.<ConstantsWithLookup>create(MerchandisingMessages.class));
        addConstants(GWT.<ConstantsWithLookup>create(PromotionMessages.class));
        addConstants(GWT.<ConstantsWithLookup>create(GeneratedMessagesEntityCommon.class));
        addConstants(GWT.<ConstantsWithLookup>create(GeneratedMessagesEntityOpenAdmin.class));
        addConstants(GWT.<ConstantsWithLookup>create(GeneratedMessagesEntityProfile.class));
        addConstants(GWT.<ConstantsWithLookup>create(GeneratedMessagesEntityFramework.class));

		setModuleTitle(BLCMain.getMessageManager().getString("merchandisingModuleTitle"));
		setModuleKey("BLCMerchandising");
		
		List<String> categoryPermissions = new ArrayList<String>();
		categoryPermissions.add("PERMISSION_CREATE_CATEGORY");
		categoryPermissions.add("PERMISSION_UPDATE_CATEGORY");
        categoryPermissions.add("PERMISSION_DELETE_CATEGORY");
        categoryPermissions.add("PERMISSION_READ_CATEGORY");
		setSection(
            BLCMain.getMessageManager().getString("categoryMainTitle"),
			"category",
			"org.broadleafcommerce.admin.client.view.catalog.category.CategoryView",
			"categoryPresenter",
			"org.broadleafcommerce.admin.client.presenter.catalog.category.CategoryPresenter",
			categoryPermissions
		);
		List<String> productPermissions = new ArrayList<String>();
		productPermissions.add("PERMISSION_CREATE_PRODUCT");
		productPermissions.add("PERMISSION_UPDATE_PRODUCT");
        productPermissions.add("PERMISSION_DELETE_PRODUCT");
        productPermissions.add("PERMISSION_READ_PRODUCT");
		setSection(
            BLCMain.getMessageManager().getString("productMainTitle"),
			"product",
			"org.broadleafcommerce.admin.client.view.catalog.product.OneToOneProductSkuView",
			"productPresenter",
			"org.broadleafcommerce.admin.client.presenter.catalog.product.OneToOneProductSkuPresenter",
			productPermissions
		);
		
		//TODO: add custom permissions for product options
		List<String> productOptionPermissions = new ArrayList<String>();
        productOptionPermissions.add("PERMISSION_CREATE_PRODUCT");
        productOptionPermissions.add("PERMISSION_UPDATE_PRODUCT");
        productOptionPermissions.add("PERMISSION_DELETE_PRODUCT");
        productOptionPermissions.add("PERMISSION_READ_PRODUCT");
        setSection(
            BLCMain.getMessageManager().getString("productOptionMainTitle"),
            "productOption",
            "org.broadleafcommerce.admin.client.view.catalog.product.ProductOptionView",
            "productOptionPresenter",
            "org.broadleafcommerce.admin.client.presenter.catalog.product.ProductOptionPresenter",
            productOptionPermissions
        );

        List<String> offerPermissions = new ArrayList<String>();
		offerPermissions.add("PERMISSION_CREATE_PROMOTION");
		offerPermissions.add("PERMISSION_UPDATE_PROMOTION");
        offerPermissions.add("PERMISSION_DELETE_PROMOTION");
        offerPermissions.add("PERMISSION_READ_PROMOTION");
		setSection(
            BLCMain.getMessageManager().getString("promotionMainTitle"),
			"offer",
			"org.broadleafcommerce.admin.client.view.promotion.OfferView",
			"offerPresenter",
			"org.broadleafcommerce.admin.client.presenter.promotion.OfferPresenter",
			offerPermissions
		);
	        List<String> priceListPermissions = new ArrayList<String>();
                priceListPermissions.add("PERMISSION_CREATE_PRICELIST");
                priceListPermissions.add("PERMISSION_UPDATE_PRICELIST");
                priceListPermissions.add("PERMISSION_DELETE_PRICELIST");
                priceListPermissions.add("PERMISSION_READ_PRICELIST");
                setSection(
            BLCMain.getMessageManager().getString("priceListMainTitle"),
                        "priceListView",
                        "org.broadleafcommerce.admin.client.view.pricelist.PriceListView",
                        "priceListPresenter",
                        "org.broadleafcommerce.admin.client.presenter.pricelist.PriceListPresenter",
                        priceListPermissions
                );
        setOrder(50);

		registerModule();
	}

}
