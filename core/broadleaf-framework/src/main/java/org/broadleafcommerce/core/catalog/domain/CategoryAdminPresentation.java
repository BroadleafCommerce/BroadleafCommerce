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
                @AdminGroupPresentation(name = CategoryAdminPresentation.GroupName.Description,
                        order = CategoryAdminPresentation.GroupOrder.Description),
                @AdminGroupPresentation(name = CategoryAdminPresentation.GroupName.ActiveDateRange,
                        order = CategoryAdminPresentation.GroupOrder.ActiveDateRange,
                        column = 1),
                @AdminGroupPresentation(name = CategoryAdminPresentation.GroupName.Advanced,
                        order = CategoryAdminPresentation.GroupOrder.Advanced)
            }
        ),
        @AdminTabPresentation(name = CategoryAdminPresentation.TabName.Marketing,
            order = CategoryAdminPresentation.TabOrder.Marketing,
            groups = {
                @AdminGroupPresentation(name = CategoryAdminPresentation.GroupName.General,
                        order = CategoryAdminPresentation.GroupOrder.General)
            }
        ),
        @AdminTabPresentation(name = CategoryAdminPresentation.TabName.Media,
            order = CategoryAdminPresentation.TabOrder.Media),
        @AdminTabPresentation(name = CategoryAdminPresentation.TabName.SearchFacets,
            order = CategoryAdminPresentation.TabOrder.SearchFacets),
        @AdminTabPresentation(name = CategoryAdminPresentation.TabName.Advanced,
            order = CategoryAdminPresentation.TabOrder.Advanced),
        @AdminTabPresentation(name = CategoryAdminPresentation.TabName.Products,
            order = CategoryAdminPresentation.TabOrder.Products)
    }
)
public interface CategoryAdminPresentation {

    public static class TabName {
        public static final String General = "CategoryImpl_General_Tab";
        public static final String Marketing = "CategoryImpl_Marketing_Tab";
        public static final String Media = "CategoryImpl_Media_Tab";
        public static final String SearchFacets = "CategoryImpl_Search_Facets_Tab";
        public static final String Advanced = "CategoryImpl_Advanced_Tab";
        public static final String Products = "CategoryImpl_Products_Tab";
    }

    public static class TabOrder {
        public static final int General = 1000;
        public static final int Marketing = 2000;
        public static final int Media = 3000;
        public static final int SearchFacets = 4000;
        public static final int Advanced = 5000;
        public static final int Products = 6000;
    }

    public static class GroupName {
        public static final String Description = "CategoryImpl_Category_Description";
        public static final String ActiveDateRange = "CategoryImpl_Active_Date_Range";
        public static final String Advanced = "CategoryImpl_Advanced";
        public static final String General = "CategoryImpl_General";
    }

    public static class GroupOrder {
        public static final int Description = 1000;
        public static final int ActiveDateRange = 2000;
        public static final int Advanced = 3000;
        public static final int General = 4000;
    }
}
