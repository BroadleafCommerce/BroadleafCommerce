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

import org.broadleafcommerce.common.domain.Auditable;
import org.broadleafcommerce.order.domain.OrderImpl;
import org.broadleafcommerce.order.service.type.OrderStatus;
import org.broadleafcommerce.time.SystemTime;
import org.broadleafcommerce.util.money.Money;
import org.testng.annotations.DataProvider;

public class OrderDataProvider {

    @DataProvider(name = "basicOrder")
    public static Object[][] provideBasicSalesOrder() {
        OrderImpl so = new OrderImpl();
        Auditable auditable = new Auditable();
        auditable.setDateCreated(SystemTime.asDate());
        so.setAuditable(auditable);
        so.setStatus(OrderStatus.IN_PROCESS);
        so.setTotal(new Money(BigDecimal.valueOf(1000)));
        return new Object[][] { { so } };
    }
}
