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
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.PaymentType;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>This entity is designed to deal with payments associated to an {@link Customer} and is used to refer to a saved 
 * payment that is stored at the Payment Gateway level. This entity can be used to represent any type of payment, 
 * such as credit cards, PayPal accounts, etc.</p>
 */
public interface CustomerPayment extends AdditionalFields, Serializable, MultiTenantCloneable<CustomerPayment> {

    public void setId(Long id);

    public Long getId();

    public Customer getCustomer();

    public void setCustomer(Customer customer);

    public Address getBillingAddress();

    public void setBillingAddress(Address billingAddress);

    public String getPaymentToken();

    public void setPaymentToken(String paymentToken);

    public PaymentType getPaymentType();

    public void setPaymentType(PaymentType paymentType);

    public PaymentGatewayType getPaymentGatewayType();

    public void setPaymentGatewayType(PaymentGatewayType paymentGatewayType);

    public boolean isDefault();

    public void setIsDefault(boolean isDefault);

    @Override
    public Map<String, String> getAdditionalFields();

    @Override
    public void setAdditionalFields(Map<String, String> additionalFields);

}
