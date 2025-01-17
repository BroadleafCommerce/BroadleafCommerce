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
package org.broadleafcommerce.profile.core.domain;

import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.locale.domain.Locale;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface Customer extends Serializable, MultiTenantCloneable<Customer> {

    Long getId();

    void setId(Long id);

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    /**
     * <p>
     * If true, this customer must go through a reset password flow.
     * </p>
     * <p>
     * During a site conversion or security breach or a matter of routine security policy,
     * it may be necessary to require users to change their password. This property will
     * not allow a user whose credentials are managed within Broadleaf to login until
     * they have reset their password.
     * </p>
     * <p>
     * Used by blUserDetailsService.
     * </p>
     */
    boolean isPasswordChangeRequired();

    void setPasswordChangeRequired(boolean passwordChangeRequired);

    String getFirstName();

    void setFirstName(String firstName);

    String getLastName();

    void setLastName(String lastName);

    String getEmailAddress();

    void setEmailAddress(String emailAddress);

    String getExternalId();

    void setExternalId(String externalId);

    ChallengeQuestion getChallengeQuestion();

    void setChallengeQuestion(ChallengeQuestion challengeQuestion);

    String getChallengeAnswer();

    void setChallengeAnswer(String challengeAnswer);

    String getUnencodedPassword();

    void setUnencodedPassword(String unencodedPassword);

    boolean isReceiveEmail();

    void setReceiveEmail(boolean receiveEmail);

    boolean isRegistered();

    void setRegistered(boolean registered);

    String getUnencodedChallengeAnswer();

    void setUnencodedChallengeAnswer(String unencodedChallengeAnswer);

    Auditable getAuditable();

    void setAuditable(Auditable auditable);

    boolean isCookied();

    void setCookied(boolean cookied);

    boolean isLoggedIn();

    void setLoggedIn(boolean loggedIn);

    boolean isAnonymous();

    void setAnonymous(boolean anonymous);

    Locale getCustomerLocale();

    void setCustomerLocale(Locale customerLocale);

    Map<String, CustomerAttribute> getCustomerAttributes();

    void setCustomerAttributes(Map<String, CustomerAttribute> customerAttributes);

    /**
     * Returns true if this user has been deactivated.
     * Most implementations will not allow the user to login if they are deactivated.
     *
     * @return
     */
    boolean isDeactivated();

    /**
     * Sets the users deactivated status.
     *
     * @param deactivated
     */
    void setDeactivated(boolean deactivated);

    List<CustomerAddress> getCustomerAddresses();

    void setCustomerAddresses(List<CustomerAddress> customerAddresses);

    List<CustomerPhone> getCustomerPhones();

    void setCustomerPhones(List<CustomerPhone> customerPhones);

    List<CustomerPayment> getCustomerPayments();

    void setCustomerPayments(List<CustomerPayment> customerPayments);

    /**
     * The code used by an external system to determine if the user is tax exempt and/or what specific taxes the user is
     * exempt from.
     *
     * @return the code for this user's tax exemption reason, usually to just be passed to an external system
     * @see {@link #isTaxExempt()}
     */
    String getTaxExemptionCode();

    /**
     * Associates a tax exemption code to this user to notate tax exemption status. Default behavior in the
     * {@link org.broadleafcommerce.core.pricing.service.tax.provider.SimpleTaxProvider} is that if this is set to
     * any value then this customer is tax exempt.
     *
     * @param exemption the tax exemption code for the customer
     * @see {@link #isTaxExempt()}
     */
    void setTaxExemptionCode(String exemption);

    /**
     * <p>
     * Convenience method to represent if this customer should be taxed or not when pricing their {@link Order}. Default
     * behavior in the {@link org.broadleafcommerce.core.pricing.service.tax.provider.SimpleTaxProvider} is that if there
     * is anything in {@link #getTaxExemptionCode()} then the customer is exempt.
     *
     * <p>
     * If you assign special meaning to the {@link #getTaxExemptionCode()} then this might be different and you should
     * determine specific tax exemption based on {@link #getTaxExemptionCode()}
     *
     * @return whether or not this customer is exempt from tax calculations
     */
    boolean isTaxExempt();

    /**
     * This returns a non-null map of transient properties that are not
     * persisted to the database.
     *
     * @return
     */
    Map<String, Object> getTransientProperties();

}
