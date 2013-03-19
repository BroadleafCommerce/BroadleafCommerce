/*
 * Copyright 2008-2009 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.server.security.service;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.openadmin.server.security.dao.AdminNavigationDao;
import org.broadleafcommerce.openadmin.server.security.domain.AdminModule;
import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This service is used to build the left hand navigation for the admin
 * @author elbertbautista
 */
@Service("blAdminNavigationService")
public class AdminNavigationServiceImpl implements AdminNavigationService {

    private static final Log LOG = LogFactory.getLog(AdminNavigationServiceImpl.class);
    private static final String PATTERN = "_";

    @Resource(name = "blAdminNavigationDao")
    protected AdminNavigationDao adminNavigationDao;

    @Override
    public List<AdminModule> buildMenu(AdminUser adminUser) {
        List<AdminModule> modules = adminNavigationDao.readAllAdminModules();
        List<AdminModule> filtered = new ArrayList<AdminModule>();

        for (AdminModule module : modules) {
            if (isUserAuthorizedToViewModule(adminUser, module)){
                filtered.add(module);
            }
        }

        BeanComparator displayComparator = new BeanComparator("displayOrder");
        Collections.sort(filtered, displayComparator);

        return filtered;
    }

    @Override
    public boolean isUserAuthorizedToViewModule(AdminUser adminUser, AdminModule module) {
        List<AdminSection> moduleSections = module.getSections();
        if (moduleSections != null && !moduleSections.isEmpty()) {
            for (AdminSection section : moduleSections) {
                if (isUserAuthorizedToViewSection(adminUser, section)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public AdminSection findAdminSectionByURI(String uri) {
        return adminNavigationDao.readAdminSectionByURI(uri);
    }
    
    @Override
    public AdminSection findAdminSectionByClass(String className) {
        return adminNavigationDao.readAdminSectionByClass(className);
    }

    @Override
    public AdminSection findAdminSectionBySectionKey(String sectionKey) {
        return adminNavigationDao.readAdminSectionBySectionKey(sectionKey);
    }

    @Override
    public boolean isUserAuthorizedToViewSection(AdminUser adminUser, AdminSection section) {
        List<AdminPermission> authorizedPermissions = section.getPermissions();
        if (adminUser.getAllRoles() != null && !adminUser.getAllRoles().isEmpty()) {
            for (AdminRole role : adminUser.getAllRoles()) {
                for (AdminPermission permission : role.getAllPermissions()){
                    if (authorizedPermissions != null) {
                        if (authorizedPermissions.contains(permission)){
                            return true;
                        }

                        for (AdminPermission authorizedPermission : authorizedPermissions) {
                            if (permission.getName().equals(parseForAllPermission(authorizedPermission.getName()))) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    protected String parseForAllPermission(String currentPermission) {
        String[] pieces = currentPermission.split(PATTERN);
        StringBuilder builder = new StringBuilder(50);
        builder.append(pieces[0]);
        builder.append("_ALL_");
        for (int j = 2; j<pieces.length; j++) {
            builder.append(pieces[j]);
            if (j < pieces.length - 1) {
                builder.append('_');
            }
        }
        return builder.toString();
    }
}
