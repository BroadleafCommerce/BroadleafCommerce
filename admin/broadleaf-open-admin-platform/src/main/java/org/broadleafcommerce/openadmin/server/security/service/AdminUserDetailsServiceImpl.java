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
package org.broadleafcommerce.openadmin.server.security.service;

import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

/**
 * @author Jeff Fischer
 */
public class AdminUserDetailsServiceImpl implements UserDetailsService {

    @Resource(name="blAdminSecurityService")
    protected AdminSecurityService adminSecurityService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        AdminUser adminUser = adminSecurityService.readAdminUserByUserName(username);
        if (adminUser == null || adminUser.getActiveStatusFlag() == null || !adminUser.getActiveStatusFlag()) {
            throw new UsernameNotFoundException("The user was not found");
        }

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (AdminRole role : adminUser.getAllRoles()) {
            for (AdminPermission permission : role.getAllPermissions()) {
                if(permission.isFriendly()) {
                    for (AdminPermission childPermission : permission.getAllChildPermissions()) {
                        authorities.add(new SimpleGrantedAuthority(childPermission.getName()));
                    }
                } else {
                    authorities.add(new SimpleGrantedAuthority(permission.getName()));
                }
            }
        }
        for (AdminPermission permission : adminUser.getAllPermissions()) {
            if(permission.isFriendly()) {
                for (AdminPermission childPermission : permission.getAllChildPermissions()) {
                    authorities.add(new SimpleGrantedAuthority(childPermission.getName()));
                }
            } else {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }
        for (String perm : AdminSecurityService.DEFAULT_PERMISSIONS) {
            authorities.add(new SimpleGrantedAuthority(perm));
        }
        return new AdminUserDetails(adminUser.getId(), username, adminUser.getPassword(), true, true, true, true, authorities);
    }

}
