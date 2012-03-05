/*
 * Copyright 2008-2009 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.payment.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import java.math.BigDecimal;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;

/**
 * @author Jeff Fischer
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_TOTALLED_PAYMENT")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "totalledPaymentInfo")
public class TotalledPaymentInfoImpl extends PaymentInfoImpl implements TotalledPaymentInfo {

    @Column(name = "SUBTOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName="Payment Amount", order=3, group="Description", prominent=true, fieldType= SupportedFieldType.MONEY)
    protected BigDecimal subTotal;

    @Column(name = "TOTALTAX", precision=19, scale=5)
    @AdminPresentation(friendlyName="Payment Amount", order=3, group="Description", prominent=true, fieldType= SupportedFieldType.MONEY)
    protected BigDecimal totalTax;

    @Column(name = "TOTALSHIPPING", precision=19, scale=5)
    @AdminPresentation(friendlyName="Payment Amount", order=3, group="Description", prominent=true, fieldType= SupportedFieldType.MONEY)
    protected BigDecimal totalShipping;

    @Column(name = "SHIPPINGDISCOUNT", precision=19, scale=5)
    @AdminPresentation(friendlyName="Payment Amount", order=3, group="Description", prominent=true, fieldType= SupportedFieldType.MONEY)
    protected BigDecimal shippingDiscount;

    @Override
    public Money getShippingDiscount() {
        return shippingDiscount == null ? null : new Money(shippingDiscount);
    }

    @Override
    public void setShippingDiscount(Money shippingDiscount) {
        this.shippingDiscount = Money.toAmount(shippingDiscount);
    }

    @Override
    public Money getSubTotal() {
        return subTotal == null ? null : new Money(subTotal);
    }

    @Override
    public void setSubTotal(Money subTotal) {
        this.subTotal = Money.toAmount(subTotal);
    }

    @Override
    public Money getTotalShipping() {
        return totalShipping == null ? null : new Money(totalShipping);
    }

    @Override
    public void setTotalShipping(Money totalShipping) {
        this.totalShipping = Money.toAmount(totalShipping);
    }

    @Override
    public Money getTotalTax() {
        return totalTax == null ? null : new Money(totalTax);
    }

    @Override
    public void setTotalTax(Money totalTax) {
        this.totalTax = Money.toAmount(totalTax);
    }

}
