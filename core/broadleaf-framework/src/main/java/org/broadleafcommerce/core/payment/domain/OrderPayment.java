/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.payment.domain;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.PaymentTransactionType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.type.OrderStatus;
import org.broadleafcommerce.core.payment.domain.secure.Referenced;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;

import java.io.Serializable;
import java.util.List;

/**
 * <p>This entity is designed to deal with payments associated to an {@link Order} and is <i>usually</i> unique for a particular
 * amount, {@link PaymentGatewayType} and {@link PaymentType} combination. This is immediately invalid for scenarios where multiple payments of the
 * same {@link PaymentType} should be supported (like paying with 2 {@link PaymentType#CREDIT_CARD} or 2 {@link PaymentType#GIFT_CARD}).
 * That said, even though the use case might be uncommon in, Broadleaf does not actively prevent that situation from occurring
 * online payments it is very common in point of sale systems.</p>
 * 
 * <p>Once an {@link OrderPayment} is created, various {@link PaymentTransaction}s can be applied to this payment as
 * denoted by {@link PaymentTransactionType}. <b>There should be at least 1 {@link PaymentTransaction} for every
 * {@link OrderPayment} that is associated with an {@link Order} that has gone through checkout</b> (which means that
 * {@link Order#getStatus()} is {@link OrderStatus#SUBMITTED}).</p>
 * 
 * <p>{@link OrderPayment}s are not actually deleted from the database but rather are only soft-deleted (archived = true)</p>
 * 
 * @see {@link PaymentTransactionType}
 * @see {@link PaymentTransaction}
 * @see {@link PaymentType}
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface OrderPayment extends Serializable, Status {

    public Long getId();

    public void setId(Long id);

    public Order getOrder();

    public void setOrder(Order order);

    /**
     * Gets the billing address associated with this payment. This might be null for some payments where no billing address
     * is required (like gift cards or account credit)
     */
    public Address getBillingAddress();

    /**
     * Sets the billing address associated with this payment. This might be null for some payments where no billing address
     * is required (like gift cards or account credit)
     */
    public void setBillingAddress(Address billingAddress);

    /**
     * The amount that this payment is allotted for. The summation of all of the {@link OrderPayment}s for a particular
     * {@link Order} should equal {@link Order#getTotal()}
     */
    public Money getAmount();

    /**
     * The amount that this payment is allotted for. The summation of all of the {@link OrderPayment}s for a particular
     * {@link Order} should equal {@link Order#getTotal()}
     */
    public void setAmount(Money amount);

    /**
     * The soft link to a {@link Referenced} entity which will be stored in the blSecurePU persistence unit. If you are not
     * attempting to store credit cards in your own database (which is the usual, recommended case) then this will not be
     * used or set. If you do use this reference number, this can be anything that is unique (like System.currentTimeMillis()).
     */
    public String getReferenceNumber();

    /**
     * Sets the soft link to a {@link Referenced} entity stored in the blSecurePU persistence unit. This will likely not
     * be used as the common case is to not store credit cards yourself.
     */
    public void setReferenceNumber(String referenceNumber);

    /**
     * The type of this payment like Credit Card or Gift Card.
     * 
     * @see {@link PaymentType}
     */
    public PaymentType getType();

    /**
     * Sets the type of this payment like Credit Card or Gift Card
     * 
     * @see {@link PaymentType}
     */
    public void setType(PaymentType type);
    
    /**
     * Gets the gateway that was used to process this order payment. Only a SINGLE payment gateway can modify transactions
     * on a particular order payment.
     */
    public PaymentGatewayType getGatewayType();

    /**
     * <p>Gets the gateway that was used to process this order payment. Only a SINGLE payment gateway can modify transactions
     * on a particular order payment.</p>
     * 
     * <p>It usually does not make sense to modify the gateway type after it has already been set once. Instead, consider
     * just archiving this payment type (by deleting it) and creating a new payment for the new gateway.</p>
     */
    public void setPaymentGatewayType(PaymentGatewayType gatewayType);
    
    /**
     * <p>All of the transactions that have been applied to this particular payment. Transactions are denoted by the various
     * {@link PaymentTransactionType}s. In almost all scenarios (as in, 99.9999% of all cases) there will be a at least one
     * {@link PaymentTransaction} for every {@link OrderPayment}.</p>
     * 
     * <p>To add a transaction to an {@link OrderPayment} see {@link #addTransaction(PaymentTransaction)}.</p>
     * 
     * @see {@link #addTransaction(PaymentTransaction)}
     */
    public List<PaymentTransaction> getTransactions();

    /**
     * <p>All of the transactions that have been applied to this particular payment. Transactions are denoted by the various
     * {@link PaymentTransactionType}s. In almost all scenarios (as in, 99.9999% of all cases) there will be a at least one
     * {@link PaymentTransaction} for every {@link OrderPayment}.</p>
     * 
     * <p>To add a transaction to an {@link OrderPayment} see {@link #addTransaction(PaymentTransaction)}.</p>
     * 
     * @see {@link #addTransaction(PaymentTransaction)}
     */
    public void setTransactions(List<PaymentTransaction> details);
    
    /**
     * A more declarative way to invoke {@link #getTransactions().add()}. This is the preferred way to add a transaction
     * to this payment.
     */
    public void addTransaction(PaymentTransaction transaction);
    
    public Money getTransactionAmountForType(PaymentTransactionType type);

    /**
     * The currency that this payment should be taken in. This is a delegate to {@link #getOrder()#getCurrency()}.
     */
    public BroadleafCurrency getCurrency();

    /**
     * TODO: consider removing
     */
    public CustomerPayment getCustomerPayment();

    /**
     * TODO: consider removing
     */
    public void setCustomerPayment(CustomerPayment customerPayment);

}
