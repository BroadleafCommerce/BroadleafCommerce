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
    
    /**
     * Returns a transaction for given <b>type</b>. This is useful when validating whether or not a {@link PaymentTransaction}
     * can actually be added to this payment. For instance, there can only be a single {@link PaymentTransactionType#AUTHORIZE}
     * for a payment.
     * 
     * @param type the type of transaction to look for within {@link #getTransactions()}
     * @return a list of transactions or an empty list if there are no transaction of that type
     */
    public List<PaymentTransaction> getTransactionsForType(PaymentTransactionType type);

    /**
     * Returns the initial transaction for this order payment. This would either be an {@link PaymentTransactionType#AUTHORIZE}
     * or {@link PaymentTransactionType#AUTHORIZE_AND_CAPTURE} or {@link PaymentTransactionType#UNCONFIRMED}.
     * Implementation-wise this would
     * be any PaymentTransaction whose parentTransaction is NULL.
     *
     * @return the initial transaction for this order payment or null if there isn't any
     */
    public PaymentTransaction getInitialTransaction();
    
    /**
     * Looks through all of the transactions for this payment and adds up the amount for the given transaction type. This
     * ignores whether the transaction was successful or not
     * 
     * @param type
     * @return the amount of all of the transactions for the given type
     * @see {@link #getSuccessfulTransactionAmountForType(PaymentTransactionType)}
     */
    public Money getTransactionAmountForType(PaymentTransactionType type);
    
    /**
     * Returns all of the transactions on this payment that were successful for the given type.
     * 
     * @param type the type of transaction
     * @return the amount of all of the transaction on this payment for the given type that have been successful
     */
    public Money getSuccessfulTransactionAmountForType(PaymentTransactionType type);

    /**
     * Looks through all of the transactions for this payment and returns whether or not
     * it contains a transaction of type {@link PaymentTransactionType#AUTHORIZE_AND_CAPTURE} or
     * {@link PaymentTransactionType#AUTHORIZE}
     *
     * @return
     */
    public boolean isConfirmed();

    /**
     * Returns whether or not this payment is considered to be the final payment on the order.
     * The default implementation considers those payment of type {@link PaymentType#THIRD_PARTY_ACCOUNT}
     * and {@link PaymentType#CREDIT_CARD} final payments because integrations with external Payment Gateways require it.
     *
     * For example, a THIRD_PARTY_ACCOUNT payment's (e.g. PayPal Express Checkout) amount to charge
     * to the customer will be automatically calculated based on other payments that have already been applied
     * to the order, such as GIFT_CARDs or ACCOUNT_CREDITs. This final amount (OrderPayment) will be sent to the gateway.
     *
     * @return
     */
    public boolean isFinalPayment();

    /**
     * The currency that this payment should be taken in. This is a delegate to {@link #getOrder()#getCurrency()}.
     */
    public BroadleafCurrency getCurrency();

    /**
     * Returns the {@link CustomerPayment} associated with this payment, if there is one
     * @return
     */
    public CustomerPayment getCustomerPayment();

    /**
     * Sets the {@link CustomerPayment} of this payment
     *
     * @see {@link CustomerPayment}
     */
    public void setCustomerPayment(CustomerPayment customerPayment);

    public Boolean isSavePayment();

    public void setSavePayment(Boolean savePayment);

    public String getPaymentName();

    public void setPaymentName(String paymentName);

    public Boolean isUseExisting();

    public void setUseExisting(Boolean useExisting);
}
