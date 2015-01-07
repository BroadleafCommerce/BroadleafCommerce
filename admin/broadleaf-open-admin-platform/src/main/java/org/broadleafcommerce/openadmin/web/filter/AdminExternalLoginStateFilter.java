/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.web.filter;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.security.BroadleafExternalAuthenticationUserDetails;
import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides a filter to be used with External Security Providers (e.g. LDAP).  When authentication is performed against
 * another system it is important to provision an admin user in Broadleaf and set up the appropriate roles.
 * This class checks to see if a user exists and if not, creates one.  It also replaces all roles associated with a user with roles that
 * match their Authentication credentials.  DO NOT USE THIS FILTER UNLESS YOU ARE AUTHENTICATING AGAINST AN EXTERNAL
 * SOURCE SUCH AS LDAP.
 * 
 * @deprecated NO LONGER REQUIRED AND SHOULD NOT BE USED. SEE BroadleafAdminLdapUserDetailsMapper.
 *
 * <p/>
 * User: Kelly Tisdell
 * Date: 6/19/12
 */
@Deprecated
public class AdminExternalLoginStateFilter extends GenericFilterBean {
    
    protected static final String BLC_ADMIN_PROVISION_USER_CHECK = "BLC_ADMIN_PROVISION_USER_CHECK";

    @Resource(name="blAdminSecurityService")
    private AdminSecurityService adminSecurityService;
    
    @Resource(name="blEntityConfiguration")
    private EntityConfiguration entityConfiguration;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest)servletRequest;
        if (request.getSession(true).getAttribute(BLC_ADMIN_PROVISION_USER_CHECK) == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                if (authentication.getPrincipal() instanceof UserDetails){
                    UserDetails userDetails = (UserDetails)authentication.getPrincipal();
                    if (userDetails != null && userDetails.getUsername() != null) {
                        AdminUser user = adminSecurityService.readAdminUserByUserName(userDetails.getUsername());
                        if (userDetails instanceof BroadleafExternalAuthenticationUserDetails) {
                            BroadleafExternalAuthenticationUserDetails broadleafUser = (BroadleafExternalAuthenticationUserDetails)userDetails;
                            if (user == null) {
                                //Provision a new user...
                                user = (AdminUser)entityConfiguration.createEntityInstance(AdminUser.class.getName());
                            }
                            saveAdminUser(broadleafUser, user);
                            request.getSession().setAttribute(BLC_ADMIN_PROVISION_USER_CHECK, Boolean.TRUE);
                        }
                        
                    }
                }
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
    
    protected void saveAdminUser(BroadleafExternalAuthenticationUserDetails broadleafUser, AdminUser user) {
        //Name, login, password, email are required.
        user.setLogin(broadleafUser.getUsername());
        user.setUnencodedPassword(broadleafUser.getPassword());

        if (user.getUnencodedPassword() == null) {
            //If Spring is configured to erase credentials, then this will always be null
            //Set the username as a default password here.
            user.setUnencodedPassword(user.getLogin());
        }

        StringBuffer name = new StringBuffer();
        if (broadleafUser.getFirstName() != null && broadleafUser.getFirstName().trim().length() > 0) {
            name.append(broadleafUser.getFirstName().trim());
            name.append(" ");
        }

        if (broadleafUser.getLastName() != null && broadleafUser.getLastName().trim().length() > 0) {
            name.append(broadleafUser.getLastName().trim());
        }

        user.setName(name.toString());
        user.setEmail(broadleafUser.getEmail());

        Set<AdminRole> roleSet = user.getAllRoles();
        //First, remove all roles associated with the user if they already existed
        if (roleSet != null){
            roleSet.clear();
        } else {
            roleSet = new HashSet<AdminRole>();
            user.setAllRoles(roleSet);
        }
        
        //Now add the appropriate roles back in
        List<AdminRole> availableRoles = adminSecurityService.readAllAdminRoles();
        if (availableRoles != null) {
            HashMap<String, AdminRole> roleMap = new HashMap<String, AdminRole>();
            for (AdminRole role : availableRoles) {
               roleMap.put(role.getName(), role);
            }
            Collection<GrantedAuthority> authorities = broadleafUser.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if (roleMap.get(authority.getAuthority()) != null){
                    roleSet.add(roleMap.get(authority.getAuthority()));
                }
            }
        }
        //Save the user data and all of the roles...
        adminSecurityService.saveAdminUser(user);
    }
}
