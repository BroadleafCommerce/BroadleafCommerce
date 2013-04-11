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

package org.broadleafcommerce.openadmin.server.security.remote;

import com.gwtincubator.security.exception.ApplicationSecurityException;
import org.broadleafcommerce.common.exception.SecurityServiceException;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.security.service.ExploitProtectionService;
import org.broadleafcommerce.common.web.SandBoxContext;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityOperationType;
import org.broadleafcommerce.openadmin.client.service.AdminSecurityService;
import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.service.type.PermissionType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Service for handeling security with Ajax components.  Serves two functions.
 * <ul>
 *     <li>
 *         Converts the ServerSide AdminUser to a client level admin user with
 *         appropriate roles defined.
 *     </li>
 *     <li>
 *         Provides a method to check if the current logged in user matches the
 *         client side user and verifies whether that user has access to the
 *         entity operation they are trying to perform.
 *     </li>
 * </ul>
 * 1.
 * @author jfischer
 *
 */
@Service("blAdminSecurityRemoteService")
public class AdminSecurityServiceRemote implements AdminSecurityService, SecurityVerifier {
    
    private static final String ANONYMOUS_USER_NAME = "anonymousUser";
    
    @Resource(name="blAdminSecurityService")
    protected org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService securityService;

    @Resource(name="blExploitProtectionService")
    protected ExploitProtectionService exploitProtectionService;

    private boolean isEntitySecurityExplicit = false;
    
    @Override
    public org.broadleafcommerce.openadmin.client.security.AdminUser getAdminUser() throws ServiceException, ApplicationSecurityException {
        AdminUser persistentAdminUser = getPersistentAdminUser();
        if (persistentAdminUser != null) {
            org.broadleafcommerce.openadmin.client.security.AdminUser response = new org.broadleafcommerce.openadmin.client.security.AdminUser();
            for (AdminRole role : persistentAdminUser.getAllRoles()) {
                response.getRoles().add(role.getName());
                for (AdminPermission permission : role.getAllPermissions()) {
                    response.getPermissions().add(permission.getName());
                }
            }
            for (AdminPermission permission : persistentAdminUser.getAllPermissions()) {
                response.getPermissions().add(permission.getName());
            }
            response.setUserName(persistentAdminUser.getLogin());
            response.setCurrentSandBoxId(String.valueOf(SandBoxContext.getSandBoxContext().getSandBoxId()));
            response.setEmail(persistentAdminUser.getEmail());
            response.setName(persistentAdminUser.getName());
            response.setPhoneNumber(persistentAdminUser.getPhoneNumber());
            response.setId(persistentAdminUser.getId());
            return response;
        }

        return null;
    }

    @Override
    public AdminUser getPersistentAdminUser() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        if (ctx != null) {
            Authentication auth = ctx.getAuthentication();
            if (auth != null && !auth.getName().equals(ANONYMOUS_USER_NAME)) {
                UserDetails temp = (UserDetails) auth.getPrincipal();

                return securityService.readAdminUserByUserName(temp.getUsername());
            }
        }

        return null;
    }
    
    @Override
    public void securityCheck(String ceilingEntityFullyQualifiedName, EntityOperationType operationType) throws ServiceException {
        if (ceilingEntityFullyQualifiedName == null) {
            throw new SecurityServiceException("Security Check Failed: ceilingEntityFullyQualifiedName not specified");
        }
        AdminUser persistentAdminUser = getPersistentAdminUser();
        PermissionType permissionType;
        switch(operationType){
            case ADD:
                permissionType = PermissionType.CREATE;
                break;
            case FETCH:
                permissionType = PermissionType.READ;
                break;
            case REMOVE:
                permissionType = PermissionType.DELETE;
                break;
            case UPDATE:
                permissionType = PermissionType.UPDATE;
                break;
            case INSPECT:
                permissionType = PermissionType.READ;
                break;
            default:
                permissionType = PermissionType.OTHER;
                break;
        }
        boolean isQualified = securityService.isUserQualifiedForOperationOnCeilingEntity(persistentAdminUser, permissionType, ceilingEntityFullyQualifiedName);
        if (!isQualified){
            //If explicit security, then this check failed. However, if not explicit security, then check to make sure there is no configured security for this entity before allowing to pass
            if (isEntitySecurityExplicit() || securityService.doesOperationExistForCeilingEntity(permissionType, ceilingEntityFullyQualifiedName)) {
                throw new SecurityServiceException("Security Check Failed for entity operation: " + operationType.toString() + " (" + ceilingEntityFullyQualifiedName + ")");
            }
        }
    }

    @Override
    public boolean isEntitySecurityExplicit() {
        return isEntitySecurityExplicit;
    }

    @Override
    public void setEntitySecurityExplicit(boolean entitySecurityExplicit) {
        isEntitySecurityExplicit = entitySecurityExplicit;
    }
}
