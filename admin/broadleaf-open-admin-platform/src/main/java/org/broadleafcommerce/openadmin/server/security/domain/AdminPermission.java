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
package org.broadleafcommerce.openadmin.server.security.domain;

import org.broadleafcommerce.openadmin.server.security.service.type.PermissionType;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author jfischer
 */
public interface AdminPermission extends Serializable {

    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);

    PermissionType getType();

    void setType(PermissionType type);

    List<AdminPermissionQualifiedEntity> getQualifiedEntities();

    void setQualifiedEntities(List<AdminPermissionQualifiedEntity> qualifiedEntities);

    Set<AdminUser> getAllUsers();

    void setAllUsers(Set<AdminUser> allUsers);

    AdminPermission clone();

    Set<AdminRole> getAllRoles();

    void setAllRoles(Set<AdminRole> allRoles);

    List<AdminPermission> getAllChildPermissions();

    List<AdminPermission> getAllParentPermissions();

    Boolean isFriendly();

}
