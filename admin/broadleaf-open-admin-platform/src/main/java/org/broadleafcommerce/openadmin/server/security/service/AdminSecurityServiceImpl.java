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
package org.broadleafcommerce.openadmin.server.security.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.email.service.EmailService;
import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.broadleafcommerce.common.security.util.PasswordChange;
import org.broadleafcommerce.common.security.util.PasswordUtils;
import org.broadleafcommerce.common.service.GenericResponse;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.openadmin.server.security.dao.AdminPermissionDao;
import org.broadleafcommerce.openadmin.server.security.dao.AdminRoleDao;
import org.broadleafcommerce.openadmin.server.security.dao.AdminUserDao;
import org.broadleafcommerce.openadmin.server.security.dao.ForgotPasswordSecurityTokenDao;
import org.broadleafcommerce.openadmin.server.security.domain.AdminPermission;
import org.broadleafcommerce.openadmin.server.security.domain.AdminRole;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.domain.ForgotPasswordSecurityToken;
import org.broadleafcommerce.openadmin.server.security.domain.ForgotPasswordSecurityTokenImpl;
import org.broadleafcommerce.openadmin.server.security.service.type.PermissionType;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 *
 * @author jfischer
 *
 */
@Service("blAdminSecurityService")
public class AdminSecurityServiceImpl implements AdminSecurityService {

    private static final Log LOG = LogFactory.getLog(AdminSecurityServiceImpl.class);

    private static int PASSWORD_TOKEN_LENGTH = 12;

    @Resource(name = "blAdminRoleDao")
    protected AdminRoleDao adminRoleDao;

    @Resource(name = "blAdminUserDao")
    protected AdminUserDao adminUserDao;
    
    @Resource(name = "blForgotPasswordSecurityTokenDao")
    protected ForgotPasswordSecurityTokenDao forgotPasswordSecurityTokenDao;

    @Resource(name = "blAdminPermissionDao")
    protected AdminPermissionDao adminPermissionDao;

    /**
     * <p>Set by {@link #setupPasswordEncoder()} if the blPasswordEncoder bean provided is the deprecated version.
     *
     * @deprecated Spring Security has deprecated this encoder interface, this will be removed in 4.2
     */
    @Deprecated
    protected org.springframework.security.authentication.encoding.PasswordEncoder passwordEncoder;

    /**
     * <p>Set by {@link #setupPasswordEncoder()} if the blPasswordEncoder bean provided is the new version.
     */
    protected PasswordEncoder passwordEncoderNew;

    /**
     * <p>This is simply a placeholder to be used by {@link #setupPasswordEncoder()} to determine if we're using the
     * new {@link PasswordEncoder} or the deprecated {@link org.springframework.security.authentication.encoding.PasswordEncoder PasswordEncoder}
     */
    @Resource(name="blAdminPasswordEncoder")
    protected Object passwordEncoderBean;

    /**
     * Optional password salt to be used with the passwordEncoder
     *
     * @deprecated use {@link #saltSource} instead, this will be removed in 4.2
     */
    @Deprecated
    protected String salt;
    
    /**
     * Use a Salt Source ONLY if there's one configured
     *
     * @deprecated the new {@link PasswordEncoder} handles salting internally, this will be removed in 4.2
     */
    @Deprecated
    @Autowired(required=false)
    @Qualifier("blAdminSaltSource")
    protected SaltSource saltSource;
    
    @Resource(name="blEmailService")
    protected EmailService emailService;

    @Resource(name="blSendAdminResetPasswordEmail")
    protected EmailInfo resetPasswordEmailInfo;

    @Resource(name="blSendAdminUsernameEmailInfo")
    protected EmailInfo sendUsernameEmailInfo;

