/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.social;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import javax.annotation.Resource;


/**
 * The SignInAdapter is exclusively used for provider sign in so a SignInAdapter
 * bean will need to be added to the Spring Social configuration.
 *
 * The signIn() method takes the local application user's user ID normalized as a String.
 * No other credentials are necessary here because by the time this method is called the user will have signed
 * into the provider and their connection with that provider has been used to prove the user's identity.
 * This adapter will then authenticate manually against Spring Security
 *
 * To use:
 * this will automatically be injected into ProviderSignInController,
 * as long as this package is scanned
 * (make sure the following is in applicationContext-servlet.xml)
 * <context:component-scan base-package="org.broadleafcommerce.core.web"/>
 *
 * @see org.springframework.social.connect.web.ProviderSignInController
 * @author elbertbautista
 *
 */
@Component("blSocialSignInAdapter")
public class BroadleafSocialSignInAdapter implements SignInAdapter {

    @Resource(name="blUserDetailsService")
    private UserDetailsService userDetailsService;

    @Override
    public String signIn(String username, Connection<?> connection, NativeWebRequest request) {
        UserDetails principal = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(token);
        return null;
    }

}
