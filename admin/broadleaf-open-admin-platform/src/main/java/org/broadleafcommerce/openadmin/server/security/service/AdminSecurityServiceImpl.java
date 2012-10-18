/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.server.security.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.email.service.EmailService;
import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.broadleafcommerce.common.security.util.PasswordChange;
import org.broadleafcommerce.common.security.util.PasswordUtils;
import org.broadleafcommerce.common.service.GenericResponse;
import org.broadleafcommerce.common.time.SystemTime;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author jfischer
 *
 */
@Service("blAdminSecurityService")
@Transactional("blTransactionManager")
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
    AdminPermissionDao adminPermissionDao;

    @Resource(name="blPasswordEncoder")
    protected PasswordEncoder passwordEncoder;
    
    /**
     * Optional password salt to be used with the passwordEncoder
     */
    protected String salt;

    @Resource(name="blEmailService")
    protected EmailService emailService;

    @Resource(name="blSendAdminResetPasswordEmail")
    protected EmailInfo resetPasswordEmailInfo;

    @Resource(name="blSendAdminUsernameEmailInfo")
    protected EmailInfo sendUsernameEmailInfo;

    // Variables to set via external configuration.
    @Value("${tokenExpiredMinutes}")
    protected int tokenExpiredMinutes = 30;

    @Value("${resetPasswordURL}")
    protected String resetPasswordURL;

    @Override
    public void deleteAdminPermission(AdminPermission permission) {
        adminPermissionDao.deleteAdminPermission(permission);
    }

    @Override
    public void deleteAdminRole(AdminRole role) {
        adminRoleDao.deleteAdminRole(role);
    }

    @Override
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
    public AdminPermission saveAdminPermission(AdminPermission permission) {
        return adminPermissionDao.saveAdminPermission(permission);
    }

    @Override
    public AdminRole saveAdminRole(AdminRole role) {
        return adminRoleDao.saveAdminRole(role);
    }

    @Override
    public AdminUser saveAdminUser(AdminUser user) {
    	if (user.getUnencodedPassword() != null) {
            user.setPassword(passwordEncoder.encodePassword(user.getUnencodedPassword(), getSalt(user)));
        }
        return adminUserDao.saveAdminUser(user);
    }

    @Override
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
        return adminPermissionDao.isUserQualifiedForOperationOnCeilingEntity(adminUser, permissionType, ceilingEntityFullyQualifiedName);
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
    public GenericResponse sendForgotUsernameNotification(String emailAddress) {
        GenericResponse response = new GenericResponse();
        List<AdminUser> users = null;
        if (emailAddress != null) {
            users = adminUserDao.readAdminUserByEmail(emailAddress);
        }
        if (users == null || users.isEmpty()) {
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
            fpst.setToken(passwordEncoder.encodePassword(token, null));
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
    public GenericResponse resetPasswordUsingToken(String username, String token, String password, String confirmPassword) {
        GenericResponse response = new GenericResponse();
        AdminUser user = null;
        if (username != null) {
            user = adminUserDao.readAdminUserByUserName(username);
        }
        checkUser(user, response);
        checkPassword(password, confirmPassword, response);
        if (token == null || "".equals(token)) {
            response.addErrorCode("invalidToken");
        }

        ForgotPasswordSecurityToken fpst = null;
        if (! response.getHasErrors()) {
            token = token.toLowerCase();
            fpst = forgotPasswordSecurityTokenDao.readToken(passwordEncoder.encodePassword(token, null));
            if (fpst == null) {
                response.addErrorCode("invalidToken");
            } else if (fpst.isTokenUsedFlag()) {
                response.addErrorCode("tokenUsed");
            } else if (isTokenExpired(fpst)) {
                response.addErrorCode("tokenExpired");
            }
        }

        if (! response.getHasErrors()) {
            user.setUnencodedPassword(password);
            saveAdminUser(user);
            fpst.setTokenUsedFlag(true);
            forgotPasswordSecurityTokenDao.saveToken(fpst);
        }

        return response;
    }
    
    protected void checkUser(AdminUser user, GenericResponse response) {
        if (user == null) {
            response.addErrorCode("invalidUser");
        } else if (user.getEmail() == null || "".equals(user.getEmail())) {
            response.addErrorCode("emailNotFound");
        } else if (user.getActiveStatusFlag() == null || ! user.getActiveStatusFlag()) {
            response.addErrorCode("inactiveUser");
        }
    }
    
    protected void checkPassword(String password, String confirmPassword, GenericResponse response) {
        if (password == null || confirmPassword == null || "".equals(password) || "".equals(confirmPassword)) {
            response.addErrorCode("invalidPassword");
        } else if (! password.equals(confirmPassword)) {
            response.addErrorCode("passwordMismatch");
        }
    }
    
    protected void checkExistingPassword(String password, AdminUser user, GenericResponse response) {
        if (!passwordEncoder.isPasswordValid(user.getPassword(), password, getSalt(user))) {
            response.addErrorCode("invalidPassword");
        }
    }

    protected boolean isTokenExpired(ForgotPasswordSecurityToken fpst) {
        Date now = SystemTime.asDate();
        long currentTimeInMillis = now.getTime();
        long tokenSaveTimeInMillis = fpst.getCreateDate().getTime();
        long minutesSinceSave = (currentTimeInMillis - tokenSaveTimeInMillis)/60000;
        return minutesSinceSave > tokenExpiredMinutes;
    }

    public int getTokenExpiredMinutes() {
        return tokenExpiredMinutes;
    }

    public void setTokenExpiredMinutes(int tokenExpiredMinutes) {
        this.tokenExpiredMinutes = tokenExpiredMinutes;
    }

    public static int getPASSWORD_TOKEN_LENGTH() {
        return PASSWORD_TOKEN_LENGTH;
    }

    public static void setPASSWORD_TOKEN_LENGTH(int PASSWORD_TOKEN_LENGTH) {
        AdminSecurityServiceImpl.PASSWORD_TOKEN_LENGTH = PASSWORD_TOKEN_LENGTH;
    }

    public String getResetPasswordURL() {
        return resetPasswordURL;
    }

    public void setResetPasswordURL(String resetPasswordURL) {
        this.resetPasswordURL = resetPasswordURL;
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
    
    /**
     * Optionally provide a salt based on a a specific AdminUser.  By default, this returns
     * the salt property of this class
     * 
     * @param customer
     * @return
     * @see {@link AdminSecurityServiceImpl#getSalt()}
     */
    public String getSalt(AdminUser user) {
        return getSalt();
    }
    
    public String getSalt() {
        return salt;
    }
    
    public void setSalt(String salt) {
        this.salt = salt;
    }

	@Override
	public GenericResponse changePassword(String username,
			String oldPassword, String password, String confirmPassword) {
		GenericResponse response = new GenericResponse();
		AdminUser user = null;
		if (username != null) {
			user = adminUserDao.readAdminUserByUserName(username);
		}
		checkUser(user, response);
		checkPassword(password, confirmPassword, response);
		checkExistingPassword(oldPassword, user, response);

		if (!response.getHasErrors()) {
			user.setUnencodedPassword(password);
			saveAdminUser(user);

		}

		return response;

	}
}
