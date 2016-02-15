/*
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.core.domain;

import org.broadleafcommerce.common.presentation.AdminGroupPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminTabPresentation;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;

/**
 * Created by ckittrell on 12/9/15.
 */
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE,
    tabs = {
        @AdminTabPresentation(name = CustomerPhoneAdminPresentation.TabName.General,
            order = CustomerPhoneAdminPresentation.TabOrder.General,
            groups = {
                @AdminGroupPresentation(name = CustomerPhoneAdminPresentation.GroupName.PhoneInfo,
                    order = CustomerPhoneAdminPresentation.GroupOrder.PhoneInfo,
                    untitled = true),
                @AdminGroupPresentation(name = CustomerPhoneAdminPresentation.GroupName.Defaults,
                    order = CustomerPhoneAdminPresentation.GroupOrder.Defaults,
                    column = 1, untitled = true)
            }
        )
    }
)
public interface CustomerPhoneAdminPresentation {
    public static class TabName {
        public static final String General = "General";
    }

    public static class TabOrder {
        public static final int General = 1000;
    }

    public static class GroupName {
        public static final String PhoneInfo = "CustomerPhoneImpl_PhoneInfo_Group";
        public static final String Defaults = "CustomerPhoneImpl_Defaults_Group";
    }

    public static class GroupOrder {
        public static final int PhoneInfo = 1000;
        public static final int Defaults = 2000;
    }

    public static class FieldOrder {
        public static final int PHONE_NAME = 1000;
    }
}
