/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.security.service.dataprovider;

import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.broadleafcommerce.openadmin.server.security.domain.AdminPermissionImpl;
import org.broadleafcommerce.openadmin.server.security.service.type.PermissionType;
import org.testng.annotations.DataProvider;

public class AdminPermissionDataProvider {
    @DataProvider(name = "setupAdminPermission")
    public static Object[][] createAdminUser() {
        AdminPermission adminPermission = new AdminPermissionImpl();
        adminPermission.setName("TestAdminPermissionName");
        adminPermission.setDescription("Test Admin Permission Description");
        adminPermission.setType(PermissionType.ALL);

        return new Object[][] { new Object[] { adminPermission } };
    }
}
