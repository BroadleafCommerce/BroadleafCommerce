/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.openadmin.server.security.ldap;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUserImpl;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.broadleafcommerce.openadmin.server.security.service.AdminUserDetails;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

/**
 * This is used to map LDAP principal and authorities into BLC security model.
 * 
 * @author Kelly Tisdell
 *
 */
public class BroadleafAdminLdapUserDetailsMapper extends LdapUserDetailsMapper {

    @Resource(name = "blAdminSecurityService")
    protected AdminSecurityService securityService;

    protected Map<String, String[]> roleNameSubstitutions;
    
    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        HashSet<String> newRoles = new HashSet<String>();

        if (roleNameSubstitutions != null && !roleNameSubstitutions.isEmpty()) {
            for (GrantedAuthority authority : authorities) {
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
            for (GrantedAuthority authority : authorities) {
                newRoles.add(authority.getAuthority());
            }
        }

        Collection<GrantedAuthority> newAuthorities = new HashSet<GrantedAuthority>();
        for (String perm : AdminSecurityService.DEFAULT_PERMISSIONS) {
            newAuthorities.add(new SimpleGrantedAuthority(perm));
        }

        HashSet<AdminRole> grantedRoles = new HashSet<AdminRole>();
        List<AdminRole> adminRoles = securityService.readAllAdminRoles();
        if (adminRoles != null) {
            for (AdminRole role : adminRoles) {
                if (newRoles.contains(role.getName())) {
                    grantedRoles.add(role);
                    Set<AdminPermission> permissions = role.getAllPermissions();
                    if (permissions != null && !permissions.isEmpty()) {
                        for (AdminPermission permission : permissions) {
                            if (permission.isFriendly()) {
                                for (AdminPermission childPermission : permission.getAllChildPermissions()) {
                                    newAuthorities.add(new SimpleGrantedAuthority(childPermission.getName()));
                                }
                            } else {
                                newAuthorities.add(new SimpleGrantedAuthority(permission.getName()));
                            }
                        }
                    }
                }
            }
        }

        String email = (String) ctx.getObjectAttribute("mail");
        String firstName = (String) ctx.getObjectAttribute("givenName");
        String lastName = (String) ctx.getObjectAttribute("sn");
        AdminUser adminUser = securityService.readAdminUserByUserName(username);
        if (adminUser == null) {
            adminUser = new AdminUserImpl();
            adminUser.setLogin(username);
        }

        if (StringUtils.isNotBlank(email)) {
            adminUser.setEmail(email);
        }

        StringBuilder name = new StringBuilder();
        if (StringUtils.isNotBlank(firstName)) {
            name.append(firstName).append(" ");
        }
        if (StringUtils.isNotBlank(lastName)) {
            name.append(lastName);
        }

        String fullName = name.toString();
        if (StringUtils.isNotBlank(fullName)) {
            adminUser.setName(fullName);
        } else {
            adminUser.setName(username);
        }

        adminUser = saveAdminUserAndSecurityData(adminUser, grantedRoles);

        return new AdminUserDetails(adminUser.getId(), username, "", true, true, true, true, newAuthorities);
    }

    protected AdminUser saveAdminUserAndSecurityData(AdminUser adminUser, Set<AdminRole> adminRoles) {
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
        if (adminRoles != null) {
            for (AdminRole role : adminRoles) {
                roleSet.add(role);
            }
        }
        //Save the user data and all of the roles...
        return securityService.saveAdminUser(adminUser);
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
