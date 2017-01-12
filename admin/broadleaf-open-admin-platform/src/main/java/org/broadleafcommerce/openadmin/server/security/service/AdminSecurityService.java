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

import org.broadleafcommerce.common.security.util.PasswordChange;
import org.broadleafcommerce.common.service.GenericResponse;
import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.service.type.PermissionType;
import org.springframework.security.authentication.dao.SaltSource;

import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public interface AdminSecurityService {

    public static final String[] DEFAULT_PERMISSIONS = { "PERMISSION_OTHER_DEFAULT", "PERMISSION_ALL_USER_SANDBOX" };

    List<AdminUser> readAllAdminUsers();
    AdminUser readAdminUserById(Long id);
    AdminUser readAdminUserByUserName(String userName);
    AdminUser saveAdminUser(AdminUser user);
    void deleteAdminUser(AdminUser user);

    List<AdminRole> readAllAdminRoles();
    AdminRole readAdminRoleById(Long id);
    AdminRole saveAdminRole(AdminRole role);
    void deleteAdminRole(AdminRole role);

    List<AdminPermission> readAllAdminPermissions();
    AdminPermission readAdminPermissionById(Long id);
    AdminPermission saveAdminPermission(AdminPermission permission);
    void deleteAdminPermission(AdminPermission permission);

    /**
     * Clears the cache used for {@link #isUserQualifiedForOperationOnCeilingEntity(AdminUser, PermissionType, String)}
     */
    void clearAdminSecurityCache();

    AdminUser changePassword(PasswordChange passwordChange);

    boolean isUserQualifiedForOperationOnCeilingEntity(AdminUser adminUser, PermissionType permissionType, String ceilingEntityFullyQualifiedName);
    boolean doesOperationExistForCeilingEntity(PermissionType permissionType, String ceilingEntityFullyQualifiedName);

    /**
     * Looks up the corresponding AdminUser and emails the address on file with
     * the associated username.
     *
     * @param emailAddress email address of user to email
     * @return Response can contain errors including (notFound)
     *
     */
    GenericResponse sendForgotUsernameNotification(String emailAddress);

    /**
     * Generates an access token and then emails the user.
     *
     * @param userName the username of the user to send a password reset email
     * @return Response can contain errors including (invalidEmail, invalidUsername, inactiveUser)
     * 
     */
    GenericResponse sendResetPasswordNotification(String userName);
    
    /**
     * Updates the password for the passed in user only if the passed
     * in token is valid for that user.
     *
     * @param username the username of the user
     * @param token a valid reset token from the email
     * @param password the new desired password
     * @param confirmPassword the password confirmation to match password
     * @return Response can contain errors including (invalidUsername, inactiveUser, invalidToken, invalidPassword, tokenExpired, passwordMismatch)
     */
    GenericResponse resetPasswordUsingToken(String username, String token, String password, String confirmPassword);

    /**
     * Change a user's password only if oldPassword matches what's stored for that user
     *
     * @param username the username to change the password for
     * @param oldPassword the user's current password
     * @param password the desired new password
     * @param confirmPassword the confirm password to ensure it matches password
     * @return Response can contain errors including (invalidUser, emailNotFound, inactiveUser, invalidPassword, passwordMismatch)
     */
    GenericResponse changePassword(String username, String oldPassword, String password, String confirmPassword);
    
    /**
     * @deprecated use {@link #getSaltSource()} instead, this will be removed in 4.2
     *
     * @return the currently used salt string
     */
    @Deprecated
    public String getSalt();
    
    /**
     * @deprecated use {@link #setSaltSource(SaltSource)} instead, this will be removed in 4.2
     *
     * @param salt the new salt string to use
     */
    @Deprecated
    public void setSalt(String salt);

    /**
     * Returns the {@link SaltSource} used with the blAdminPasswordEncoder to encrypt the user password. Usually configured in
     * applicationContext-admin-security.xml. This is not a required property and will return null if not configured
     *
     * @deprecated the new {@link org.springframework.security.crypto.password.PasswordEncoder PasswordEncoder} handles salting internally, this will be removed in 4.2
     *
     * @return the currently used {@link SaltSource}
     */
    @Deprecated
    public SaltSource getSaltSource();
    
    /**
     * Sets the {@link SaltSource} used with blAdminPasswordEncoder to encrypt the user password. Usually configured within
     * applicationContext-admin-security.xml
     *
     * @deprecated the new {@link org.springframework.security.crypto.password.PasswordEncoder PasswordEncoder} handles salting internally, this will be removed in 4.2
     * 
     * @param saltSource the new {@link SaltSource} to use
     */
    @Deprecated
    public void setSaltSource(SaltSource saltSource);
    
    /**
     * Gets the salt object for the current admin user. By default this delegates to {@link #getSaltSource()}. If there is
     * not a {@link SaltSource} configured ({@link #getSaltSource()} returns null) then this also returns null.
     *
     * @deprecated the new {@link org.springframework.security.crypto.password.PasswordEncoder PasswordEncoder} handles salting internally, this will be removed in 4.2
     * 
     * @param user the {@link AdminUser} to get {@link org.springframework.security.core.userdetails.UserDetails UserDetails} from
     * @param unencodedPassword the unencoded password
     * @return the salt for the current admin user
     */
    @Deprecated
    public Object getSalt(AdminUser user, String unencodedPassword);

    /**
     * Returns a list of admin users that match the given email. This could potentially return more than one user if the
     * admin.user.requireUniqueEmailAddress property is set to false.
     *
     * @param email the email address to search for
     * @return a {@link List} of {@link AdminUser} matching the provided email address
     */
    public List<AdminUser> readAdminUsersByEmail(String email);

}
