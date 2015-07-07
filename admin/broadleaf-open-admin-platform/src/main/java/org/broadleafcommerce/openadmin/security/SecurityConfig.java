/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
