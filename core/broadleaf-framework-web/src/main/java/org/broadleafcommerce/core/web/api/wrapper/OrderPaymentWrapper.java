/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.money.util.CurrencyAdapter;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.util.xml.BigDecimalRoundingAdapter;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.service.OrderPaymentService;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.util.Currency;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * This is a JAXB wrapper around OrderPayment.
 * <p/>
 * User: Elbert Bautista
 * Date: 4/26/12
 */
@XmlRootElement(name = "paymentInfo")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class OrderPaymentWrapper extends BaseWrapper implements APIWrapper<OrderPayment>, APIUnwrapper<OrderPayment> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected Long orderId;

    @XmlElement
    protected String type;

    @XmlElement
    protected AddressWrapper billingAddress;

    @XmlElement
    @XmlJavaTypeAdapter(value = BigDecimalRoundingAdapter.class)
    protected BigDecimal amount;

    @XmlElement
    @XmlJavaTypeAdapter(value = CurrencyAdapter.class)
    protected Currency currency;

    @XmlElement
    protected String referenceNumber;

    @Override
    public void wrapDetails(OrderPayment model, HttpServletRequest request) {
        this.id = model.getId();

        if (model.getOrder() != null) {
            this.orderId = model.getOrder().getId();
        }

        if (model.getType() != null) {
            this.type = model.getType().getType();
        }

        if (model.getBillingAddress() != null) {
            AddressWrapper addressWrapper = (AddressWrapper) context.getBean(AddressWrapper.class.getName());
            addressWrapper.wrapDetails(model.getBillingAddress(), request);
            this.billingAddress = addressWrapper;
        }

        if (model.getAmount() != null) {
            this.amount = model.getAmount().getAmount();
            this.currency = model.getAmount().getCurrency();
        }

        this.referenceNumber = model.getReferenceNumber();
    }

    @Override
    public void wrapSummary(OrderPayment model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    @Override
    public OrderPayment unwrap(HttpServletRequest request, ApplicationContext context) {
        OrderPaymentService paymentInfoService = (OrderPaymentService) context.getBean("blOrderPaymentService");
        OrderPayment paymentInfo = paymentInfoService.create();

        paymentInfo.setType(PaymentType.getInstance(this.type));

        if (this.billingAddress != null) {
            paymentInfo.setBillingAddress(this.billingAddress.unwrap(request, context));
        }

        if (this.amount != null) {
            if (this.currency != null) {
                paymentInfo.setAmount(new Money(this.amount, this.currency));
            } else {
                paymentInfo.setAmount(new Money(this.amount));
            }
        }

        paymentInfo.setReferenceNumber(this.referenceNumber);

        return paymentInfo;
    }
}
