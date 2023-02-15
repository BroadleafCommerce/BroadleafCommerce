/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.security.dao;

import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.service.type.PermissionType;

import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public interface AdminPermissionDao {

    public List<AdminPermission> readAllAdminPermissions();
    public AdminPermission readAdminPermissionById(Long id);
    public AdminPermission readAdminPermissionByName(String name);
    public AdminPermission saveAdminPermission(AdminPermission permission);
    public void deleteAdminPermission(AdminPermission permission);
    public boolean isUserQualifiedForOperationOnCeilingEntity(AdminUser adminUser, PermissionType permissionType, String ceilingEntityFullyQualifiedName);
    public boolean isUserQualifiedForOperationOnCeilingEntityViaDefaultPermissions(String ceilingEntityFullyQualifiedName);
    public boolean doesOperationExistForCeilingEntity(PermissionType permissionType, String ceilingEntityFullyQualifiedName);
    public AdminPermission readAdminPermissionByNameAndType(String name, String type);

}
