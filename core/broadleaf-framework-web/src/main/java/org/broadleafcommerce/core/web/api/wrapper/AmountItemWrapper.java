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

import org.broadleafcommerce.core.payment.domain.AmountItem;
import org.broadleafcommerce.core.payment.domain.AmountItemImpl;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.service.PaymentInfoService;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around PaymentInfo.
 * <p/>
 * User: Elbert Bautista
 * Date: 4/26/12
 */
@XmlRootElement(name = "amountItem")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class AmountItemWrapper  extends BaseWrapper implements APIWrapper<AmountItem>, APIUnwrapper<AmountItem> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected String description;

    @XmlElement
    protected Long paymentInfoId;

    @XmlElement
    protected Long quantity;

    @XmlElement
    protected String shortDescription;

    @XmlElement
    protected String systemId;

    @XmlElement
    protected BigDecimal unitPrice;

    @Override
    public void wrapDetails(AmountItem model, HttpServletRequest request) {
        this.id = model.getId();
        this.description = model.getDescription();

        if (model.getPaymentInfo() != null ) {
            this.paymentInfoId = model.getPaymentInfo().getId();
        }

        this.quantity = model.getQuantity();
        this.shortDescription = model.getShortDescription();
        this.systemId = model.getSystemId();
        this.unitPrice = model.getUnitPrice();
    }

    @Override
    public void wrapSummary(AmountItem model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    @Override
    public AmountItem unwrap(HttpServletRequest request, ApplicationContext context) {
        AmountItem amountItem = new AmountItemImpl();
        amountItem.setId(this.id);
        amountItem.setDescription(this.description);

        PaymentInfoService paymentInfoService = (PaymentInfoService) context.getBean("blPaymentInfoService");
        PaymentInfo paymentInfo = paymentInfoService.readPaymentInfoById(this.paymentInfoId);
        if (paymentInfo != null) {
            amountItem.setPaymentInfo(paymentInfo);
        }

        amountItem.setQuantity(this.quantity);
        amountItem.setShortDescription(this.shortDescription);
        amountItem.setSystemId(this.systemId);
        amountItem.setUnitPrice(this.unitPrice);

        return amountItem;
    }
}
