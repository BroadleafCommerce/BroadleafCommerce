/*
 * #%L
 * BroadleafCommerce Integration
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
package org.broadleafcommerce.core.payment;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.OrderPaymentImpl;
import org.testng.annotations.DataProvider;

import java.math.BigDecimal;

public class PaymentInfoDataProvider {

    @DataProvider(name = "basicPaymentInfo")
    public static Object[][] provideBasicSalesPaymentInfo() {
        OrderPayment sop = new OrderPaymentImpl();
        sop.setAmount(new Money(BigDecimal.valueOf(10.99)));
        sop.setReferenceNumber("987654321");
        sop.setType(PaymentType.CREDIT_CARD);
        return new Object[][] { { sop } };
    }
}
