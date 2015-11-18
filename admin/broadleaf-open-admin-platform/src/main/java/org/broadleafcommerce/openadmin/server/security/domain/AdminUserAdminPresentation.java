/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.security.domain;

import org.broadleafcommerce.common.presentation.AdminGroupPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminTabPresentation;

@AdminPresentationClass(friendlyName = "AdminUserImpl_baseAdminUser",
    tabs = {
        @AdminTabPresentation(name = AdminUserAdminPresentation.TabName.General,
            order = AdminUserAdminPresentation.TabOrder.General,
            groups = {
                @AdminGroupPresentation(name = AdminUserAdminPresentation.GroupName.User,
                    order = AdminUserAdminPresentation.GroupOrder.User),
                @AdminGroupPresentation(name = AdminUserAdminPresentation.GroupName.AdditionalFields,
                    order = AdminUserAdminPresentation.GroupOrder.AdditionalFields),
                @AdminGroupPresentation(name = AdminUserAdminPresentation.GroupName.RolesAndPermissions,
                    order = AdminUserAdminPresentation.GroupOrder.RolesAndPermissions,
                    column = 1),
                @AdminGroupPresentation(name = AdminUserAdminPresentation.GroupName.Miscellaneous,
                    order = AdminUserAdminPresentation.GroupOrder.Miscellaneous,
                    column = 1, untitled = true)
            }
        )
    }
)
public interface AdminUserAdminPresentation {

    public static class TabName {
        public static final String General = "General_Tab";
    }

    public static class TabOrder {
        public static final int General = 1000;
    }

    public static class GroupName {
        public static final String User = "AdminUserImpl_User";
        public static final String AdditionalFields = "AdminUserImpl_AdditionalFields";

        public static final String RolesAndPermissions = "AdminUserImpl_RolesAndPermissions";
        public static final String Miscellaneous = "AdminUserImpl_Miscellaneous";
    }

    public static class GroupOrder {
        public static final int User = 1000;
        public static final int AdditionalFields = 2000;

        public static final int RolesAndPermissions = 1000;
        public static final int Miscellaneous = 2000;
    }

    public static class FieldOrder {
        public static final int NAME = 1000;
        public static final int LOGIN = 2000;
        public static final int EMAIL = 3000;
        public static final int PHONE_NUMBER = 4000;
        public static final int PASSWORD = 5000;

        public static final int ACTIVE_STATUS_FLAG = 1000;

        public static final int ROLES = 1000;
        public static final int PERMISSIONS = 2000;
    }

}
