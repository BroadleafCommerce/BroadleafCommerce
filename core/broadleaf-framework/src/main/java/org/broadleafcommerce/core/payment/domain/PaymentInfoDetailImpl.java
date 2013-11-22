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
import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.currency.util.CurrencyCodeIdentifiable;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Jerry Ocanas (jocanas)
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER_PAYMENT_DETAILS")
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.READONLY,
                                            booleanOverrideValue = true))
    }
)
public class PaymentInfoDetailImpl implements PaymentInfoDetail, CurrencyCodeIdentifiable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PaymentInfoDetailId")
    @GenericGenerator(
        name="PaymentInfoDetailId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="PaymentInfoDetailImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.payment.domain.PaymentInfoDetailImpl")
        }
    )
    @Column(name = "PAYMENT_DETAIL_ID")
    protected Long id;

    @ManyToOne(targetEntity = PaymentInfoImpl.class, optional = false)
    @JoinColumn(name = "PAYMENT_INFO")
    @AdminPresentation(excluded = true)
    protected PaymentInfo paymentInfo;

    @Column(name = "PAYMENT_INFO_DETAIL_TYPE")
    @AdminPresentation(friendlyName = "PaymentInfoDetailTypeImpl_Type",
            fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.core.payment.domain.PaymentInfoDetailType",
            prominent = true, gridOrder = 1000)
    protected String type;

    @Column(name = "PAYMENT_AMOUNT")
    @AdminPresentation(friendlyName = "PaymentInfoDetailTypeImpl_Amount", fieldType = SupportedFieldType.MONEY,
        prominent = true, gridOrder = 2000)
    protected BigDecimal amount;

    @ManyToOne(targetEntity = BroadleafCurrencyImpl.class)
    @JoinColumn(name = "CURRENCY_CODE")
    @AdminPresentation(excluded = true)
    protected BroadleafCurrency currency;

    @Column(name = "DATE_RECORDED")
    @AdminPresentation(friendlyName = "PaymentInfoDetailTypeImpl_Date", prominent = true, gridOrder = 3000)
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
        return PaymentInfoDetailType.getInstance(type);
    }

    @Override
    public void setType(PaymentInfoDetailType type) {
        this.type = (type == null) ? null : type.getType();
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

    @Override
    public String getCurrencyCode() {
        if (currency != null) {
            return currency.getCurrencyCode();
        }
        return ((CurrencyCodeIdentifiable) paymentInfo).getCurrencyCode();
    }
}