    /**
     * <p>Sets either {@link #passwordEncoder} or {@link #passwordEncoderNew} based on the type of {@link #passwordEncoderBean}
     * in order to provide bean configuration backwards compatibility with the deprecated {@link org.springframework.security.authentication.encoding.PasswordEncoder PasswordEncoder} bean.
     *
     * <p>{@link #passwordEncoderBean} is set by the bean defined as "blPasswordEncoder".
     *
     * <p>This class will utilize either the new or deprecated PasswordEncoder type depending on which is not null.
     *
     * @throws NoSuchBeanDefinitionException if {@link #passwordEncoderBean} is null or not an instance of either PasswordEncoder
     */
    @PostConstruct
    protected void setupPasswordEncoder() {
        passwordEncoderNew = null;
        passwordEncoder = null;
        if (passwordEncoderBean instanceof PasswordEncoder) {
            passwordEncoderNew = (PasswordEncoder) passwordEncoderBean;
        } else if (passwordEncoderBean instanceof org.springframework.security.authentication.encoding.PasswordEncoder) {
            passwordEncoder = (org.springframework.security.authentication.encoding.PasswordEncoder) passwordEncoderBean;
        } else {
            throw new NoSuchBeanDefinitionException("No PasswordEncoder bean is defined");
        }
    }

    protected int getTokenExpiredMinutes() {
        return BLCSystemProperty.resolveIntSystemProperty("tokenExpiredMinutes");
    }    

    protected String getResetPasswordURL() {
        return BLCSystemProperty.resolveSystemProperty("resetPasswordURL");
    }

    @Override
    @Transactional("blTransactionManager")
    public void deleteAdminPermission(AdminPermission permission) {
        adminPermissionDao.deleteAdminPermission(permission);
    }

    @Override
    @Transactional("blTransactionManager")
    public void deleteAdminRole(AdminRole role) {
        adminRoleDao.deleteAdminRole(role);
    }

    @Override
    @Transactional("blTransactionManager")
    public void deleteAdminUser(AdminUser user) {
        adminUserDao.deleteAdminUser(user);
    }

    @Override
    public AdminPermission readAdminPermissionById(Long id) {
        return adminPermissionDao.readAdminPermissionById(id);
    }

    @Override
    public AdminRole readAdminRoleById(Long id) {
        return adminRoleDao.readAdminRoleById(id);
    }

    @Override
    public AdminUser readAdminUserById(Long id) {
        return adminUserDao.readAdminUserById(id);
    }

    @Override
    @Transactional("blTransactionManager")
    public AdminPermission saveAdminPermission(AdminPermission permission) {
        return adminPermissionDao.saveAdminPermission(permission);
    }

    @Override
    @Transactional("blTransactionManager")
    public AdminRole saveAdminRole(AdminRole role) {
        return adminRoleDao.saveAdminRole(role);
    }

    @Override
    @Transactional("blTransactionManager")
    public AdminUser saveAdminUser(AdminUser user) {
        boolean encodePasswordNeeded = false;
        String unencodedPassword = user.getUnencodedPassword();

        if (user.getUnencodedPassword() != null) {
            encodePasswordNeeded = true;
            user.setPassword(unencodedPassword);
        }

        // If no password is set, default to a secure password.
        if (user.getPassword() == null) {
            user.setPassword(generateSecurePassword());
        }

        AdminUser returnUser = adminUserDao.saveAdminUser(user);

        if (encodePasswordNeeded) {
            returnUser.setPassword(encodePassword(unencodedPassword, getSalt(returnUser, unencodedPassword)));
        }



        return adminUserDao.saveAdminUser(returnUser);
    }

    protected String generateSecurePassword() {
        return RandomStringUtils.randomAlphanumeric(16);
    }

    @Override
    @Transactional("blTransactionManager")
    public AdminUser changePassword(PasswordChange passwordChange) {
        AdminUser user = readAdminUserByUserName(passwordChange.getUsername());
        user.setUnencodedPassword(passwordChange.getNewPassword());
        user = saveAdminUser(user);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(passwordChange.getUsername(), passwordChange.getNewPassword(), auth.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authRequest);
        auth.setAuthenticated(false);
        return user;
    }

    @Override
    public boolean isUserQualifiedForOperationOnCeilingEntity(AdminUser adminUser, PermissionType permissionType, String ceilingEntityFullyQualifiedName) {
        boolean response = adminPermissionDao.isUserQualifiedForOperationOnCeilingEntity(adminUser, permissionType, ceilingEntityFullyQualifiedName);
        if (!response) {
            response = adminPermissionDao.isUserQualifiedForOperationOnCeilingEntityViaDefaultPermissions(ceilingEntityFullyQualifiedName);
        }
        return response;
    }

    @Override
    public boolean doesOperationExistForCeilingEntity(PermissionType permissionType, String ceilingEntityFullyQualifiedName) {
        return adminPermissionDao.doesOperationExistForCeilingEntity(permissionType, ceilingEntityFullyQualifiedName);
    }

