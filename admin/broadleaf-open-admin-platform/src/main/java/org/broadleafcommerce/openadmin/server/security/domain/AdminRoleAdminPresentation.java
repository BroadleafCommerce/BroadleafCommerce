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

@AdminPresentationClass(friendlyName = "AdminRoleImpl_baseAdminRole",
    tabs = {
        @AdminTabPresentation(
            groups = {
                @AdminGroupPresentation(name = AdminRoleAdminPresentation.GroupName.Role,
                    order = AdminRoleAdminPresentation.GroupOrder.Role),
                @AdminGroupPresentation(name = AdminRoleAdminPresentation.GroupName.Permissions,
                    order = AdminRoleAdminPresentation.GroupOrder.Permissions,
                    column = 1, borderless = true)
            }
        )
    }
)
public interface AdminRoleAdminPresentation {

    public static class TabName {
    }

    public static class TabOrder {
    }

    public static class GroupName {
        public static final String Role = "AdminRoleImpl_Role";
        public static final String Permissions = "AdminRoleImpl_Permissions";
    }

    public static class GroupOrder {
        public static final int Role = 1000;
        public static final int Permissions = 1000;
    }

}
