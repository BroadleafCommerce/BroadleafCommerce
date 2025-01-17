/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import java.io.Serial;
import java.util.Collection;

/**
 * Extended DTO class to support salts based on the primary key of the admin user. This allows username changes for
 * admin users.
 *
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link AdminSecurityService}
 * @see {@link AdminUserDetailsServiceImpl}
 */
public class AdminUserDetails extends User {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID of the persistent {@link org.broadleafcommerce.openadmin.server.security.domain.AdminUser}
     */
    protected Long id;

    public AdminUserDetails(
            final Long id,
            final String username,
            final String password,
            final Collection<? extends GrantedAuthority> authorities
    ) {
        this(
                id,
                username,
                password,
                true,
                true,
                true,
                true,
                authorities
        );
    }

    public AdminUserDetails(
            final Long id,
            final String username,
            final String password,
            final boolean enabled,
            final boolean accountNonExpired,
            final boolean credentialsNonExpired,
            final boolean accountNonLocked,
            final Collection<? extends GrantedAuthority> authorities
    ) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        setId(id);
    }

    public AdminUserDetails withId(final Long id) {
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
    public void setId(final Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("Id: ").append(this.id).append("; ");

        return sb.toString();
    }

}