    @Override
    public AdminUser readAdminUserByUserName(String userName) {
        return adminUserDao.readAdminUserByUserName(userName);
    }

    @Override
    public List<AdminUser> readAdminUsersByEmail(String email) {
        return adminUserDao.readAdminUserByEmail(email);
    }

    @Override
    public List<AdminUser> readAllAdminUsers() {
        return adminUserDao.readAllAdminUsers();
    }

    @Override
    public List<AdminRole> readAllAdminRoles() {
        return adminRoleDao.readAllAdminRoles();
    }

    @Override
    public List<AdminPermission> readAllAdminPermissions() {
        return adminPermissionDao.readAllAdminPermissions();
    }

    @Override
    @Transactional("blTransactionManager")
    public GenericResponse sendForgotUsernameNotification(String emailAddress) {
        GenericResponse response = new GenericResponse();
        List<AdminUser> users = null;
        if (emailAddress != null) {
            users = adminUserDao.readAdminUserByEmail(emailAddress);
        }
        if (CollectionUtils.isEmpty(users)) {
            response.addErrorCode("notFound");
        } else {
            List<String> activeUsernames = new ArrayList<String>();
            for (AdminUser user : users) {
                if (user.getActiveStatusFlag()) {
                    activeUsernames.add(user.getLogin());
                }
            }

            if (activeUsernames.size() > 0) {
                HashMap<String, Object> vars = new HashMap<String, Object>();
                vars.put("accountNames", activeUsernames);
                emailService.sendTemplateEmail(emailAddress, getSendUsernameEmailInfo(), vars);
            } else {
                // send inactive username found email.
                response.addErrorCode("inactiveUser");
            }
        }
        return response;
    }

    @Override
    @Transactional("blTransactionManager")
    public GenericResponse sendResetPasswordNotification(String username) {
        GenericResponse response = new GenericResponse();
        AdminUser user = null;
        
        if (username != null) {
            user = adminUserDao.readAdminUserByUserName(username);
        }
        
        checkUser(user,response);
        
        if (! response.getHasErrors()) {        
            String token = PasswordUtils.generateTemporaryPassword(PASSWORD_TOKEN_LENGTH);
            token = token.toLowerCase();

            ForgotPasswordSecurityToken fpst = new ForgotPasswordSecurityTokenImpl();
            fpst.setAdminUserId(user.getId());
            fpst.setToken(encodePassword(token, null));
            fpst.setCreateDate(SystemTime.asDate());
            forgotPasswordSecurityTokenDao.saveToken(fpst);
            
            HashMap<String, Object> vars = new HashMap<String, Object>();
            vars.put("token", token);
            String resetPasswordUrl = getResetPasswordURL();
            if (!StringUtils.isEmpty(resetPasswordUrl)) {
                if (resetPasswordUrl.contains("?")) {
                    resetPasswordUrl=resetPasswordUrl+"&token="+token;
                } else {
                    resetPasswordUrl=resetPasswordUrl+"?token="+token;
                }
            }
            vars.put("resetPasswordUrl", resetPasswordUrl);
            emailService.sendTemplateEmail(user.getEmail(), getResetPasswordEmailInfo(), vars);
            
        }
        return response;
    }

