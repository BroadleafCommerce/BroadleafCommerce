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
package org.broadleafcommerce.security.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.security.dao.AdminPermissionDao;
import org.broadleafcommerce.security.dao.AdminRoleDao;
import org.broadleafcommerce.security.dao.AdminUserDao;
import org.broadleafcommerce.security.domain.AdminPermission;
import org.broadleafcommerce.security.domain.AdminRole;
import org.broadleafcommerce.security.domain.AdminUser;
import org.springframework.stereotype.Service;

@Service("blAdminSecurityService")
public class AdminSecurityServiceImpl implements AdminSecurityService {

    @Resource
    AdminRoleDao adminRoleDao;

    @Resource
    AdminUserDao adminUserDao;

    @Resource
    AdminPermissionDao adminPermissionDao;

    public void deleteAdminPermission(AdminPermission permission) {
        adminPermissionDao.deleteAdminPermission(permission);
    }

    public void deleteAdminRole(AdminRole role) {
        adminRoleDao.deleteAdminRole(role);
    }

    public void deleteAdminUser(AdminUser user) {
        adminUserDao.deleteAdminUser(user);
    }

    public AdminPermission readAdminPermissionById(Long id) {
        return adminPermissionDao.readAdminPermissionById(id);
    }

    public AdminRole readAdminRoleById(Long id) {
        return adminRoleDao.readAdminRoleById(id);
    }

    public AdminUser readAdminUserById(Long id) {
        return adminUserDao.readAdminUserById(id);
    }

    public AdminPermission saveAdminPermission(AdminPermission permission) {
        return adminPermissionDao.saveAdminPermission(permission);
    }

    public AdminRole saveAdminRole(AdminRole role) {
        return adminRoleDao.saveAdminRole(role);
    }

    public AdminUser saveAdminUser(AdminUser user) {
        return adminUserDao.saveAdminUser(user);
    }

    public AdminUser readAdminUserByUserName(String userName) {
        return adminUserDao.readAdminUserByUserName(userName);
    }

    public List<AdminUser> readAllAdminUsers() {
        return adminUserDao.readAllAdminUsers();
    }

    public List<AdminRole> readAllAdminRoles() {
        return adminRoleDao.readAllAdminRoles();
    }

    public List<AdminPermission> readAllAdminPermissions() {
        return adminPermissionDao.readAllAdminPermissions();
    }
}
