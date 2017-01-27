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
package org.broadleafcommerce.openadmin.server.security.external;

import org.broadleafcommerce.common.security.BroadleafExternalAuthenticationUserDetails;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.broadleafcommerce.openadmin.server.security.service.user.AdminUserProvisioningService;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import java.util.Collection;

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
