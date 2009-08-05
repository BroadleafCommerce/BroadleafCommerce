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
package org.broadleafcommerce.test.dataprovider;

import java.math.BigDecimal;

import org.broadleafcommerce.payment.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.PaymentInfoImpl;
import org.broadleafcommerce.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.util.money.Money;
import org.testng.annotations.DataProvider;

public class PaymentInfoDataProvider {

    @DataProvider(name = "basicPaymentInfo")
    public static Object[][] provideBasicSalesPaymentInfo() {
        PaymentInfo sop = new PaymentInfoImpl();
        sop.setAmount(new Money(BigDecimal.valueOf(10.99)));
        sop.setReferenceNumber("987654321");
        sop.setType(PaymentInfoType.CREDIT_CARD);
        return new Object[][] { { sop } };
    }
}
