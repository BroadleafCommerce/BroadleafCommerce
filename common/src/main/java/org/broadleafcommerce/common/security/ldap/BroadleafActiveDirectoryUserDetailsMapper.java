/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.security.ldap;

import org.broadleafcommerce.common.security.BroadleafExternalAuthenticationUserDetails;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * This class allows Spring to do it's thing with respect to mapping user details from
 * LDAP to the Spring's security framework. However, this class allows us to specify whether
 * to use the user's user name from LDAP, or to use their email address to map them to a Broadleaf
 * user.  It also allows us to override the role names (GrantedAuthorities) that come from LDAP with
 * names that may be more suitable for Broadleaf.
 *
 *
 *
 * @author Kelly Tisdell
 *
 */
public class BroadleafActiveDirectoryUserDetailsMapper extends LdapUserDetailsMapper {

    protected boolean useEmailAddressAsUsername = true;

    protected boolean additiveRoleNameSubstitutions = false;

    protected Map<String, String[]> roleNameSubstitutions;

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        Collection<GrantedAuthority> newAuthorities = new HashSet<GrantedAuthority>();

        if (roleNameSubstitutions != null && ! roleNameSubstitutions.isEmpty()) {
            for (GrantedAuthority authority : authorities) {
                if (roleNameSubstitutions.containsKey(authority.getAuthority())) {
                    String[] roles = roleNameSubstitutions.get(authority.getAuthority());
                    for (String role : roles) {
                        newAuthorities.add(new SimpleGrantedAuthority(role.trim()));
                    }
                    if (additiveRoleNameSubstitutions) {
                        newAuthorities.add(authority);
                    }
                } else {
                    newAuthorities.add(authority);
                }
            }
        } else {
            newAuthorities.addAll(authorities);
        }

        String email = (String)ctx.getObjectAttribute("mail");
        UserDetails userDetails = null;
        if (useEmailAddressAsUsername) {
            if (email != null) {
                userDetails = super.mapUserFromContext(ctx, email, newAuthorities);
            }
        }

        if (userDetails == null) {
            userDetails = super.mapUserFromContext(ctx, username, newAuthorities);
        }
        
        String password = userDetails.getPassword();
        if (password == null) {
            password = userDetails.getUsername();
        }

        BroadleafExternalAuthenticationUserDetails broadleafUser = new BroadleafExternalAuthenticationUserDetails(userDetails.getUsername(), password, userDetails.getAuthorities());
        broadleafUser.setFirstName((String)ctx.getObjectAttribute("givenName"));
        broadleafUser.setLastName((String)ctx.getObjectAttribute("sn"));
        broadleafUser.setEmail(email);

        return broadleafUser;
    }

    /**
     * The LDAP server may contain a user name other than an email address.  If the email address should be used to map to a Broadleaf user, then
     * set this to true.  The principal will be set to the user's email address returned from the LDAP server.
     * @param value
     */
    public void setUseEmailAddressAsUsername(boolean value) {
        this.useEmailAddressAsUsername = value;
    }

    /**
     * This allows you to declaratively set a map containing values that will substitute role names from LDAP to Broadleaf roles names in cases that they might be different.
     * For example, if you have a role specified in LDAP under "memberOf" with a DN of "Marketing Administrator", you might want to
     * map that to the role "ADMIN".  By default the prefix "ROLE_" will be pre-pended to this name. So to configure this, you would specify:
     *
     * <bean class="org.broadleaf.loadtest.web.security.ActiveDirectoryUserDetailsContextMapper">
     *     <property name="roleMappings">
     *         <map>
     *             <entry key="Marketing_Administrator" value="CATALOG_ADMIN"/>
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

    /**
     * This should be used in conjunction with the roleNameSubstitutions property.
     * If this is set to true, this will add the mapped roles to the list of original granted authorities.  If set to false, this will replace the original granted
     * authorities with the mapped ones. Defaults to false.
     *
     * @param additiveRoleNameSubstitutions
     */
    public void setAdditiveRoleNameSubstitutions(boolean additiveRoleNameSubstitutions) {
        this.additiveRoleNameSubstitutions = additiveRoleNameSubstitutions;
    }
}