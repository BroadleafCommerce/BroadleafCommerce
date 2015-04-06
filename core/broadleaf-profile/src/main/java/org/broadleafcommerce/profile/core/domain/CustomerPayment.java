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

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.persistence.Status;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * <p>This entity is designed to deal with payments associated to an {@link Customer} and is used to refer to a saved 
 * payment that is stored at the Payment Gateway level. This entity can be used to represent any type of payment, 
 * such as credit cards, PayPal accounts, etc.</p>
 */
public interface CustomerPayment extends Status, Serializable, AdditionalFields, MultiTenantCloneable<CustomerPayment> {

    public void setId(Long id);

    public Long getId();

    public Customer getCustomer();

    public void setCustomer(Customer customer);

    public Address getBillingAddress();

    public void setBillingAddress(Address billingAddress);

    public String getPaymentToken();

    public void setPaymentToken(String paymentToken);

    public boolean isDefault();

    public void setIsDefault(boolean isDefault);

    public String getLastPaymentStatus();

    public void setLastPaymentStatus(String aDefault);

    public Map<String, String> getAdditionalFields();

    public void setAdditionalFields(Map<String, String> additionalFields);

    /**
     * Returns the name of this payment.
     *
     * @return String
     */
    public String getName();

    /**
     * Sets the name of this payment.
     *
     * @param name
     */
    public void setName(String name);

    /**
     * Returns the {@link Date} on which this payment is set to expire.
     * For credit cards, this would be 00:00:00 of the first day after expiration month. 
     * 
     * @return {@link Date}
     */
    public Date getExpirationDate();

    /**
     * Sets the {@link Date} on which this payment is set to expire.
     * For credit cards, this would be 00:00:00 of the first day after expiration month.
     *
     * @param expirationDate
     */
    public void setExpirationDate(Date expirationDate);

    /**
     * Returns the last four digits of the credit card that this payment represents.
     * This field would be used on {@link CustomerPayment}s that represent credit card payments.  
     *
     * @return String
     */
    public String getLastFour();

    /**
     * Sets the last four digits of the credit card that this payment represents.
     * This field would be set on {@link CustomerPayment}s that represent credit card payments.
     *
     * @param lastFour
     */
    public void setLastFour(String lastFour);

    /**
     * Returns the {@link org.broadleafcommerce.common.payment.CreditCardType} of the card that this payment represents.
     * This field would be used on {@link CustomerPayment}s that represent credit card payments.  
     *
     * @return String
     */
    public String getCardType();

    /**
     * Sets the {@link org.broadleafcommerce.common.payment.CreditCardType} of the card that this payment represents.
     * This field would be set on {@link CustomerPayment}s that represent credit card payments.
     *
     * @param cardType
     */
    public void setCardType(String cardType);

    public Date getLastExpirationNotification();

    public void setLastExpirationNotification(Date lastExpirationNotification);

    public boolean isActualExpiration();

    public void setActualExpiration(boolean actualExpiration);

    public String getGatewayType();

    public void setGatewayType(String gatewayType);
}
