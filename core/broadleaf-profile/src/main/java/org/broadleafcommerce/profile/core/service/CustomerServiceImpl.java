/*-
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.event.BroadleafApplicationEventPublisher;
import org.broadleafcommerce.common.id.service.IdGenerationService;
import org.broadleafcommerce.common.rule.MvelHelper;
import org.broadleafcommerce.common.security.util.PasswordChange;
import org.broadleafcommerce.common.security.util.PasswordReset;
import org.broadleafcommerce.common.security.util.PasswordUtils;
import org.broadleafcommerce.common.service.GenericResponse;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.util.StringUtil;
import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.profile.core.dao.CustomerAddressDao;
import org.broadleafcommerce.profile.core.dao.CustomerDao;
import org.broadleafcommerce.profile.core.dao.CustomerForgotPasswordSecurityTokenDao;
import org.broadleafcommerce.profile.core.dao.RoleDao;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerForgotPasswordSecurityToken;
import org.broadleafcommerce.profile.core.domain.CustomerForgotPasswordSecurityTokenImpl;
import org.broadleafcommerce.profile.core.domain.CustomerRole;
import org.broadleafcommerce.profile.core.domain.CustomerRoleImpl;
import org.broadleafcommerce.profile.core.domain.Role;
import org.broadleafcommerce.profile.core.dto.CustomerRuleHolder;
import org.broadleafcommerce.profile.core.event.ForgotPasswordEvent;
import org.broadleafcommerce.profile.core.event.ForgotUsernameEvent;
import org.broadleafcommerce.profile.core.event.RegisterCustomerEvent;
import org.broadleafcommerce.profile.core.service.handler.PasswordUpdatedHandler;
import org.broadleafcommerce.profile.core.service.listener.PostRegistrationObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

@Service("blCustomerService")
public class CustomerServiceImpl implements CustomerService {
    private static final Log LOG = LogFactory.getLog(CustomerServiceImpl.class);
    private static final int PASSWORD_LENGTH = 16;

    @Autowired
    @Qualifier("blApplicationEventPublisher")
    protected BroadleafApplicationEventPublisher eventPublisher;

    @Resource(name="blCustomerDao")
    protected CustomerDao customerDao;

    @Resource(name="blCustomerAddressDao")
    protected CustomerAddressDao customerAddressDao;

    @Resource(name = "blIdGenerationService")
    protected IdGenerationService idGenerationService;

    @Resource(name = "blCustomerForgotPasswordSecurityTokenDao")
    protected CustomerForgotPasswordSecurityTokenDao customerForgotPasswordSecurityTokenDao;

    @Resource(name = "blPasswordEncoder")
    protected PasswordEncoder passwordEncoderBean;

    @Resource(name = "blRoleDao")
    protected RoleDao roleDao;

    protected int tokenExpiredMinutes = 30;
    protected int passwordTokenLength = 20;

    protected final List<PostRegistrationObserver> postRegisterListeners = new ArrayList<PostRegistrationObserver>();
    protected List<PasswordUpdatedHandler> passwordResetHandlers = new ArrayList<PasswordUpdatedHandler>();
    protected List<PasswordUpdatedHandler> passwordChangedHandlers = new ArrayList<PasswordUpdatedHandler>();

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public Customer saveCustomer(Customer customer) {
        return saveCustomer(customer, customer.isRegistered());
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public Customer saveCustomer(Customer customer, boolean register) {
        if (customer.getId() == null) {
            customer.setId(findNextCustomerId());
        }

        if (customer.getUsername() == null) {
            customer.setUsername(String.valueOf(customer.getId()));
            if (readCustomerById(customer.getId()) != null) {
                throw new IllegalArgumentException("Attempting to save a customer with an id (" + customer.getId() + ") " +
                        "that already exists in the database. This can occur when legacy customers have been migrated to " +
                        "Broadleaf customers, but the batchStart setting has not been declared for id generation. In " +
                        "such a case, the defaultBatchStart property of IdGenerationDaoImpl (spring id of " +
                        "blIdGenerationDao) should be set to the appropriate start value.");
            }
        }

        if (register && !customer.isRegistered()) {
            customer.setRegistered(true);
        }

        if (customer.getUnencodedPassword() != null) {
            customer.setPassword(encodePassword(customer.getUnencodedPassword()));
        }

        // let's make sure they entered a new challenge answer (we will populate
        // the password field with hashed values so check that they have changed
        // id
        if (customer.getUnencodedChallengeAnswer() != null && !customer.getUnencodedChallengeAnswer().equals(customer.getChallengeAnswer())) {
            customer.setChallengeAnswer(encodePassword(customer.getUnencodedChallengeAnswer()));
        }
        return customerDao.save(customer);
    }

    protected String generateSecurePassword() {
        return PasswordUtils.generateSecurePassword(PASSWORD_LENGTH);
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public Customer registerCustomer(Customer customer, String password, String passwordConfirm) {
        customer.setRegistered(true);

        // When unencodedPassword is set the save() will encode it
        customer.setUnencodedPassword(password);
        Customer retCustomer = saveCustomer(customer);
        createRegisteredCustomerRoles(retCustomer);
        
        eventPublisher.publishEvent(new RegisterCustomerEvent(this, retCustomer.getId()));
        notifyPostRegisterListeners(retCustomer);

        return retCustomer;
    }

    @Override
    public void createRegisteredCustomerRoles(Customer customer) {
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
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
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
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public Customer resetPassword(PasswordReset passwordReset) {
        Customer customer = readCustomerByUsername(passwordReset.getUsername());
        String newPassword = PasswordUtils.generateSecurePassword(passwordReset.getPasswordLength());
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
        for (Iterator<PostRegistrationObserver> iter = postRegisterListeners.iterator(); iter.hasNext(); ) {
            PostRegistrationObserver listener = iter.next();
            listener.processRegistrationEvent(customer);
        }
    }

    @Override
    public Customer createCustomer() {
        return createCustomerFromId(null);
    }

    @Override
    public Customer createCustomerWithNullId() {
        return customerDao.create();
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

    @Override
    public Long findNextCustomerId() {
        return idGenerationService.findNextId(Customer.class.getCanonicalName());
    }

    @Override
    @Deprecated
    public Customer createNewCustomer() {
        return createCustomerFromId(null);
    }

    @Override
    public void deleteCustomer(Customer customer) {
        roleDao.removeCustomerRolesByCustomerId(customer.getId());
        customerAddressDao.hardDeleteCustomerAddressesForCustomer(customer.getId());
        customerDao.delete(customer);
    }

    @Override
    public void detachCustomer(Customer customer) {
        customerDao.detach(customer);
    }

    @Override
    public Customer readCustomerByUsername(String username) {
        return customerDao.readCustomerByUsername(username);
    }

    @Override
    public Customer readCustomerByUsername(String username, Boolean cacheable) {
        return customerDao.readCustomerByUsername(username, cacheable);
    }

    @Override
    public Customer readCustomerById(Long id) {
        return customerDao.readCustomerById(id);
    }

    @Override
    public Customer readCustomerByExternalId(String userExternalId) {
        return customerDao.readCustomerByExternalId(userExternalId);
    }

    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoderBean.encode(rawPassword);
    }

    @Override
    public boolean isPasswordValid(String rawPassword, String encodedPassword) {
        return passwordEncoderBean.matches(rawPassword, encodedPassword);
    }

    @Override
    public boolean customerPassesCustomerRule(Customer customer, CustomerRuleHolder customerRuleHolder) {
        String customerRule = customerRuleHolder.getCustomerRule();
        Map<String, Object> ruleParams = buildCustomerRuleParams(customer);
        return customerRule == null || MvelHelper.evaluateRule(customerRule, ruleParams);
    }

    protected Map<String, Object> buildCustomerRuleParams(Customer customer) {
        HashMap<String, Object> vars = new HashMap<>();
        vars.put("customer", customer);
        return vars;
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
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public GenericResponse sendForgotUsernameNotification(String emailAddress) {
        GenericResponse response = new GenericResponse();
        List<Customer> customers = null;
        if (emailAddress != null) {
            customers = customerDao.readCustomersByEmail(emailAddress);
        }

        if (CollectionUtils.isEmpty(customers)) {
            response.addErrorCode("notFound");
        } else {
            List<String> activeUsernames = new ArrayList<String>();
            for (Customer customer : customers) {
                if (!customer.isDeactivated()) {
                    activeUsernames.add(customer.getUsername());
                }
            }

            if (activeUsernames.size() > 0) {
                eventPublisher.publishEvent(new ForgotUsernameEvent(this, emailAddress, activeUsernames));
            } else {
                // send inactive username found email.
                response.addErrorCode("inactiveUser");
            }
        }
        return response;
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public GenericResponse sendForgotPasswordNotification(String username, String resetPasswordUrl) {
        GenericResponse response = new GenericResponse();
        Customer customer = null;

        if (username != null) {
            customer = customerDao.readCustomerByUsername(username);
        }

        checkCustomer(customer, response);

        if (!response.getHasErrors()) {
            String token = PasswordUtils.generateSecurePassword(getPasswordTokenLength());
            token = token.toLowerCase();

            CustomerForgotPasswordSecurityToken fpst = new CustomerForgotPasswordSecurityTokenImpl();
            fpst.setCustomerId(customer.getId());
            fpst.setToken(encodePassword(token));
            fpst.setCreateDate(SystemTime.asDate());
            customerForgotPasswordSecurityTokenDao.saveToken(fpst);

            if (!StringUtils.isEmpty(resetPasswordUrl)) {
                if (resetPasswordUrl.contains("?")) {
                    resetPasswordUrl = resetPasswordUrl+"&token="+token;
                } else {
                    resetPasswordUrl = resetPasswordUrl+"?token="+token;
                }
            }

            eventPublisher.publishEvent(new ForgotPasswordEvent(this, customer.getId(), token, resetPasswordUrl));
        }
        return response;
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public GenericResponse sendForcedPasswordChangeNotification(String username, String resetPasswordUrl) {
        return sendForgotPasswordNotification(username, resetPasswordUrl);
    }

    @Override
    public GenericResponse checkPasswordResetToken(String token, Customer customer) {
        GenericResponse response = new GenericResponse();
        checkPasswordResetToken(token, customer, response);
        return response;
    }

    protected CustomerForgotPasswordSecurityToken checkPasswordResetToken(String token, Customer customer, GenericResponse response) {
        if (StringUtils.isBlank(token)) {
            response.addErrorCode("invalidToken");
        }

        String rawToken = token;

        CustomerForgotPasswordSecurityToken fpst = null;
        if (!response.getHasErrors()) {
            if (customer == null) {
                fpst = customerForgotPasswordSecurityTokenDao.readToken(encodePassword(token));
            } else {
                List<CustomerForgotPasswordSecurityToken> fpstoks = customerForgotPasswordSecurityTokenDao.readUnusedTokensByCustomerId(customer.getId());
                for (CustomerForgotPasswordSecurityToken fpstok : fpstoks) {
                    if (isPasswordValid(rawToken, fpstok.getToken())) {
                        fpst = fpstok;
                        break;
                    }
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
        return fpst;
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public GenericResponse resetPasswordUsingToken(String username, String token, String password, String confirmPassword) {
        GenericResponse response = new GenericResponse();
        Customer customer = null;
        if (username != null) {
            customer = customerDao.readCustomerByUsername(username);
        }
        checkCustomer(customer, response);
        checkPassword(password, confirmPassword, response);
        CustomerForgotPasswordSecurityToken fpst = checkPasswordResetToken(token, customer, response);

        if (!response.getHasErrors()) {
            if (!customer.getId().equals(fpst.getCustomerId())) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Password reset attempt tried with mismatched customer and token " + customer.getId() + ", " + StringUtil.sanitize(token));
                }
                response.addErrorCode("invalidToken");
            }
        }

        if (!response.getHasErrors()) {
            customer.setUnencodedPassword(password);
            customer.setPasswordChangeRequired(false);
            saveCustomer(customer);
            invalidateAllTokensForCustomer(customer);
        }

        return response;
    }

    protected void invalidateAllTokensForCustomer(Customer customer) {
        List<CustomerForgotPasswordSecurityToken> tokens = customerForgotPasswordSecurityTokenDao.readUnusedTokensByCustomerId(customer.getId());
        for (CustomerForgotPasswordSecurityToken token : tokens) {
            token.setTokenUsedFlag(true);
            customerForgotPasswordSecurityTokenDao.saveToken(token);
        }
    }

    protected void checkCustomer(Customer customer, GenericResponse response) {
        if (customer == null) {
            response.addErrorCode("invalidCustomer");
        } else if (StringUtils.isBlank(customer.getEmailAddress())) {
            response.addErrorCode("emailNotFound");
        } else if (customer.isDeactivated()) {
            response.addErrorCode("inactiveUser");
        }
    }

    protected void checkPassword(String password, String confirmPassword, GenericResponse response) {
        if (StringUtils.isBlank(password) || StringUtils.isBlank(confirmPassword)) {
            response.addErrorCode("invalidPassword");
        } else if (!password.equals(confirmPassword)) {
            response.addErrorCode("passwordMismatch");
        }
    }

    protected boolean isTokenExpired(CustomerForgotPasswordSecurityToken fpst) {
        Date now = SystemTime.asDate();
        long currentTimeInMillis = now.getTime();
        long tokenSaveTimeInMillis = fpst.getCreateDate().getTime();
        long minutesSinceSave = (currentTimeInMillis - tokenSaveTimeInMillis) / 60000;
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



    @Override
    public List<Customer> readBatchCustomers(int start, int pageSize) {
        return customerDao.readBatchCustomers(start, pageSize);
    }

    @Override
    public Long readNumberOfCustomers() {
        return customerDao.readNumberOfCustomers();
    }
}
