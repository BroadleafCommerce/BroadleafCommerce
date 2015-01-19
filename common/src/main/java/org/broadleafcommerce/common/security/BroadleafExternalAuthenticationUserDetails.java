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
package org.broadleafcommerce.common.security;

import org.broadleafcommerce.common.site.domain.Site;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * This is an extension of Spring's User class to provide additional data to the UserDetails interface.  This should be used by derivitave
 * authentication providers to return an instance of UserDetails when authenticating against a system other than the Broadleaf tables (e.g. LDAP)
 * <p/>
 * User: Kelly Tisdell
 * Date: 6/19/12
 */
public class BroadleafExternalAuthenticationUserDetails extends User {
    
    private static final long serialVersionUID = 1L;

    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private Site site;

    /**
     * This sets the username, password, and authorities.  
     * It also set the enabled, accountNonExpired, credentialsNonExpired, and accountNonLocked properties to true.
     * @param username
     * @param password
     * @param authorities
     */
    public BroadleafExternalAuthenticationUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, true, true, true, true, authorities);
    }

    public BroadleafExternalAuthenticationUserDetails(String username, String password,
            boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
            boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

}
