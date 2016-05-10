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
package org.broadleafcommerce.openadmin.security;

import org.broadleafcommerce.openadmin.server.security.remote.EntityOperationType;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public class SecurityConfig {

    private String ceilingEntityFullyQualifiedName;
    private List<EntityOperationType> requiredTypes;
    private List<String> permissions = new ArrayList<String>();
    private List<String> roles = new ArrayList<String>();
    
    public String getCeilingEntityFullyQualifiedName() {
        return ceilingEntityFullyQualifiedName;
    }
    
    public void setCeilingEntityFullyQualifiedName(
            String ceilingEntityFullyQualifiedName) {
        this.ceilingEntityFullyQualifiedName = ceilingEntityFullyQualifiedName;
    }
    
    public List<EntityOperationType> getRequiredTypes() {
        return requiredTypes;
    }
    
    public void setRequiredTypes(List<EntityOperationType> requiredTypes) {
        this.requiredTypes = requiredTypes;
    }
    
    public List<String> getPermissions() {
        return permissions;
    }
    
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}
