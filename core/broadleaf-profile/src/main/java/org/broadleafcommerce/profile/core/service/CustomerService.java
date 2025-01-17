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

import org.broadleafcommerce.common.security.util.PasswordChange;
import org.broadleafcommerce.common.security.util.PasswordReset;
import org.broadleafcommerce.common.service.GenericResponse;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.dto.CustomerRuleHolder;
import org.broadleafcommerce.profile.core.service.handler.PasswordUpdatedHandler;
import org.broadleafcommerce.profile.core.service.listener.PostRegistrationObserver;

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

    public Customer readCustomerByExternalId(String userExternalId);

    public Customer createCustomer();

    /**
     * Returns a non-persisted {@link Customer} with a null id. Typically used with registering a new
     * customer or creating a new anonymous customer. Creating a customer with null id so that we don't
     * need to query the database for the next id everytime an anonymous customer browses the site.
     */
    Customer createCustomerWithNullId();

    /**
     * Delete the customer entity from the persistent store
     *
     * @param customer the customer entity to remove
     */
    void deleteCustomer(Customer customer);

    /**
     * Detaches the given Customer instance from the entity manager.
     *
     * @param customer
     */
    void detachCustomer(Customer customer);

    /**
     * Returns a {@link Customer} by first looking in the database, otherwise creating a new non-persisted {@link Customer}
     *
     * @param customerId the id of the customer to lookup
     */
    public Customer createCustomerFromId(Long customerId);

    /**
     * Returns a non-persisted {@link Customer}.
     *
     * @deprecated use {@link #createCustomer()} or {@link #createCustomerWithNullId()}} instead.
     */
    @Deprecated
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
     * Generates an access token and then emails the user.
     *
     * @param userName - the user to send a reset password email to.
     * @param forgotPasswordUrl - Base url to include in the email.
     * @return Response can contain errors including (invalidEmail, invalidUsername, inactiveUser)
     *
     */
    GenericResponse sendForcedPasswordChangeNotification(String userName, String forgotPasswordUrl);


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
     * Encodes the clear text parameter, using the salt provided by PasswordEncoder. Does not change the customer properties.
     * This method only encodes the password and returns the encoded result.
     * <p>
     * This method can only be called once per password. The salt is randomly generated internally in the {@link org.springframework.security.crypto.password.PasswordEncoder PasswordEncoder}
     * and appended to the hash to provide the resulting encoded password. Once this has been called on a password,
     * going forward all checks for authenticity must be done by {@link #isPasswordValid(String, String)} as encoding the
     * same password twice will result in different encoded passwords.
     *
     * @param rawPassword the unencoded password
     * @return the encoded password
     */
    public String encodePassword(String rawPassword);

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

    /**
     * Determines if the given customer passes the MVEL customer rule
     *
     * @param customer
     * @param customerRuleHolder an MVEL rule targeting Customers
     * @return true if the customer passes the rule, false otherwise
     */
    public boolean customerPassesCustomerRule(Customer customer, CustomerRuleHolder customerRuleHolder);

    List<Customer> readBatchCustomers(int start, int pageSize);

    Long readNumberOfCustomers();
}
