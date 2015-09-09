/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.common.presentation.AdminGroupPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminTabPresentation;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;

/**
 * Created by brandon on 9/3/15.
 */
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "PageImpl_basePage",
    tabs = {
        @AdminTabPresentation(name = PageAdminPresentation.TabName.General,
            order = PageAdminPresentation.TabOrder.General,
            groups = {
                @AdminGroupPresentation(name = PageAdminPresentation.GroupName.Basic,
                    order = PageAdminPresentation.GroupOrder.Basic),
                @AdminGroupPresentation(name = PageAdminPresentation.GroupName.Data,
                    order = PageAdminPresentation.GroupOrder.Data),
                @AdminGroupPresentation(name = PageAdminPresentation.GroupName.Misc,
                    order = PageAdminPresentation.GroupOrder.Misc,
                    column = 1)
            }
        )
    }
)
public interface PageAdminPresentation {
    public static class TabName {
        public static final String General = "PageImpl_General_Tab";
    }

    public static class TabOrder {
        public static final int General = 1000;
    }

    public static class GroupName {
        public static final String Basic = "PageImpl_Basic";
        public static final String Data = "PageImpl_Data";
        public static final String Misc = "PageImpl_Misc";
    }

    public static class GroupOrder {
        public static final int Basic = 1000;
        public static final int Data = 2000;
        public static final int Misc = 3000;
    }
}
