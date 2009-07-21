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

    @Override
    public void deleteAdminPermission(AdminPermission permission) {
        adminPermissionDao.deleteAdminPermission(permission);
    }

    @Override
    public void deleteAdminRole(AdminRole role) {
        adminRoleDao.deleteAdminRole(role);
    }

    @Override
    public void deleteAdminUser(AdminUser user) {
        adminUserDao.deleteAdminUser(user);
    }

    @Override
    public AdminPermission readAdminPermissionById(Long id) {
        return adminPermissionDao.readAdminPermissionById(id);
    }

    @Override
    public AdminRole readAdminRoleById(Long id) {
        return adminRoleDao.readAdminRoleById(id);
    }

    @Override
    public AdminUser readAdminUserById(Long id) {
        return adminUserDao.readAdminUserById(id);
    }

    @Override
    public AdminPermission saveAdminPermission(AdminPermission permission) {
        return adminPermissionDao.saveAdminPermission(permission);
    }

    @Override
    public AdminRole saveAdminRole(AdminRole role) {
        return adminRoleDao.saveAdminRole(role);
    }

    @Override
    public AdminUser saveAdminUser(AdminUser user) {
        return adminUserDao.saveAdminUser(user);
    }

    @Override
    public AdminUser readAdminUserByUserName(String userName) {
        return adminUserDao.readAdminUserByUserName(userName);
    }
}
