/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.payment.domain;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Jerry Ocanas (jocanas)
 */
public interface PaymentInfoDetail extends Serializable {

    public Long getId();

    public void setId(Long id);

    public PaymentInfo getPaymentInfo();

    public void setPaymentInfo(PaymentInfo paymentInfo);

    public PaymentInfoDetailType getType();

    public void setType(PaymentInfoDetailType type);

    public Money getAmount();

    public void setAmount(Money amount);

    public BroadleafCurrency getCurrency();

    public void setCurrency(BroadleafCurrency currency);

    public Date getDate();

    public void setDate(Date date);

}
