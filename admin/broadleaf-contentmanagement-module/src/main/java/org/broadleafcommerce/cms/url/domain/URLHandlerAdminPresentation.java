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
package org.broadleafcommerce.cms.url.domain;

import org.broadleafcommerce.common.presentation.AdminGroupPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminTabPresentation;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;

/**
 * @author Jon Fleschler (jfleschler)
 */
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "URLHandlerImpl_friendyName",
    tabs = {
        @AdminTabPresentation(name = URLHandlerAdminPresentation.TabName.General,
            order = URLHandlerAdminPresentation.TabOrder.General,
                groups = {
                        @AdminGroupPresentation(name = URLHandlerAdminPresentation.GroupName.General,
                                order = URLHandlerAdminPresentation.GroupOrder.General,
                                borderless = true),
                        @AdminGroupPresentation(name = URLHandlerAdminPresentation.GroupName.Redirect,
                                order = URLHandlerAdminPresentation.GroupOrder.Redirect,
                                column = 1)
                }
        )
    }
)

public interface URLHandlerAdminPresentation {


    public static class TabName {
        public static final String General = "URLHandlerImpl_tab_General";
    }

    public static class TabOrder {
        public static final int General = 1000;
    }

    public static class GroupName {
        public static final String General = "URLHandlerImpl_group_General";
        public static final String Redirect = "URLHandlerImpl_group_Redirect";
    }

    public static class GroupOrder {
        public static final int General = 1000;
        public static final int Redirect = 2000;
    }
}
