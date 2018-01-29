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

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.security.BroadleafExternalAuthenticationUserDetails;
import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUserImpl;
import org.broadleafcommerce.openadmin.server.security.external.AdminExternalLoginUserExtensionManager;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityHelper;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

/**
 * This component allows for the default provisioning of an AdminUser and roles in the Broadleaf database, based on the 
 * external authentication of a user (e.g. LDAP or custom external authentication provider).
 * 
 * @author Kelly Tisdell
 *
 */
@Service("blAdminUserProvisioningService")
public class AdminUserProvisioningServiceImpl implements AdminUserProvisioningService {

    @Resource(name = "blAdminSecurityService")
    protected AdminSecurityService securityService;

    @Resource(name = "blAdminExternalLoginExtensionManager")
    protected AdminExternalLoginUserExtensionManager adminExternalLoginExtensionManager;

    @Resource(name="blAdminSecurityHelper")
    protected AdminSecurityHelper adminSecurityHelper;

    protected Map<String, String[]> roleNameSubstitutions;

    @Override
    public AdminUserDetails provisionAdminUser(BroadleafExternalAuthenticationUserDetails details) {
        HashSet<String> newRoles = new HashSet<String>();

        if (roleNameSubstitutions != null && !roleNameSubstitutions.isEmpty()) {
            for (GrantedAuthority authority : details.getAuthorities()) {
                if (roleNameSubstitutions.containsKey(authority.getAuthority())) {
                    String[] roles = roleNameSubstitutions.get(authority.getAuthority());
                    for (String role : roles) {
                        newRoles.add(role.trim());
                    }
                } else {
                    newRoles.add(authority.getAuthority());
                }
            }
        } else {
            for (GrantedAuthority authority : details.getAuthorities()) {
                newRoles.add(authority.getAuthority());
            }
        }

        HashSet<SimpleGrantedAuthority> newAuthorities = new HashSet<>();
        for (String perm : AdminSecurityService.DEFAULT_PERMISSIONS) {
            newAuthorities.add(new SimpleGrantedAuthority(perm));
        }
        List<SimpleGrantedAuthority> newAuthoritiesList = new ArrayList<>(newAuthorities);
        HashSet<AdminRole> grantedRoles = new HashSet<AdminRole>();
        List<AdminRole> adminRoles = securityService.readAllAdminRoles();
        if (adminRoles != null) {
            for (AdminRole role : adminRoles) {
                if (newRoles.contains(role.getName())) {
                    grantedRoles.add(role);
                    adminSecurityHelper.addAllPermissionsToAuthorities(newAuthoritiesList, role.getAllPermissions());
                }
            }
        }

        // Spring security expects everything to begin with ROLE_ for things like hasRole() expressions so this adds additional
        // authorities with those mappings, as well as new ones with ROLE_ instead of PERMISSION_.
        // At the end of this, given a permission set like:
        // PERMISSION_ALL_PRODUCT
        // The following authorities will appear in the final list to Spring security:
        // PERMISSION_ALL_PRODUCT, ROLE_PERMISSION_ALL_PRODUCT, ROLE_ALL_PRODUCT
        ListIterator<SimpleGrantedAuthority> it = new ArrayList<>(newAuthorities).listIterator();
        while (it.hasNext()) {
            SimpleGrantedAuthority auth = it.next();
            if (auth.getAuthority().startsWith(AdminUserDetailsServiceImpl.LEGACY_ROLE_PREFIX)) {
                it.add(new SimpleGrantedAuthority(AdminUserDetailsServiceImpl.DEFAULT_SPRING_SECURITY_ROLE_PREFIX + auth.getAuthority()));
                it.add(new SimpleGrantedAuthority(auth.getAuthority().replaceAll(AdminUserDetailsServiceImpl.LEGACY_ROLE_PREFIX, 
                        AdminUserDetailsServiceImpl.DEFAULT_SPRING_SECURITY_ROLE_PREFIX)));
            }
        }
        
        
        AdminUser adminUser = securityService.readAdminUserByUserName(details.getUsername());
        if (adminUser == null) {
            adminUser = new AdminUserImpl();
            adminUser.setLogin(details.getUsername());
        }

        if (StringUtils.isNotBlank(details.getEmail())) {
            adminUser.setEmail(details.getEmail());
        }

        StringBuilder name = new StringBuilder();
        if (StringUtils.isNotBlank(details.getFirstName())) {
            name.append(details.getFirstName()).append(" ");
        }
        if (StringUtils.isNotBlank(details.getLastName())) {
            name.append(details.getLastName());
        }

        String fullName = name.toString();
        if (StringUtils.isNotBlank(fullName)) {
            adminUser.setName(fullName);
        } else {
            adminUser.setName(details.getUsername());
        }

        //We have to do this because BLC replies on the role relationships being stored in the DB
        Set<AdminRole> roleSet = adminUser.getAllRoles();
        //First, remove all roles associated with the user if they already existed
        if (roleSet != null) {
            //First, remove all role relationships in case they have changed
            roleSet.clear();
        } else {
            roleSet = new HashSet<AdminRole>();
            adminUser.setAllRoles(roleSet);
        }

        //Now, add all of the role relationships back.
        if (grantedRoles != null) {
            for (AdminRole role : grantedRoles) {
                roleSet.add(role);
            }
        }

        //Add optional support for things like Multi-Tenant, etc...
        adminExternalLoginExtensionManager.getProxy().performAdditionalAuthenticationTasks(adminUser, details);

        //Save the user data and all of the roles...
        adminUser = securityService.saveAdminUser(adminUser);

        return new AdminUserDetails(adminUser.getId(), details.getUsername(), "", true, true, true, true, newAuthoritiesList);
    }

    /**
     * This allows you to declaratively set a map containing values that will substitute role names from LDAP to Broadleaf roles names in cases that they might be different.
     * For example, if you have a role specified in LDAP under "memberOf" with a DN of "Marketing Administrator", you might want to
     * map that to the role "ADMIN".  By default the prefix "ROLE_" will be pre-pended to this name. So to configure this, you would specify:
     *
     * <bean class="org.broadleaf.loadtest.web.security.ActiveDirectoryUserDetailsContextMapper">
     *     <property name="roleMappings">
     *         <map>
     *             <entry key="Marketing_Administrator" value="ROLE_CATALOG_ADMIN"/>
     *         </map>
     *     </property>
     * </bean>
     *
     * With this configuration, all roles returned by LDAP that have a DN of "Marketing Administrator" will be converted to "ADMIN"
     * @param roleNameSubstitutions
     */
    public void setRoleNameSubstitutions(Map<String, String[]> roleNameSubstitutions) {
        this.roleNameSubstitutions = roleNameSubstitutions;
    }
}
