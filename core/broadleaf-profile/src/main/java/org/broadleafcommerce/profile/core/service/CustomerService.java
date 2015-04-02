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

    public Customer readCustomerByUsername(String username, Boolean cacheable);

    public Customer readCustomerByEmail(String emailAddress);

    public Customer changePassword(PasswordChange passwordChange);

    public Customer readCustomerById(Long userId);

    public Customer createCustomer();

    /**
     * Delete the customer entity from the persistent store
     *
     * @param customer the customer entity to remove
     */
    void deleteCustomer(Customer customer);

    /**
     * Returns a {@link Customer} by first looking in the database, otherwise creating a new non-persisted {@link Customer}
     *
     * @param customerId the id of the customer to lookup
     */
    public Customer createCustomerFromId(Long customerId);
    
    /**
     * Returns a non-persisted {@link Customer}. Typically used with registering a new customer.
     */
    public Customer createNewCustomer();
    
    /**
     * Subclassed implementations can assign unique roles for various customer types
     * 
     * @param customer {@link Customer} to create roles for
     */
    public void createRegisteredCustomerRoles(Customer customer);

    public void addPostRegisterListener(PostRegistrationObserver postRegisterListeners);

    public void removePostRegisterListener(PostRegistrationObserver postRegisterListeners);
    
    public Customer resetPassword(PasswordReset passwordReset);
    
    public List<PasswordUpdatedHandler> getPasswordResetHandlers();

    public void setPasswordResetHandlers(List<PasswordUpdatedHandler> passwordResetHandlers);
    
    public List<PasswordUpdatedHandler> getPasswordChangedHandlers();

    public void setPasswordChangedHandlers(List<PasswordUpdatedHandler> passwordChangedHandlers);
    
    /**
     * Looks up the corresponding {@link Customer} and emails the address on file with
     * the associated username.
     *
     * @param emailAddress user's email address
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
     * <p>
     * This method can only be used when using the deprecated {@link org.springframework.security.authentication.encoding.PasswordEncoder PasswordEncoder} bean, otherwise an exception will be thrown.
     * The new {@link org.springframework.security.crypto.password.PasswordEncoder PasswordEncoder} bean requires passing in a Customer to find the appropriate token.
     *
     * @deprecated  {@link #checkPasswordResetToken(String, Customer)}, this will be removed in 4.2
     * 
     * @param token password reset token
     * @return Response can contain errors including (invalidToken, tokenUsed, and tokenExpired)
     */
    @Deprecated
    public GenericResponse checkPasswordResetToken(String token);

    /**
     * Verifies that a customer has a valid token.
     *
     * @param token password reset token
     * @param customer {@link Customer} who owns the token
     * @return Response can contain errors including (invalidToken, tokenUsed, and tokenExpired)
     */
    public GenericResponse checkPasswordResetToken(String token, Customer customer);

    /**
     * Allow customers to call from subclassed service.
     *
     * @return the next customerId to be used
     */
    public Long findNextCustomerId();
    
    /**
     * @deprecated use {@link #getSaltSource()} instead, this will be removed in 4.2
     *
     * @return currently used salt string
     */
    @Deprecated
    public String getSalt();
    
    /**
     * @deprecated use {@link #setSaltSource(SaltSource)} instead, this will be removed in 4.2
     *
     * @param salt new salt string to use
     */
    @Deprecated
    public void setSalt(String salt);

    /**
     * Returns the {@link SaltSource} used with the blPasswordEncoder to encrypt the user password. Usually configured in
     * applicationContext-security.xml. This is not a required property and will return null if not configured
     *
     * @deprecated the new {@link org.springframework.security.crypto.password.PasswordEncoder PasswordEncoder} handles salting internally, this will be removed in 4.2
     *
     * @return the currently used {@link SaltSource}
     */
    @Deprecated
    public SaltSource getSaltSource();
    
    /**
     * Sets the {@link SaltSource} used with blPasswordEncoder to encrypt the user password. Usually configured within
     * applicationContext-security.xml
     *
     * @deprecated the new {@link org.springframework.security.crypto.password.PasswordEncoder PasswordEncoder} handles salting internally, this will be removed in 4.2
     *
     * @param saltSource the new {@link SaltSource} to use
     */
    @Deprecated
    public void setSaltSource(SaltSource saltSource);
    
    /**
     * @deprecated use {@link #getSalt(Customer, String)} instead, this will be removed in 4.2
     */
    @Deprecated
    public Object getSalt(Customer customer);
    
    /**
     * Gets the salt object for the current customer. By default this delegates to {@link #getSaltSource()}. If there is
     * not a {@link SaltSource} configured ({@link #getSaltSource()} returns null) then this also returns null.
     *
     * @deprecated the new {@link org.springframework.security.crypto.password.PasswordEncoder PasswordEncoder} handles salting internally, this will be removed in 4.2
     *
     * @param customer the {@link Customer} to get {@link org.springframework.security.core.userdetails.UserDetails UserDetails} from
     * @param unencodedPassword the unencoded password
     * @return the salt for the current customer
     */
    @Deprecated
    public Object getSalt(Customer customer, String unencodedPassword);
    
    /**
     * Encodes the clear text parameter, using the customer as a potential Salt. Does not change the customer properties. 
     * This method only encodes the password and returns the encoded result.
     * <p>
     * The externally salted {@link org.springframework.security.authentication.encoding.PasswordEncoder PasswordEncoder} support is
     * being deprecated, following in Spring Security's footsteps, in order to move towards self salting hashing algorithms such as bcrypt.
     * Bcrypt is a superior hashing algorithm that randomly generates a salt per password in order to protect against rainbow table attacks
     * and is an intentionally expensive algorithm to further guard against brute force attempts to crack hashed passwords.
     * Additionally, having the encoding algorithm handle the salt internally reduces code complexity and dependencies such as {@link SaltSource}.
     *
     * @deprecated the new {@link org.springframework.security.crypto.password.PasswordEncoder PasswordEncoder} handles salting internally, this will be removed in 4.2
     *
     * @param clearText the unencoded password
     * @param customer the {@link Customer} to use for the salt
     * @return the encoded password
     */
    @Deprecated
    public String encodePassword(String clearText, Customer customer);

    /**
     * Encodes the clear text parameter, using the salt provided by PasswordEncoder. Does not change the customer properties.
     * This method only encodes the password and returns the encoded result.
     * <p>
     * This method can only be called once per password. The salt is randomly generated internally in the {@link org.springframework.security.crypto.password.PasswordEncoder PasswordEncoder}
     * and appended to the hash to provide the resulting encoded password. Once this has been called on a password,
     * going forward all checks for authenticity must be done by {@link #isPasswordValid(String, String)} as encoding the
     * same password twice will result in different encoded passwords.
     *
     * @param clearText the unencoded password
     * @return the encoded password
     */
    public String encodePassword(String clearText);

    /**
     * Use this to determine if passwords match using a {@link Customer} for salting. Don't encode the password separately since sometimes salts
     * are generated randomly and stored with the password.
     * <p>
     * The externally salted {@link org.springframework.security.authentication.encoding.PasswordEncoder PasswordEncoder} support is
     * being deprecated, following in Spring Security's footsteps, in order to move towards self salting hashing algorithms such as bcrypt.
     * Bcrypt is a superior hashing algorithm that randomly generates a salt per password in order to protect against rainbow table attacks
     * and is an intentionally expensive algorithm to further guard against brute force attempts to crack hashed passwords.
     * Additionally, having the encoding algorithm handle the salt internally reduces code complexity and dependencies such as {@link SaltSource}.
     *
     * @deprecated the new {@link org.springframework.security.crypto.password.PasswordEncoder PasswordEncoder} handles salting internally, this will be removed in 4.2
     * 
     * @param rawPassword the unencoded password
     * @param encodedPassword the encoded password to compare against
     * @param customer the {@link Customer} to use for the salt
     * @return true if the unencoded password matches the encoded password, false otherwise
     */
    @Deprecated
    public boolean isPasswordValid(String rawPassword, String encodedPassword, Customer customer);

    /**
     * Determines if a password is valid by comparing it to the encoded string, salting is handled internally to the {@link org.springframework.security.crypto.password.PasswordEncoder PasswordEncoder}.
     * <p>
     * This method must always be called to verify if a password is valid after the original encoded password is generated
     * due to {@link org.springframework.security.crypto.password.PasswordEncoder PasswordEncoder} randomly generating salts internally and appending them to the resulting hash.
     *
     * @param rawPassword the unencoded password
     * @param encodedPassword the encoded password to compare against
     * @return true if the unencoded password matches the encoded password, false otherwise
     */
    public boolean isPasswordValid(String rawPassword, String encodedPassword);

}
