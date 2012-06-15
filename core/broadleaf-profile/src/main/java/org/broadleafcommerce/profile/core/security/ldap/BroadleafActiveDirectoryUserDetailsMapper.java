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

package org.broadleafcommerce.profile.core.security.ldap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.Resource;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.User;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.broadleafcommerce.profile.core.service.UserService;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class allows Spring to do it's thing with respect to mapping user details from
 * LDAP to the Spring's security framework. However, this class allows us to specify whether
 * to use the user's user name from LDAP, or to use their email address to map them to a Broadleaf
 * user.  It also allows us to override the role names (GrantedAuthorities) that come from LDAP with
 * names that may be more suitable for Broadleaf.
 *
 * See the
 *
 * @author Kelly Tisdell
 *
 */
public class BroadleafActiveDirectoryUserDetailsMapper extends LdapUserDetailsMapper {

    @Resource(name="blUserService")
    protected UserService userService;

    @Resource(name="blCustomerService")
    protected CustomerService customerService;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    protected boolean useEmailAddressAsUsername = true;

    protected boolean additiveRoleNameSubstitutions = false;

    protected Map<String, String[]> roleNameSubstitutions;

    protected String provisionType; //Could also be either "ADMIN" or "CUSTOMER"

    /**
     * See setter: <code>setProvisionType(String)</code>
     * @param provisionType
     */
    public BroadleafActiveDirectoryUserDetailsMapper(String provisionType) {
        setProvisionType(provisionType);
    }

    /**
     * This is invoked to allow Broadleaf to provision a user (or customer) if they do not already exist. This is possible
     * in an LDAP situation where the user exists in LDAP, but there is no record in Broadleaf tables for that user. Implementors of this
     * method are required to check the that the user or customer does not exist before provisioning them.
     *
     * @param userDetails
     */
    protected void provisionUser(DirContextOperations ctx, UserDetails userDetails) {
        if ("ADMIN".equals(provisionType)) {
            //This is for the admin
            User user = userService.readUserByUsername(userDetails.getUsername());
            if (user == null) {
                //Create a user...
                user = (User)entityConfiguration.createEntityInstance(User.class.getName());
                user.setUsername(userDetails.getUsername());
                user.setPassword(userDetails.getPassword());
                if (user.getPassword() == null) {
                    user.setPassword("NO_PASSWORD");
                }
                userService.saveUser(user);
            }
        } else {
            //This is for the customer
            Customer cust = customerService.readCustomerByUsername(userDetails.getUsername());
            if (cust == null) {
                //Create a customer...
                cust = customerService.createCustomer();

                cust.setUsername(userDetails.getUsername());
                customerService.saveCustomer(cust, true);
            }
        }
    }

    @Override
    @Transactional
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

        UserDetails userDetails;
        if (useEmailAddressAsUsername && email != null) {
            userDetails = super.mapUserFromContext(ctx, email, newAuthorities);
        }
        userDetails = super.mapUserFromContext(ctx, username, newAuthorities);

        provisionUser(ctx, userDetails);

        return userDetails;
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

    /**
     * This is a required property that must either be "ADMIN" or "CUSTOMER". This property tells the system whether to provision a customer or user
     * in the case where the user is authenticated successfully against LDAP, but there is no corresponding customer or user record in the database.
     *
     * Typically, this will be set to either "CUSTOMER" or "ADMIN", and will be set in different applications.  For example, in a main, customer facing
     * eCommerce site, this component will be configured with "CUSTOMER".  In the admin application, this will be configured with "ADMIN".  This MUST be set to
     * the correct value.  If you look up a user who is supposed to have admin privileges, and you have this configured wrong, it will create a customer from that
     * user.
     *
     * @param provisionType
     */
    public void setProvisionType(String provisionType) {
        if (provisionType == null || (! "ADMIN".equals(provisionType) && ! "CUSTOMER".equals(provisionType))) {
            throw new IllegalArgumentException("The property or constructor arg \"provisionType\" cannot be null and must be set to either \"ADMIN\" or \"CUSTOMER\"");
        }
        this.provisionType = provisionType;
    }
}