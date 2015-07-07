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
package org.broadleafcommerce.profile.core.domain;

import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.locale.domain.Locale;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface Customer extends Serializable, MultiTenantCloneable<Customer> {

    public Long getId();

    public void setId(Long id);

    public String getUsername();

    public void setUsername(String username);

    public String getPassword();

    public void setPassword(String password);

    public boolean isPasswordChangeRequired();

    public void setPasswordChangeRequired(boolean passwordChangeRequired);

    public String getFirstName();

    public void setFirstName(String firstName);

    public String getLastName();

    public void setLastName(String lastName);

    public String getEmailAddress();

    public void setEmailAddress(String emailAddress);

    public ChallengeQuestion getChallengeQuestion();

    public void setChallengeQuestion(ChallengeQuestion challengeQuestion);

    public String getChallengeAnswer();

    public void setChallengeAnswer(String challengeAnswer);

    public String getUnencodedPassword();

    public void setUnencodedPassword(String unencodedPassword);

    public boolean isReceiveEmail();

    public void setReceiveEmail(boolean receiveEmail);

    public boolean isRegistered();

    public void setRegistered(boolean registered);

    public String getUnencodedChallengeAnswer();

    public void setUnencodedChallengeAnswer(String unencodedChallengeAnswer);

    public Auditable getAuditable();

    public void setAuditable(Auditable auditable);
    
    public void setCookied(boolean cookied);
    
    public boolean isCookied();

    public void setLoggedIn(boolean loggedIn);
    
    public boolean isLoggedIn();

    public void setAnonymous(boolean anonymous);
    
    public boolean isAnonymous(); 

    public Locale getCustomerLocale();

    public void setCustomerLocale(Locale customerLocale);
    
    public Map<String, CustomerAttribute> getCustomerAttributes();

    public void setCustomerAttributes(Map<String, CustomerAttribute> customerAttributes);
    
    /**
     * Returns true if this user has been deactivated.
     * Most implementations will not allow the user to login if they are deactivated.
     * 
     * @return
     */
    public boolean isDeactivated();
    
    /**
     * Sets the users deactivated status.
     * 
     * @param deactivated
     */
    public void setDeactivated(boolean deactivated);

    public List<CustomerAddress> getCustomerAddresses();

    public void setCustomerAddresses(List<CustomerAddress> customerAddresses);

    public List<CustomerPhone> getCustomerPhones();

    public void setCustomerPhones(List<CustomerPhone> customerPhones);

    public List<CustomerPayment> getCustomerPayments();

    public void setCustomerPayments(List<CustomerPayment> customerPayments);

    /**
     * The code used by an external system to determine if the user is tax exempt and/or what specific taxes the user is
     * exempt from.
     * @return the code for this user's tax exemption reason, usually to just be passed to an external system
     * @see {@link #isTaxExempt()}
     */
    public String getTaxExemptionCode();

    /**
     * Associates a tax exemption code to this user to notate tax exemption status. Default behavior in the
     * {@link org.broadleafcommerce.core.pricing.service.tax.provider.SimpleTaxProvider} is that if this is set to
     * any value then this customer is tax exempt.
     * 
     * @param exemption the tax exemption code for the customer
     * @see {@link #isTaxExempt()}
     */
    public void setTaxExemptionCode(String exemption);
    
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
    public boolean isTaxExempt();
    
    /**
     * This returns a non-null map of transient properties that are not 
     * persisted to the database.
     * 
     * @return
     */
    public Map<String, Object> getTransientProperties();

}
