/*
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.core.service;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.email.service.EmailService;
import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.broadleafcommerce.common.security.util.PasswordChange;
import org.broadleafcommerce.common.security.util.PasswordReset;
import org.broadleafcommerce.common.security.util.PasswordUtils;
import org.broadleafcommerce.common.service.GenericResponse;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.profile.core.dao.CustomerDao;
import org.broadleafcommerce.profile.core.dao.CustomerForgotPasswordSecurityTokenDao;
import org.broadleafcommerce.profile.core.dao.RoleDao;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerForgotPasswordSecurityToken;
import org.broadleafcommerce.profile.core.domain.CustomerForgotPasswordSecurityTokenImpl;
import org.broadleafcommerce.profile.core.domain.CustomerRole;
import org.broadleafcommerce.profile.core.domain.CustomerRoleImpl;
import org.broadleafcommerce.profile.core.domain.Role;
import org.broadleafcommerce.profile.core.service.handler.PasswordUpdatedHandler;
import org.broadleafcommerce.profile.core.service.listener.PostRegistrationObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

@Service("blCustomerService")
public class CustomerServiceImpl implements CustomerService {
    private static final Log LOG = LogFactory.getLog(CustomerServiceImpl.class);
    
    @Resource(name="blCustomerDao")
    protected CustomerDao customerDao;

    @Resource(name="blIdGenerationService")
    protected IdGenerationService idGenerationService;
    
    @Resource(name="blCustomerForgotPasswordSecurityTokenDao")
    protected CustomerForgotPasswordSecurityTokenDao customerForgotPasswordSecurityTokenDao;  

    @Resource(name="blPasswordEncoder")
    protected PasswordEncoder passwordEncoder;
    
    /**
     * Optional password salt to be used with the passwordEncoder
     * @deprecated utilize {@link #saltSource} instead so that it can be shared between this class as well as Spring's
     * authentication manager
     */
    @Deprecated
    protected String salt;
    
    /**
     * Use a Salt Source ONLY if there's one configured
     */
    @Autowired(required=false)
    @Qualifier("blSaltSource")
    protected SaltSource saltSource;
    
    @Resource(name="blRoleDao")
    protected RoleDao roleDao;
    
    @Resource(name="blEmailService")
    protected EmailService emailService;
    
    @Resource(name="blForgotPasswordEmailInfo")
    protected EmailInfo forgotPasswordEmailInfo;

    @Resource(name="blForgotUsernameEmailInfo")
    protected EmailInfo forgotUsernameEmailInfo;    
    
    @Resource(name="blRegistrationEmailInfo")
    protected EmailInfo registrationEmailInfo;    
    
    @Resource(name="blChangePasswordEmailInfo")
    protected EmailInfo changePasswordEmailInfo;       
    
    protected int tokenExpiredMinutes = 30;
    protected int passwordTokenLength = 20;   
             
    protected final List<PostRegistrationObserver> postRegisterListeners = new ArrayList<PostRegistrationObserver>();
    protected List<PasswordUpdatedHandler> passwordResetHandlers = new ArrayList<PasswordUpdatedHandler>();
    protected List<PasswordUpdatedHandler> passwordChangedHandlers = new ArrayList<PasswordUpdatedHandler>();

    @Override
    public Customer saveCustomer(Customer customer) {
        return saveCustomer(customer, true);
    }

    @Override
    public Customer saveCustomer(Customer customer, boolean register) {
        if (register && !customer.isRegistered()) {
            customer.setRegistered(true);
        }
        
        if (customer.getUnencodedPassword() != null) {
            customer.setPassword(encodePassword(customer.getUnencodedPassword(), customer));
        }

        // let's make sure they entered a new challenge answer (we will populate
        // the password field with hashed values so check that they have changed
        // id
        if (customer.getUnencodedChallengeAnswer() != null && !customer.getUnencodedChallengeAnswer().equals(customer.getChallengeAnswer())) {
            customer.setChallengeAnswer(encodePassword(customer.getUnencodedChallengeAnswer(), customer));
        }
        return customerDao.save(customer);
    }
    
    protected String generateSecurePassword() {
        return RandomStringUtils.randomAlphanumeric(16);
    }

    @Override
    public Customer registerCustomer(Customer customer, String password, String passwordConfirm) {
        customer.setRegistered(true);

        // When unencodedPassword is set the save() will encode it
        if (customer.getId() == null) {
            customer.setId(findNextCustomerId());
        }
        customer.setUnencodedPassword(password);
        Customer retCustomer = saveCustomer(customer);
        createRegisteredCustomerRoles(retCustomer);
        
        HashMap<String, Object> vars = new HashMap<String, Object>();
        vars.put("customer", retCustomer);
        
        emailService.sendTemplateEmail(customer.getEmailAddress(), getRegistrationEmailInfo(), vars);        
        notifyPostRegisterListeners(retCustomer);
        return retCustomer;
    }

    /**
     * Subclassed implementations can assign unique roles for various customer types 
     * 
     * @param customer
     */
    protected void createRegisteredCustomerRoles(Customer customer) {
        Role role = roleDao.readRoleByName("ROLE_USER");
        CustomerRole customerRole = new CustomerRoleImpl();
        customerRole.setRole(role);
        customerRole.setCustomer(customer);
        roleDao.addRoleToCustomer(customerRole);
    }

    @Override
    public Customer readCustomerByEmail(String emailAddress) {
        return customerDao.readCustomerByEmail(emailAddress);
    }

    @Override
    public Customer changePassword(PasswordChange passwordChange) {
        Customer customer = readCustomerByUsername(passwordChange.getUsername());
        customer.setUnencodedPassword(passwordChange.getNewPassword());
        customer.setPasswordChangeRequired(passwordChange.getPasswordChangeRequired());
        customer = saveCustomer(customer);
        
        for (PasswordUpdatedHandler handler : passwordChangedHandlers) {
            handler.passwordChanged(passwordChange, customer, passwordChange.getNewPassword());
        }
        
        return customer;
    }
    
    @Override
    public Customer resetPassword(PasswordReset passwordReset) {
        Customer customer = readCustomerByUsername(passwordReset.getUsername());
        String newPassword = PasswordUtils.generateTemporaryPassword(passwordReset.getPasswordLength());
        customer.setUnencodedPassword(newPassword);
        customer.setPasswordChangeRequired(passwordReset.getPasswordChangeRequired());
        customer = saveCustomer(customer);
        
        for (PasswordUpdatedHandler handler : passwordResetHandlers) {
            handler.passwordChanged(passwordReset, customer, newPassword);
        }
        
        return customer;
    }

    @Override
    public void addPostRegisterListener(PostRegistrationObserver postRegisterListeners) {
        this.postRegisterListeners.add(postRegisterListeners);
    }

    @Override
    public void removePostRegisterListener(PostRegistrationObserver postRegisterListeners) {
        if (this.postRegisterListeners.contains(postRegisterListeners)) {
            this.postRegisterListeners.remove(postRegisterListeners);
        }
    }

    protected void notifyPostRegisterListeners(Customer customer) {
        for (Iterator<PostRegistrationObserver> iter = postRegisterListeners.iterator(); iter.hasNext();) {
            PostRegistrationObserver listener = iter.next();
            listener.processRegistrationEvent(customer);
        }
    }

    @Override
    public Customer createCustomer() {
        return createCustomerFromId(null);
    }

    @Override
    public Customer createCustomerFromId(Long customerId) {
        Customer customer = customerId != null ? readCustomerById(customerId) : null;
        if (customer == null) {
            customer = customerDao.create();
            if (customerId != null) {
                customer.setId(customerId);
            } else {
                customer.setId(findNextCustomerId());
            }
        }
        return customer;
    }

    /**
     * Allow customers to call from subclassed service.
     * @return
     */
    @Override
    public Long findNextCustomerId() {
        return idGenerationService.findNextId("org.broadleafcommerce.profile.core.domain.Customer");
    }
    
    @Override
    public Customer createNewCustomer() {
        return createCustomerFromId(null);
    }

    @Override
    public void deleteCustomer(Customer customer) {
        customerDao.delete(customer);
    }

    @Override
    public Customer readCustomerByUsername(String username) {
        return customerDao.readCustomerByUsername(username);
    }

    @Override
    public Customer readCustomerById(Long id) {
        return customerDao.readCustomerById(id);
    }

    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * 
     * @deprecated use {@link #getSalt(Customer, String)} instead
     */
    @Deprecated
    @Override
    public Object getSalt(Customer customer) {
        return getSalt(customer, "");
    }
    
    /**
     * Optionally provide a salt based on a customer.  By default, this returns
     * the salt property
     * 
     * @param customer
     * @return
     * @see {@link CustomerServiceImpl#getSalt()}
     */
    @Override
    public Object getSalt(Customer customer, String unencodedPassword) {
        Object salt = null;
        if (saltSource != null && customer != null) {
            salt = saltSource.getSalt(new CustomerUserDetails(customer.getId(), customer.getUsername(), unencodedPassword, new ArrayList<GrantedAuthority>()));
        }
        return salt;
    }
    
    @Override
    public String encodePassword(String clearText, Customer customer) {
        return passwordEncoder.encodePassword(clearText, getSalt(customer, clearText));
    }

    @Override
    public boolean isPasswordValid(String rawPassword, String encodedPassword, Customer customer) {
        return passwordEncoder.isPasswordValid(encodedPassword, rawPassword, getSalt(customer, rawPassword));
    }

    @Override
    @Deprecated
    public String getSalt() {
        return salt;
    }
    
    @Override
    @Deprecated
    public void setSalt(String salt) {
        this.salt = salt;
    }
    
    @Override
    public SaltSource getSaltSource() {
        return saltSource;
    }
    
    @Override
    public void setSaltSource(SaltSource saltSource) {
        this.saltSource = saltSource;
    }

    @Override
    public List<PasswordUpdatedHandler> getPasswordResetHandlers() {
        return passwordResetHandlers;
    }

    @Override
    public void setPasswordResetHandlers(List<PasswordUpdatedHandler> passwordResetHandlers) {
        this.passwordResetHandlers = passwordResetHandlers;
    }

    @Override
    public List<PasswordUpdatedHandler> getPasswordChangedHandlers() {
        return passwordChangedHandlers;
    }

    @Override
    public void setPasswordChangedHandlers(List<PasswordUpdatedHandler> passwordChangedHandlers) {
        this.passwordChangedHandlers = passwordChangedHandlers;
    }
    
    @Override
    public GenericResponse sendForgotUsernameNotification(String emailAddress) {
        GenericResponse response = new GenericResponse();
        List<Customer> customers = null;
        if (emailAddress != null) {
            customers = customerDao.readCustomersByEmail(emailAddress);
        }

        if (customers == null || customers.isEmpty()) {
            response.addErrorCode("notFound");
        } else {
            List<String> activeUsernames = new ArrayList<String>();
            for (Customer customer: customers) {
                if (! customer.isDeactivated()) {
                    activeUsernames.add(customer.getUsername());
                }
            }

            if (activeUsernames.size() > 0) {
                HashMap<String, Object> vars = new HashMap<String, Object>();
                vars.put("userNames", activeUsernames);
                emailService.sendTemplateEmail(emailAddress, getForgotUsernameEmailInfo(), vars);
            } else {
                // send inactive username found email.
                response.addErrorCode("inactiveUser");
            }
        }
        return response;
    }

    @Override
    public GenericResponse sendForgotPasswordNotification(String username, String resetPasswordUrl) {
        GenericResponse response = new GenericResponse();
        Customer customer = null;

        if (username != null) {
            customer = customerDao.readCustomerByUsername(username);
        }

        checkCustomer(customer,response);

        if (! response.getHasErrors()) {        
            String token = PasswordUtils.generateTemporaryPassword(getPasswordTokenLength());
            token = token.toLowerCase();

            Object salt = getSalt(customer, token);

            String saltString = null;
            if (salt != null) {
                saltString = Hex.encodeHexString(salt.toString().getBytes());
            }

            CustomerForgotPasswordSecurityToken fpst = new CustomerForgotPasswordSecurityTokenImpl();
            fpst.setCustomerId(customer.getId());
            fpst.setToken(passwordEncoder.encodePassword(token, saltString));
            fpst.setCreateDate(SystemTime.asDate());
            customerForgotPasswordSecurityTokenDao.saveToken(fpst);

            if (saltString != null) {
                token = token + '-' + saltString;
            }

            HashMap<String, Object> vars = new HashMap<String, Object>();
            vars.put("token", token);
            if (!StringUtils.isEmpty(resetPasswordUrl)) {
                if (resetPasswordUrl.contains("?")) {
                    resetPasswordUrl=resetPasswordUrl+"&token="+token;
                } else {
                    resetPasswordUrl=resetPasswordUrl+"?token="+token;
                }
            }
            vars.put("resetPasswordUrl", resetPasswordUrl); 
            emailService.sendTemplateEmail(customer.getEmailAddress(), getForgotPasswordEmailInfo(), vars);
        }
        return response;
    }
    
    @Override
    public GenericResponse checkPasswordResetToken(String token) {
        GenericResponse response = new GenericResponse();
        checkPasswordResetToken(token, response);               
        return response;
    }
    
    protected CustomerForgotPasswordSecurityToken checkPasswordResetToken(String token, GenericResponse response) {
        if (token == null || "".equals(token)) {
            response.addErrorCode("invalidToken");
        }
        
        String rawToken = null;
        String salt = null;

        String[] tokens = token.split("-");
        if (tokens.length > 2) {
            response.addErrorCode("invalidToken");
        } else {
            rawToken = tokens[0].toLowerCase();
            if (tokens.length == 2) {
                salt = tokens[1];
            }
        }


        CustomerForgotPasswordSecurityToken fpst = null;
        if (!response.getHasErrors()) {
            fpst = customerForgotPasswordSecurityTokenDao.readToken(passwordEncoder.encodePassword(rawToken, salt));
            if (fpst == null) {
                response.addErrorCode("invalidToken");
            } else if (fpst.isTokenUsedFlag()) {
                response.addErrorCode("tokenUsed");
            } else if (isTokenExpired(fpst)) {
                response.addErrorCode("tokenExpired");
            }
        }       
        return fpst;
    }
    
    @Override
    public GenericResponse resetPasswordUsingToken(String username, String token, String password, String confirmPassword) {
        GenericResponse response = new GenericResponse();
        Customer customer = null;
        if (username != null) {
            customer = customerDao.readCustomerByUsername(username);
        }
        checkCustomer(customer, response);
        checkPassword(password, confirmPassword, response);
        CustomerForgotPasswordSecurityToken fpst = checkPasswordResetToken(token, response);
        
        if (! response.getHasErrors()) {
            if (! customer.getId().equals(fpst.getCustomerId())) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Password reset attempt tried with mismatched customer and token " + customer.getId() + ", " + token);
                }
                response.addErrorCode("invalidToken");
            }
        }

        if (! response.getHasErrors()) {
            customer.setUnencodedPassword(password);
            saveCustomer(customer);
            fpst.setTokenUsedFlag(true);
            customerForgotPasswordSecurityTokenDao.saveToken(fpst);
        }

        return response;        
    }
    
    protected void checkCustomer(Customer customer, GenericResponse response) {       
        if (customer == null) {         
            response.addErrorCode("invalidCustomer");
        } else if (customer.getEmailAddress() == null || "".equals(customer.getEmailAddress())) {
            response.addErrorCode("emailNotFound");
        } else if (customer.isDeactivated()) {
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

    protected boolean isTokenExpired(CustomerForgotPasswordSecurityToken fpst) {
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

    public int getPasswordTokenLength() {
        return passwordTokenLength;
    }

    public void setPasswordTokenLength(int passwordTokenLength) {
        this.passwordTokenLength = passwordTokenLength;
    }

    public EmailInfo getForgotPasswordEmailInfo() {
        return forgotPasswordEmailInfo;
    }

    public void setForgotPasswordEmailInfo(EmailInfo forgotPasswordEmailInfo) {
        this.forgotPasswordEmailInfo = forgotPasswordEmailInfo;
    }

    public EmailInfo getForgotUsernameEmailInfo() {
        return forgotUsernameEmailInfo;
    }

    public void setForgotUsernameEmailInfo(EmailInfo forgotUsernameEmailInfo) {
        this.forgotUsernameEmailInfo = forgotUsernameEmailInfo;
    }

    public EmailInfo getRegistrationEmailInfo() {
        return registrationEmailInfo;
    }

    public void setRegistrationEmailInfo(EmailInfo registrationEmailInfo) {
        this.registrationEmailInfo = registrationEmailInfo;
    }

    public EmailInfo getChangePasswordEmailInfo() {
        return changePasswordEmailInfo;
    }

    public void setChangePasswordEmailInfo(EmailInfo changePasswordEmailInfo) {
        this.changePasswordEmailInfo = changePasswordEmailInfo;
    }
}
