/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.admin.client.modules.catalog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;
import org.broadleafcommerce.admin.client.MerchandisingMessages;
import org.broadleafcommerce.admin.client.PromotionMessages;
import org.broadleafcommerce.openadmin.client.*;

import java.util.ArrayList;
import java.util.List;

public class ProductOptionModule extends AbstractModule {

    public void onModuleLoad() {
        addConstants(GWT.<ConstantsWithLookup>create(MerchandisingMessages.class));
        addConstants(GWT.<ConstantsWithLookup>create(PromotionMessages.class));
        addConstants(GWT.<ConstantsWithLookup>create(GeneratedMessagesEntityCommon.class));
        addConstants(GWT.<ConstantsWithLookup>create(GeneratedMessagesEntityOpenAdmin.class));
        addConstants(GWT.<ConstantsWithLookup>create(GeneratedMessagesEntityProfile.class));
        addConstants(GWT.<ConstantsWithLookup>create(GeneratedMessagesEntityFramework.class));

        setModuleTitle(BLCMain.getMessageManager().getString("merchandisingModuleTitle"));
        setModuleKey("BLCProductOptionModule");

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

        setOrder(50);

        registerModule();
    }
}
