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

import org.broadleafcommerce.common.security.util.PasswordChange;
import org.broadleafcommerce.common.security.util.PasswordReset;
import org.broadleafcommerce.common.service.GenericResponse;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.handler.PasswordUpdatedHandler;
import org.broadleafcommerce.profile.core.service.listener.PostRegistrationObserver;
import org.springframework.security.authentication.dao.SaltSource;

import java.util.List;

public interface CustomerService {

    public Customer saveCustomer(Customer customer);

    public Customer saveCustomer(Customer customer, boolean register);

    public Customer registerCustomer(Customer customer, String password, String passwordConfirm);

    public Customer readCustomerByUsername(String customerName);

    public Customer readCustomerByEmail(String emailAddress);

    public Customer changePassword(PasswordChange passwordChange);

    public Customer readCustomerById(Long userId);

    public Customer createCustomer();

    /**
     * Returns a <code>Customer</code> by first looking in the database, otherwise creating a new non-persisted <code>Customer</code>
     * @param customerId the id of the customer to lookup
     * @return either a <code>Customer</code> from the database if it exists, or a new non-persisted <code>Customer</code>
     */
    public Customer createCustomerFromId(Long customerId);
    
    /**
     * Returns a non-persisted <code>Customer</code>.    Typically used with registering a new customer.
     */
    public Customer createNewCustomer();

    public void addPostRegisterListener(PostRegistrationObserver postRegisterListeners);

    public void removePostRegisterListener(PostRegistrationObserver postRegisterListeners);
    
    public Customer resetPassword(PasswordReset passwordReset);
    
    public List<PasswordUpdatedHandler> getPasswordResetHandlers();

    public void setPasswordResetHandlers(List<PasswordUpdatedHandler> passwordResetHandlers);
    
    public List<PasswordUpdatedHandler> getPasswordChangedHandlers();

    public void setPasswordChangedHandlers(List<PasswordUpdatedHandler> passwordChangedHandlers);
    
    /**
     * Looks up the corresponding Customer and emails the address on file with
     * the associated username.
     *
     * @param emailAddress
     * @return Response can contain errors including (notFound)
     */
    GenericResponse sendForgotUsernameNotification(String emailAddress);

    /**
     * Generates an access token and then emails the user.
     *
     * @param userName - the user to send a reset password email to.
     * @param forgotPasswordUrl - Base url to include in the email.
     * @return Response can contain errors including (invalidEmail, invalidUsername, inactiveUser)
     * 
     */
    GenericResponse sendForgotPasswordNotification(String userName, String forgotPasswordUrl);
    
    /**
     * Updates the password for the passed in customer only if the passed
     * in token is valid for that customer.
     *
     * @param username Username of the customer
     * @param token Valid reset token
     * @param password new password
     *
     * @return Response can contain errors including (invalidUsername, inactiveUser, invalidToken, invalidPassword, tokenExpired)
     */
    GenericResponse resetPasswordUsingToken(String username, String token, String password, String confirmPassword);
    
    /**
     * Verifies that the passed in token is valid.   
     * 
     * Returns responseCodes of "invalidToken", "tokenUsed", and "tokenExpired".
     * @param token
     * @return
     */
    public GenericResponse checkPasswordResetToken(String token);
    
    public Long findNextCustomerId();
    
    /**
     * @deprecated use {@link #getSaltSource()} instead
     */
    @Deprecated
    public String getSalt();
    
    /**
     * @deprecated use {@link #setSaltSource(SaltSource)} instead
     */
    @Deprecated
    public void setSalt(String salt);

    /**
     * Returns the {@link SaltSource} used with the blPasswordEncoder to encrypt the user password. Usually configured in
     * applicationContext-security.xml. This is not a required property and will return null if not configured
     */
    public SaltSource getSaltSource();
    
    /**
     * Sets the {@link SaltSource} used with blPasswordencoder to encrypt the user password. Usually configured within
     * applicationContext-security.xml
     * 
     * @param saltSource
     */
    public void setSaltSource(SaltSource saltSource);
    
    /**
     * Gets the salt object for the current customer. By default this delegates to {@link #getSaltSource()}. If there is
     * not a {@link SaltSource} configured ({@link #getSaltSource()} returns null) then this also returns null.
     * 
     * @param customer
     * @return the salt for the current customer
     */
    public Object getSalt(Customer customer);
    
    /**
     * Encodes the clear text parameter, using the customer as a potential Salt. Does not change the customer properties. 
     * This method only encodes the password and returns the encoded result.
     * @param clearText
     * @param customer
     * @return
     */
    public String encodePassword(String clearText, Customer customer);

}
