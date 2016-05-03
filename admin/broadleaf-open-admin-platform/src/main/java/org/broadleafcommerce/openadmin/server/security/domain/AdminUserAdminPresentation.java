/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
        public static final String General = "General";
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
