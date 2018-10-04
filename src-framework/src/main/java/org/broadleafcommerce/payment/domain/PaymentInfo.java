/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.payment.domain;

import java.io.Serializable;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.Phone;
import org.broadleafcommerce.util.money.Money;

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

}
