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
import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Jerry Ocanas (jocanas)
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER_PAYMENT_DETAILS")
public class PaymentInfoDetailImpl implements PaymentInfoDetail {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PaymentInfoDetailId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PaymentInfoDetailId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PaymentInfoDetailImpl", allocationSize = 50)
    @Column(name = "PAYMENT_DETAIL_ID")
    protected Long id;

    @ManyToOne(targetEntity = PaymentInfoImpl.class, optional = false)
    @JoinColumn(name = "PAYMENT_INFO")
    @AdminPresentation(excluded = true)
    protected PaymentInfo paymentInfo;

    @Column(name = "PAYMENT_INFO_DETAIL_TYPE")
    @AdminPresentation(friendlyName = "PaymentInfoDetailTypeImpl_Type")
    protected PaymentInfoDetailType type;

    @Column(name = "PAYMENT_AMOUNT")
    @AdminPresentation(friendlyName = "PaymentInfoDetailTypeImpl_Amount")
    protected BigDecimal amount;

    @ManyToOne(targetEntity = BroadleafCurrencyImpl.class)
    @JoinColumn(name = "CURRENCY_CODE")
    @AdminPresentation(friendlyName = "PaymentInfoDetailTypeImpl_Currency_Code")
    protected BroadleafCurrency currency;

    @Column(name = "DATE_RECORDED")
    @AdminPresentation(friendlyName = "PaymentInfoDetailTypeImpl_Date")
    protected Date date;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    @Override
    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    @Override
    public PaymentInfoDetailType getType() {
        return type;
    }

    @Override
    public void setType(PaymentInfoDetailType type) {
        this.type = type;
    }

    @Override
    public Money getAmount() {
        return amount == null ? BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getCurrency()) : BroadleafCurrencyUtils.getMoney(amount, getCurrency());
    }

    @Override
    public void setAmount(Money amount) {
        this.amount = amount.getAmount();
    }

    @Override
    public BroadleafCurrency getCurrency() {
        return currency;
    }

    @Override
    public void setCurrency(BroadleafCurrency currency) {
        this.currency = currency;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;
    }
}
