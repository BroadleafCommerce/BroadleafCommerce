/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
