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

import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;


/**
 * Extended DTO class to support salts based on the primary key of the admin user. This allows username changes for
 * admin users.
 * 
 * @see {@link AdminSecurityService}
 * @see {@link AdminUserDetailsServiceImpl}
 * @author Phillip Verheyden (phillipuniverse)
 */
public class AdminUserDetails extends User {
    
    private static final long serialVersionUID = 1L;
    
    protected Long id;
    
    public AdminUserDetails(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this(id, username, password, true, true, true, true, authorities);
    }
    
    public AdminUserDetails(Long id, String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
    }
    
    public AdminUserDetails withId(Long id) {
        setId(id);
        return this;
    }
    
    /**
     * @return the primary key of the Admin User
     */
    public Long getId() {
        return id;
    }
    
    /**
     * @param id the primary key of the Admin User
     */
    public void setId(Long id) {
        this.id = id;
    }

}
