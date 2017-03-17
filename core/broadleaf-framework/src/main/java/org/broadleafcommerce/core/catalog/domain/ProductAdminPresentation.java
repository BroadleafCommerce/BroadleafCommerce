/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
                    untitled = true),
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.Image,
                    order = ProductAdminPresentation.GroupOrder.Image,
                    column = 1),
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.ActiveDateRange,
                    order = ProductAdminPresentation.GroupOrder.ActiveDateRange,
                    column = 1),
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.Financial,
                    order = ProductAdminPresentation.GroupOrder.Financial,
                    column = 1),
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.Miscellaneous,
                    order = ProductAdminPresentation.GroupOrder.Miscellaneous,
                    column = 1, collapsed = true)
            }
        ),
        @AdminTabPresentation(name = ProductAdminPresentation.TabName.Pricing,
            order = ProductAdminPresentation.TabOrder.Pricing,
            groups = {
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.Price,
                    order = ProductAdminPresentation.GroupOrder.Price),
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.Discountable,
                    order = ProductAdminPresentation.GroupOrder.Discountable,
                    untitled = true, column = 1)
            }
        ),
        @AdminTabPresentation(name = ProductAdminPresentation.TabName.Marketing,
            order = ProductAdminPresentation.TabOrder.Marketing,
            groups = {
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.Marketing,
                    order = ProductAdminPresentation.GroupOrder.Marketing,
                    untitled = true)
            }
        ),
        @AdminTabPresentation(name = ProductAdminPresentation.TabName.Media,
            order = ProductAdminPresentation.TabOrder.Media
        ),
        @AdminTabPresentation(name = ProductAdminPresentation.TabName.ProductOptions,
            order = ProductAdminPresentation.TabOrder.ProductOptions,
            groups = {
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.ProductOptionsAdvanced,
                    order = ProductAdminPresentation.GroupOrder.ProductOptionsAdvanced,
                    untitled = true, column = 1)
            }
        ),
        @AdminTabPresentation(name = ProductAdminPresentation.TabName.Inventory,
            order = ProductAdminPresentation.TabOrder.Inventory,
            groups = {
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.Inventory,
                    order = ProductAdminPresentation.GroupOrder.Inventory,
                    untitled = true)
            }
        ),
        @AdminTabPresentation(name = ProductAdminPresentation.TabName.Shipping,
            order = ProductAdminPresentation.TabOrder.Shipping,
            groups = {
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.ShippingDimensions,
                    order = ProductAdminPresentation.GroupOrder.ShippingDimensions),
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.ShippingOther,
                    order = ProductAdminPresentation.GroupOrder.ShippingOther,
                    untitled = true),
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.ShippingFulfillment,
                    order = ProductAdminPresentation.GroupOrder.ShippingFulfillment,
                    untitled = true, column = 1),
                @AdminGroupPresentation(name = ProductAdminPresentation.GroupName.ShippingContainer,
                    order = ProductAdminPresentation.GroupOrder.ShippingContainer,
                    untitled = true, column = 1)
            }
        )
    }
)

public interface ProductAdminPresentation {

    public static class TabName {

        public static final String General = "General";
        public static final String Pricing = "ProductImpl_Pricing_Tab";
        public static final String Marketing = "ProductImpl_Marketing_Tab";
        public static final String Media = "SkuImpl_Media_Tab";
        public static final String ProductOptions = "ProductImpl_Product_Options_Tab";
        public static final String Inventory = "ProductImpl_Inventory_Tab";
        public static final String Shipping = "ProductImpl_Shipping_Tab";

    }

    public static class TabOrder {

        public static final int General = 1000;
        public static final int Pricing = 2000;
        public static final int Marketing = 3000;
        public static final int Media = 4000;
        public static final int ProductOptions = 5000;
        public static final int Inventory = 6000;
        public static final int Shipping = 7000;
    }

    public static class GroupName {

        public static final String General = "General";
        public static final String Image = "ProductImpl_Product_Image";
        public static final String ActiveDateRange = "ProductImpl_Product_Active_Date_Range";
        public static final String Financial = "ProductImpl_Financial";
        public static final String Miscellaneous = "ProductImpl_General_Misc";

        public static final String Marketing = "ProductImpl_Marketing";

        public static final String ProductOptionsAdvanced = "ProductImpl_Options_Advanced";

        public static final String Price = "SkuImpl_Price";
        public static final String Discountable = "SkuImpl_Sku_Discountable";

        public static final String Inventory = "SkuImpl_Sku_Inventory";

        public static final String ShippingDimensions = "SkuImpl_Dimensions_Group";
        public static final String ShippingFulfillment = "SkuImpl_Fulfillment_Group";
        public static final String ShippingContainer = "SkuImpl_Container_Group";
        public static final String ShippingOther = "SkuImpl_Other_Group";

        public static final String Advanced = "ProductImpl_Advanced";
    }

    public static class GroupOrder {

        public static final int General = 1000;
        public static final int Image = 2000;
        public static final int ActiveDateRange = 3000;
        public static final int Financial = 4000;
        public static final int Miscellaneous = 5000;

        public static final int Marketing = 1000;

        public static final int ProductOptionsAdvanced = 1000;

        public static final int Price = 1000;
        public static final int Discountable = 2000;

        public static final int Inventory = 1000;

        public static final int ShippingDimensions = 1000;
        public static final int ShippingOther = 2000;
        public static final int ShippingFulfillment = 3000;
        public static final int ShippingContainer = 4000;

        public static final int Advanced = 1000;
    }

    public static class FieldOrder {

        public static final int NAME = 1000;
        public static final int LONG_DESCRIPTION = 3000;
        public static final int DEFAULT_CATEGORY = 4000;
        public static final int MANUFACTURER = 5000;
        public static final int URL = 6000;

        public static final int ACTIVE_START_DATE = 1000;
        public static final int ACTIVE_END_DATE = 2000;

        public static final int RETAIL_PRICE = 1000;
        public static final int SALE_PRICE = 2000;

        public static final int FULFILLMENT_TYPE = 1000;
    }
}
