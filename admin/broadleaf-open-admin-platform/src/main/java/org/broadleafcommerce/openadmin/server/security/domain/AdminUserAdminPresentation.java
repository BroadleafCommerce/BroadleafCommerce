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
                @AdminGroupPresentation(name = AdminUserAdminPresentation.GroupName.PermissionsAndRoles,
                    order = AdminUserAdminPresentation.GroupOrder.PermissionsAndRoles,
                    column = 1)
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
        public static final String PermissionsAndRoles = "AdminUserImpl_PermissionsAndRoles";
    }

    public static class GroupOrder {
        public static final int User = 1000;
        public static final int PermissionsAndRoles = 2000;
    }

}
