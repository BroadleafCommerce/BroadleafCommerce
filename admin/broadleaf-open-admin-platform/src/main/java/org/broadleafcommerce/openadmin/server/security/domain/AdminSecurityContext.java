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
