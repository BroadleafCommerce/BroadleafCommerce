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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

/**
 * @author Jeff Fischer
 */
@Component("blAdminUserDetailsService")
public class AdminUserDetailsServiceImpl implements UserDetailsService {

    @Resource(name="blAdminSecurityService")
    protected AdminSecurityService adminSecurityService;
    
    public static final String LEGACY_ROLE_PREFIX = "PERMISSION_";
    public static final String DEFAULT_SPRING_SECURITY_ROLE_PREFIX = "ROLE_";
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        AdminUser adminUser = adminSecurityService.readAdminUserByUserName(username);
        if (adminUser == null || adminUser.getActiveStatusFlag() == null || !adminUser.getActiveStatusFlag()) {
            throw new UsernameNotFoundException("The user was not found");
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
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
        
        // Spring security expects everything to begin with ROLE_, so this transforms all the PERMISSION_* into ROLE_*
        CollectionUtils.transform(authorities, new Transformer<SimpleGrantedAuthority, SimpleGrantedAuthority>() {

            @Override
            public SimpleGrantedAuthority transform(SimpleGrantedAuthority input) {
                String authority = input.getAuthority();
                if (authority.startsWith(LEGACY_ROLE_PREFIX)) {
                    return new SimpleGrantedAuthority(authority.replace(LEGACY_ROLE_PREFIX, DEFAULT_SPRING_SECURITY_ROLE_PREFIX));
                } else {
                    return input;
                }
            }
        });
        
        return new AdminUserDetails(adminUser.getId(), username, adminUser.getPassword(), true, true, true, true, authorities);
    }

}
