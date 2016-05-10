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
package org.broadleafcommerce.openadmin.server.security.domain;

import org.broadleafcommerce.openadmin.server.service.type.ContextType;

import java.io.Serializable;
import java.util.Set;

public interface AdminSecurityContext extends Serializable {

    public ContextType getContextType();

    public void setContextType(ContextType contextType);

    public String getContextKey();

    public void setContextKey(String contextKey);

    public Set<AdminRole> getAllRoles();

    public void setAllRoles(Set<AdminRole> allRoles);

    public Set<AdminPermission> getAllPermissions();

    public void setAllPermissions(Set<AdminPermission> allPermissions);

}
