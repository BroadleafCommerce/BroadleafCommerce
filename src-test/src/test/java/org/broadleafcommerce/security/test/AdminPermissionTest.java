/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.security.test;

import javax.annotation.Resource;

import org.broadleafcommerce.security.domain.AdminPermission;
import org.broadleafcommerce.security.service.AdminSecurityService;
import org.broadleafcommerce.security.test.dataprovider.AdminPermissionDataProvider;
import org.broadleafcommerce.test.integration.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class AdminPermissionTest extends BaseTest {
    @Resource
    AdminSecurityService adminSecurityService;

    @Test(groups =  {"testAdminPermissionSave"}, dataProvider = "setupAdminPermission", dataProviderClass = AdminPermissionDataProvider.class)
    @Rollback(true)
    public void testAdminPermissionSave(AdminPermission permission) throws Exception {
        AdminPermission newPermission = adminSecurityService.saveAdminPermission(permission);

        AdminPermission permissionFromDB = adminSecurityService.readAdminPermissionById(newPermission.getId());

        assert(permissionFromDB != null);
    }

}
