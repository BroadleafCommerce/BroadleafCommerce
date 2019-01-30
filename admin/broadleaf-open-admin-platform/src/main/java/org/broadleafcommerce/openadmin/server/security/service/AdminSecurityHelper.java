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
package org.broadleafcommerce.openadmin.server.security.service;

import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * @author Philip Baggett (pbaggett)
 */
public interface AdminSecurityHelper {

    /**
     * Adds the hierarchy of each admin permission to the collection of granted authorities.
     *  @param grantedAuthorities the collection that authorities will be added to
     * @param adminPermissions the collection of permissions who's hierarchies will be put into grantedAuthorities
     */
    void addAllPermissionsToAuthorities(List<SimpleGrantedAuthority> grantedAuthorities, Collection<AdminPermission> adminPermissions);
}