    @Override
    @Transactional("blTransactionManager")
    public GenericResponse resetPasswordUsingToken(String username, String token, String password, String confirmPassword) {
        GenericResponse response = new GenericResponse();
        AdminUser user = null;
        if (username != null) {
            user = adminUserDao.readAdminUserByUserName(username);
        }
        checkUser(user, response);
        checkPassword(password, confirmPassword, response);
        if (StringUtils.isBlank(token)) {
            response.addErrorCode("invalidToken");
        }

        ForgotPasswordSecurityToken fpst = null;
        if (! response.getHasErrors()) {
            token = token.toLowerCase();
            List<ForgotPasswordSecurityToken> fpstoks = forgotPasswordSecurityTokenDao.readUnusedTokensByAdminUserId(user.getId());
            for (ForgotPasswordSecurityToken fpstok : fpstoks) {
                if (isPasswordValid(fpstok.getToken(), token, null)) {
                    fpst = fpstok;
                    break;
                }
            }
            if (fpst == null) {
                response.addErrorCode("invalidToken");
            } else if (fpst.isTokenUsedFlag()) {
                response.addErrorCode("tokenUsed");
            } else if (isTokenExpired(fpst)) {
                response.addErrorCode("tokenExpired");
            }
        }

        if (! response.getHasErrors()) {
            if (! user.getId().equals(fpst.getAdminUserId())) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Password reset attempt tried with mismatched user and token " + user.getId() + ", " + token);
                }
                response.addErrorCode("invalidToken");
            }
        }

        if (! response.getHasErrors()) {
            user.setUnencodedPassword(password);
            saveAdminUser(user);
            invalidateAllTokensForAdminUser(user);
        }

        return response;
    }

    protected void invalidateAllTokensForAdminUser(AdminUser user) {
        List<ForgotPasswordSecurityToken> tokens = forgotPasswordSecurityTokenDao.readUnusedTokensByAdminUserId(user.getId());
        for (ForgotPasswordSecurityToken token : tokens) {
            token.setTokenUsedFlag(true);
            forgotPasswordSecurityTokenDao.saveToken(token);
        }
    }

    protected void checkUser(AdminUser user, GenericResponse response) {
        if (user == null) {
            response.addErrorCode("invalidUser");
        } else if (StringUtils.isBlank(user.getEmail())) {
            response.addErrorCode("emailNotFound");
        } else if (BooleanUtils.isNotTrue(user.getActiveStatusFlag())) {
            response.addErrorCode("inactiveUser");
        }
    }
    
    protected void checkPassword(String password, String confirmPassword, GenericResponse response) {
        if (StringUtils.isBlank(password) || StringUtils.isBlank(confirmPassword)) {
            response.addErrorCode("invalidPassword");
        } else if (! password.equals(confirmPassword)) {
            response.addErrorCode("passwordMismatch");
        }
    }

    protected void checkExistingPassword(String unencodedPassword, AdminUser user, GenericResponse response) {
        if (!isPasswordValid(user.getPassword(), unencodedPassword, getSalt(user, unencodedPassword))) {
            response.addErrorCode("invalidPassword");
        }
    }

    protected boolean isTokenExpired(ForgotPasswordSecurityToken fpst) {
        Date now = SystemTime.asDate();
        long currentTimeInMillis = now.getTime();
        long tokenSaveTimeInMillis = fpst.getCreateDate().getTime();
        long minutesSinceSave = (currentTimeInMillis - tokenSaveTimeInMillis)/60000;
        return minutesSinceSave > getTokenExpiredMinutes();
    }

    public static int getPASSWORD_TOKEN_LENGTH() {
        return PASSWORD_TOKEN_LENGTH;
    }

    public static void setPASSWORD_TOKEN_LENGTH(int PASSWORD_TOKEN_LENGTH) {
        AdminSecurityServiceImpl.PASSWORD_TOKEN_LENGTH = PASSWORD_TOKEN_LENGTH;
    }

    public EmailInfo getSendUsernameEmailInfo() {
        return sendUsernameEmailInfo;
    }

    public void setSendUsernameEmailInfo(EmailInfo sendUsernameEmailInfo) {
        this.sendUsernameEmailInfo = sendUsernameEmailInfo;
    }

    public EmailInfo getResetPasswordEmailInfo() {
        return resetPasswordEmailInfo;
    }

    public void setResetPasswordEmailInfo(EmailInfo resetPasswordEmailInfo) {
        this.resetPasswordEmailInfo = resetPasswordEmailInfo;
    }

    @Deprecated
    @Override
    public Object getSalt(AdminUser user, String unencodedPassword) {
        Object salt = null;
        if (saltSource != null) {
            salt = saltSource.getSalt(new AdminUserDetails(user.getId(), user.getLogin(), unencodedPassword, new ArrayList<GrantedAuthority>()));
        }
        return salt;
    }

    @Deprecated
    @Override
    public String getSalt() {
        return salt;
    }

    @Deprecated
    @Override
    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Deprecated
    @Override
    public SaltSource getSaltSource() {
        return saltSource;
    }

    @Deprecated
    @Override
    public void setSaltSource(SaltSource saltSource) {
        this.saltSource = saltSource;
    }

    @Override
    @Transactional("blTransactionManager")
    public GenericResponse changePassword(String username,
            String oldPassword, String password, String confirmPassword) {
        GenericResponse response = new GenericResponse();
        AdminUser user = null;
        if (username != null) {
            user = adminUserDao.readAdminUserByUserName(username);
        }
        checkUser(user, response);
        checkPassword(password, confirmPassword, response);

        if (!response.getHasErrors()) {
            checkExistingPassword(oldPassword, user, response);
        }

        if (!response.getHasErrors()) {
            user.setUnencodedPassword(password);
            saveAdminUser(user);

        }

        return response;

    }

    /**
     * Determines if a password is valid by comparing it to the encoded string, optionally using a salt.
     * <p>
     * The externally salted {@link org.springframework.security.authentication.encoding.PasswordEncoder PasswordEncoder} support is
     * being deprecated, following in Spring Security's footsteps, in order to move towards self salting hashing algorithms such as bcrypt.
     * Bcrypt is a superior hashing algorithm that randomly generates a salt per password in order to protect against rainbow table attacks
     * and is an intentionally expensive algorithm to further guard against brute force attempts to crack hashed passwords.
     * Additionally, having the encoding algorithm handle the salt internally reduces code complexity and dependencies such as {@link SaltSource}.
     *
     * @deprecated the new {@link PasswordEncoder} handles salting internally, this will be removed in 4.2
     *
     * @param encodedPassword the encoded password
     * @param rawPassword the unencoded password
     * @param salt the optional salt
     * @return true if rawPassword matches the encodedPassword, false otherwise
     */
    @Deprecated
    protected boolean isPasswordValid(String encodedPassword, String rawPassword, Object salt) {
        if (usingDeprecatedPasswordEncoder()) {
            return passwordEncoder.isPasswordValid(encodedPassword, rawPassword, salt);
        } else {
            return isPasswordValid(encodedPassword, rawPassword);
        }
    }

    /**
     * Determines if a password is valid by comparing it to the encoded string, salting is handled internally to the {@link PasswordEncoder}.
     * <p>
     * This method must always be called to verify if a password is valid after the original encoded password is generated
     * due to {@link PasswordEncoder} randomly generating salts internally and appending them to the resulting hash.
     *
     * @param encodedPassword the encoded password
     * @param rawPassword the raw password to check against the encoded password
     * @return true if rawPassword matches the encodedPassword, false otherwise
     */
    protected boolean isPasswordValid(String encodedPassword, String rawPassword) {
        return passwordEncoderNew.matches(rawPassword, encodedPassword);
    }

    /**
     * Generate an encoded password from a raw password, optionally using a salt.
     * <p>
     * The externally salted {@link org.springframework.security.authentication.encoding.PasswordEncoder PasswordEncoder} support is
     * being deprecated, following in Spring Security's footsteps, in order to move towards self salting hashing algorithms such as bcrypt.
     * Bcrypt is a superior hashing algorithm that randomly generates a salt per password in order to protect against rainbow table attacks
     * and is an intentionally expensive algorithm to further guard against brute force attempts to crack hashed passwords.
     * Additionally, having the encoding algorithm handle the salt internally reduces code complexity and dependencies such as {@link SaltSource}.
     *
     * @deprecated the new {@link PasswordEncoder} handles salting internally, this will be removed in 4.2
     *
     * @param rawPassword
     * @param salt
     * @return
     */
    @Deprecated
    protected String encodePassword(String rawPassword, Object salt) {
        if (usingDeprecatedPasswordEncoder()) {
            return passwordEncoder.encodePassword(rawPassword, salt);
        } else {
            return encodePassword(rawPassword);
        }
    }

    /**
     * Generate an encoded password from a raw password, salting is handled internally to the {@link PasswordEncoder}.
     * <p>
     * This method can only be called once per password. The salt is randomly generated internally in the {@link PasswordEncoder}
     * and appended to the hash to provide the resulting encoded password. Once this has been called on a password,
     * going forward all checks for authenticity must be done by {@link #isPasswordValid(String, String)} as encoding the
     * same password twice will result in different encoded passwords.
     *
     * @param rawPassword the unencoded password to encode
     * @return the encoded password
     */
    protected String encodePassword(String rawPassword) {
        return passwordEncoderNew.encode(rawPassword);
    }

    @Deprecated
    protected boolean usingDeprecatedPasswordEncoder() {
        return passwordEncoder != null;
    }
}
