/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.payment.domain;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.broadleafcommerce.profile.core.domain.Phone;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface PaymentInfo extends Serializable {

    public Long getId();

    public void setId(Long id);

    public Order getOrder();

    public void setOrder(Order order);

    public Address getAddress();

    public void setAddress(Address address);

    public Phone getPhone();

    public void setPhone(Phone phone);

    public Money getAmount();

    public void setAmount(Money amount);

    public String getReferenceNumber();

    public void setReferenceNumber(String referenceNumber);

    public PaymentInfoType getType();

    public void setType(PaymentInfoType type);
    
    public void setAmountItems(List<AmountItem> amountItems);
    
    public List<AmountItem> getAmountItems();
    
    public String getCustomerIpAddress();

    public void setCustomerIpAddress(String customerIpAddress);

    public Map<String, String> getAdditionalFields();

    public void setAdditionalFields(Map<String, String> additionalFields);

    public Map<String, String[]> getRequestParameterMap();

    public void setRequestParameterMap(Map<String, String[]> requestParameterMap);

    public Referenced createEmptyReferenced();

    public List<PaymentInfoDetail> getPaymentInfoDetails();

    public void setPaymentInfoDetails(List<PaymentInfoDetail> details);

    public Money getPaymentCapturedAmount();

    public Money getPaymentCreditedAmount();

    public Money getReverseAuthAmount();

    public BroadleafCurrency getCurrency();

    public CustomerPayment getCustomerPayment();

    public void setCustomerPayment(CustomerPayment customerPayment);
}
