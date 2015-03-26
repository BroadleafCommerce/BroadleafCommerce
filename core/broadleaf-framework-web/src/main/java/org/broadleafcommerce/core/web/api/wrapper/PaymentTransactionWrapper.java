/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.money.util.CurrencyAdapter;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.util.xml.BigDecimalRoundingAdapter;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.core.payment.service.OrderPaymentService;
import org.springframework.context.ApplicationContext;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@XmlRootElement(name = "transaction")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class PaymentTransactionWrapper extends BaseWrapper implements APIWrapper<PaymentTransaction>, APIUnwrapper<PaymentTransaction> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected Long orderPaymentId;

    @XmlElement
    protected Long parentTransactionId;

    @XmlElement
    protected String type;

    @XmlElement
    protected String customerIpAddress;

    @XmlElement
    protected String rawResponse;

    @XmlElement
    protected Boolean success;

    @XmlElement
    @XmlJavaTypeAdapter(value = BigDecimalRoundingAdapter.class)
    protected BigDecimal amount;

    @XmlElement
    @XmlJavaTypeAdapter(value = CurrencyAdapter.class)
    protected Currency currency;

    @XmlElement(name = "element")
    @XmlElementWrapper(name = "additionalFields")
    protected List<MapElementWrapper> additionalFields;

    @Override
    public void wrapDetails(PaymentTransaction model, HttpServletRequest request) {
        this.id = model.getId();

        if (model.getOrderPayment() != null) {
            this.orderPaymentId = model.getOrderPayment().getId();
        }

        if (model.getParentTransaction() != null) {
            this.parentTransactionId = model.getParentTransaction().getId();
        }

        if (model.getType() != null) {
            this.type = model.getType().getType();
        }

        this.customerIpAddress = model.getCustomerIpAddress();
        this.rawResponse = model.getRawResponse();
        this.success = model.getSuccess();

        if (model.getAmount() != null) {
            this.amount = model.getAmount().getAmount();
            this.currency = model.getAmount().getCurrency();
        }

        this.additionalFields = super.createElementWrappers(model);

    }

    @Override
    public void wrapSummary(PaymentTransaction model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    @Override
    public PaymentTransaction unwrap(HttpServletRequest request, ApplicationContext context) {
        OrderPaymentService orderPaymentService = (OrderPaymentService) context.getBean("blOrderPaymentService");
        PaymentTransaction transaction = orderPaymentService.createTransaction();

        if (this.parentTransactionId != null) {
            PaymentTransaction parentTransaction = orderPaymentService.readTransactionById(this.parentTransactionId);
            transaction.setParentTransaction(parentTransaction);
        }

        transaction.setType(PaymentTransactionType.getInstance(this.type));

        if (this.additionalFields != null && !this.additionalFields.isEmpty()) {
            Map<String, String> fields = new HashMap<String, String>();
            for (MapElementWrapper mapElementWrapper : this.additionalFields) {
                fields.put(mapElementWrapper.getKey(), mapElementWrapper.getValue());
            }

            transaction.setAdditionalFields(fields);
        }

        if (this.amount != null) {
            if (this.currency != null) {
                transaction.setAmount(new Money(this.amount, this.currency));
            } else {
                transaction.setAmount(new Money(this.amount));
            }
        }

        transaction.setCustomerIpAddress(this.customerIpAddress);
        transaction.setRawResponse(this.rawResponse);
        transaction.setSuccess(this.success);

        return transaction;
    }
}
