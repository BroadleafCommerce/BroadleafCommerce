/*
 * #%L
 * BroadleafCommerce Integration
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
package org.broadleafcommerce.security.service;

import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.broadleafcommerce.security.service.dataprovider.AdminRoleDataProvider;
import org.broadleafcommerce.test.TestNGAdminIntegrationSetup;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

import javax.annotation.Resource;

public class AdminRoleTest extends TestNGAdminIntegrationSetup {
    @Resource
    AdminSecurityService adminSecurityService;

    @Test(groups =  {"testAdminRoleSave"}, dataProvider = "setupAdminRole", dataProviderClass = AdminRoleDataProvider.class)
    @Rollback(true)
    public void testAdminRoleSave(AdminRole role) throws Exception {
        AdminRole newRole = adminSecurityService.saveAdminRole(role);

        AdminRole roleFromDB = adminSecurityService.readAdminRoleById(newRole.getId());

        assert(roleFromDB != null);
    }

}
