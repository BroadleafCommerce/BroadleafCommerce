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
package org.broadleafcommerce.openadmin.server.security.remote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.SecurityServiceException;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.security.service.ExploitProtectionService;
import org.broadleafcommerce.common.web.SandBoxContext;
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
    private static final Log LOG = LogFactory.getLog(AdminSecurityServiceRemote.class);
    
    @Resource(name="blAdminSecurityService")
    protected org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService securityService;

    @Resource(name="blExploitProtectionService")
    protected ExploitProtectionService exploitProtectionService;
    
    @Override
    public org.broadleafcommerce.openadmin.server.security.remote.AdminUser getAdminUser() throws ServiceException {
        AdminUser persistentAdminUser = getPersistentAdminUser();
        if (persistentAdminUser != null) {
            org.broadleafcommerce.openadmin.server.security.remote.AdminUser response = new org.broadleafcommerce.openadmin.server.security.remote.AdminUser();
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
            SecurityServiceException ex = new SecurityServiceException("Security Check Failed for entity operation: " + operationType.toString() + " (" + ceilingEntityFullyQualifiedName + ")");
            //check if the requested entity is not configured and warn
            if (!securityService.doesOperationExistForCeilingEntity(permissionType, ceilingEntityFullyQualifiedName)) {
                LOG.warn("Detected security request for an unregistered ceiling entity (" + ceilingEntityFullyQualifiedName + "). " +
                        "As a result, the request failed. Please make sure to configure security for any ceiling entities " +
                        "referenced via the admin. This is usually accomplished by adding records in the " +
                        "BLC_ADMIN_PERMISSION_ENTITY table. Note, depending on how the entity in question is used, you " +
                        "may need to add to BLC_ADMIN_PERMISSION, BLC_ADMIN_ROLE_PERMISSION_XREF and BLC_ADMIN_SEC_PERM_XREF.", ex);
            }
            throw ex;
        }
    }
}
