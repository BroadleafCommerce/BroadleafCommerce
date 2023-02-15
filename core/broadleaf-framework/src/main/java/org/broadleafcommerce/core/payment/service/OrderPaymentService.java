/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.core.payment.domain.PaymentLog;
import org.broadleafcommerce.core.payment.domain.PaymentTransaction;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;

import java.util.List;

public interface OrderPaymentService {

    public OrderPayment save(OrderPayment payment);

    public PaymentTransaction save(PaymentTransaction transaction);

    public PaymentLog save(PaymentLog log);

    public OrderPayment readPaymentById(Long paymentId);

    public List<OrderPayment> readPaymentsForOrder(Order order);

    public OrderPayment create();

    /**
     * Deletes a payment from the system. Note that this is just a soft-delete and simply archives this entity
     * 
     * @see {@link OrderPayment#getArchived()}
     */
    public void delete(OrderPayment payment);

    public PaymentTransaction createTransaction();

    public PaymentTransaction readTransactionById(Long transactionId);

    public PaymentLog createLog();

    /**
     * <p>
     * Create an {@link org.broadleafcommerce.core.payment.domain.OrderPayment} with a single
     * {@link org.broadleafcommerce.common.payment.PaymentTransactionType#UNCONFIRMED}
     * {@link org.broadleafcommerce.core.payment.domain.PaymentTransaction} initialized with the
     * passed in amount and order.
     *
     * <p>
     * Used typically during the payment flow of checkout, where a customer intends to pay for their
     * order with a saved payment token.
     *
     * @param order
     * @param customerPayment
     * @param amount
     * @return
     */
    public OrderPayment createOrderPaymentFromCustomerPayment(Order order, CustomerPayment customerPayment, Money amount);

    /**
     * <p>
     * Create a {@link org.broadleafcommerce.profile.core.domain.CustomerPayment} token for the passed in Customer
     * given a {@link org.broadleafcommerce.core.payment.domain.PaymentTransaction}. This assumes that the
     * token and any additional request attributes needed to do another transaction for this specific gateway
     * has already been persisted to the {@link org.broadleafcommerce.core.payment.domain.PaymentTransaction#getAdditionalFields()} map.
     * Specifically, the {@link org.broadleafcommerce.common.payment.PaymentAdditionalFieldType#TOKEN} has been set.
     *
     * <p>
     * Used typically during the complete checkout flow when a token needs to be saved from
     * a "confirmed" transaction
     *
     * @param transaction
     * @return
     */
    public CustomerPayment createCustomerPaymentFromPaymentTransaction(PaymentTransaction transaction);

    /**
     * <p>
     * Will attempt to populate the {@link org.broadleafcommerce.profile.core.domain.CustomerPayment#setPaymentToken(String)}
     * by looking at the {@link org.broadleafcommerce.core.payment.domain.PaymentTransaction#getAdditionalFields()}
     * for key {@link org.broadleafcommerce.common.payment.PaymentAdditionalFieldType#TOKEN}.
     *
     * <p>
     * Usually used during a checkout flow when there is a direct response from the gateway (e.g. transparent redirect).
     *
     * @param customerPayment
     * @param transaction
     * @see {@link org.broadleafcommerce.core.checkout.service.workflow.ValidateAndConfirmPaymentActivity}
     */
    public void populateCustomerPaymentToken(CustomerPayment customerPayment, PaymentTransaction transaction);

}
