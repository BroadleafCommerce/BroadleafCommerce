/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.security.domain;

import org.broadleafcommerce.openadmin.server.security.service.type.PermissionType;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author jfischer
 *
 */
public interface AdminPermission extends Serializable {

    public void setId(Long id);
    public Long getId();
    public String getName();
    public void setName(String name);
    public String getDescription();
    public void setDescription(String description);
    public PermissionType getType();

    public void setType(PermissionType type);

    public List<AdminPermissionQualifiedEntity> getQualifiedEntities();

    public void setQualifiedEntities(List<AdminPermissionQualifiedEntity> qualifiedEntities);

    public Set<AdminUser> getAllUsers();

    public void setAllUsers(Set<AdminUser> allUsers);

    public AdminPermission clone();
    
    public Set<AdminRole> getAllRoles();
    public void setAllRoles(Set<AdminRole> allRoles);
}
