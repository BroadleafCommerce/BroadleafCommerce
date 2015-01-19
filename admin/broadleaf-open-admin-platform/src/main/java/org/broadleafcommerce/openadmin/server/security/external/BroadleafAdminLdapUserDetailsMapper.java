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
package org.broadleafcommerce.openadmin.server.security.external;

import org.broadleafcommerce.common.security.BroadleafExternalAuthenticationUserDetails;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.broadleafcommerce.openadmin.server.security.service.AdminUserProvisioningService;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import java.util.Collection;
import java.util.Map;

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

    @Resource(name = "blAdminUserProvisioningService")
    protected AdminUserProvisioningService provisioningService;

    protected Map<String, String[]> roleNameSubstitutions;
    
    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        String email = (String) ctx.getObjectAttribute("mail");
        String firstName = (String) ctx.getObjectAttribute("givenName");
        String lastName = (String) ctx.getObjectAttribute("sn");
        
        BroadleafExternalAuthenticationUserDetails details = new BroadleafExternalAuthenticationUserDetails(username, "", authorities);
        details.setEmail(email);
        details.setFirstName(firstName);
        details.setLastName(lastName);
        details.setSite(determineSite(ctx, username, authorities));

        return provisioningService.provisionAdminUser(details);
    }

    /**
     * Allows for a hook to determine the Multi-Tenant site for this user from the ctx, username, and authorities. Default is 
     * to return null (no site).  Implementors may wish to subclass this to determine the Site from the context.
     * 
     * If the user is not associated with the current site, or if there is a problem determining the Site, an instance of 
     * <code>org.springframework.security.core.AuthenticationException</code> should be thrown.
     * 
     * @return
     */
    protected Site determineSite(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        return null;
    }
}
