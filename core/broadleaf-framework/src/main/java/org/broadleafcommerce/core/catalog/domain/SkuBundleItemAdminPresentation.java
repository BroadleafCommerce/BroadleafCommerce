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
 * @author Chris Kittrell (ckittrell)
 */
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.FALSE,
    tabs = {
        @AdminTabPresentation(name = SkuBundleItemAdminPresentation.TabName.General,
            order = SkuBundleItemAdminPresentation.TabOrder.General,
            groups = {
                @AdminGroupPresentation(name = SkuBundleItemAdminPresentation.GroupName.General,
                    order = SkuBundleItemAdminPresentation.GroupOrder.General,
                    untitled = true)
            }
        )
    }
)

public interface SkuBundleItemAdminPresentation {

    public static class TabName {

        public static final String General = "General";

    }

    public static class TabOrder {

        public static final int General = 1000;
    }

    public static class GroupName {

        public static final String General = "General";
    }

    public static class GroupOrder {

        public static final int General = 1000;
    }

    public static class FieldOrder {

        public static final int SKU = 1000;
        public static final int QUANTITY = 2000;
        public static final int ITEM_SALE_PRICE = 3000;
    }
}
