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
 * Created by brandon on 8/28/15.
 */
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "CategoryImpl_baseCategory",
    tabs = {
        @AdminTabPresentation(name = CategoryAdminPresentation.TabName.General,
            order = CategoryAdminPresentation.TabOrder.General,
            groups = {
                @AdminGroupPresentation(name = CategoryAdminPresentation.GroupName.General,
                    order = CategoryAdminPresentation.GroupOrder.General),
                @AdminGroupPresentation(name = CategoryAdminPresentation.GroupName.ActiveDateRange,
                    order = CategoryAdminPresentation.GroupOrder.ActiveDateRange,
                    column = 1),
                @AdminGroupPresentation(name = CategoryAdminPresentation.GroupName.Miscellaneous,
                    order = CategoryAdminPresentation.GroupOrder.Miscellaneous,
                    column = 1)
            }
        ),
        @AdminTabPresentation(name = CategoryAdminPresentation.TabName.Marketing,
            order = CategoryAdminPresentation.TabOrder.Marketing
        ),
        @AdminTabPresentation(name = CategoryAdminPresentation.TabName.Media,
            order = CategoryAdminPresentation.TabOrder.Media),
        @AdminTabPresentation(name = CategoryAdminPresentation.TabName.Search,
            order = CategoryAdminPresentation.TabOrder.Search),
        @AdminTabPresentation(name = CategoryAdminPresentation.TabName.Subcategories,
            order = CategoryAdminPresentation.TabOrder.Subcategories
        ),
        @AdminTabPresentation(name = CategoryAdminPresentation.TabName.Products,
            order = CategoryAdminPresentation.TabOrder.Products,
            groups = {
                @AdminGroupPresentation(name = CategoryAdminPresentation.GroupName.ProductDefaults,
                    order = CategoryAdminPresentation.GroupOrder.ProductDefaults,
                    column = 1)
            }
        )
    }
)
public interface CategoryAdminPresentation {

    public static class TabName {
        public static final String General = "General";
        public static final String Marketing = "CategoryImpl_Marketing_Tab";
        public static final String Media = "CategoryImpl_Media_Tab";
        public static final String Search = "CategoryImpl_Search_Tab";
        public static final String Subcategories = "CategoryImpl_Subcategories_Tab";
        public static final String Products = "CategoryImpl_Products_Tab";
    }

    public static class TabOrder {
        public static final int General = 1000;
        public static final int Marketing = 2000;
        public static final int Media = 3000;
        public static final int Search = 4000;
        public static final int Subcategories = 5000;
        public static final int Products = 6000;
    }

    public static class GroupName {
        public static final String General = "General";
        public static final String ActiveDateRange = "CategoryImpl_Active_Date_Range";
        public static final String Miscellaneous = "CategoryImpl_Category_Miscellaneous";
        public static final String ProductDefaults = "CategoryImpl_ProductDefaults";
    }

    public static class GroupOrder {
        public static final int General = 1000;
        public static final int ActiveDateRange = 1000;
        public static final int Miscellaneous = 2000;
        public static final int ProductDefaults = 1000;
    }

    public static class FieldOrder {
        public static final int CustomAttributes = 9000;
    }
}
