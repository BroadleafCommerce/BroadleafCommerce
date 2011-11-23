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

package org.broadleafcommerce.openadmin.server.security.service;

import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.service.type.PermissionType;
import org.broadleafcommerce.openadmin.server.security.util.PasswordChange;

import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public interface AdminSecurityService {

    public List<AdminUser> readAllAdminUsers();
    public AdminUser readAdminUserById(Long id);
    public AdminUser readAdminUserByUserName(String userName);
    public AdminUser saveAdminUser(AdminUser user);
    public void deleteAdminUser(AdminUser user);

    public List<AdminRole> readAllAdminRoles();
    public AdminRole readAdminRoleById(Long id);
    public AdminRole saveAdminRole(AdminRole role);
    public void deleteAdminRole(AdminRole role);

    public List<AdminPermission> readAllAdminPermissions();
    public AdminPermission readAdminPermissionById(Long id);
    public AdminPermission saveAdminPermission(AdminPermission permission);
    public void deleteAdminPermission(AdminPermission permission);
    public AdminUser changePassword(PasswordChange passwordChange);

    public boolean isUserQualifiedForOperationOnCeilingEntity(AdminUser adminUser, PermissionType permissionType, String ceilingEntityFullyQualifiedName);
    public boolean doesOperationExistForCeilingEntity(PermissionType permissionType, String ceilingEntityFullyQualifiedName);

}
