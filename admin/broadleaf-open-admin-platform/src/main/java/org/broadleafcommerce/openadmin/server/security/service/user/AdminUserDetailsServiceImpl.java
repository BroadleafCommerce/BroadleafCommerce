/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.security.service.user;

import org.broadleafcommerce.openadmin.server.security.dao.AdminUserDao;
import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityHelper;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.Resource;

/**
 * @author Jeff Fischer
 */
@Component("blAdminUserDetailsService")
public class AdminUserDetailsServiceImpl implements UserDetailsService {

    @Resource(name="blAdminUserDao")
    protected AdminUserDao adminUserDao;

    @Resource(name="blAdminSecurityHelper")
    protected AdminSecurityHelper adminSecurityHelper;

    public static final String LEGACY_ROLE_PREFIX = "PERMISSION_";
    public static final String DEFAULT_SPRING_SECURITY_ROLE_PREFIX = "ROLE_";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        AdminUser adminUser = adminUserDao.readAdminUserByUserName(username);
        if (adminUser == null) {
            List<AdminUser> results = adminUserDao.readAdminUserByEmail(username);
            if (!CollectionUtils.isEmpty(results)) {
                adminUser = results.get(0);
            }
        }
        if (adminUser == null || adminUser.getActiveStatusFlag() == null || !adminUser.getActiveStatusFlag()) {
            throw new UsernameNotFoundException("The user was not found");
        }

        return buildDetails(username, adminUser);
    }

    protected UserDetails buildDetails(String username, AdminUser adminUser) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (AdminRole role : adminUser.getAllRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            adminSecurityHelper.addAllPermissionsToAuthorities(authorities, role.getAllPermissions());
        }
        adminSecurityHelper.addAllPermissionsToAuthorities(authorities, adminUser.getAllPermissions());
        for (String perm : AdminSecurityService.DEFAULT_PERMISSIONS) {
            authorities.add(new SimpleGrantedAuthority(perm));
        }

        // Spring security expects everything to begin with ROLE_ for things like hasRole() expressions so this adds additional
        // authorities with those mappings, as well as new ones with ROLE_ instead of PERMISSION_.
        // At the end of this, given a permission set like:
        // PERMISSION_ALL_PRODUCT
        // The following authorities will appear in the final list to Spring security:
        // PERMISSION_ALL_PRODUCT, ROLE_PERMISSION_ALL_PRODUCT, ROLE_ALL_PRODUCT
        ListIterator<SimpleGrantedAuthority> it = authorities.listIterator();
        while (it.hasNext()) {
            SimpleGrantedAuthority auth = it.next();
            if (auth.getAuthority().startsWith(LEGACY_ROLE_PREFIX)) {
                it.add(new SimpleGrantedAuthority(DEFAULT_SPRING_SECURITY_ROLE_PREFIX + auth.getAuthority()));
                it.add(new SimpleGrantedAuthority(auth.getAuthority().replaceAll(LEGACY_ROLE_PREFIX, DEFAULT_SPRING_SECURITY_ROLE_PREFIX)));
            }
        }

        return new AdminUserDetails(adminUser.getId(), username, adminUser.getPassword(), true, true, true, true, authorities);
    }

}
