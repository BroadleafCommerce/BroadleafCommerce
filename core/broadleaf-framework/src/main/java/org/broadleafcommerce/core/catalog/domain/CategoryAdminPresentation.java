/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
        public static final String General = "CategoryImpl_General_Tab";
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
        public static final String General = "CategoryImpl_General";
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
}
