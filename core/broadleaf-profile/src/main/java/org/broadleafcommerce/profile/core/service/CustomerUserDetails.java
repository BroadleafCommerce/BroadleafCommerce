/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.profile.core.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Created in order to utilize the Customer's primary key to salt passwords with. This allows username changes without
 * requiring a password reset since the primary key should never change.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link UserDetailsServiceImpl}
 * @see {@link CustomerServiceImpl#getSalt(org.broadleafcommerce.profile.core.domain.Customer)}
 */
public class CustomerUserDetails extends User {

    private static final long serialVersionUID = 1L;
    
    protected Long id;
    
    public CustomerUserDetails(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this(id, username, password, true, true, true, true, authorities);
    }
    
    public CustomerUserDetails(Long id, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
    }
    
    public CustomerUserDetails withId(Long id) {
        setId(id);
        return this;
    }
    
    /**
     * @return the primary key of the Customer
     */
    public Long getId() {
        return id;
    }
    
    /**
     * @param id the primary key of the Customer
     */
    public void setId(Long id) {
        this.id = id;
    }
    
}