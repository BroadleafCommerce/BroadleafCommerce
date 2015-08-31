/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.presentation.AdminGroupPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminTabPresentation;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;

/**
 * @author Jon Fleschler (jfleschler)
 */
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "baseProduct",
    tabs = {
        @AdminTabPresentation(name = ProductAdminPresentation.TabName.General,
            order = ProductAdminPresentation.TabOrder.General,
            groups = {
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.General,
                    order = ProductAdminPresentation.GroupOrder.General,
                    borderless = true),
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.Image,
                    order = ProductAdminPresentation.GroupOrder.Image,
                    column = 1),
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.Price,
                    order = ProductAdminPresentation.GroupOrder.Price),
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.ActiveDateRange,
                    order = ProductAdminPresentation.GroupOrder.ActiveDateRange,
                    column = 1),
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.Financial,
                    order = ProductAdminPresentation.GroupOrder.Financial)
            }
        ),
        @AdminTabPresentation(name = ProductAdminPresentation.TabName.Marketing,
            order = ProductAdminPresentation.TabOrder.Marketing,
            groups = {
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.Badges,
                    order = ProductAdminPresentation.GroupOrder.Badges)
            }
        ),
        @AdminTabPresentation(name = ProductAdminPresentation.TabName.Media,
            order = ProductAdminPresentation.TabOrder.Media
        ),
        @AdminTabPresentation(name = ProductAdminPresentation.TabName.ProductOptions,
            order = ProductAdminPresentation.TabOrder.ProductOptions
        ),
        @AdminTabPresentation(name = ProductAdminPresentation.TabName.Shipping,
            order = ProductAdminPresentation.TabOrder.Shipping,
            groups = {
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.Shipping,
                    order = ProductAdminPresentation.GroupOrder.Shipping)
            }
        ),
        @AdminTabPresentation(name = ProductAdminPresentation.TabName.Advanced,
            order = ProductAdminPresentation.TabOrder.Advanced,
            groups = {
                    @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.Advanced,
                            order = ProductAdminPresentation.GroupOrder.Advanced)
            }
        )
    }
)

public interface ProductAdminPresentation {

    public static class TabName {

        public static final String General = "ProductImpl_General_Tab";
        public static final String Marketing = "ProductImpl_Marketing_Tab";
        public static final String Media = "SkuImpl_Media_Tab";
        public static final String ProductOptions = "ProductImpl_Product_Options_Tab";
        public static final String Inventory = "ProductImpl_Inventory_Tab";
        public static final String Shipping = "ProductImpl_Shipping_Tab";
        public static final String Advanced = "ProductImpl_Advanced_Tab";

    }

    public static class TabOrder {

        public static final int General = 1000;
        public static final int Marketing = 2000;
        public static final int Media = 3000;
        public static final int ProductOptions = 4000;
        public static final int Inventory = 5000;
        public static final int Shipping = 6000;
        public static final int Advanced = 7000;
    }

    public static class GroupName {

        public static final String General = "ProductImpl_Product_Description";
        public static final String Image = "ProductImpl_Product_Image";
        public static final String Price = "SkuImpl_Price";
        public static final String ActiveDateRange = "ProductImpl_Product_Active_Date_Range";
        public static final String Advanced = "ProductImpl_Advanced";
        public static final String Inventory = "SkuImpl_Sku_Inventory";
        public static final String Badges = "ProductImpl_Badges";
        public static final String Shipping = "ProductWeight_Shipping";
        public static final String Financial = "ProductImpl_Financial";
    }

    public static class GroupOrder {

        public static final int General = 1000;
        public static final int Image = 1000;
        public static final int Price = 2000;
        public static final int ActiveDateRange = 3000;
        public static final int Advanced = 1000;
        public static final int Inventory = 1000;
        public static final int Badges = 1000;
        public static final int Shipping = 1000;
        public static final int Financial = 4000;
    }

    public static class FieldOrder {

        public static final int NAME = 1000;
        public static final int SHORT_DESCRIPTION = 2000;
        public static final int PRIMARY_MEDIA = 3000;
        public static final int LONG_DESCRIPTION = 4000;
        public static final int DEFAULT_CATEGORY = 5000;
        public static final int MANUFACTURER = 6000;
        public static final int URL = 7000;
    }
}
