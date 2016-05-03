/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.payment.dao;

import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentLog;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;

import java.util.List;

public interface OrderPaymentDao {

    public OrderPayment readPaymentById(Long paymentId);

    public OrderPayment save(OrderPayment payment);

    public PaymentTransaction save(PaymentTransaction transaction);

    public PaymentLog save(PaymentLog log);

    public List<OrderPayment> readPaymentsForOrder(Order order);

    public OrderPayment create();

    public void delete(OrderPayment payment);

    public PaymentTransaction createTransaction();

    public PaymentTransaction readTransactionById(Long transactionId);

    public PaymentLog createLog();

}
